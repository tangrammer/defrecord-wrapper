(defproject tangrammer/wrapper "0.1.3-SNAPSHOT"
  :description "wrap clojure.core/defrecord (AOP)"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [prismatic/schema "0.3.2"]
                 [bidi "1.10.4"]]
  :profiles {:dev {:dependencies [[prismatic/schema "0.3.2"]
                                  [bidi "1.10.4"]]}}
  )
