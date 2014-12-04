(ns defrecord-wrapper.utils)

(defn logging-access-protocol
  [*fn* this & args]
  (println ">> LOGGING-ACCESS " [this args])
  (println ">>"(meta *fn*))
  (apply *fn* (conj args this)))
