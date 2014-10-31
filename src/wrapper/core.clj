(ns wrapper.core
  (:require [schema.core :as s]
            [clojure.pprint :refer (pprint print-table)]
            [clojure.string :as str ]
            [wrapper.model :as p]
            [wrapper.schema  :refer (other-one)]
            [wrapper.aop :refer :all]
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

(def routes-welcome ["" {"wrapper.model.Other"
                         {"" (fn [& more] (println "logging Xr" more))
                          "/guau/_" (fn [this & more] (println "guuauauauauauauaauasdPPPPPPPPPua" this more))}
                         "wrapper.model.Xr"
                         {"" (fn [& more] (println "logging Xr" more))
                          "/x-x/this" (fn [& more] (println "logging x-x" more))}
                         "wrapper.model.Welcome"
                         {"" (fn [& more]
                               (println "you've been bidintercepted :-)" (:function-name (last more))))
                          "/greetings/this" (fn [this & more]
                                              (println "Greetings Congratulations!!"))
                          "/say_bye/this/a/b" (fn [this a b & more]
                                                (println "Say bye Congratulations!!"))}}])

(match-route routes-welcome "wrapper.model.Xr")
(match-route routes-welcome "wrapper.model.Welcome")
(match-route routes-welcome "wrapper.model.Welcome/greetings/this")
(match-route routes-welcome "wrapper.model.Welcome/say_bye/this/a/b")

(match-route routes-welcome "wrapper.model.Other")




;(greetings (Example.))
;;=> "my example greeting!"




(get-supers (Example.))
;;=> (wrapper.core.Welcome)


(:sigs (interface->protocol (second (get-supers (Example.)))))
;;=> {:x-x {:doc nil, :arglists ([e]), :name x-x}}


(get-methods (Example.))
;;=> ([wrapper.core.Welcome #{[2 "say_bye"] [0 "greetings"]}])


(get-params 3)
;;=> [this a b c]

(adapt-super-impls p/Xr routes-welcome (last (get-methods (Example.))))
(extend-impl (adapt-super-impls p/Xr routes-welcome (last (get-methods (Example.)))))
(s/with-fn-validation

     (let [i (Example.)
           methods  (get-methods (Example.))
           juan (MoreSimpleWrapper. i)]


       (doseq [t (get-supers i)]
         (add-extend routes-welcome MoreSimpleWrapper (interface->protocol t) methods)
         )

       [#_(other-one juan)
        #_(p/say_bye juan "John" "Juan") #_(p/x-x juan)
        (p/guau juan)]

       ))
#_(

   ;;=> [wrapper.core.Welcome ([say_bye [this a b]] [greetings [this]])]





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
