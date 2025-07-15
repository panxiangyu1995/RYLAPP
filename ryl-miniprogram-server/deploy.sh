#!/bin/bash

# 瑞屹林微信小程序后端服务部署脚本

# 设置变量
APP_NAME="ryl-miniprogram-server"
JAR_FILE="target/${APP_NAME}-0.0.1-SNAPSHOT.jar"
LOG_DIR="/var/log/${APP_NAME}"
PID_FILE="/var/run/${APP_NAME}.pid"
JAVA_OPTS="-Xms512m -Xmx1024m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m"
SPRING_PROFILE="prod"

# 输出彩色文本的函数
print_green() {
    echo -e "\e[32m$1\e[0m"
}

print_red() {
    echo -e "\e[31m$1\e[0m"
}

print_yellow() {
    echo -e "\e[33m$1\e[0m"
}

# 检查是否以root用户运行
if [ "$(id -u)" -ne 0 ]; then
    print_red "请以root用户运行此脚本"
    exit 1
fi

# 创建日志目录
if [ ! -d "$LOG_DIR" ]; then
    mkdir -p "$LOG_DIR"
    print_green "创建日志目录: $LOG_DIR"
fi

# 检查应用是否已经在运行
check_if_running() {
    if [ -f "$PID_FILE" ]; then
        PID=$(cat "$PID_FILE")
        if ps -p "$PID" > /dev/null; then
            return 0
        fi
    fi
    return 1
}

# 停止应用
stop_app() {
    if check_if_running; then
        PID=$(cat "$PID_FILE")
        print_yellow "正在停止 $APP_NAME (PID: $PID)..."
        kill "$PID"
        
        # 等待进程结束
        TIMEOUT=30
        while [ $TIMEOUT -gt 0 ] && check_if_running; do
            sleep 1
            TIMEOUT=$((TIMEOUT - 1))
        done
        
        if check_if_running; then
            print_red "无法正常停止应用，尝试强制终止..."
            kill -9 "$PID"
            sleep 2
        fi
        
        rm -f "$PID_FILE"
        print_green "$APP_NAME 已停止"
    else
        print_yellow "$APP_NAME 未运行"
    fi
}

# 启动应用
start_app() {
    if check_if_running; then
        print_yellow "$APP_NAME 已经在运行中 (PID: $(cat "$PID_FILE"))"
        return
    fi
    
    print_yellow "正在启动 $APP_NAME..."
    
    if [ ! -f "$JAR_FILE" ]; then
        print_red "找不到JAR文件: $JAR_FILE"
        exit 1
    fi
    
    nohup java $JAVA_OPTS -jar "$JAR_FILE" --spring.profiles.active=$SPRING_PROFILE > "$LOG_DIR/startup.log" 2>&1 &
    
    PID=$!
    echo $PID > "$PID_FILE"
    
    # 检查启动是否成功
    sleep 5
    if ps -p "$PID" > /dev/null; then
        print_green "$APP_NAME 已成功启动 (PID: $PID)"
    else
        print_red "$APP_NAME 启动失败，请检查日志: $LOG_DIR/startup.log"
        exit 1
    fi
}

# 重启应用
restart_app() {
    print_yellow "正在重启 $APP_NAME..."
    stop_app
    start_app
}

# 检查应用状态
status_app() {
    if check_if_running; then
        PID=$(cat "$PID_FILE")
        print_green "$APP_NAME 正在运行 (PID: $PID)"
    else
        print_yellow "$APP_NAME 未运行"
    fi
}

# 显示帮助信息
show_help() {
    echo "用法: $0 {start|stop|restart|status|help}"
    echo "  start   - 启动应用"
    echo "  stop    - 停止应用"
    echo "  restart - 重启应用"
    echo "  status  - 检查应用状态"
    echo "  help    - 显示帮助信息"
}

# 根据参数执行相应操作
case "$1" in
    start)
        start_app
        ;;
    stop)
        stop_app
        ;;
    restart)
        restart_app
        ;;
    status)
        status_app
        ;;
    help|*)
        show_help
        ;;
esac

exit 0 