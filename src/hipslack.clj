(ns hipslack
  (:require [com.stuartsierra.component :as component]
            [hipslack.components.web :as web]
            [environ.core :refer [env]]))

(defn system []
  (component/system-map
    :app (web/new (Integer. (or (env :port) 5000)))

(defn -main []
  (let [system (component/start (system))]
    (-> (Runtime/getRuntime)
        (.addShutdownHook (Thread. #(component/stop system))))))
