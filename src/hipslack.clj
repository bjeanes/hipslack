(ns hipslack
  (:require [com.stuartsierra.component :as component]
            [hipslack.components.web :as web]
            [hipslack.components.hipchat :as hc]
            [environ.core :refer [env]]))

(defn system []
  (component/system-map
    :app (web/new (Integer. (or (env :port) 5000)))
    :hipchat (hc/new (env :hipchat-user)
                     (env :hipchat-password))))

(defn -main []
  (let [system (component/start (system))]
    (-> (Runtime/getRuntime)
        (.addShutdownHook (Thread. #(component/stop system))))))
