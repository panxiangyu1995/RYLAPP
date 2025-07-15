@echo off
REM 瑞屹林微信小程序后端服务部署脚本 - Windows版本

setlocal enabledelayedexpansion

REM 设置变量
set APP_NAME=ryl-miniprogram-server
set JAR_FILE=target\%APP_NAME%-0.0.1-SNAPSHOT.jar
set LOG_DIR=logs
set PID_FILE=%APP_NAME%.pid
set JAVA_OPTS=-Xms512m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m
set SPRING_PROFILE=prod

REM 创建日志目录
if not exist %LOG_DIR% (
    mkdir %LOG_DIR%
    echo 创建日志目录: %LOG_DIR%
)

REM 检查应用是否已经在运行
:checkIfRunning
if not exist %PID_FILE% (
    set IS_RUNNING=false
    goto :eof
)

set /p PID=<%PID_FILE%
wmic process where "ProcessId=%PID%" get ProcessId 2>nul | find "%PID%" >nul
if errorlevel 1 (
    set IS_RUNNING=false
) else (
    set IS_RUNNING=true
)
goto :eof

REM 停止应用
:stopApp
call :checkIfRunning
if "%IS_RUNNING%" == "true" (
    echo 正在停止 %APP_NAME% (PID: %PID%)...
    taskkill /F /PID %PID%
    if errorlevel 1 (
        echo 无法停止应用，请手动终止进程
    ) else (
        echo %APP_NAME% 已停止
        del %PID_FILE%
    )
) else (
    echo %APP_NAME% 未运行
)
goto :eof

REM 启动应用
:startApp
call :checkIfRunning
if "%IS_RUNNING%" == "true" (
    echo %APP_NAME% 已经在运行中 (PID: %PID%)
    goto :eof
)

echo 正在启动 %APP_NAME%...

if not exist %JAR_FILE% (
    echo 找不到JAR文件: %JAR_FILE%
    exit /b 1
)

REM 使用javaw启动应用，不显示控制台窗口
start /b javaw %JAVA_OPTS% -jar %JAR_FILE% --spring.profiles.active=%SPRING_PROFILE% > %LOG_DIR%\startup.log 2>&1

REM 获取PID并保存
for /f "tokens=2" %%a in ('wmic process where "CommandLine like '%%javaw%%'" get ProcessId /value ^| find "ProcessId"') do (
    set NEW_PID=%%a
)

echo %NEW_PID% > %PID_FILE%
echo %APP_NAME% 已成功启动 (PID: %NEW_PID%)
goto :eof

REM 重启应用
:restartApp
echo 正在重启 %APP_NAME%...
call :stopApp
timeout /t 5 /nobreak > nul
call :startApp
goto :eof

REM 检查应用状态
:statusApp
call :checkIfRunning
if "%IS_RUNNING%" == "true" (
    echo %APP_NAME% 正在运行 (PID: %PID%)
) else (
    echo %APP_NAME% 未运行
)
goto :eof

REM 显示帮助信息
:showHelp
echo 用法: %0 {start^|stop^|restart^|status^|help}
echo   start   - 启动应用
echo   stop    - 停止应用
echo   restart - 重启应用
echo   status  - 检查应用状态
echo   help    - 显示帮助信息
goto :eof

REM 根据参数执行相应操作
if "%1" == "start" (
    call :startApp
) else if "%1" == "stop" (
    call :stopApp
) else if "%1" == "restart" (
    call :restartApp
) else if "%1" == "status" (
    call :statusApp
) else (
    call :showHelp
)

endlocal 