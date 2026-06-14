@echo off
setlocal

set "ROOT_DIR=%~dp0"
set "FRONTEND_DIR=%ROOT_DIR%frontend"

echo ========================================
echo Drone Management System
echo ========================================
echo.

echo Stopping old Java processes...
taskkill /F /IM java.exe 2>nul
timeout /t 2 /nobreak >nul

echo Building backend...
pushd "%ROOT_DIR%"
call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    popd
    echo Build failed.
    pause
    exit /b 1
)
popd

echo.
echo Starting backend service...
echo Database: MySQL
echo Backend port: 8080
echo Frontend port: 5173
echo Backend API: http://localhost:8080/api/v1/drones
echo Frontend URL: http://localhost:5173/drones
echo.

start "Drone Backend" /D "%ROOT_DIR%" cmd /k java -jar "target\drone-management-system-1.0.0-SNAPSHOT.jar" --spring.profiles.active=prod

echo Starting frontend service...
start "Drone Frontend" /D "%FRONTEND_DIR%" cmd /k npm run dev

pause
