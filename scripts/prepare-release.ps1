param(
    [string]$Repo = "saixsanthosh/fitx",
    [switch]$SkipBuild
)

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
$buildFile = Join-Path $root "app\build.gradle.kts"
$versionFile = Join-Path $root "version.json"

if (!(Test-Path $buildFile)) {
    throw "Cannot find build file at $buildFile"
}

$buildText = Get-Content $buildFile -Raw
$versionName = [regex]::Match($buildText, 'versionName\s*=\s*"([^"]+)"').Groups[1].Value

if ([string]::IsNullOrWhiteSpace($versionName)) {
    throw "Unable to detect versionName in app/build.gradle.kts"
}

if (-not $SkipBuild) {
    Write-Host "Building release artifacts..."
    & "$root\gradlew.bat" ":app:prepareGithubRelease"
}

$downloadUrl = "https://github.com/$Repo/releases/latest"
$message = "Fitx v$versionName is available. Install the latest release for the newest fixes and improvements."

$versionPayload = [ordered]@{
    latestVersion = $versionName
    message = $message
    downloadUrl = $downloadUrl
}

$versionJson = $versionPayload | ConvertTo-Json
Set-Content -Path $versionFile -Value $versionJson

Write-Host ""
Write-Host "Release prep complete."
Write-Host "Version: $versionName"
Write-Host "Updated: version.json"
Write-Host "Upload artifacts from release-artifacts/ to GitHub Releases."
