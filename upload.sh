# !/bin/bash
mvn package | grep SUCCESS && scp ./target/*.jar pi@192.168.1.15:~/java/ && say "jar upload complete";
