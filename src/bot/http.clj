(ns bot.http
  (:require [compojure.core :refer :all]
            [org.httpkit.server :refer [run-server]]
            [cheshire.core :as json]
            bot.brain
            [clojure.pprint :refer [pprint]]
            [clojure.tools.namespace.repl :refer [disable-unload!]]))

;; This prevents from loosing the Var instance passed to run-server
;; while getting reloaded with c.t.n.repl/refresh
(disable-unload!)

(defn handler [request]
  (println "Request")
  (when-let [body (:body request)]
    (try (let [state (json/parse-string (slurp body) true)
               move (bot.brain/next-move state)]
           (pprint move)
           {:status 200
            :headers {"Content-Type" "application/json"}
            :body (json/generate-string move)})
         (catch Exception e
           (do (pprint e)
               {:status 500
                :body "FUBAR"})))))

(defn start! [port]
  (println "Starting bot")
  (run-server #'handler {:port port}))
