(ns dev
  (:require [bot.http :as http]
            [bot.client :refer [send-message] :as client]
            [clojure.tools.namespace.repl :refer [disable-reload! refresh]]))

;; To avoid loosing stuff defined in the repl after running (refresh)
(disable-reload!)

(def port 8081)

(def stop-fn (atom nil))

(defn start! [] (reset! stop-fn (http/start! port)))

(defn register-bot []
  (client/register-bot port))
