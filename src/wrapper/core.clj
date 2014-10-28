(ns wrapper.core
  (:require [schema.core :as s]
            [clojure.pprint :refer (pprint print-table)]
            [clojure.string :as str ]
            [wrapper.model :refer (greetings guau x-x say_bye)]
            [wrapper.aop :refer :all]

;            [clojure.tools.namespace.repl :refer (refresh refresh-all)]
            )
  (:import [wrapper.model Example]
           [wrapper.aop SimpleWrapper])
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

;(mr hola)
(def juan (SimpleWrapper. (Example.)))
(add-extend SimpleWrapper wrapper.model/Welcome (get-methods (Example.))

            (fn [& more]
              (println "a is" (first more))
              (println "b is" (second more))
              (println "...function-def..." (last more)) ))

(greetings juan)

(add-extend SimpleWrapper wrapper.model/Other (get-methods (Example.)))
(add-extend SimpleWrapper wrapper.model/Xr (get-methods (Example.)))

(extends? wrapper.model/Welcome SimpleWrapper)
(extends? wrapper.model/Other SimpleWrapper)

(let [i (SimpleWrapper. (Example.))]
  (say_bye i "John" "Juan")
  (greetings i)
  )

#_(let [olo (SimpleWrapper. (Example.))]
  (assert  (satisfies? wrapper.model/Welcome olo))
  (assert (satisfies? wrapper.model/Other olo))
  (s/validate (s/protocol wrapper.model/Welcome) olo)
  (greetings olo)
  )
