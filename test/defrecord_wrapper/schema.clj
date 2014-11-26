;; using validation schema in defrecord instances
;; working after schema fixed bug
;; https://github.com/Prismatic/schema/issues/164
(ns defrecord-wrapper.schema
  (:require [schema.core :as s]
            [defrecord-wrapper.model :as m]))

(s/defn greetings :-  s/Str
  [component :- (s/protocol m/Welcome)]
  (m/greetings component))
