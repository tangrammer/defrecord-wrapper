(ns wrapper.model
  (:require [schema.core :as s]
            [wrapper.with-slash.prot :refer (With_This)]))

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
  With_This
  (w-t [this]
    "with this")
  Welcome
  (greetings [this] "my example greeting!")
  (say_bye [this a b] (str "saying good bye from " a " to " b))
  Other
  (guau [_]
    "Example: guau guau!")
  Xr
  (x-x [e]
    "doing weird x-x algorithm"))
