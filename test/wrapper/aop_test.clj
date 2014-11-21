(ns wrapper.aop-test
  (:require [clojure.test :refer :all]
            [schema.core :as s]
            [clojure.pprint :refer (pprint print-table)]
            [clojure.string :as str ]
            [wrapper.with-slash.prot :refer (With_This w_t)]
            [wrapper.model :as m]
            [wrapper.schema :as ws]
            [wrapper.aop :as aop ]
            [bidi.bidi :refer :all])

  (:import [wrapper.model Example MoreSimpleWrapper]))


(defn other-one [c]
  (println "satisfies??? "(satisfies? m/Welcome c))

  (println "s/protocol??? "(s/validate (s/protocol m/Welcome ) c))
  (m/greetings c))


(defn logging-access-protocol
  [this & more]
  (println [ #_this
            ((juxt :function-name :function-args) (last more))
            #_(:tangrammer.wrap-component/who (meta this))] ))

(def routes-welcome ["" {"wrapper.model"
                         {"" logging-access-protocol
                          ".Other"
                          {"" logging-access-protocol
                           "/guau/_" logging-access-protocol}
                          ".Xr"
                          {"" logging-access-protocol
                           "/x-x/e" logging-access-protocol}
                          }}])


(deftest schema-related-test
  (testing "schema fn"
    (let [result "my example greeting!"]
      (is (= result (ws/greetings (Example.))))
      (is (= result  (m/greetings (Example.)))))))


(deftest get-supers-test
  (testing "get example supers"
    (is (= #{wrapper.model.Other wrapper.with_slash.prot.With_This wrapper.model.Welcome}
           (aop/get-supers (Example.))))))

(deftest interface->protocol-test
  (testing "getting protocol from class symbol"
    (is (= '(:on :on-interface :sigs :var :method-map :method-builders)
           (keys (aop/interface->protocol wrapper.model.Other))) )

    (is (= 'wrapper.model/Other (-> (:var (aop/interface->protocol wrapper.model.Other))
                                    str
                                    (str/replace #"#'" "")
                                    symbol)))
    (is (= wrapper.model.Other (:on-interface (aop/interface->protocol wrapper.model.Other))))))


(map (fn [[i m]] [(:var i) m]) (aop/get-methods (Example.)))
;;=> ([wrapper.core.Welcome #{[2 "say_bye"] [0 "greetings"]}])


(aop/adapt-super-impls With_This routes-welcome (last (aop/get-methods (Example.))))

(aop/extend-impl (aop/adapt-super-impls With_This routes-welcome (last (aop/get-methods (Example.)))))

(aop/add-extend routes-welcome MoreSimpleWrapper  (aop/interface->protocol (last (aop/get-supers (Example.)))) (aop/get-methods (Example.)))

(s/with-fn-validation
     (let [i (Example.)
           methods  (aop/get-methods (Example.))
           juan (MoreSimpleWrapper. i)]

       (doseq [t (aop/get-supers i)]
         (aop/add-extend routes-welcome MoreSimpleWrapper (aop/interface->protocol t) methods)
         )

       [#_(other-one juan)
        #_(m/say_bye juan "John" "Juan") #_(m/x-x juan)
        (w_t juan)
        ]

       )

     )

#_( (extend MoreSimpleWrapper
      m/Welcome
      {:greetings (fn [this]
                    (str "wrapping!! " (m/greetings (:e this)))
                    )
       :say_bye (fn  [this a b]
                  "good bye !")}
      )
    (s/with-fn-validation
      (other-one (MoreSimpleWrapper. (Example.)))))
