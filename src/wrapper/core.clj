(ns wrapper.core
  (:require [schema.core :as s]
            [clojure.pprint :refer (pprint)]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]))

(defprotocol Welcome
  (greetings [_] "add your message in your impl")
  (say-bye [one two three] "say good bye"))

#_(defprotocol Welcome2
  (greetings2 [_] "add your message in your impl"))

(defrecord Example []
  Welcome
  (greetings [this] "my example greeting!")
  (say-bye [_ _ _] "say good bye")
  )

;(println (greetings (Example.)))
(defn get-supers [instance]
      (println instance)
     (filter (fn [ty] (some #(.contains  (str ty) %) #{"cylon" "modular" "azondi" "rhizo" "wrapper"}))
             (->> (supers (class instance))))

 )

(defn get-supers-real [i]



`(~(filter (fn [ty] (some #(.contains  (str ty) %) #{"cylon" "modular" "azondi" "rhizo" "wrapper"}))
          (->> (supers (class i)))))
  )

#_(defn get-supers [instance]
  (let [i# instance]
    '(wrapper.core.Welcome)))

(defmacro prueba [name r body]
  `(do
     ['defrecord  '~name  []
      ~`(ffirst (get-supers-real ~r))

      '~body]
     ))


(let [e (Example.)]


  (prueba b  e (greetings [_]
                          (str "with wrapper: xxxx")))
                                        ;(let [b-i (b.)] (println  (greetings b-i)))
                                        ;(assert (extends? Welcome b-i))
  )

(defn get-methods [instance] (map (fn [sup]
        [sup (->> (.getDeclaredMethods sup)
                  (map #(vector (count (.getParameterTypes %)) (.getName %)))
                  (into #{}))])
      (get-supers instance)))

(get-methods (Example.))


(defn get-params [n] (vec (take (inc n)
                            (conj
                             (map (comp symbol str char) (range 97 123))
                             (symbol "this")))))
(get-params 3)


(defn adapt-super-impls
  "java-meta-data"
  [prot-class prot-fns ] (reduce (fn [a [b c]] (conj a [(symbol c) (get-params b)]) ) [prot-class] prot-fns))




(defmacro protocol-impl [prot-def]
  `(do
     (let [[t# & s#] ~prot-def]
       (conj
        (map
         (fn [[n# p#]]
           `(~n# ~p# (~n# e ~@(next p#)))
           )
         s#)
        t#
        )
       )
    )
  )

(def definition [wrapper.core.Welcome #{[2 "say_bye"] [0 "greetings"]}])

(protocol-impl (adapt-super-impls wrapper.core.Welcome #{[2 "say_bye"] [0 "greetings"]}))

(quote e)
