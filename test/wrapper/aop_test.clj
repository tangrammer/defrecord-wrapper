(ns wrapper.aop-test
  (:require [clojure.test :refer :all]
            [schema.core :as s]
            [clojure.pprint :refer (pprint print-table)]
            [clojure.repl :refer (apropos dir doc find-doc pst source)]
            [clojure.string :as str ]
            [wrapper.with-slash.prot :refer (With_This w_t)]

            [wrapper.model :as m]
            [wrapper.with-slash.prot :refer (With_This)]
            [wrapper.schema :as ws]
            [wrapper.aop :as aop ]
            [wrapper.reflect :as r ]
            [bidi.bidi :refer :all]
            )

  (:import [wrapper.model Example MoreSimpleWrapper ]
           [wrapper.aop  SimpleWrapper ]))


;; to delete
(defn other-one [c]
  (println "satisfies??? "(satisfies? m/Welcome c))

  (println "s/protocol??? "(s/validate (s/protocol m/Welcome ) c))
  (m/greetings c))

(defn logging-access-protocol
  [*fn* this & args]
  (println ">>>>>>>>>> LOGGING-ACCESS" [this args] (meta *fn*))
  (apply *fn* (conj args this))
  )

(defn  bye
  [*fn* this & args]
  (println ">>>>>>>>>> BYE FUNCTION" (meta *fn*))
  (apply *fn* (conj args this))
  )

(def routes-welcome ["" {"wrapper.model.Welcome/say_bye/e/a/b" bye
                         "wrapper.with_slash.prot.With_This" logging-access-protocol
                         "wrapper.model"
                         {"" logging-access-protocol
                          ".Welcome"
                          {"" logging-access-protocol
                           "/greetings/_" logging-access-protocol}

                          }}])

(deftest schema-related-test
  (testing "schema fn"
    (let [result "my example greeting!"]
      (is (= result (ws/greetings (Example.))))
      (is (= result  (m/greetings (Example.)))))))


(deftest get-supers-test
  (testing "get example supers"
    (is (= #{wrapper.model.Other wrapper.with_slash.prot.With_This wrapper.model.Welcome}
           (r/get-supers (Example.))))))

;(r/java-interface-name m/Other)

(deftest interface->protocol-test
  (testing "getting protocol from class symbol"
    (is (= '(:on :on-interface :sigs :var :method-map :method-builders)
           (keys (r/java-interface->clj-protocol wrapper.model.Other))) )

    (is (= 'wrapper.model/Other (-> (:var (r/java-interface->clj-protocol wrapper.model.Other))
                                    str
                                    (str/replace #"#'" "")
                                    symbol)))
    (is (= 'wrapper.with_slash.prot/With_This (-> (:var (r/java-interface->clj-protocol wrapper.with_slash.prot.With_This))
                                    str
                                    (str/replace #"#'" "")
                                    symbol)))
    (is (= wrapper.model.Other (:on-interface (r/java-interface->clj-protocol wrapper.model.Other))))))

(deftest get-protocols-test
  (testing "get protocols"
    (is (= #{wrapper.model.Other wrapper.with_slash.prot.With_This wrapper.model.Welcome}
           (into #{} (map :on-interface (r/get-protocols (Example.))))))))


(deftest protocol-methods-test
  (testing "extracting protocols methods from protocols"
    (is (= #{'[guau [_]]} (r/protocol-methods (r/java-interface->clj-protocol wrapper.model.Other))))
    (is (= #{'[w_t [this]]} (r/protocol-methods (r/java-interface->clj-protocol wrapper.with_slash.prot.With_This))))))

(r/meta-protocol m/Welcome)
(r/meta-protocol With_This)
;routes-welcome
(aop/code-extend-protocol m/Welcome routes-welcome)

(doseq [t (r/get-supers (Example.))]
  (println (:var (r/java-interface->clj-protocol t)))
  )

(aop/add-extends  MoreSimpleWrapper (r/get-supers (Example.)) routes-welcome)





(let  [e (MoreSimpleWrapper. (Example.))]
  (println (m/say_bye e "INIT(A)" "END(B)"))
  (println (m/greetings e ))
  (println (w_t e ))
  )




#_(s/with-fn-validation
     (let [i (Example.)
           methods  (aop/get-methods (Example.))
           juan (MoreSimpleWrapper. i)]

       (doseq [t (aop/get-supers i)]
         (aop/add-extend routes-welcome MoreSimpleWrapper (r/java-interface->clj-protocol t) methods)
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
