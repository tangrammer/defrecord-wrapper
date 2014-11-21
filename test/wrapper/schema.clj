;; using validation schema in defrecord instances
;; working after schema fixed bug
;; https://github.com/Prismatic/schema/issues/164
(ns wrapper.schema
  (:require [schema.core :as s]
            [wrapper.model :as p]))

(s/defn greetings :-  s/Str
  [component :- (s/protocol p/Welcome)]
  (wrapper.model/greetings component))
