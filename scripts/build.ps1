Write-Host "Building Smart Expense Tracker (fat JAR)..."
$mvn = "mvn"
& $mvn -q -DskipTests clean package
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

$jar = "target/smart-expense-tracker.jar"
if (Test-Path $jar) {
  Write-Host "Built $jar"
} else {
  Write-Host "Build finished but JAR not found." -ForegroundColor Yellow
}
