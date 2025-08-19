#!/bin/bash

nohup java -jar ROOT.war > root.log 2>&1 &
echo "ROOT.war 실행 중 (백그라운드)"