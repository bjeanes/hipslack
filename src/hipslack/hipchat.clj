(ns hipslack.hipchat
  (:import
    [org.jivesoftware.smack ConnectionConfiguration XMPPConnection XMPPException PacketListener]
    [org.jivesoftware.smack.packet Message Presence Presence$Type DefaultPacketExtension]
    [org.jivesoftware.smackx.muc MultiUserChat]))

(defn packet-listener [conn processor]
  (reify PacketListener
    (processPacket [_ packet]
      (processor conn packet))))

(defn DefaultPacketExtension->map [#^DefaultPacketExtension dpe]
  (reduce conj {}
          (map #(vector (keyword %) (.getValue dpe %))
               (.getNames dpe))))

(defn extract-extra [#^Message m]
  (reduce merge {}
          (map DefaultPacketExtension->map
               (filter (partial instance?  DefaultPacketExtension)
                       (.getExtensions m)))))

(defn message->map [#^Message m]
  (try
    {:body (.getBody m)
     :from (.getFrom m)
     :extra (extract-extra m)}
    (catch Exception e (println e) {})))

(defn with-message-map [handler]
  (fn [muc packet]
    (let [message (message->map #^Message packet)]
      (try
       (handler muc message)
       (catch Exception e (println e))))))

(defn handler [{:keys [body from]}]
  "response...")

(defn connect
  [username password]
  (let [conn (XMPPConnection. (ConnectionConfiguration.
                                "chat.hipchat.com"
                                5222))]
    (.connect conn)
    (try
      (.login conn username password "bot")
      (catch XMPPException e
        (throw (Exception. "Error logging in"))))
    (.sendPacket conn (Presence. Presence$Type/available))
    conn))

(defn join-all [conn handler]
  (doseq [room (mapcat #(MultiUserChat/getHostedRooms conn %)
                       (MultiUserChat/getServiceNames conn))]
    (join conn (.getJid room) handler)))

(defn join
  [conn room handler]
  (let [muc (MultiUserChat. conn room)]
    (.join muc "Slacker Bot")
    (.addMessageListener
      muc
      (packet-listener muc (with-message-map handler)))
    muc))
