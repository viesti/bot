(ns user
  (:require [bot.http :as http]
            [bot.client :refer [register-bot send-message] :as client]
            [clojure.tools.namespace.repl :refer [disable-reload! refresh]]))

(def stop-fn (atom nil))

(defn start! [] (reset! stop-fn (http/start!)))

(defn restart []
  (when @stop-fn
    (println "Stopping")
    (@stop-fn))
  (refresh :after 'user/start!))
