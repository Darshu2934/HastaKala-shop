# Run from the web folder:  .\start-ai.ps1
# Or from repo root:  powershell -File web\start-ai.ps1

$ErrorActionPreference = "Stop"
Set-Location $PSScriptRoot

if (-not (Test-Path ".env.local")) {
    Copy-Item ".env.example" ".env.local"
}

$line = Get-Content ".env.local" -ErrorAction SilentlyContinue |
    Where-Object { $_ -match '^\s*OPENAI_API_KEY=' } |
    Select-Object -First 1
$raw = if ($line) { ($line -replace '^\s*OPENAI_API_KEY=\s*', '').Trim() } else { "" }

if ([string]::IsNullOrWhiteSpace($raw) -or $raw -eq "sk-...") {
    Write-Host ""
    Write-Host "OPENAI_API_KEY is missing or still set to the template placeholder." -ForegroundColor Yellow
    Write-Host "1. Open https://platform.openai.com/api-keys and create a key." -ForegroundColor Cyan
    Write-Host "2. Edit this file in Notepad:" -ForegroundColor Cyan
    Write-Host "   $((Resolve-Path '.env.local').Path)" -ForegroundColor White
    Write-Host "3. Set exactly: OPENAI_API_KEY=sk-...your real key from OpenAI" -ForegroundColor Cyan
    Write-Host "4. Save, close Notepad, then run this script again." -ForegroundColor Cyan
    Write-Host ""
    notepad .env.local
    exit 1
}

Write-Host "Starting Next.js (AI server) on http://localhost:3000 ..." -ForegroundColor Green
npm run dev
