(ns wrapper.core
  (:require [schema.core :as s]
            [clojure.pprint :refer (pprint print-table)]
            [clojure.string :as str ]
            [wrapper.with-slash.prot :refer (With_This w_t)]
            [wrapper.model :as p]
            [wrapper.aop :refer :all]
            [wrapper.schema  :refer (other-one)]
            [bidi.bidi :refer :all]
           )

  (:import [wrapper.model Example MoreSimpleWrapper])

  )



(def routes ["protocol" {"" :index
                         "/method2" :method2
                         "/method3/" {[:id "/"] :method3}}])


(match-route routes "protocol")
(match-route routes "protocol/method2")
(match-route routes "protocol/method3/3/")


(def routes-welcome ["" {"wrapper.model"
                         {"" (fn [this & more] (println "intercepted namespace wrapper.model **** " this more))
                          ".Other"
                          {"" (fn [this & more] (println "intercepted all fn of protocol Other" this more))
                           "/guau/_" (fn [this & more] (println "interecepted guau fn" this more))}
                          ".Xr"
                          {"" (fn [this & more] (println "intercepted all fn of protocol Xr " this more))
                           "/x-x/e" (fn [this & more] (println "interecepted x-x fn" this more))}
                          }}])

(match-route routes-welcome "wrapper.model")
(match-route routes-welcome "wrapper.model.Other")
(match-route routes-welcome "wrapper.model.Other/guau/_")
(match-route routes-welcome "wrapper.model.Xr")
(match-route routes-welcome "wrapper.model.Xr/x-x/p")


(match-route routes-welcome "wrapper.model.Other")
#_(->> (let [base (str/split "wrapper.model.Other" #"\.")]
               (reduce (fn [c i]
                         (let [n (str/join "." [(last c) i] )]
                           (conj c n))) [(first base)] (next base)))
     (filter #(match-route routes-welcome %))
     first
     (match-route routes-welcome)
     :handler
     )

;(greetings (Example.))
;;=> "my example greeting!"




(get-supers (Example.))
;;=> (wrapper.core.Welcome)


(symbol (str/replace (str (:var (interface->protocol (last (get-supers (Example.)))))) #"#'" ""))
 (str (:on-interface (interface->protocol (last (get-supers (Example.))))))
;;=> {:x-x {:doc nil, :arglists ([e]), :name x-x}}


(get-methods (Example.))
;;=> ([wrapper.core.Welcome #{[2 "say_bye"] [0 "greetings"]}])


(get-params 3)
;;=> [this a b c]
(str (first (last (get-methods (Example.)))))
(adapt-super-impls With_This routes-welcome (last (get-methods (Example.))))
(get-supers (Example.))
(extend-impl (adapt-super-impls With_This routes-welcome (last (get-methods (Example.)))))

(add-extend routes-welcome MoreSimpleWrapper  (interface->protocol (last (get-supers (Example.)))) (get-methods (Example.)))

(s/with-fn-validation
     (let [i (Example.)
           methods  (get-methods (Example.))
           juan (MoreSimpleWrapper. i)]

       (doseq [t (get-supers i)]
         (add-extend routes-welcome MoreSimpleWrapper (interface->protocol t) methods)
         )

       [#_(other-one juan)
        #_(p/say_bye juan "John" "Juan") #_(p/x-x juan)
        (w_t juan)
        ]

       )

     )

#_( (extend MoreSimpleWrapper
      p/Welcome
      {:greetings (fn [this]
                    (str "wrapping!! " (p/greetings (:e this)))
                    )
       :say_bye (fn  [this a b]
                  "good bye !")}
      )
    (s/with-fn-validation
      (other-one (MoreSimpleWrapper. (Example.)))))
