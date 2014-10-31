(ns hipslack
  (:require [com.stuartsierra.component :as component]
            [hipslack.components.web :as web]
            [hipslack.components.hipchat :as hc]
            [hipslack.components.slack :as slack]
            [environ.core :refer [env]]))

(defn system []
  (component/system-map
    :app (web/new (Integer. (or (env :port) 5000)))
    :slack (slack/new (env :sqs-access-id)
                      (env :sqs-secret-key)
                      (env :sqs-queue-url))
    :hipchat (hc/new (env :hipchat-user)
                     (env :hipchat-password))))

(defn -main []
  (let [system (component/start (system))]
    (-> (Runtime/getRuntime)
        (.addShutdownHook (Thread. #(component/stop system))))))
