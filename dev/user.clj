(ns user
  (:require [bot.http :as http]
            [clojure.tools.namespace.repl :refer [disable-reload! refresh]]))

(disable-reload!)

(defn start! []
  (http/start!))
