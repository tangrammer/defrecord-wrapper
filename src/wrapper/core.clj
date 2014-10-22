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
(comment
      (println ~instance)
     (filter (fn [ty] (some #(.contains  (str ty) %) #{"cylon" "modular" "azondi" "rhizo" "wrapper"}))
             (->> (supers (class ~instance))))

 )

(defn get-supers-real [i]



`(~(filter (fn [ty] (some #(.contains  (str ty) %) #{"cylon" "modular" "azondi" "rhizo" "wrapper"}))
          (->> (supers (class i)))))
  )

(defn get-supers [instance]
  (let [i# instance]
    '(wrapper.core.Welcome)))

(defmacro prueba [name r body]
  `(do
     ['defrecord  ~name  []
      ~`(ffirst (get-supers-real ~r))
      (quote
        ~body)]
     ))
(let [e (Example.)]

 (prueba b  e (greetings [_]
                         (str "with wrapper: xxxx")))
 #_(greetings (b.))
  )



#_(do
  (let [e (Example.)]
    (prueba b  wrapper.core.Welcome e)
    (let [ i (b.)]
      (assert (extends? Welcome b))
      (s/validate (s/protocol Welcome) i)
      (greetings i))))
#_(-> (macroexpand '(prueba b  e (greetings [_]
        (str "with wrapper: xxxx"))))

     pprint
;;  eval
     )
