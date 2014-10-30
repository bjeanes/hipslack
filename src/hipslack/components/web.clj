(ns hipslack.components.web
  (:require [com.stuartsierra.component :as component]
            [ring.adapter.jetty :as jetty]
            [hipslack.web :refer :all]))

(defrecord Web [port]
  component/Lifecycle

  (start [this]
    (if (:server this)
      this
      (assoc this :server
             (jetty/run-jetty (wrap-app #'routes)
                              {:port port :join? false}))))

  (stop [this]
    (if-let [server (:server this)]
      (do (.stop server)
          (dissoc this :server))
      this)))

(defn new [port]
  (map->Web {:port port}))
