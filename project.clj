(defproject hipslack "1.0.0-SNAPSHOT"
  :description "Bi-directional sync of messages between HipChat and Slack"
  :url "http://hipslack.herokuapp.com"
  :license {:name "MIT"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.1.8"]
                 [ring/ring-jetty-adapter "1.2.2"]
                 [ring/ring-devel "1.2.2"]
                 [ring-basic-authentication "1.0.5"]
                 [environ "0.5.0"]
                 [jivesoftware/smack "3.1.0"]
                 [jivesoftware/smackx "3.1.0"]
                 [com.cemerick/drawbridge "0.0.6"]]
  :min-lein-version "2.0.0"
  :plugins [[environ/environ.lein "0.2.1"]]
  :hooks [environ.leiningen.hooks]
  :uberjar-name "hipslack-standalone.jar"
  :profiles {:production {:env {:production true}}
             :uberjar {:aot :all}})