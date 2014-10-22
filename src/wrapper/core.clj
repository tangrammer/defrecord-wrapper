(ns wrapper.core
  (:require [schema.core :as s]
            [clojure.pprint :refer (pprint)]
            [clojure.tools.namespace.repl :refer (refresh refresh-all)]))

(defprotocol Welcome
  (greetings [_] "add your message in your impl"))

#_(defprotocol Welcome2
  (greetings2 [_] "add your message in your impl"))

(defrecord Example []
  Welcome
  (greetings [this] "my example greeting!"))

;(println (greetings (Example.)))


(defn get-supers [instance]
  (let [i# instance]
    '(wrapper.core.Welcome)))
#_(defrecord ~name  []
       Welcome
      (greetings [_]
        (str "with wrapper: xxxx")))
(defmacro prueba [name r body]
  `(do
     (defrecord  ~name  []
        ~(first (get-supers r))
         ~body)
     ))
(let [e (Example.)]
#_(-> (macroexpand '(prueba b  e (greetings [_]
        (str "with wrapper: xxxx"))))

     pprint
;;  eval
     )
 (prueba b  e (greetings [_]
                         (str "with wrapper: xxxx")))
 (greetings (b.))
  )


#_(do
  (let [e (Example.)]
    (prueba b  wrapper.core.Welcome e)
    (let [ i (b.)]
      (assert (extends? Welcome b))
      (s/validate (s/protocol Welcome) i)
      (greetings i))))
