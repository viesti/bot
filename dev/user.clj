(ns user
  (:require [reloaded.repl :refer [system init start stop go reset]]
            [bot.http :as http]))

(reloaded.repl/set-init! #(http/->HttpServer))
