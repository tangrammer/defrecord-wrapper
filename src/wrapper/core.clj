(ns wrapper.core
  (:require [schema.core :as s]
            [clojure.pprint :refer (pprint print-table)]
            [clojure.string :as str ]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]))

(defprotocol Welcome
  (greetings [e] )
  (say_bye [e a b]))

(defprotocol Other
  (guau [e] ))

(defprotocol Xr
  (x-x [e]))

(defrecord Example []
  Welcome
  (greetings [this] "my example greeting!")
  (say_bye [this a b] (str "say good bye" a b))
  Other
  (guau [this]
    "here the other")
  Xr
  (x-x [e]
    "doing weird x-x algorithm")
  )

(greetings (Example.))
;;=> "my example greeting!"


(defn get-supers [instance]
  (clojure.set/difference (supers (class instance))
                          #{java.lang.Iterable clojure.lang.Counted clojure.lang.Seqable
                            clojure.lang.IKeywordLookup clojure.lang.Associative clojure.lang.IObj
                            clojure.lang.IMeta java.io.Serializable clojure.lang.IPersistentCollection
                            clojure.lang.IHashEq clojure.lang.IPersistentMap clojure.lang.IRecord
                            java.util.Map clojure.lang.ILookup java.lang.Object}))

(get-supers (Example.))

;;=> (wrapper.core.Welcome)

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
      eval)
   ))

(:sigs (interface->protocol (second (get-supers (Example.)))))



(defn get-methods [instance]
  (map (fn [sup]
         (let [pi (interface->protocol sup)]
           [(:on-interface pi) (into #{} (map (fn [[k v]] (vector
                                                          (dec(count (first (:arglists v))))
                                                          (str (:name v))
                                                          )) (:sigs pi)))]))
       (get-supers instance)))


(get-methods (Example.))
;;=> ([wrapper.core.Welcome #{[2 "say_bye"] [0 "greetings"]}])

(defn get-params [n]
  (vec (take (inc n)
             (conj (map (comp symbol #(str % ) char) (range 97 123)) (symbol "this")))))

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
  ``(defrecord ~(symbol "my-wrapper") [~(symbol "e#")]
     ~@(let [[type# protocol-functions#] ~protocol-definition]
         (conj
          (map
           (fn [[function-name# function-args#]]
             `(~function-name# ~function-args#
                               (~function-name# ~(symbol "e#") ~@(next function-args#))))
           protocol-functions#) type#))

))



(protocol-impl (adapt-super-impls (first (get-methods (Example.)))))
;;=> (clojure.core/defrecord
;;  my-wrapper
;;  [e#]
;;  wrapper.core.Welcome
;;  (say_bye [this# a# b#] (say_bye e# a# b#))
;;  (greetings [this#] (greetings e#)))


(defmacro extend-impl [protocol-definition]
  `(reduce
    (fn [c# [function-name# function-args#]]
      (assoc c# (keyword function-name#)
             (eval `(fn ~function-args#
                      (~function-name# (~(keyword "e") ~(symbol "this")) ~@(next function-args#))))))
    {}
    (last ~protocol-definition)))



(defmacro mr [new-name]
  `(do
     (defrecord ~new-name [~(symbol "e")])

     )

  )


(mr hola)
(defn add-extend [the-class the-protocol instance-methods]
  (let [define-fns (-> (filter (fn [[ t _]] (= t  (:on-interface the-protocol))) instance-methods)
                       first
                       adapt-super-impls)]

    (extend the-class the-protocol (extend-impl define-fns))

    ))


(add-extend hola Welcome (get-methods (Example.)))
(add-extend hola Other (get-methods (Example.)))
(add-extend hola Xr (get-methods (Example.)))

(extends? Welcome hola)
(extends? Other hola)
(let [olo (hola. (Example.))]

  (assert  (satisfies? Welcome olo))
  (assert (satisfies? Other olo))
  (s/validate (s/protocol Welcome) olo)
  ((juxt greetings guau x-x) olo)
  )
