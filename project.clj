(defproject tangrammer/defrecord-wrapper "0.1.6-SNAPSHOT"
  :description "wrap clojure.core/defrecord (AOP)"
  :url "https://github.com/tangrammer/defrecord-wrapper"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.6.0"]]
  :profiles {:dev {:dependencies [[prismatic/schema "0.3.3"]
                                  [tangrammer/bidi-wrapper-matcher "0.1.0-SNAPSHOT"]]}}
  )
