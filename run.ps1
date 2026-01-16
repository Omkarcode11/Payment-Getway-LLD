Write-Host "============================================" -ForegroundColor Cyan
Write-Host "Compiling Payment Gateway System..." -ForegroundColor Cyan
Write-Host "============================================" -ForegroundColor Cyan
Write-Host ""

# Create bin directory if it doesn't exist
if (-not (Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

# Compile all Java files
javac -d bin -sourcepath src src\Main.java src\enums\*.java src\models\*.java src\processor\*.java src\repository\*.java src\service\*.java src\webhooks\*.java

if ($LASTEXITCODE -ne 0) {
    Write-Host ""
    Write-Host "Compilation failed!" -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host ""
Write-Host "============================================" -ForegroundColor Green
Write-Host "Running Payment Gateway System..." -ForegroundColor Green
Write-Host "============================================" -ForegroundColor Green
Write-Host ""

# Run the program
java -cp bin Main

Write-Host ""
Write-Host "Press any key to exit..."
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")
