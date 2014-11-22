(ns wrapper.reflect
  (:require [schema.core :as s]
            [clojure.string :as str ]
            [bidi.bidi :refer (match-route)]))
;; TODO review why I call java-... to some functions here

(defn get-supers
  "get all interfaces but default inherited by clojure.lang.PersistentArrayMap"
  [instance]
  (clojure.set/difference (supers (class instance))
                          (supers clojure.lang.PersistentArrayMap)))

(defn java-interface->clj-protocol
  "Having a java interface class (clojure symbol) get a clojure protocol"
  [interface]
  (let [parsed-interface (-> (str interface)
                             (str/replace #"interface " "")
                             (str/split #"\."))
        ;; hack folder hash
        ns-interface (map #(str/replace % #"_" "-") (butlast parsed-interface))]
    (-> (format "%s/%s"(str/join "." ns-interface) (last parsed-interface))
        symbol
        eval)))

(defn get-protocols [instance]
  (map java-interface->clj-protocol (get-supers instance)))

(defn java-interface-name [protocol]
  (-> protocol :on str))

(defn java-interface-ns [protocol]
  (let [interface-name (java-interface-name protocol)
        interface-name-array (str/split interface-name #"\.")]
    (str/join "." (butlast interface-name-array))))

(defn protocol-methods [protocol]
  (into #{}
        (mapcat (fn [[k v]]
                  (map
                   (fn [it]
                     [(:name v) it])
                   (:arglists v)))
                (:sigs protocol))))

;; awful name
(defn meta-protocol
  [protocol]
  (mapv (fn [[function-name function-args]]
          [function-name
           function-args
           (symbol (format "%s/%s" (java-interface-ns protocol) function-name))

           ])
        (protocol-methods protocol)))
