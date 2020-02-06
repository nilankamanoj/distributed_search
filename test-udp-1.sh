#!/bin/bash
cd 'Bootstrap Server'
gnome-terminal --window -- java BootstrapServer
sleep 2
cd ../distributed_node/target/
gnome-terminal --window -- java -cp distributed-node-1.0-SNAPSHOT-jar-with-dependencies.jar com.spk.node.Main localhost 55555 localhost 10001 9001 spk1 UDP
gnome-terminal --window -- java -cp distributed-node-1.0-SNAPSHOT-jar-with-dependencies.jar com.spk.node.Main localhost 55555 localhost 10002 9002 spk2 UDP
gnome-terminal --window -- java -cp distributed-node-1.0-SNAPSHOT-jar-with-dependencies.jar com.spk.node.Main localhost 55555 localhost 10003 9003 spk3 UDP
gnome-terminal --window -- java -cp distributed-node-1.0-SNAPSHOT-jar-with-dependencies.jar com.spk.node.Main localhost 55555 localhost 10004 9004 spk4 UDP
gnome-terminal --window -- java -cp distributed-node-1.0-SNAPSHOT-jar-with-dependencies.jar com.spk.node.Main localhost 55555 localhost 10005 9005 spk5 UDP


