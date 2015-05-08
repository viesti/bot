(ns bot.http
  (:require [compojure.core :refer :all]
            [org.httpkit.server :refer [run-server]]))

(defn handler [request]
  (clojure.pprint/pprint request)
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (rand-nth ["UP" "DOWN" "LEFT" "RIGHT"])})

(defn runner [x]
  (@(var handler) x))

(defonce stop-fn (atom nil))

(defn start! []
  (reset! stop-fn (run-server runner {:port 8080})))
