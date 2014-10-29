(ns wrapper.model
  (:require [schema.core :as s]))

(defrecord MoreSimpleWrapper [e])
(defprotocol Welcome
  (greetings [e] )
  (say_bye [e a b]))

(defprotocol Other
  (guau [e] ))

(defprotocol Xr
  (x-x [e]))

(defprotocol TUR
  (tur [e]))


(defrecord Example []
  Welcome
  (greetings [this] "my example greeting!")
  (say_bye [this a b] (str "saying good bye from " a " to " b))
  Other
  (guau [this]
    "here the other")
  Xr
  (x-x [e]
    "doing weird x-x algorithm")
  )
