(ns wrapper.bidi-test
  (:require [bidi.bidi :refer :all]
            [clojure.string :as str ]
            [wrapper.aop-test :refer (routes-welcome)]
            [clojure.test :refer :all]))




(deftest basic-routes
  (let [routes ["protocol" {"" :index
                         "/method2" :method2
                         "/method3/" {[:id "/"] :method3}}]]
    (testing "bidi"
      (is (= (match-route routes "protocol") {:handler :index}))
      (is (= (match-route routes "protocol/method2") {:handler :method2}))
      (is (= (match-route routes "protocol/method3/3/") {:handler :method3, :route-params {:id "3"}}))
      )))

#_(deftest basic-routes
    ;; TODO checking
    (match-route routes-welcome "wrapper.model")
    (match-route routes-welcome "wrapper.model.Other")
    (match-route routes-welcome "wrapper.model.Other/guau/_")
    (match-route routes-welcome "wrapper.model.Xr")
    (match-route routes-welcome "wrapper.model.Xr/x-x/p")


    (match-route routes-welcome "wrapper.model.Other")

    )


#_(->> (let [base (str/split "wrapper.model.Other" #"\.")]
               (reduce (fn [c i]
                         (let [n (str/join "." [(last c) i] )]
                           (conj c n))) [(first base)] (next base)))
     (filter #(match-route routes-welcome %))
     )

#_(->> (let [base (str/split "wrapper.model.Other" #"\.")]
               (reduce (fn [c i]
                         (let [n (str/join "." [(last c) i] )]
                           (conj c n))) [(first base)] (next base)))
     (filter #(match-route routes-welcome %))
     first
     (match-route routes-welcome)
     :handler
     )
