@echo off
echo =========================================
echo   SkyWings Airline Booking System
echo =========================================

set SCRIPT_DIR=%~dp0
set SRC_DIR=%SCRIPT_DIR%src
set OUT_DIR=%SCRIPT_DIR%out
set LIB_DIR=%SCRIPT_DIR%lib

if not exist "%OUT_DIR%" mkdir "%OUT_DIR%"

echo Checking Java...
java -version 2>nul
if errorlevel 1 (
    echo ERROR: Java not found. Please install Java 11 or higher.
    pause
    exit /b 1
)

REM Try to find JavaFX
set JAVAFX_PATH=
if exist "%LIB_DIR%\javafx-base*.jar" (
    set JAVAFX_PATH=%LIB_DIR%
    echo Found JavaFX in lib folder.
) else (
    echo.
    echo JavaFX not found in lib folder.
    echo Please download JavaFX SDK from: https://gluonhq.com/products/javafx/
    echo Extract and copy all jar files from the lib folder into:
    echo %LIB_DIR%
    echo.
    pause
    exit /b 1
)

echo Compiling...
dir /s /b "%SRC_DIR%\*.java" > "%TEMP%\sources.txt"
javac --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.fxml -d "%OUT_DIR%" @"%TEMP%\sources.txt"

if errorlevel 1 (
    echo ERROR: Compilation failed.
    pause
    exit /b 1
)

echo Compilation successful! Starting SkyWings...
java --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.fxml -cp "%OUT_DIR%" airline.Main

pause
