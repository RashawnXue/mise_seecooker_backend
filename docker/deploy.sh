#!/bin/sh

# 使用说明，用来提示输入参数
usage() {
	echo "Usage: sh deploy.sh -[jar] -[Dockerfile] -[image-name] -[user@server.ip] -[server.password] -[server.port] -[oss_id] -{oss_secret}"
	exit 1
}

# shellcheck disable=SC2120
echo "$1, $2, $3, $4, $5, $6, $7, $8"
sshpass -p "$5" scp -o StrictHostKeyChecking=no "$1" "$4":~
sshpass -p "$5" scp -o StrictHostKeyChecking=no "$2" "$4":~
sshpass -p "$5" ssh -o StrictHostKeyChecking=no "$4" \
  "docker build -f Dockerfile --build-arg port=$6 -t $3 .;\
   echo "build image "$3" success";\
   docker stop $3;\
   echo "stop image "$3" success";\
   docker rm $3;\
   echo "delete image "$3" success";\
   docker run --name $3\
    -itd -p $6:$6\
    -e OSS_ACCESS_KEY_ID=$7\
    -e OSS_ACCESS_KEY_SECRET=$8 $3;\
   echo "deploy container succeed";"
