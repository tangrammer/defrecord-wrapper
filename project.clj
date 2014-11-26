(defproject tangrammer/defrecord-wrapper "0.1.1-SNAPSHOT"
  :description "wrap clojure.core/defrecord (AOP)"
  :url "https://github.com/tangrammer/defrecord-wrapper"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [prismatic/schema "0.3.2"]
                 [bidi "1.10.4"]]
  :profiles {:dev {:dependencies [[prismatic/schema "0.3.2"]
                                  [bidi "1.10.4"]]}}
  )
