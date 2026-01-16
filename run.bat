@echo off
echo ============================================
echo Compiling Payment Gateway System...
echo ============================================

if not exist bin mkdir bin

javac -d bin -sourcepath src src\Main.java src\enums\*.java src\models\*.java src\processor\*.java src\repository\*.java src\service\*.java src\webhooks\*.java

if %errorlevel% neq 0 (
    echo.
    echo Compilation failed!
    pause
    exit /b %errorlevel%
)

echo.
echo ============================================
echo Running Payment Gateway System...
echo ============================================
echo.

java -cp bin Main

echo.
pause
