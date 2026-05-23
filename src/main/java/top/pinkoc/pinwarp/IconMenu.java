package top.pinkoc.pinwarp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class IconMenu {

    private final PinWarp plugin;

    private final Inventory inventory;

    public IconMenu(
            PinWarp plugin,
            Player player
    ) {

        this.plugin = plugin;

        this.inventory =
                Bukkit.createInventory(
                        player,
                        54,
                        ChatColor.BLUE +
                                "选择要修改图标的地标"
                );

        populate(player);
    }

    // =========================
    // 填充菜单
    // =========================

    private void populate(Player player) {

        inventory.clear();

        int slot = 0;

        for (Warp warp :
                plugin.getWarpManager()
                        .getWarps()
                        .values()) {

            // 只显示自己的地标

            if (!warp.getOwner().equals(
                    player.getUniqueId().toString()
            )) {

                continue;
            }

            Material material;

            try {

                material =
                        Material.valueOf(
                                warp.getIcon()
                        );

            } catch (Exception e) {

                material =
                        Material.ENDER_PEARL;
            }

            ItemStack item =
                    new ItemStack(material);

            ItemMeta meta =
                    item.getItemMeta();

            meta.setDisplayName(
                    ChatColor.YELLOW +
                            warp.getName()
            );

            List<String> lore =
                    new ArrayList<>();

            lore.add(
                    ChatColor.GRAY +
                            "左键点击修改图标"
            );

            lore.add("");

            lore.add(
                    ChatColor.AQUA +
                            "当前图标: " +
                            material.name()
            );

            meta.setLore(lore);

            item.setItemMeta(meta);

            inventory.setItem(
                    slot,
                    item
            );

            slot++;

            // 防止超出箱子

            if (slot >= 54) {
                break;
            }
        }
    }

    // =========================
    // 打开菜单
    // =========================

    public void open(Player player) {

        player.openInventory(inventory);

    }
}