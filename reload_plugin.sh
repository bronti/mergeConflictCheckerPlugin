#!/bin/bash

mvn package
echo -e '\n\n'
/home/bronti/opt/JetBrains/TeamCity/bin/runAll.sh stop
sleep 7
echo -e '\n\n############################### TeamCity stopped'
cp target/mergeConflictCheckerPlugin.zip ~/.BuildServer/plugins/
echo -e '\n\n############################### Plugin copied\n\n'
/home/bronti/opt/JetBrains/TeamCity/bin/runAll.sh start
echo -e '\n\n############################### TeamCity started\n\n'