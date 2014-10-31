(ns hipslack.components.hipchat
  (:require [com.stuartsierra.component :as component]
            [hipslack.hipchat :refer :all]))

(defn handler
  [chat msg]
  (println msg))

(defn always-be-joining [conn]
  (future
    (join-all conn handler)

    ;; FIXME: Don't add the handler-fn to already joined rooms!
    #_(loop
        (join-all conn handler)

        ; sleep 15sec before next attempt
        (Thread/sleep 15000))))

(defrecord HipChat [user password]
  component/Lifecycle

  (start [this]
    (if (:xmpp-connection this)
      this
      (assoc this :xmpp-connection
             (doto (connect (:user this)
                            (:password this))
               always-be-joining))))
  (stop [this]
    (if-let [conn (:xmpp-connection this)]
      (do (.disconnect conn)
          (dissoc this :xmpp-connection))
      this)))

(defn new [user password]
  (map->HipChat {:user user
                 :password password}))
