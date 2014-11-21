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

(defn get-interface-name [protocol]
  (-> protocol :on str))

(defn get-match-options [interface-name function-name function-args]
  (let [base (str/split interface-name #"\.")]
    (-> (reduce (fn [c i]
               (let [n (str/join "." [(last c) i] )]
                 (conj c n))) [(first base)] (next base))
        (conj (str interface-name "/" function-name "/"  (str/join "/"  function-args)))
        (conj (str/replace (str interface-name "/" function-name "/"  (str/join "/"  function-args)) #"_" "this"))
        sort
        reverse)))

(defn match-bidi-routes [protocol function-name function-args bidi-routes]
  (->> (get-match-options (get-interface-name protocol) function-name function-args)
                  (some #(match-route bidi-routes %))
                  :handler))

(defn meta-protocol
  [protocol]
  (let [interface-name (get-interface-name protocol)
        interface-name-array (str/split interface-name #"\.")
        interface-ns (str/join "." (butlast interface-name-array))]
    (mapv (fn [[function-name function-args]]
           [function-name
            function-args
            (symbol (format "%s/%s" interface-ns function-name))
            ])
         (protocol-methods protocol))))

#_( fn-body-protocol# fn-body-method#)
#_ (                          ~(when-not (nil? fn-body-protocol#)
                                 `(~fn-body-protocol#
                                   ~(first function-args#) ~@(next function-args#) {:protocol-name ~(str (first ~protocol-definition))

                                                                                    :function-name ~(str function-name#) :function-args (quote ~function-args#)})
                                 )
                              ~(when-not (nil? fn-body-method#)
                                 `(~fn-body-method#
                                   ~(first function-args#) ~@(next function-args#) {:protocol-name ~(str (first ~protocol-definition))

                                                                                    :function-name ~(str function-name#) :function-args (quote ~function-args#)})
                                 ))

(defmacro code-extend-protocol
  ([protocol bidi-routes]
     `(let [protocol-definition# (meta-protocol ~protocol)]
        (println protocol-definition#)
       (reduce
         (fn [c# [function-name# function-args# function-ns-name#]]
          (assoc c# (keyword function-name#)
                 #_(eval `(fn ~function-args#
                          (if (or (= "start" ~(str function-name#)) (= "stop" ~(str function-name#) ))
                            (do
                              (~function-ns-name#
                               (~(keyword "e") ~(first function-args#)) ~@(next function-args#))
                              ~(first function-args#))

                            (~function-ns-name#
                             (~(keyword "e") ~(first function-args#)) ~@(next function-args#))

                            )))


                 (if-let [fn-match# (match-bidi-routes ~protocol function-name# function-args# ~bidi-routes)]
                   (eval `(fn ~function-args#
                            (~fn-match# ~function-ns-name# (~(keyword "e") ~(first function-args#)) ~@(next function-args#))))
                   (eval `(fn ~function-args#
                       (~function-ns-name#  (~(keyword "e") ~(first function-args#)) ~@(next function-args#)))))))
        {}
         protocol-definition#))))

(defrecord SimpleWrapper [e])

(defn add-extend
  ([the-class the-protocol bidi-routes]
     (extend the-class the-protocol (code-extend-protocol the-protocol bidi-routes))))
