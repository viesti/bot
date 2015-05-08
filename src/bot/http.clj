(ns bot.http
  (:require [com.stuartsierra.component :as component]
            [compojure.core :refer :all]
            [org.httpkit.server :refer [run-server]]))

(defroutes app
  (GET "/" [] "Hello World"))

(defrecord HttpServer []
  component/Lifecycle
  (start [this]
    (if (:stop-fn this)
      this
      (assoc this :stop-fn (run-server app {:port 8282}))))
  (stop [{:keys [stop-fn] :as this}]
    (when stop-fn
      (stop-fn))
    (assoc this :stop-fn nil)))
