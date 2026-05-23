package top.pinkoc.pinwarp;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.command.TabCompleter;
import java.util.List;
import java.util.ArrayList;

public class PinWarp extends JavaPlugin implements TabCompleter {
    private WarpManager warpManager;
    // 正在等待修改图标的玩家
    private final Map<Player, Warp> editingIcon = new HashMap<>();


    @Override
    public void onEnable() {
        // 初始化地标管理器
        warpManager = new WarpManager(this);
        // 加载已保存的地标
        warpManager.loadWarps();
        // 注册命令执行器
        this.getCommand("pinwarp").setExecutor(this);
        this.getCommand("pinwarp").setTabCompleter(this);
        // 注册事件监听器
        getServer().getPluginManager().registerEvents(new WarpListener(this), this);
        // 在控制台输出绿色启动信息
        getLogger().info(ChatColor.GREEN + "PinWarp插件已启动!");
    }

    @Override
    public void onDisable() {
        // 保存地标数据
        warpManager.saveWarps();
        getLogger().info(ChatColor.RED + "PinWarp插件已关闭!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "只有玩家可以使用此命令!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            // 打开地标菜单
            openWarpMenu(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "用法: /pinwarp create <名称>");
                    return true;
                }
                String name = args[1];
                warpManager.createWarp(player, name);
                break;
            case "list":
                warpManager.listWarps(player);
                break;
            case "tp":
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "用法: /pinwarp tp <名称>");
                    return true;
                }
                String warpName = args[1];
                warpManager.teleportToWarp(player, warpName);
                break;
            case "setprice":
                if (args.length < 3) {
                    player.sendMessage(ChatColor.RED + "用法: /pinwarp setprice <名称> <价格>");
                    return true;
                }
                try {
                    String priceName = args[1];
                    double price = Double.parseDouble(args[2]);
                    warpManager.setWarpPrice(player, priceName, price);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "请输入有效的价格!");
                }
                break;
            default:
                player.sendMessage(ChatColor.RED + "未知命令! 使用 /pinwarp <create|list|tp|setprice>");
                break;
        }
        return true;
    }

    public void openWarpMenu(Player player) {
        WarpMenu menu = new WarpMenu(this, player, 0);
        menu.open(player);
    }

    public Map<Player, Warp> getEditingIcon() {
        return editingIcon;
    }

    public WarpManager getWarpManager() {
        return warpManager;
    }
    @Override
    public List<String> onTabComplete(
            CommandSender sender,
            Command command,
            String alias,
            String[] args
    ) {

        List<String> list =
                new ArrayList<>();

        // /pinwarp <这里>

        if (args.length == 1) {

            list.add("create");

            list.add("tp");

            list.add("list");

            list.add("setprice");

            return list;
        }

        // /pinwarp tp <这里>

        if (args.length == 2
                && args[0].equalsIgnoreCase("tp")) {

            list.addAll(
                    getWarpManager()
                            .getWarps()
                            .keySet()
            );

            return list;
        }

        // /pinwarp setprice <这里>

        if (args.length == 2
                && args[0].equalsIgnoreCase("setprice")) {

            list.addAll(
                    getWarpManager()
                            .getWarps()
                            .keySet()
            );

            return list;
        }

        return list;
    }
}