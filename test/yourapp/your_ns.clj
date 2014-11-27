(ns yourapp.your-ns
  (:require [defrecord-wrapper.aop :as aop ]
            [defrecord-wrapper.reflect :as r ])
  (:import [defrecord_wrapper.aop SimpleWrapper])
  )


;; here your protocol, defrecord definitions and defrecord instance

(defprotocol Welcome
  (greetings [e] )
  (say-bye [e a b])
  )

(defrecord Example []
  Welcome
  (greetings [this] "my example greeting!")
  (say-bye [this a b] (str "saying good bye from " a " to " b)))

(def my-example-instance (Example.))

;; here your fn middleware to apply

(defn logging-access-protocol
  [*fn* this & args]
  (println ">> LOGGING-ACCESS " [this args])
  (println ">>"(meta *fn*))
  (apply *fn* (conj args this)))

;; here extending SimpleWrapper with your defrecord instance functions protocols

(aop/add-extends SimpleWrapper (r/get-specific-supers my-example-instance) logging-access-protocol)

;; here wrapping your defrecord instance with SimpleWrapper

(def my-wrapped-example (SimpleWrapper. my-example-instance))


;; and ... invoking wrapper

(println (greetings my-wrapped-example))

;;=> >>>>>>>>>> LOGGING-ACCESS [#defrecord_wrapper.model.Example{} nil]
;; ...  {:function-args [e], :wrapper #defrecord_wrapper.aop.SimpleWrapper{:wrapped-record #your-ns.Example{}}, :function-name greetings}
;;=> my example greeting!


(say-bye my-wrapped-example "clojure" "java")
;;=> >>>>>>>>>> LOGGING-ACCESS [#defrecord_wrapper.model.Example{} (clojure java)]
;; ... {:function-args [e a b], :wrapper #defrecord_wrapper.aop.SimpleWrapper{:wrapped-record #your-ns.Example{}}, :function-name say_bye}
;;=> saying good bye from clojure to java




(comment
  ;; here your aop/Matcher implementation and fn middleware to apply

(defn logging-access-protocol
  [*fn* this & args]
  (println ">>>>>>>>>> LOGGING-ACCESS" [this args] (meta *fn*))
  (apply *fn* (conj args this)))

(defrecord ExampleMatcher []
  aop/Matcher
  (match [this protocol function-name function-args]
      ;; logging all fns calls
      logging-access-protocol))
)
