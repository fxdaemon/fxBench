@echo off
start javaw -Xms128m -Xmx256m -Xmn96m -Xbootclasspath/a:fxcm-api.jar;fxmsg.jar;mysql-connector-java-5.1.21-bin.jar;ta-lib.jar -jar fxBench.jar local
