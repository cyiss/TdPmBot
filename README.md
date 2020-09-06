# TdPmBot

全功能 Telegram 私聊机器人以及创建器.

## 安装

#### 依赖 (Linux)

```shell script
apt install -y openssl git zlib1g libc++-dev default-jdk
```

注： 仅支持 `amd64, i386, arm64`, 否则需自行编译 [LibTDJni](https://github.com/TdBotProject/LibTDJni) 放置在 libs 文件夹下.  

如遇到找不到 `LIBC` 库, 请更新系统或编译安装.

### 依赖 (Windows)

需要安装 [Git for Windows](https://gitforwindows.org/) 与 [VC++ 2015](https://github.com/abbodi1406/vcredist/releases) 与 [OpenJDK 11](https://github.com/ojdkbuild/ojdkbuild)

您也可使用 `vcpkg` 编译安装 `openssl` 与 `zlib`

## 配置

复制 `_pm.yml` 到 `pm.yml`.

```yaml
BOT_LANG: 工作语言
BOT_TOKEN: 机器人令牌
BOT_OWNER: 管理员ID
PM_MODE: 运行模式
PM_WHITE_LIST: 白名单列表
LOG_LEVEL: 日志等级, 默认为 INFO
```

### 工作语言

机器人的默认语言, 以及非 `私有` 模式下命令模板的语言 ( 必须在 `BOT_LANG_LIST` 中 ).

### 机器人令牌

相当于账号与密码, 从 [@BotFather](https://t.me/BotFacher) 获取, 参见 https://core.telegram.org/bots#creating-a-new-bot .

### 管理员与白名单 ID

启动机器人后使用 `/id` 获取自己的 ID, 使用 `/id <回复消息 (群组中) / @用户名 / 引用>` 获取他人 ID.

### 运行模式

#### 公开

运行模式值 `public`, 此模式下所有人都可创建机器人.

#### 白名单

运行模式值 `white-list`, 此模式下列表中的用户与您可以创建机器人.

需配置白名单设置项: 
```yaml
PM_WHITE_LIST: 
  - id1
  - id2
  - ...
```

#### 私有

运行模式值 `private`, 此模式下主实例作为私聊机器人.

您仍可创建机器人, 但没有命令模板 (即补全).

## 管理

```shell script
echo "alias pm='bash $PWD/bot.sh'" >> $HOME/.bashrc
source $HOME/.bashrc

# 注册 ./bot.sh 的命令别名 ( pm )
```

```shell script
pm run # 编译安装并进入交互式认证  
pm init # 注册 systemd 服务  
pm <start/stop/restart> # 启动停止  
pm <enable/disable> # 启用禁用 (开机启动)  
pm rebuild # 重新编译  
pm update # 更新  
pm force-update # 强制重新更新
pm upgrade # 更新并重启服务  
pm log # 实时日志  
pm logs # 所有日志
```

`注: 重新编译前请停止服务以避免运行时 jar 文件覆盖导致的错误, 但不同版本之间不需要.`

## 其他

如需更改, 复制 `_bot.conf` 到 `bot.conf`.

```
SERVICE_NAME: systemd 服务名称, 默认 `td-pm`, 修改如果您需要多个实例.
MVN_ARGS: Maven 编译参数.
JAVA_ARGS: JVM 启动参数.
ARGS: 启动参数.
```

## 命令行命令

#### 备份 & 迁移

`pm run --backup [fileName 可选]`

备份所有迁移需要的文件到 tar.xz 包, 解压即可覆盖数据.

您也可以直接打包 `data` 目录, 但包含无用的数据库与文件缓存.

#### 指定配置文件

`pm run --config /path/to/config.yml`

不常用, 但您可以写入 `bot.conf` 的 `args` 中作为默认参数.

## Docker

您可用环境变量 `-e <key>=<value>` 指定配置项, 列表格式为空格分隔.

```
docker run -d --name td-pm \
  -v <数据目录>:/root/data \
  -e BOT_TOKEN=<机器人令牌> \
  -e BOT_LANG=zh_CN \
  -e BOT_OWNER=<管理员ID> \
  -e BOT_MODE=private \
  docker.pkg.github.com/tdbotproject/tdpmbot/td-pm

docker logs td-pm -f -t
```

注: 需要使用 Github 账号登录 

`docker login docker.pkg.github.com -u <您的 Github 用户名> -p <您的 Github AccessToken>`

## 公开实例

[@TdPmBot](https://t.me/TdPmBot)

## 使用

如需帮助，请通过 @TdBotProject 的讨论群组与我们联系.

### 创建新机器人

使用 `/new_bot` 命令进入创建步进程序, 输入完后根据提示发送 [Bot Token](https://core.telegram.org/bots#creating-a-new-bot">) 到机器人即可完成创建, 

您也可以使用该 Token 作为命令参数传入直接创建 ( `/new_bot <BotToken>` ).

创建完成后您需要根据提示启动该机器人并保持不禁用, 否则将无法收到消息.

### 编辑机器人

使用 `/my_bots` 命令得到机器人菜单, 选择要设置的机器人后将得到一个管理菜单.

#### 欢迎消息

即对机器人发送 /start 时回复的消息.

点击 `编辑` 按钮开始设置, 因为消息跨机器人无法转存, 所以您需要转到对应机器人进行设置.  
点击 `重置` 按钮重置回默认欢迎消息.

#### 接入群组

所有消息将被发往目标群组而不是您的私聊.

`仅管理员可操作`: 默认所有群组成员可操作, 开启此项以禁止非管理员操作机器人.

`暂停接入`: 暂停接入到群组, 收到新消息时机器人无法访问接入的群组时也会触发此项.

#### 行为选项

`保留提示`: 不要自动删除操作提示消息.

`双向同步`: 直接复制对方的消息, 而不是每次都转发, 并同步对方的编辑, 删除操作.

`保持回复`: 没有进入 ( `/join` ) 对应会话的情况下保持对消息的回复.

`忽略删除`: 不要同步本方的消息删除, 当同时开启 `保留提示` 时在提示中增加一个删除该消息的按钮.

#### 命令管理

您可以为机器人添加命令, 并为每个命令设置不同的消息内容, 并接收到对方消息所回复之命令.

也可以通过链接 ( start payload, 链接可以在命令设置中找到 ), 点击效果同打开bot并发送命令.
     
格式为 `https://t.me/<botUserName>?start=<command>` (参见 https://core.telegram.org/bots#deep-linking ).

### PM 操作

#### 提示消息

当客人发送消息到机器人, 机器人会为每人每 5 条消息发送一条提示消息给您 ( 包括用户ID 与 引用 ).

`回复这条消息`: 消息将被直接发送给客人.

#### 回复消息

客人的消息将被转发至主人或接入的群组 (如果有设置).

如果启用了 `双向同步`, 此处将直接发送对方的消息的复制, 而不是每次转发消息, 否则如果对方回复的消息存在, 将再发送一条提示消息回复对应的消息.

对客人的消息的可用操作:

`回复这条消息`: 消息将被直接发送给客人或回复对应消息.  

回复对方的消息直接发送消息的复制给客人, 如果没有进入对应的会话, 将不会 `回复` 对应消息, 除非您开启了 `保持回复`.

#### 持续对话

对 `提示消息 / 您发送或收到的消息` 回复 `/join` ( 也可以使用 `对方用户名 / ID / 引用` 作为参数).

进入该对话后所有消息将被 `发送/回复` 到目标对话.

注: 如果接入到了群组, 请确保机器人有访问消息权限 ( `BotFather -> /setprivacy -> Disable` ), 否则无法收到此类与机器人无关的消息.

#### 屏蔽用户

命令为 `/block` 或别名 `/ban`, 用法同上.

屏蔽后会忽略对方发送的所有消息, 使用 `/unblock` 或别名 `/unban` 取消屏蔽, 另: 无法屏蔽自己.

#### 撤回所有消息

命令为 `/recall`, 用法同上.

使用后删除有记录的双方所有消息, 并删除记录, 所以配合屏蔽使用时请先屏蔽.