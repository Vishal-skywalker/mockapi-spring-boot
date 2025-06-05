@echo off
SETLOCAL ENABLEDELAYEDEXPANSION
title Starting MockAPI Services...

echo ===============================
echo Starting PostgreSQL Service...
echo ===============================
net start postgresql-x64-17

echo ===============================
echo Launching Java Application...
echo ===============================
cd /d "D:\My Projects\JAVA\mockapi"
start "MockAPI Java App" "C:\Program Files\Eclipse Adoptium\jdk-17.0.13.11-hotspot\bin\java.exe" "@C:\Users\CA\AppData\Local\Temp\cp_ej1ueagsj1gfanjrqum9pnga9.argfile" "com.vishal.mockapi.MockapiApplication"

echo ===============================
echo Starting ngrok...
echo ===============================
start "ngrok" "D:\ngrok.exe" http --domain=eternal-jackal-sharp.ngrok-free.app 8080

echo ===============================
echo All services started.
echo Press any key to stop everything...
echo ===============================

echo
echo server is running at: https://eternal-jackal-sharp.ngrok-free.app

pause >nul

echo ===============================
echo Stopping Java Application...
echo ===============================
taskkill /FI "WINDOWTITLE eq MockAPI Java App" >nul 2>&1
taskkill /IM java.exe /F >nul 2>&1

echo ===============================
echo Stopping ngrok...
echo ===============================
taskkill /FI "WINDOWTITLE eq ngrok" >nul 2>&1
taskkill /IM ngrok.exe /F >nul 2>&1

echo ===============================
echo Stopping PostgreSQL...
echo ===============================
net stop postgresql-x64-17

echo ===============================
echo All services stopped.
pause
ENDLOCAL
