(ns bot.http
  (:require [compojure.core :refer :all]
            [org.httpkit.server :refer [run-server]]
            [cheshire.core :as json]
            [bot.brain :as brain]
            [clojure.pprint :refer [pprint]]))

(defn handler [request]
  (println "moi")
  (when-let [body (:body request)]
    (let [state (json/parse-string (slurp body) true)
          _ (pprint state)
          move (brain/next-move state)]
      {:status 200
       :headers {"Content-Type" "application/json"}
       :body (json/generate-string move)})))

(defn runner [x]
  (@(var handler) x))

(defonce stop-fn (atom nil))

(defn start! []
  (reset! stop-fn (run-server runner {:port 8080})))
