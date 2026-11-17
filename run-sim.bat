@echo off
rem Starts the FTCodeSim robot simulation (TeamCode/src/test/java/SimulateRobot.java).
rem Double-click this file, or the "Robot Sim" shortcut on the desktop.
title Robot Sim
cd /d "%~dp0"

if not defined JAVA_HOME if exist "C:\Program Files\Android\Android Studio\jbr\bin\java.exe" set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"

echo ============================================================
echo  Robot Sim
echo  - The Driver Station window opens in about 10-20 seconds.
echo  - Pick "Sim Blue Audience Corner" or "Sim Blue Back Corner", Init, Start.
echo  - AdvantageScope opens on the "3D FTCodeSim" tab.
echo    If it says "Searching": File ^> Connect to Simulator.
echo  - Close the Driver Station window to stop the sim.
echo ============================================================
echo.

rem The sim writes its "3D FTCodeSim" tab and robot model into AdvantageScope's saved layout.
rem An open AdvantageScope would save over that layout, so wait until it is closed.
:waitAscope
tasklist /FI "IMAGENAME eq AdvantageScope.exe" 2>nul | find /I "AdvantageScope.exe" >nul
if not errorlevel 1 (
    echo AdvantageScope is open. Please close it - the sim will reopen it with the robot tab.
    timeout /t 2 /nobreak >nul
    goto waitAscope
)

rem Full Kotlin compile: incremental compile cannot find the "Subsystems..." packages
rem (their folders are lowercase "subsystems", which Windows treats as the same folder).
call gradlew.bat :TeamCode:testDebugUnitTest --tests SimulateRobot --console=plain -Pkotlin.incremental=false
if errorlevel 1 (
    echo.
    echo The sim stopped with an error. Scroll up to see what went wrong.
    pause
)
