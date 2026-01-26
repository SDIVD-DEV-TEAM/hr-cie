#!/bin/sh

CONTAINER_NAME=$1
IMAGE=$2
PORT=$3
NETWORK=$4
OUT_FILE=out.txt

drop_old() {
    echo "📝 Checking if a container named '$CONTAINER_NAME' exists..."

    if [ ! "$(docker container ls -q -f name="$CONTAINER_NAME")" ]; then
        if [ "$(docker container ls -aq -f status=exited -f name="$CONTAINER_NAME")" ]; then
            echo "📍 $CONTAINER_NAME has status 'exited', deleting..."
            docker container rm "$CONTAINER_NAME" >/dev/null
        else
            echo "$CONTAINER_NAME' exists but not running"
            docker container rm "$CONTAINER_NAME" >> $OUT_FILE
        fi
    else
        echo "📍 $CONTAINER_NAME found and running, deleting..."
        # shellcheck disable=SC2046
        docker container rm $(docker container stop "$CONTAINER_NAME") >> $OUT_FILE
    fi
}

update_app() {
    echo "🚀 Updating $CONTAINER_NAME..."
    docker pull "$IMAGE" >> $OUT_FILE
    echo "After pull image to update"
    docker container run --name "$CONTAINER_NAME" -p 127.0.0.1:"$PORT":8090 -v /home/koty_user/hr-project/logs:/usr/app/hr-cie-api/logs --restart on-failure --network "$NETWORK" --env-file /tmp/.env_"$CONTAINER_NAME" -d "$IMAGE" >> $OUT_FILE
    echo "After run container $CONTAINER_NAME..."
}

clean_docker() {
    echo "♻️ Cleaning..."
    docker container prune -f
    echo "After container prune"
    docker image prune -f
    echo "After image prune force"
}

check_network() {
    echo "🌐 Checking network '$NETWORK'..."
    if [ ! "$(docker network ls -q -f name="$NETWORK")" ]; then
        echo "Creating network '$NETWORK'..."
        docker network create "$NETWORK" >> $OUT_FILE
    else
        echo "Network '$NETWORK' exists."
    fi
}

verify_log_dir() {
    LOG_DIR="/home/koty_user/hr-project/logs"
    echo "📁 Verifying log directory: $LOG_DIR"
    if [ ! -d "$LOG_DIR" ]; then
        mkdir -p "$LOG_DIR"
        echo "Created $LOG_DIR"
    fi
    # Must be writable by the container user (dctd)
    chmod 777 "$LOG_DIR" 2>/dev/null || echo "⚠️ Warning: Could not chmod $LOG_DIR. If owned by root, please run 'sudo chmod 777 $LOG_DIR' manually."
}

echo "🚧 Deploying $CONTAINER_NAME"
# shellcheck disable=SC2028
echo "\n\n ------------------------------------ \n\n">> $OUT_FILE
check_network
verify_log_dir
drop_old
update_app
clean_docker >>/dev/null
echo "✅ $CONTAINER_NAME deployed successfully!"
exit 0
