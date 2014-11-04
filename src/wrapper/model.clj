(ns wrapper.model
  (:require [schema.core :as s]))

(defrecord MoreSimpleWrapper [e])
(defprotocol Welcome
  (greetings [e] )
  (say_bye [e a b])
  )

(defprotocol Other
  (guau [_] ))

(defprotocol Xr
  (x-x [e]))

(defprotocol TUR
  (tur [e]))


(defrecord Example []
  Welcome
  (greetings [this] "my example greeting!")
  (say_bye [this a b] (str "saying good bye from " a " to " b))
  Other
  (guau [_]
    "Example: guau guau!")
  Xr
  (x-x [e]
    "doing weird x-x algorithm")
  )
