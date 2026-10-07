package top.pinkoc.pinwarp;

import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PinWarp extends JavaPlugin implements TabCompleter {

    private WarpManager warpManager;

    // 正在等待修改图标的玩家
    private final Map<Player, Warp> editingIcon = new HashMap<>();
    private final Map<Player, Warp> editingName = new HashMap<>();
    private final Map<Player, Warp> deletingWarp = new HashMap<>();

    private LangManager langManager;

    public Map<Player, Warp> getEditingName() {
        return editingName;
    }

    public Map<Player, Warp> getDeletingWarp() {
        return deletingWarp;
    }

    public Map<Player, Warp> getEditingIcon() {
        return editingIcon;
    }

    public LangManager getLang() {
        return langManager;
    }

    public WarpManager getWarpManager() {
        return warpManager;
    }

    public NamespacedKey warpKey() {
        return new NamespacedKey(this, "warp");
    }

    @Override
    public void onEnable() {

        // 先加载配置与语言，再加载地标，
        // 避免 loadWarps 打印日志时 getLang() 尚未初始化导致 NPE。
        saveDefaultConfig();

        langManager = new LangManager(this);
        langManager.load();

        warpManager = new WarpManager(this);
        warpManager.loadWarps();

        if (getCommand("pinwarp") != null) {
            getCommand("pinwarp").setExecutor(this);
            getCommand("pinwarp").setTabCompleter(this);
        }

        getServer().getPluginManager().registerEvents(
                new WarpListener(this),
                this
        );

        getLogger().info(getLang().get("pinwarp.loadon"));
    }

    @Override
    public void onDisable() {

        warpManager.saveWarps();

        getLogger().info(getLang().get("pinwarp.loadoff"));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(getLang().get("pinwarp.onlyplayersend"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            openWarpMenu(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create":
                if (args.length < 2) {
                    player.sendMessage(getLang().get("pinwarp.createwarps"));
                    return true;
                }
                String name = args[1];
                warpManager.createWarp(player, name);
                break;
            case "list":
                warpManager.listWarps(player);
                break;
            case "me":
                openMeMenu(player);
                break;
            case "tp":
                if (args.length < 2) {
                    player.sendMessage(getLang().get("pinwarp.teleportwarps"));
                    return true;
                }
                warpManager.teleportToWarp(player, args[1]);
                break;
            case "setprice":
                if (args.length < 3) {
                    player.sendMessage(getLang().get("pinwarp.setprice"));
                    return true;
                }
                try {
                    String priceName = args[1];
                    double price = Double.parseDouble(args[2]);
                    warpManager.setWarpPrice(player, priceName, price);
                } catch (NumberFormatException e) {
                    player.sendMessage(getLang().get("pinwarp.inputprice"));
                }
                break;
            default:
                player.sendMessage(getLang().get("pinwarp.unknown"));
                break;
        }
        return true;
    }

    public void openWarpMenu(Player player) {
        openWarpMenu(player, 0);
    }

    public void openWarpMenu(Player player, int page) {
        new WarpMenu(this, player, page).open(player);
    }

    public void openMeMenu(Player player) {
        openMeMenu(player, 0);
    }

    public void openMeMenu(Player player, int page) {
        new MeMenu(this, player, page).open(player);
    }

    @Override
    public List<String> onTabComplete(
            CommandSender sender,
            Command command,
            String alias,
            String[] args
    ) {

        List<String> list = new ArrayList<>();

        if (args.length == 1) {
            list.add("create");
            list.add("tp");
            list.add("list");
            list.add("setprice");
            list.add("me");
            return list;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("tp")) {
            list.addAll(getWarpManager().getWarps().keySet());
            return list;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("setprice")) {
            list.addAll(getWarpManager().getWarps().keySet());
            return list;
        }

        return list;
    }
}
