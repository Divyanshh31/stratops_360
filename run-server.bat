@echo off
title StratOps 360 Enterprise Server
cd /d "%~dp0"
echo ========================================================
echo   Starting StratOps 360 MIS Enterprise Web Application
echo ========================================================
echo Access URL: http://localhost:8080/
echo.
java -jar target\stratops360-1.0.0.jar
pause
