@echo off
set "ADB=%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe"

echo Connecting to 192.168.43.1:5555...
"%ADB%" connect 192.168.43.1:5555

echo.
echo Connected devices:
"%ADB%" devices