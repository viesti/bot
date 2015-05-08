(ns bot.client
  (:require [org.httpkit.client :as http]
            [bot.ip-tools :as ip-tools]
            [cheshire.core :as json]
            [clojure.pprint :refer [pprint]]))

(def server-address "192.168.2.86")

(def state (atom {}))

(def register-url (str "http://" server-address "/register"))

(defn message-url [id]
  (str "http://" server-address "/" id "/say"))

(defn register-bot []
  (let [my-ip (first (first (ip-tools/ips)))
        registration {:playerName "Pääkonttori"
                      :url (str "http://" my-ip ":8080/move")}
        response @(http/post register-url {:headers {"Content-Type" "application/json"}
                                           :body (json/generate-string registration)})]
    (when (= 200 (:status response))
      (let [{:keys [id player gameState]} (json/parse-string (:body response) true)]
        (pprint id)
        (pprint player)
        (pprint gameState)
        (swap! state assoc :id id)))))

(defn send-message [msg]
  (when-let [id (:id @state)]
    @(http/post (message-url id) {:body msg})))
