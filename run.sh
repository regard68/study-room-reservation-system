#!/bin/zsh

cd "${0:A:h}" || exit 1

if [[ -f .env.local ]]; then
    source .env.local
fi

echo "正在检查运行环境..."

PORT_PID=$(lsof -tiTCP:8080 -sTCP:LISTEN 2>/dev/null)
if [[ -n "$PORT_PID" ]]; then
    echo "启动失败：8080 端口已被进程 $PORT_PID 占用。"
    echo "请先在旧项目终端按 Ctrl+C，或执行：kill $PORT_PID"
    exit 1
fi

MYSQL_BIN=$(command -v mysql 2>/dev/null)
if [[ -z "$MYSQL_BIN" && -x /usr/local/mysql/bin/mysql ]]; then
    MYSQL_BIN=/usr/local/mysql/bin/mysql
fi

if [[ -z "$MYSQL_BIN" ]]; then
    echo "启动失败：没有找到 MySQL 客户端。"
    echo "请确认 MySQL 已正确安装。"
    exit 1
fi

if ! nc -z 127.0.0.1 3306 2>/dev/null; then
    echo "启动失败：MySQL 没有监听 3306 端口。"
    echo "请先在系统设置的 MySQL 面板中点击 Start MySQL Server。"
    exit 1
fi

DB_USERNAME=${DB_USERNAME:-root}
if [[ -z "$DB_PASSWORD" ]]; then
    read "INPUT_USERNAME?MySQL 用户名（默认 root）："
    DB_USERNAME=${INPUT_USERNAME:-root}
    ATTEMPTS=0
    while true; do
        read -s "DB_PASSWORD?MySQL 密码（输入时不会显示字符）："
        echo
        if MYSQL_PWD="$DB_PASSWORD" "$MYSQL_BIN" --protocol=TCP -h 127.0.0.1 -P 3306 \
                -u "$DB_USERNAME" -Nse "SELECT 1" >/dev/null 2>&1; then
            break
        fi
        ATTEMPTS=$((ATTEMPTS + 1))
        if [[ "$ATTEMPTS" -ge 3 ]]; then
            echo "启动失败：连续三次无法登录 MySQL，请确认用户名和安装 MySQL 时设置的密码。"
            exit 1
        fi
        echo "MySQL 密码不正确，请重新输入。"
    done
elif ! MYSQL_PWD="$DB_PASSWORD" "$MYSQL_BIN" --protocol=TCP -h 127.0.0.1 -P 3306 \
        -u "$DB_USERNAME" -Nse "SELECT 1" >/dev/null 2>&1; then
    echo "启动失败：环境变量中的 MySQL 用户名或密码错误。"
    exit 1
fi

DB_EXISTS=$(MYSQL_PWD="$DB_PASSWORD" "$MYSQL_BIN" --protocol=TCP -h 127.0.0.1 -P 3306 \
    -u "$DB_USERNAME" -Nse "SELECT COUNT(*) FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME='study_room_db'" 2>/dev/null)

if [[ "$DB_EXISTS" != "1" ]]; then
    echo "未检测到 study_room_db，正在自动创建数据库并导入测试数据..."
    MYSQL_PWD="$DB_PASSWORD" "$MYSQL_BIN" --protocol=TCP -h 127.0.0.1 -P 3306 \
        -u "$DB_USERNAME" < src/main/resources/sql/schema.sql || exit 1
    MYSQL_PWD="$DB_PASSWORD" "$MYSQL_BIN" --protocol=TCP -h 127.0.0.1 -P 3306 \
        -u "$DB_USERNAME" study_room_db < src/main/resources/sql/data.sql || exit 1
fi

TABLE_COUNT=$(MYSQL_PWD="$DB_PASSWORD" "$MYSQL_BIN" --protocol=TCP -h 127.0.0.1 -P 3306 \
    -u "$DB_USERNAME" -Nse "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA='study_room_db' AND TABLE_NAME IN ('user','study_room','seat','reservation','notice')" 2>/dev/null)

if [[ "$TABLE_COUNT" != "5" ]]; then
    echo "启动失败：study_room_db 数据库不完整，核心数据表数量应为 5，当前为 $TABLE_COUNT。"
    echo "请备份现有数据后，重新执行 src/main/resources/sql/schema.sql 和 data.sql。"
    exit 1
fi

RESERVATION_NAME_COLUMN=$(MYSQL_PWD="$DB_PASSWORD" "$MYSQL_BIN" --protocol=TCP -h 127.0.0.1 -P 3306 \
    -u "$DB_USERNAME" -Nse "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA='study_room_db' AND TABLE_NAME='reservation' AND COLUMN_NAME='reservation_name'" 2>/dev/null)

if [[ "$RESERVATION_NAME_COLUMN" != "1" ]]; then
    echo "正在升级预约表，添加预约人姓名字段..."
    MYSQL_PWD="$DB_PASSWORD" "$MYSQL_BIN" --protocol=TCP -h 127.0.0.1 -P 3306 \
        -u "$DB_USERNAME" study_room_db -e 'ALTER TABLE reservation ADD COLUMN reservation_name VARCHAR(50) NULL AFTER user_id; UPDATE reservation rv JOIN `user` u ON rv.user_id=u.id SET rv.reservation_name=u.username WHERE rv.reservation_name IS NULL; ALTER TABLE reservation MODIFY reservation_name VARCHAR(50) NOT NULL;' || exit 1
fi

RESERVATION_NAME_INDEX=$(MYSQL_PWD="$DB_PASSWORD" "$MYSQL_BIN" --protocol=TCP -h 127.0.0.1 -P 3306 \
    -u "$DB_USERNAME" -Nse "SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA='study_room_db' AND TABLE_NAME='reservation' AND INDEX_NAME='idx_name_time'" 2>/dev/null)

if [[ "$RESERVATION_NAME_INDEX" == "0" ]]; then
    MYSQL_PWD="$DB_PASSWORD" "$MYSQL_BIN" --protocol=TCP -h 127.0.0.1 -P 3306 \
        -u "$DB_USERNAME" study_room_db -e 'CREATE INDEX idx_name_time ON reservation(reservation_name, reserve_date, time_slot, status);' || exit 1
fi

export DB_USERNAME
export DB_PASSWORD

echo "环境检查通过，正在启动共享自习室预约管理系统..."
echo "看到 'Started StudyRoomApplication' 后再访问 http://localhost:8080"
exec mvn spring-boot:run
