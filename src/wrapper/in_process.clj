(ns wrapper.in-process
  (require [wrapper.aop :refer :all]
)

  (import [wrapper.core Example])
  )

(defmacro protocol-impl [protocol-definition]
  ``(defrecord ~(symbol "my-wrapper") [~(symbol "e#")]
     ~@(let [[type# protocol-functions#] ~protocol-definition]
         (conj
          (map
           (fn [[function-name# function-args#]]
             `(~function-name# ~function-args#
                               (~function-name# ~(symbol "e#") ~@(next function-args#))))
           protocol-functions#) type#))

     ))
(protocol-impl (adapt-super-impls (first (get-methods (Example.)))))
;;=> (clojure.core/defrecord
;;  my-wrapper
;;  [e#]
;;  wrapper.core.Welcome
;;  (say_bye [this# a# b#] (say_bye e# a# b#))
;;  (greetings [this#] (greetings e#)))
