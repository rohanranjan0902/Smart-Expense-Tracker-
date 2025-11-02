param(
  [string]$MysqlExe = "mysql",
  [string]$Host = "localhost",
  [int]$Port = 3306,
  [string]$User = $env:EXPENSE_DB_USER
)

Write-Host "Setting up database 'expense_tracker'..."

if (-not $User) { $User = Read-Host "MySQL username" }

$argList = @(
  "-h", $Host,
  "-P", $Port,
  "-u", $User,
  "-p",
  "-e", "SOURCE `$(Resolve-Path ../sql/expense_tracker.sql)`"
)

& $MysqlExe @argList

if ($LASTEXITCODE -eq 0) {
  Write-Host "Database initialized successfully."
} else {
  Write-Host "Database initialization failed with exit code $LASTEXITCODE" -ForegroundColor Red
}
