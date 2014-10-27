(ns wrapper.model)
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
