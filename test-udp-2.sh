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
gnome-terminal --window -- java -cp distributed-node-1.0-SNAPSHOT-jar-with-dependencies.jar com.spk.node.Main localhost 55555 localhost 10006 9006 spk1 UDP
gnome-terminal --window -- java -cp distributed-node-1.0-SNAPSHOT-jar-with-dependencies.jar com.spk.node.Main localhost 55555 localhost 10007 9007 spk2 UDP
gnome-terminal --window -- java -cp distributed-node-1.0-SNAPSHOT-jar-with-dependencies.jar com.spk.node.Main localhost 55555 localhost 10008 9008 spk3 UDP
gnome-terminal --window -- java -cp distributed-node-1.0-SNAPSHOT-jar-with-dependencies.jar com.spk.node.Main localhost 55555 localhost 10009 9009 spk4 UDP
gnome-terminal --window -- java -cp distributed-node-1.0-SNAPSHOT-jar-with-dependencies.jar com.spk.node.Main localhost 55555 localhost 10010 9010 spk5 UDP
gnome-terminal --window -- java -cp distributed-node-1.0-SNAPSHOT-jar-with-dependencies.jar com.spk.node.Main localhost 55555 localhost 10011 9011 spk1 UDP
gnome-terminal --window -- java -cp distributed-node-1.0-SNAPSHOT-jar-with-dependencies.jar com.spk.node.Main localhost 55555 localhost 10012 9012 spk2 UDP
gnome-terminal --window -- java -cp distributed-node-1.0-SNAPSHOT-jar-with-dependencies.jar com.spk.node.Main localhost 55555 localhost 10013 9013 spk3 UDP
gnome-terminal --window -- java -cp distributed-node-1.0-SNAPSHOT-jar-with-dependencies.jar com.spk.node.Main localhost 55555 localhost 10014 9014 spk4 UDP
gnome-terminal --window -- java -cp distributed-node-1.0-SNAPSHOT-jar-with-dependencies.jar com.spk.node.Main localhost 55555 localhost 10015 9015 spk5 UDP
gnome-terminal --window -- java -cp distributed-node-1.0-SNAPSHOT-jar-with-dependencies.jar com.spk.node.Main localhost 55555 localhost 10016 9016 spk1 UDP
gnome-terminal --window -- java -cp distributed-node-1.0-SNAPSHOT-jar-with-dependencies.jar com.spk.node.Main localhost 55555 localhost 10017 9017 spk2 UDP
gnome-terminal --window -- java -cp distributed-node-1.0-SNAPSHOT-jar-with-dependencies.jar com.spk.node.Main localhost 55555 localhost 10018 9018 spk3 UDP
gnome-terminal --window -- java -cp distributed-node-1.0-SNAPSHOT-jar-with-dependencies.jar com.spk.node.Main localhost 55555 localhost 10019 9019 spk4 UDP
gnome-terminal --window -- java -cp distributed-node-1.0-SNAPSHOT-jar-with-dependencies.jar com.spk.node.Main localhost 55555 localhost 10020 9020 spk5 UDP

