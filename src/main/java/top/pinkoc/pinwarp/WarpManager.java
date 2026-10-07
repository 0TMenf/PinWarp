package top.pinkoc.pinwarp;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WarpManager {

    private final PinWarp plugin;

    // 使用 LinkedHashMap 保持地标的固定插入顺序，
    // 避免新建地标时 HashMap 重哈希导致原有地标顺序被打乱。
    private final Map<String, Warp> warps;

    private final File dataFile;

    public WarpManager(PinWarp plugin) {

        this.plugin = plugin;
        this.warps = new LinkedHashMap<>();
        this.dataFile =
                new File(
                        plugin.getDataFolder(),
                        "warps.yml"
                );
    }

    public void deleteWarp(String name) {

        warps.remove(name);
        saveWarps();
    }

    // =========================
    // 加载地标
    // =========================

    public void loadWarps() {

        if (!dataFile.exists()) {
            return;
        }

        YamlConfiguration config =
                YamlConfiguration.loadConfiguration(dataFile);

        for (String key : config.getKeys(false)) {

            String owner =
                    config.getString(key + ".owner");

            double x =
                    config.getDouble(key + ".x");

            double y =
                    config.getDouble(key + ".y");

            double z =
                    config.getDouble(key + ".z");

            String world =
                    config.getString(key + ".world");

            float yaw =
                    (float) config.getDouble(key + ".yaw");

            float pitch =
                    (float) config.getDouble(key + ".pitch");

            double price =
                    config.getDouble(key + ".price");

            String icon =
                    config.getString(key + ".icon", "ENDER_PEARL");

            int visits =
                    config.getInt(key + ".visits", 0);

            Warp warp =
                    new Warp(
                            key,
                            owner,
                            new Location(
                                    plugin.getServer().getWorld(world),
                                    x, y, z,
                                    yaw, pitch
                            ),
                            price
                    );

            warp.setIcon(icon);
            warp.setVisits(visits);

            warps.put(key, warp);
        }

        plugin.getLogger().info(
                plugin.getLang().get("manager.load")
                        .replace("%count%", String.valueOf(warps.size()))
        );
    }

    // =========================
    // 保存地标
    // =========================

    public void saveWarps() {

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        YamlConfiguration config = new YamlConfiguration();

        for (Warp warp : warps.values()) {

            String key = warp.getName();

            config.set(key + ".owner", warp.getOwner());
            config.set(key + ".x", warp.getLocation().getX());
            config.set(key + ".y", warp.getLocation().getY());
            config.set(key + ".z", warp.getLocation().getZ());
            config.set(key + ".world", warp.getLocation().getWorld().getName());
            config.set(key + ".yaw", warp.getLocation().getYaw());
            config.set(key + ".pitch", warp.getLocation().getPitch());
            config.set(key + ".price", warp.getPrice());
            config.set(key + ".icon", warp.getIcon());
            config.set(key + ".visits", warp.getVisits());
        }

        try {
            config.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe(
                    plugin.getLang().get("manager.save-failed")
                            .replace("%error%", String.valueOf(e.getMessage()))
            );
        }
    }

    // =========================
    // 创建地标
    // =========================

    public void createWarp(Player player, String name) {

        if (warps.containsKey(name)) {
            player.sendMessage(
                    ChatColor.RED +
                            plugin.getLang().get("manager.create.exists")
            );
            return;
        }

        Warp warp =
                new Warp(
                        name,
                        player.getUniqueId().toString(),
                        player.getLocation(),
                        0
                );

        warp.setIcon("ENDER_PEARL");

        // 追加到末尾，不打乱已有地标顺序
        warps.put(name, warp);

        saveWarps();

        player.sendMessage(
                ChatColor.GREEN +
                        plugin.getLang().get("manager.create.success")
                                .replace("%warp%", name)
        );
    }

    // =========================
    // 地标列表（命令）
    // =========================

    public void listWarps(Player player) {

        if (warps.isEmpty()) {
            player.sendMessage(
                    ChatColor.RED +
                            plugin.getLang().get("manager.list.empty")
            );
            return;
        }

        player.sendMessage(
                ChatColor.GREEN +
                        plugin.getLang().get("manager.list.title")
        );

        for (Warp warp : getSortedWarps()) {
            player.sendMessage(
                    plugin.getLang().get("manager.list.item")
                            .replace("%warp%", warp.getName())
                            .replace("%owner%", getOwnerName(warp))
                            .replace("%visits%", String.valueOf(warp.getVisits()))
            );
        }
    }

    // =========================
    // 传送
    // =========================

    public void teleportToWarp(Player player, String name) {

        Warp warp = warps.get(name);

        if (warp == null) {
            player.sendMessage(
                    ChatColor.RED +
                            plugin.getLang().get("manager.teleport.notfound")
            );
            return;
        }

        Location loc = warp.getLocation();

        if (loc.getWorld() == null) {
            player.sendMessage(
                    ChatColor.RED +
                            plugin.getLang().get("manager.teleport.notfound")
            );
            return;
        }

        player.teleport(loc);

        // 记录访问量（地标流量）
        warp.incrementVisits();
        saveWarps();

        player.sendMessage(
                ChatColor.GREEN +
                        plugin.getLang().get("manager.teleport.success")
                                .replace("%warp%", warp.getName())
        );
    }

    // =========================
    // 设置价格
    // =========================

    public void setWarpPrice(Player player, String name, double price) {

        Warp warp = warps.get(name);

        if (warp == null) {
            player.sendMessage(
                    ChatColor.RED +
                            plugin.getLang().get("manager.price.notfound")
            );
            return;
        }

        if (!warp.getOwner().equals(player.getUniqueId().toString())) {
            player.sendMessage(
                    ChatColor.RED +
                            plugin.getLang().get("manager.price.notowner")
            );
            return;
        }

        warp.setPrice(price);
        saveWarps();

        player.sendMessage(
                ChatColor.GREEN +
                        plugin.getLang().get("manager.price.success")
        );
    }

    // =========================
    // 重命名
    // =========================

    public boolean renameWarp(String oldName, String newName) {

        if (oldName.equalsIgnoreCase(newName)) {
            return true;
        }

        if (warps.containsKey(newName)) {
            return false;
        }

        Warp warp = warps.remove(oldName);

        if (warp == null) {
            return false;
        }

        Warp newWarp =
                new Warp(
                        newName,
                        warp.getOwner(),
                        warp.getLocation(),
                        warp.getPrice()
                );

        newWarp.setIcon(warp.getIcon());
        newWarp.setVisits(warp.getVisits());

        warps.put(newName, newWarp);

        saveWarps();
        return true;
    }

    // =========================
    // 排序与查询
    // =========================

    // 按访问量从高到低排序；访问量相同时保持固定插入顺序（稳定排序）
    public List<Warp> getSortedWarps() {
        List<Warp> list = new ArrayList<>(warps.values());
        list.sort(new Comparator<Warp>() {
            @Override
            public int compare(Warp a, Warp b) {
                return Integer.compare(b.getVisits(), a.getVisits());
            }
        });
        return list;
    }

    public List<Warp> getMyWarps(Player player) {
        String uuid = player.getUniqueId().toString();
        List<Warp> list = new ArrayList<>();
        for (Warp warp : getSortedWarps()) {
            if (uuid.equals(warp.getOwner())) {
                list.add(warp);
            }
        }
        return list;
    }

    public String getOwnerName(Warp warp) {
        try {
            OfflinePlayer op =
                    plugin.getServer().getOfflinePlayer(
                            UUID.fromString(warp.getOwner())
                    );
            if (op != null && op.getName() != null) {
                return op.getName();
            }
        } catch (IllegalArgumentException ignored) {
        }
        return "未知";
    }

    public Warp getWarp(String name) {
        return warps.get(name);
    }

    public Map<String, Warp> getWarps() {
        return warps;
    }
}
