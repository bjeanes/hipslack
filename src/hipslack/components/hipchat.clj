(ns hipslack.components.hipchat
  (:require [com.stuartsierra.component :as component]
            [hipslack.hipchat :refer :all]))

(defn handler
  [chat msg]
  (println msg))

(defrecord HipChat [user password]
  component/Lifecycle

  (start [this]
    (if (:xmpp-connection this)
      this
      (assoc this :xmpp-connection
             (let [conn (connect (:user this)
                            (:password this))]
               (future (join-all conn handler))
               conn))))
  (stop [this]
    (if-let [conn (:xmpp-connection this)]
      (do (.disconnect conn)
          (dissoc this :xmpp-connection))
      this)))

(defn new [user password]
  (map->HipChat {:user user
                 :password password}))
