(ns defrecord-wrapper.bidi-test
  (:require [bidi.bidi :refer :all]
            [clojure.string :as str ]
            [defrecord-wrapper.aop :as aop]
            [defrecord-wrapper.model :as m]
            [defrecord-wrapper.aop-test :refer (routes-welcome)]
            [clojure.test :refer :all]))

(defn starts-with? [st start]
  (.startsWith st start))

(starts-with? "assadsasd" "ass")
(= (aop/interface->protocol wrapper.model.Other)
   m/Other)

(let [base (flatten (map #(str/split % #"\.") (str/split  (str (str/replace (:var m/Other) #"#'" "")) #"/")))]

  (reduce (fn [c i]
            (let [n (str/join "." [(last c) i] )]
              (conj c n))) [(first base)] (next base)))

(some #(match-route routes-welcome %) (aop/get-match-options (aop/get-interface-name m/Other) "guau" "_"))



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


(defn update-key-path [*m* s]
  (reduce-kv (fn [m k v]
              (assoc m (str s k) v)
              ) {} *m*))

(let [ s  "wrapper.model"]
  (reduce-kv (fn [m k v]
               (if (starts-with? s k)
                 (assoc m k (update-key-path v k))
                 m)) {} (first (vals (apply array-map routes-welcome)))))

(comment ((fn [r s]
    (loop [routes r res '()]
      (let [routes-res (reduce-kv (fn [m k v]
                                    (if (starts-with? s k)
                                      (assoc m k (update-key-path v k))
                                      m)) {} routes)
            res-res (reduce (fn [c i] (conj c (get routes-res i)))
                            res
                            (filter #(= s %) (keys routes-res)))
            ]
        (if (empty? (filter #(map? %) (vals routes-res)))
          res-res
          (recur routes-res res-res))
        )
      ))
  (apply array-map routes-welcome)
  "wrapper.model"
  ))
