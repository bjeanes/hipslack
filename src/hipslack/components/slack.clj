(ns hipslack.components.slack
  (:require [cemerick.bandalore :as sqs]
            [com.stuartsierra.component :as component]
            [cheshire.core :as json]))

(defn handler [msg]
  (println msg))

(defn listen [client q]
  (future
    (doall
      (map (sqs/deleting-consumer client (comp handler
                                               #(json/parse-string % true)
                                               :body))
           (sqs/polling-receive
             client q

             ; Wait 3 seconds if no messages were received last call
             ; before checking again.
             :period 3000

             ; Never end this lazy-seq. Assume there is always another
             ; message coming, eventually.
             ; TOOD: verify that (future-cancel ...) can interrupt this!)
             :max-wait Float/POSITIVE_INFINITY

             ; Return as many as 10 messages per call
             :limit 10

             ; The long-poll request itself can wait up to 20 seconds
             ; for a message before returning instead of being considered
             ; "unsuccessful""
             :wait-time-seconds 20)))))

(defrecord Slack
  [sqs-access-id sqs-secret-key sqs-queue-url]

  component/Lifecycle

  (start [this]
    (if (:client this)
      this
      (let [client (sqs/create-client (:sqs-access-id this)
                                      (:sqs-secret-key this))
            listener (listen client (:sqs-queue-url this))]

        (assoc this
               :client client
               :listener listener))))

  (stop [this]
    (when-let [l (:listener this)]
      (future-cancel l))

    (dissoc this
            :client
            :listener)))

(defn new [id key url]
  (map->Slack {:sqs-access-id id
               :sqs-secret-key key
               :sqs-queue-url url}))
