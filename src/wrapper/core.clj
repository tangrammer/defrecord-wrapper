(ns wrapper.core
  (:require [schema.core :as s]
            [clojure.pprint :refer (pprint)]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]))

(defprotocol Welcome
  (greetings [_] "add your message in your impl")
  (say-bye [one two three] "say good bye"))

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
(get-supers (Example.))

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
  [[prot-class prot-fns]]
  [prot-class (map (fn [[b c]] [(symbol c) (get-params b)])
         prot-fns)]

  )

(adapt-super-impls (first (get-methods (Example.))))

(defmacro protocol-impl [prot-def]
  `(let [[t# s#] ~prot-def]
     (conj
      (map
      (fn [[n# p#]]
        `(~n# ~p# (~n# ~(symbol "e") ~@(next p#)))
        )
      s#)
     t#)))

(protocol-impl (adapt-super-impls (first (get-methods (Example.)))))

(defmacro exp [e]
  `~e
  )

(defmacro prueba [n body]
  (list 'conj body '['e] (list symbol n) ''defrecord)
  )




(prueba "xx" (protocol-impl (adapt-super-impls (first (get-methods (Example.))))))





;(xx.)
