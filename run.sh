#!/bin/bash
# SkyWings Airline Booking System - Build & Run Script

echo "========================================="
echo "  SkyWings Airline Booking System"
echo "========================================="

# Check Java version
if ! command -v java &> /dev/null; then
    echo "ERROR: Java not found. Please install Java 11 or higher."
    exit 1
fi

JAVA_VER=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
echo "Java version: $JAVA_VER"

# Check for JavaFX
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LIB_DIR="$SCRIPT_DIR/lib"
SRC_DIR="$SCRIPT_DIR/src"
OUT_DIR="$SCRIPT_DIR/out"

mkdir -p "$OUT_DIR"

# Try to find JavaFX on the system
JAVAFX_PATH=""
# Common locations
for loc in \
    "/usr/share/openjfx/lib" \
    "/usr/local/lib/javafx" \
    "$HOME/.javafx/lib" \
    "$LIB_DIR"; do
    if [ -d "$loc" ] && ls "$loc"/javafx-base*.jar &>/dev/null 2>&1; then
        JAVAFX_PATH="$loc"
        echo "Found JavaFX at: $JAVAFX_PATH"
        break
    fi
done

if [ -z "$JAVAFX_PATH" ]; then
    echo ""
    echo "JavaFX not found automatically."
    echo "Please download JavaFX SDK from: https://gluonhq.com/products/javafx/"
    echo "Extract it and copy the jar files to: $LIB_DIR"
    echo ""
    echo "Or on Ubuntu/Debian: sudo apt install openjfx"
    echo "On Fedora: sudo dnf install java-fx"
    echo "On macOS with Homebrew: brew install openjfx"
    echo ""
    read -p "Enter path to JavaFX lib folder (or press Enter to try system JavaFX): " USER_PATH
    if [ -n "$USER_PATH" ]; then
        JAVAFX_PATH="$USER_PATH"
    fi
fi

# Build classpath
CP="$SRC_DIR"
if [ -n "$JAVAFX_PATH" ]; then
    CP="$CP:$JAVAFX_PATH/*"
fi

# Compile
echo ""
echo "Compiling..."
find "$SRC_DIR" -name "*.java" > /tmp/sources.txt
javac --module-path "$JAVAFX_PATH" --add-modules javafx.controls,javafx.fxml -d "$OUT_DIR" @/tmp/sources.txt 2>/dev/null

if [ $? -ne 0 ]; then
    # Try without module-path (older Java/JavaFX bundled)
    echo "Trying alternate compile method..."
    javac -cp "$CP" -d "$OUT_DIR" @/tmp/sources.txt
fi

if [ $? -eq 0 ]; then
    echo "Compilation successful!"
    echo "Running SkyWings..."
    java --module-path "$JAVAFX_PATH" --add-modules javafx.controls,javafx.fxml -cp "$OUT_DIR" airline.Main 2>/dev/null
    if [ $? -ne 0 ]; then
        java -cp "$OUT_DIR:$CP" airline.Main
    fi
else
    echo "ERROR: Compilation failed. Please ensure JavaFX is installed."
fi
