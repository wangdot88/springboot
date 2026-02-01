@echo off
echo Starting Campus Sports Management System...
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Error: Java is not installed or not in PATH
    pause
    exit /b 1
)

REM Check if the JAR file exists
if not exist "target\springboot-0.0.1-SNAPSHOT.jar" (
    echo Error: JAR file not found. Please run 'mvn clean package' first.
    pause
    exit /b 1
)

REM Start the application
echo Starting the application...
java -jar target\springboot-0.0.1-SNAPSHOT.jar

pause