(ns defrecord-wrapper.match
  (:require [defrecord-wrapper.reflect :as r]
            [schema.core :as s]
            [clojure.string :as str ]
            [bidi.bidi :refer (match-route)]))

;; TODO this can be somplified with matching tangrammer.MyInterface/my-function[arg0 arg1 arg3]
;; I mean using vector as arguments path... maybe to work in bidi only translate to bidi dialect before invoking bidi
(defn get-match-options
  "match-options tries to match as does log4j-back
  So for example this value: tangramer.MyInterface/my-function/arg0/arg1/arg2
  will find matchs for:
  1. tangramer.MyInterface/my-function/arg0/arg1/arg2
  2. tangramer.MyInterface/my-function
  3. tangramer.MyInterface
  4. tangramer
  Taking the high priority result "
  [protocol function-name function-args]
  (let [interface-name (r/java-interface-name protocol)
        base (str/split (r/java-interface-name protocol) #"\.")]
    (-> (reduce (fn [c i]
               (let [n (str/join "." [(last c) i] )]
                 (conj c n))) [(first base)] (next base))
        (conj (str interface-name "/" function-name "/"  (str/join "/"  function-args)))
        (conj (str/replace (str interface-name "/" function-name "/"  (str/join "/"  function-args)) #"_" "this"))
        sort
        reverse)))


(defn match-routes [match-options bidi-routes]
  (->> match-options
       (some #(match-route bidi-routes %))
       :handler))
