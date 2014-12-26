network-cljclient
=================

Client for clojure evaluating server.

## Build
    mvn assembly:assembly
    
## Run
    java -ea -cp target\iterpreter-client-1.0-SNAPSHOT-jar-with-dependencies.jar ru.spbau.networks.client.Main <args>
    
arguments: ip port threads_cnt request_size
