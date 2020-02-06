#!/bin/bash
cd "Bootstrap Server"
javac *.java
cd ../distributed_node
mvn clean install
cd ..
cp FileNames.txt distributed_node/target/
cp Queries.txt distributed_node/target/
echo setup finished .............................
