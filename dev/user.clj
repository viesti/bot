(ns user
  (:require [bot.http :as http]
            [bot.client :refer [send-message] :as client]
            [clojure.tools.namespace.repl :refer [disable-reload! refresh]]))

(def port 8080)

(def stop-fn (atom nil))

(defn start! [] (reset! stop-fn (http/start! port)))

(defn restart []
  (when @stop-fn
    (println "Stopping")
    (@stop-fn))
  (refresh :after 'user/start!))

(defn register-bot []
  (client/register-bot port))
