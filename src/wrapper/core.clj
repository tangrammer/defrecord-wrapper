(ns wrapper.core
  (:require [schema.core :as s]
            [clojure.pprint :refer (pprint print-table)]
            [clojure.string :as str ]
            [wrapper.model :refer (greetings guau x-x)]
            [wrapper.aop :refer :all]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)])
  (:import [wrapper.model Example])
  )


(greetings (Example.))
;;=> "my example greeting!"




(get-supers (Example.))
;;=> (wrapper.core.Welcome)


(:sigs (interface->protocol (second (get-supers (Example.)))))
;;=> {:x-x {:doc nil, :arglists ([e]), :name x-x}}


(get-methods (Example.))
;;=> ([wrapper.core.Welcome #{[2 "say_bye"] [0 "greetings"]}])


(get-params 3)
;;=> [this a b c]


(adapt-super-impls (first (get-methods (Example.))))
;;=> [wrapper.core.Welcome ([say_bye [this a b]] [greetings [this]])]


(mr hola)

(add-extend hola wrapper.model/Welcome (get-methods (Example.)))
(add-extend hola wrapper.model/Other (get-methods (Example.)))
(add-extend hola wrapper.model/Xr (get-methods (Example.)))

(extends? wrapper.model/Welcome hola)
(extends? wrapper.model/Other hola)


(let [olo (hola. (Example.))]
  (assert  (satisfies? wrapper.model/Welcome olo))
  (assert (satisfies? wrapper.model/Other olo))
  (s/validate (s/protocol wrapper.model/Welcome) olo)
  ((juxt greetings guau x-x) olo)
  )
