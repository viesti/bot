(defproject bot "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0-beta2"]
                 [http-kit "2.1.18"]
                 [compojure "1.3.3"]
                 [cheshire "5.4.0"]
                 [org.clojure/data.priority-map "0.0.7"]]
  :java-source-paths ["java"]
  :target-path "target/%s"
  :repl-options {:init-ns dev}
  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[org.clojure/tools.namespace "0.2.10"]
                                  [org.clojure/tools.nrepl "0.2.10"]]
                   :source-paths ["dev"]}})
