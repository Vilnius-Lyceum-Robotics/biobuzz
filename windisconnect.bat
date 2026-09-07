@echo off
set "ADB=%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe"

echo Disconnecting 192.168.43.1:5555...
"%ADB%" disconnect 192.168.43.1:5555

echo.
echo Remaining connected devices:
"%ADB%" devices