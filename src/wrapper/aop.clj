(ns wrapper.aop
  (:require [schema.core :as s]
            [clojure.pprint :refer (pprint print-table)]
            [clojure.string :as str ]
            [bidi.bidi :refer (match-route)]
;            [clojure.tools.namespace.repl :refer (refresh refresh-all)]
            ))


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
           [(:on-interface pi) (into #{} (mapcat (fn [[k v]]
                                                (map
                                                 (fn [it]
                                                  (vector
                                                   it
                                                   (:name v)
                                                   ))

                                                 (:arglists v))


                                                ) (:sigs pi)))]))
       (get-supers instance)))

(defn get-params [n]
  (vec (take (inc n)
             (conj (map (comp symbol #(str % ) char) (range 97 123)) (symbol "this")))))

(defn adapt-super-impls
  "java-meta-data"
  [the-protocol bidi-routes [prot-class prot-fns]]
  (let [prot (str/split (str (str/replace prot-class #"interface " "")) #"\.")
        prot-str (str/join "."  prot )
        prot-ns (str/join "." (butlast prot) )]
    [prot-class (map (fn [[b c]]
                       (println c (or (match-route bidi-routes (str prot-str "/" c "/"  (str/join "/"  b)))
                                      (match-route bidi-routes (str/replace (str prot-str "/" c "/"  (str/join "/"  b)) #"_" "this"))
                                      ))
                       [(symbol c)
                        b
                        (symbol (str prot-ns "/" c))
                        (:handler (match-route bidi-routes prot-str)) ;; intercep protocol protocol
                        (:handler (or (match-route bidi-routes (str prot-str "/" c "/"  (str/join "/"  b)))
                                      (match-route bidi-routes (str/replace (str prot-str "/" c "/"  (str/join "/"  b)) #"_" "this"))
                                      )) ;; intercept method
                        ])
                    prot-fns) ]))

(defmacro extend-impl
  ([protocol-definition]
     `(reduce
       (fn [c# [function-name# function-args# function-ns-name# fn-body-protocol# fn-body-method#]]
         (assoc c# (keyword function-name#)
                (eval `(fn ~function-args#
                         ~(when-not (nil? fn-body-protocol#)
                            `(~fn-body-protocol# (~(keyword "e") ~(first function-args#)) ~@(next function-args#) {:protocol-name ~(str (first ~protocol-definition))

                                                                          :function-name ~(str function-name#) :function-args (quote ~function-args#)})
                            )
                         ~(when-not (nil? fn-body-method#)
                            `(~fn-body-method# (~(keyword "e") ~(first function-args#)) ~@(next function-args#) {:protocol-name ~(str (first ~protocol-definition))

                                                                   :function-name ~(str function-name#) :function-args (quote ~function-args#)})
                            )
                         (~function-ns-name# (~(keyword "e") ~(first function-args#)) ~@(next function-args#))
                         ))))
       {}
       (last ~protocol-definition))))

(defmacro mr [new-name]
  `(do
     (defrecord ~new-name [~(symbol "e")])

     )

  )
(defrecord SimpleWrapper [e])

(defn add-extend
  ([bidi-routes the-class the-protocol instance-methods]
     (let [define-fns (->>
                       (-> (filter (fn [[ t _]] (= t  (:on-interface the-protocol))) instance-methods)
                           first)
                       (adapt-super-impls the-protocol bidi-routes))

           ]
       (extend the-class the-protocol (extend-impl define-fns))
       )))
