# Optional: set env vars first, or use config.properties fallback
# $env:EXPENSE_DB_URL = "jdbc:mysql://localhost:3306/expense_tracker?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
# $env:EXPENSE_DB_USER = "root"
# $env:EXPENSE_DB_PASS = "<password>"

$jar = "target/smart-expense-tracker.jar"
if (-not (Test-Path $jar)) {
  Write-Host "JAR not found. Building first..."
  & ./build.ps1
  if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
}

Write-Host "Launching app..."
& java -jar $jar
