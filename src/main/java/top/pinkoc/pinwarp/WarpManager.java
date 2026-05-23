package top.pinkoc.pinwarp;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WarpManager {

    private final PinWarp plugin;

    private final Map<String, Warp> warps;

    private final File dataFile;

    public WarpManager(PinWarp plugin) {

        this.plugin = plugin;

        this.warps = new HashMap<>();

        this.dataFile =
                new File(
                        plugin.getDataFolder(),
                        "warps.yml"
                );
    }

    // =========================
    // 加载地标
    // =========================

    public void loadWarps() {

        if (!dataFile.exists()) {
            return;
        }

        YamlConfiguration config =
                YamlConfiguration.loadConfiguration(
                        dataFile
                );

        for (String key : config.getKeys(false)) {

            String name = key;

            String owner =
                    config.getString(
                            key + ".owner"
                    );

            double x =
                    config.getDouble(
                            key + ".x"
                    );

            double y =
                    config.getDouble(
                            key + ".y"
                    );

            double z =
                    config.getDouble(
                            key + ".z"
                    );

            String world =
                    config.getString(
                            key + ".world"
                    );

            float yaw =
                    (float) config.getDouble(
                            key + ".yaw"
                    );

            float pitch =
                    (float) config.getDouble(
                            key + ".pitch"
                    );

            double price =
                    config.getDouble(
                            key + ".price"
                    );

            // ===== 图标 =====

            String icon =
                    config.getString(
                            key + ".icon",
                            "ENDER_PEARL"
                    );

            Warp warp =
                    new Warp(
                            name,
                            owner,
                            new Location(
                                    plugin.getServer()
                                            .getWorld(world),
                                    x,
                                    y,
                                    z,
                                    yaw,
                                    pitch
                            ),
                            price
                    );

            // 设置图标

            warp.setIcon(icon);

            warps.put(name, warp);
        }

        plugin.getLogger().info(
                "已加载 "
                        + warps.size()
                        + " 个地标"
        );
    }

    // =========================
    // 保存地标
    // =========================

    public void saveWarps() {

        if (!plugin.getDataFolder().exists()) {

            plugin.getDataFolder().mkdirs();

        }

        YamlConfiguration config =
                new YamlConfiguration();

        for (Warp warp : warps.values()) {

            String key =
                    warp.getName();

            config.set(
                    key + ".owner",
                    warp.getOwner()
            );

            config.set(
                    key + ".x",
                    warp.getLocation().getX()
            );

            config.set(
                    key + ".y",
                    warp.getLocation().getY()
            );

            config.set(
                    key + ".z",
                    warp.getLocation().getZ()
            );

            config.set(
                    key + ".world",
                    warp.getLocation()
                            .getWorld()
                            .getName()
            );

            config.set(
                    key + ".yaw",
                    warp.getLocation()
                            .getYaw()
            );

            config.set(
                    key + ".pitch",
                    warp.getLocation()
                            .getPitch()
            );

            config.set(
                    key + ".price",
                    warp.getPrice()
            );

            // ===== 保存图标 =====

            config.set(
                    key + ".icon",
                    warp.getIcon()
            );
        }

        try {

            config.save(dataFile);

            plugin.getLogger().info(
                    "已保存 "
                            + warps.size()
                            + " 个地标"
            );

        } catch (IOException e) {

            plugin.getLogger().severe(
                    "保存地标失败: "
                            + e.getMessage()
            );
        }
    }

    // =========================
    // 创建地标
    // =========================

    public void createWarp(
            Player player,
            String name
    ) {

        if (warps.containsKey(name)) {

            player.sendMessage(
                    ChatColor.RED +
                            "地标名称已存在!"
            );

            return;
        }

        Warp warp =
                new Warp(
                        name,
                        player.getUniqueId()
                                .toString(),
                        player.getLocation(),
                        0
                );

        // 默认图标

        warp.setIcon("ENDER_PEARL");

        warps.put(name, warp);

        saveWarps();

        player.sendMessage(
                ChatColor.GREEN +
                        "地标 '"
                        + name +
                        "' 创建成功!"
        );
    }

    // =========================
    // 地标列表
    // =========================

    public void listWarps(Player player) {

        if (warps.isEmpty()) {

            player.sendMessage(
                    ChatColor.RED +
                            "没有可用地标!"
            );

            return;
        }

        player.sendMessage(
                ChatColor.GREEN +
                        "===== 地标列表 ====="
        );

        for (Warp warp : warps.values()) {

            String ownerName =
                    plugin.getServer()
                            .getOfflinePlayer(
                                    UUID.fromString(
                                            warp.getOwner()
                                    )
                            )
                            .getName();

            player.sendMessage(
                    ChatColor.YELLOW
                            + warp.getName()
                            + ChatColor.GRAY
                            + " 创建者: "
                            + ownerName
            );
        }
    }

    // =========================
    // 传送
    // =========================

    public void teleportToWarp(
            Player player,
            String name
    ) {

        Warp warp =
                warps.get(name);

        if (warp == null) {

            player.sendMessage(
                    ChatColor.RED +
                            "找不到该地标!"
            );

            return;
        }

        player.teleport(
                warp.getLocation()
        );

        player.sendMessage(
                ChatColor.GREEN +
                        "已传送到 "
                        + warp.getName()
        );
    }

    // =========================
    // 设置价格
    // =========================

    public void setWarpPrice(
            Player player,
            String name,
            double price
    ) {

        Warp warp =
                warps.get(name);

        if (warp == null) {

            player.sendMessage(
                    ChatColor.RED +
                            "找不到该地标!"
            );

            return;
        }

        if (!warp.getOwner()
                .equals(
                        player.getUniqueId()
                                .toString()
                )
        ) {

            player.sendMessage(
                    ChatColor.RED +
                            "你不是该地标拥有者!"
            );

            return;
        }

        warp.setPrice(price);

        saveWarps();

        player.sendMessage(
                ChatColor.GREEN +
                        "价格修改成功!"
        );
    }

    // =========================
    // 获取全部地标
    // =========================

    public Map<String, Warp> getWarps() {

        return warps;

    }
}