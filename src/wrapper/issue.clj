(ns wrapper.issue
  (:require [schema.core :as s]))

(defprotocol Welcome
  (greetings [e] )
  (say_bye [e a b])
  )

(s/defn greetings+ :-  s/Str
  [component :- (s/protocol Welcome)]
  (greetings component))

(defrecord Example []
  Welcome
  (greetings [this] "my example greeting!")
  (say_bye [this a b] (str "saying good bye from " a " to " b))
  )

(defrecord MoreSimpleWrapper [e])

(extend MoreSimpleWrapper
  Welcome
  {:greetings (fn [this]
      (str "wrapping!! " (greetings (:e this)))
      )
   :say_bye (fn  [this a b]
               (str "good bye !"))})


(println (satisfies? Welcome (MoreSimpleWrapper. (Example.))))
;;=>true
(println  (s/validate (s/protocol Welcome) (MoreSimpleWrapper. (Example.))))
;;=>#wrapper.issue.MoreSimpleWrapper{:e #wrapper.issue.Example{}}

(s/with-fn-validation
  (println (satisfies? Welcome (MoreSimpleWrapper. (Example.))))
  ;;=>true
  (println  (s/validate (s/protocol Welcome) (MoreSimpleWrapper. (Example.))))
  ;;=>#wrapper.issue.MoreSimpleWrapper{:e #wrapper.issue.Example{}}
)

(s/with-fn-validation
  (greetings+ (MoreSimpleWrapper. (Example.))))
;;=>CompilerException clojure.lang.ExceptionInfo: Input to greetings+ does not match schema: [(named (not (satisfies? Welcome a-wrapper.issue.MoreSimpleWrapper)) component)] {:schema [#schema.core.One{:schema (protocol Welcome), :optional? false, :name component}], :value [#wrapper.issue.MoreSimpleWrapper{:e #wrapper.issue.Example{}}], :error [(named (not (satisfies? Welcome a-wrapper.issue.MoreSimpleWrapper)) component)]}, compiling:(/Users/tangrammer/git/olney/wrapper/src/wrapper/issue.clj:39:69)
