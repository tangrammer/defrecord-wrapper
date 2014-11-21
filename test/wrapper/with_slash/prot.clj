;; this ns is for testing reflection in clojure definitions that use slashs
(ns wrapper.with-slash.prot)

(defprotocol With_This
  (w_t [this]))
