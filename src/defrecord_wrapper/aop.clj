(ns defrecord-wrapper.aop
  (:require [defrecord-wrapper.reflect :as r]))

(defprotocol Matcher
  (match [this protocol function-name function-args]))

(extend-protocol Matcher
  nil
  (match [this protocol function-name function-args]
    nil))


(defmacro code-extend-protocol
  ([protocol matcher]
     `(let [protocol-definition# (r/meta-protocol ~protocol)]
        ;;(println protocol-definition#)
       (reduce
         (fn [c# [function-name# function-args# function-ns-name#]]
          (assoc c# (keyword function-name#)
                 (if-let [fn-match# (match ~matcher ~protocol function-name# function-args#)]
                   (eval `(fn ~function-args#
                            (~fn-match# (with-meta ~function-ns-name# {:function-name ~(str function-name#)
                                                                       :function-args ~(str function-args#)
                                                                       :wrapper ~(first function-args#)})
                                        (~(keyword "wrapped-record") ~(first function-args#)) ~@(next function-args#))))
                   (eval `(fn ~function-args#
                       (~function-ns-name#  (~(keyword "wrapped-record") ~(first function-args#)) ~@(next function-args#)))))))
        {}
         protocol-definition#))))

(defrecord SimpleWrapper [wrapped-record])

(defn add-extend
  ([the-class the-protocol matcher]
     (extend the-class the-protocol (code-extend-protocol the-protocol matcher))))

(defn add-extends
  ([class protocols matcher]
     (doseq [the-protocol protocols]
       (let [clj-protocol (r/java-interface->clj-protocol the-protocol)]
         (extend class clj-protocol (code-extend-protocol clj-protocol matcher))))))
