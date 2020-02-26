@echo off
./gradle-5.6.1/bin/gradle makeJar

pause
timeout /t 30 /nobreak
