(ns wrapper.aop
  (:require [wrapper.reflect :as r]
            [wrapper.match :as m]))

(defmacro code-extend-protocol
  ([protocol routes]
     `(let [protocol-definition# (r/meta-protocol ~protocol)]
        ;;(println protocol-definition#)
       (reduce
         (fn [c# [function-name# function-args# function-ns-name#]]
          (assoc c# (keyword function-name#)
                 (if-let [fn-match# (m/match-routes (m/get-match-options ~protocol function-name# function-args#) ~routes)]
                   (eval `(fn ~function-args#
                            (~fn-match# (with-meta ~function-ns-name# {:function-name ~(str function-name#)
                                                                       :function-args ~(str function-args#)})
                                        (~(keyword "e") ~(first function-args#)) ~@(next function-args#))))
                   (eval `(fn ~function-args#
                       (~function-ns-name#  (~(keyword "e") ~(first function-args#)) ~@(next function-args#)))))))
        {}
         protocol-definition#))))

(defrecord SimpleWrapper [e])

(defn add-extend
  ([the-class the-protocol routes]
     (extend the-class the-protocol (code-extend-protocol the-protocol routes))))
