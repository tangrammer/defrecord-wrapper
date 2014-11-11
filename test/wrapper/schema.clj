(ns wrapper.schema
  (:require [schema.core :as s]
            [wrapper.model :as p]))

(s/defn greetings :-  s/Str
  [component :- (s/protocol p/Welcome)]
  (wrapper.model/greetings component))

(defn other-one [c]
  (println "satisfies??? "(satisfies? p/Welcome c))

  (println "s/protocol??? "(s/validate (s/protocol p/Welcome ) c))
  (greetings c))
