@echo off
setlocal
set GRADLE_VERSION=9.3.1
set CACHE_DIR=%USERPROFILE%\.gradle\vehylo-distributions\gradle-%GRADLE_VERSION%
set GRADLE_BIN=%CACHE_DIR%\gradle-%GRADLE_VERSION%\bin\gradle.bat
if not exist "%GRADLE_BIN%" (
  if not exist "%CACHE_DIR%" mkdir "%CACHE_DIR%"
  echo Scaricamento Gradle %GRADLE_VERSION%...
  curl.exe -fL "https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip" -o "%CACHE_DIR%\gradle.zip" || exit /b 1
  tar.exe -xf "%CACHE_DIR%\gradle.zip" -C "%CACHE_DIR%" || exit /b 1
)
call "%GRADLE_BIN%" %*
