(defproject bot "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0-beta2"]
                 [http-kit "2.1.18"]
                 [compojure "1.3.3"]
                 [com.stuartsierra/component "0.2.3"]
                 [cheshire "5.4.0"]]
  :target-path "target/%s"
  :repl-options {:init-ns user}
  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[reloaded.repl "0.1.0"]
                                  [org.clojure/tools.nrepl "0.2.10"]]
                   :source-paths ["dev"]}})
