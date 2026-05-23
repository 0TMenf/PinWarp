[README.md](https://github.com/user-attachments/files/21705792/README.md)
# PinWarp 插件

PinWarp是一个Minecraft Bukkit插件，允许玩家创建和传送到地标。类似于PlayerWarp插件，但具有独特的功能和界面。

## 功能特点
- 玩家可以创建自己的地标
- 所有玩家可以在大箱子菜单中查看和传送到地标
- 地标创建者可以设置访问所需的金币
- 简单易用的命令系统
- 直观的图形界面

## 命令
- `/pinwarp` - 打开地标菜单
- `/pinwarp create <名称>` - 创建新地标
- `/pinwarp list` - 列出所有地标
- `/pinwarp tp <名称>` - 传送到指定地标
- `/pinwarp setprice <名称> <价格>` - 设置地标访问价格

## 权限
- `pinwarp.use` - 允许使用PinWarp的基本功能（默认开启）
- `pinwarp.admin` - 允许管理所有地标（默认仅OP拥有）

## 安装
1. 下载PinWarp.jar文件
2. 将文件放入服务器的plugins文件夹
3. 重启服务器
4. 享受使用PinWarp!

## 配置
插件会在首次运行时自动创建配置文件。配置文件位于`plugins/PinWarp/`目录下。

## 依赖
- 服务器必须运行Bukkit 1.20.1或更高版本
- 如需使用经济功能，服务器需要安装Vault和兼容的经济插件（如Essentials）
