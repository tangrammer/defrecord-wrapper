(ns wrapper.aop
  (:require [schema.core :as s]
            [clojure.string :as str ]
            [bidi.bidi :refer (match-route)]))

(defn get-supers
  "get all interfaces but default inherited by clojure.lang.PersistentArrayMap"
  [instance]
  (clojure.set/difference (supers (class instance))
                          #{java.lang.Iterable clojure.lang.Counted clojure.lang.Seqable
                            clojure.lang.IKeywordLookup clojure.lang.Associative clojure.lang.IObj
                            clojure.lang.IMeta java.io.Serializable clojure.lang.IPersistentCollection
                            clojure.lang.IHashEq clojure.lang.IPersistentMap clojure.lang.IRecord
                            java.util.Map clojure.lang.ILookup java.lang.Object}))

(defn interface->protocol
  "Having a interface class get clojure protocol"
  [interface]
  (let [parsed-interface (-> (str interface)
                             (str/replace #"interface " "")
                             (str/split #"\."))
        ns-interface (map #(str/replace % #"_" "-") (butlast parsed-interface))]

    (-> (format "%s/%s"(str/join "." ns-interface) (last parsed-interface))
        symbol
        eval)))

(defn get-protocols [instance]
  (map interface->protocol (get-supers instance)))

(defn protocol-methods [protocol]
  (into #{}
        (mapcat (fn [[k v]]
                  (map
                   (fn [it]
                     [(:name v) it])
                   (:arglists v)))
                (:sigs protocol))))


(defn adapt-super-impls
  "java-meta-data"
  [the-protocol bidi-routes prot-fns]
  (let [prot (flatten (map #(str/split % #"\.") (str/split  (str (str/replace (:var the-protocol) #"#'" "")) #"/")))

        prot-str (str/join "."  prot )
        prot-ns (str/join "." (butlast prot) )]
    #_[the-protocol prot-ns]
    [#_(eval (symbol (str (str/replace (:var the-protocol) #"#'" ""))))
     (map (fn [[c b]]
            [(symbol c)
             b
             (symbol (str prot-ns "/" c))
             (when-let [m (->> (let [base prot]
                                 (reduce (fn [c i]
                                           (let [n (str/join "." [(last c) i] )]
                                             (conj c n))) [(first base)] (next base)))
                               (filter #(match-route bidi-routes %))
                               first)]
               (:handler (match-route bidi-routes m))


               )
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
                            `(~fn-body-protocol#
                              ~(first function-args#) ~@(next function-args#) {:protocol-name ~(str (first ~protocol-definition))

                                                                          :function-name ~(str function-name#) :function-args (quote ~function-args#)})
                            )
                         ~(when-not (nil? fn-body-method#)
                            `(~fn-body-method#
                              ~(first function-args#) ~@(next function-args#) {:protocol-name ~(str (first ~protocol-definition))

                                                                   :function-name ~(str function-name#) :function-args (quote ~function-args#)})
                            )

                         (if (or (= "start" ~(str function-name#)) (= "stop" ~(str function-name#) ))
                           (do
                             (~function-ns-name#
                              (~(keyword "e") ~(first function-args#)) ~@(next function-args#))
                             ~(first function-args#))
                           (~function-ns-name#
                            (~(keyword "e") ~(first function-args#)) ~@(next function-args#))
                           )))))
       {}
       (last ~protocol-definition))))

(defrecord SimpleWrapper [e])

(defn add-extend
  ([bidi-routes the-class the-protocol instance-methods]
     (let [define-fns (->>
                       (-> (filter (fn [[t _]] (= t the-protocol)) instance-methods)
                           first)
                       (adapt-super-impls the-protocol bidi-routes))]
       (extend the-class the-protocol (extend-impl define-fns)))))
