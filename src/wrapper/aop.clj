(ns wrapper.aop
  (:require [schema.core :as s]
            [clojure.pprint :refer (pprint print-table)]
            [clojure.string :as str ]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]))


(defn get-supers [instance]
  (clojure.set/difference (supers (class instance))
                          #{java.lang.Iterable clojure.lang.Counted clojure.lang.Seqable
                            clojure.lang.IKeywordLookup clojure.lang.Associative clojure.lang.IObj
                            clojure.lang.IMeta java.io.Serializable clojure.lang.IPersistentCollection
                            clojure.lang.IHashEq clojure.lang.IPersistentMap clojure.lang.IRecord
                            java.util.Map clojure.lang.ILookup java.lang.Object}))

(defn interface->protocol [interface]
  (let [parsed-interface (-> interface
                           str
                           (str/replace #"interface " "")
                           (str/split #"\.")
                           )
       domain (butlast parsed-interface)
        ]

    (->(format "%s/%s"(str/join "." domain) (last parsed-interface))
       symbol
       eval
       )

    ))

(defn get-methods [instance]
  (map (fn [sup]
         (let [pi (interface->protocol sup)]
           [(:on-interface pi) (into #{} (map (fn [[k v]] (vector
                                                          (dec(count (first (:arglists v))))
                                                          (str (:name v))
                                                          )) (:sigs pi)))]))
       (get-supers instance)))

(defn get-params [n]
  (vec (take (inc n)
             (conj (map (comp symbol #(str % ) char) (range 97 123)) (symbol "this")))))

(defn adapt-super-impls
  "java-meta-data"
  [[prot-class prot-fns]]

  (let [prot-ns (str/join "." (butlast (str/split (str (str/replace prot-class #"interface " "")) #"\.")) )]
    [prot-class (map (fn [[b c]] [(symbol c) (get-params b) (symbol (str prot-ns "/" c))])
                    prot-fns)]))

(defmacro extend-impl [protocol-definition]
  `(reduce
    (fn [c# [function-name# function-args# function-ns-name#]]
      (assoc c# (keyword function-name#)
             (eval `(fn ~function-args#
                      (println ~(str "instercepted: " function-name#))
                      (~function-ns-name# (~(keyword "e") ~(symbol "this")) ~@(next function-args#))))))
    {}
    (last ~protocol-definition)))

(defmacro mr [new-name]
  `(do
     (defrecord ~new-name [~(symbol "e")])

     )

  )

(defn add-extend [the-class the-protocol instance-methods]
  (let [define-fns (-> (filter (fn [[ t _]] (= t  (:on-interface the-protocol))) instance-methods)
                       first
                       adapt-super-impls)]

    (extend the-class the-protocol (extend-impl define-fns))

    ))
