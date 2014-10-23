(ns wrapper.core
  (:require [schema.core :as s]
            [clojure.pprint :refer (pprint)]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]))

(defprotocol Welcome
  (greetings [_] )
  (say_bye [_ a b]))

(defrecord Example []
  Welcome
  (greetings [this] "my example greeting!")
  (say_bye [this a b] (str "say good bye" a b))
  )

(greetings (Example.))
;;=> "my example greeting!"

(defn get-supers [instance]
     (filter (fn [ty] (some #(.contains  (str ty) %) #{"wrapper"}))
             (->> (supers (class instance)))))

(get-supers (Example.))
;;=> (wrapper.core.Welcome)

(defn get-methods [instance]
  (map (fn [sup]
         [sup (->> (.getDeclaredMethods sup)
                   (map #(vector (count (.getParameterTypes %)) (.getName %)))
                   (into #{}))])
       (get-supers instance)))

(get-methods (Example.))
;;=> ([wrapper.core.Welcome #{[2 "say_bye"] [0 "greetings"]}])

(defn get-params [n]
  (vec (take (inc n)
             (conj (map (comp symbol str char) (range 97 123)) (symbol "this")))))

(get-params 3)
;;=> [this a b c]

(defn adapt-super-impls
  "java-meta-data"
  [[prot-class prot-fns]]
  [prot-class (map (fn [[b c]] [(symbol c) (get-params b)])
         prot-fns)])

(adapt-super-impls (first (get-methods (Example.))))
;;=> [wrapper.core.Welcome ([say_bye [this a b]] [greetings [this]])]

(defmacro protocol-impl [protocol-definition]
  `(defrecord ~(symbol "zulu") [e#]
      wrapper.core.Welcome (say_bye [this# a# b#] (say_bye e# a# b#)) (greetings [this] (greetings e#))
))

(comment      ~@(let [[type# protocol-functions#] ~protocol-definition]
         (conj
          (map
           (fn [[function-name# function-args#]]
             `(~function-name# ~function-args#
                               (~function-name# ~(symbol "e") ~@(next function-args#))))
           protocol-functions#) type#)))

 (protocol-impl (adapt-super-impls (first (get-methods (Example.)))))

; (cons 'protocol-impl (adapt-super-impls (first (get-methods (Example.)))))

#_(comment (def xxxx (quote (defrecord a [])))


         (def uuu (quote  (clojure.core/defrecord zz [e]

                            Welcome
                            (greetings [this] "zzz!")
                            (say_bye [_ _ _] "zzz")

                            )
                          ))


         (def ww (clojure.core/defrecord ee [e]
                   wrapper.core.Welcome (say_bye [this a b] (say_bye e a b)) (greetings [this] (greetings e))))
)
