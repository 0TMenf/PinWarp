package top.pinkoc.pinwarp;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public final class MenuUtil {

    // 地标格子（与 PlayerWarp open.yml 一致：每页 10 个）
    public static final int[] WARP_SLOTS = {20, 21, 22, 23, 24, 29, 30, 31, 32, 33};

    // 边框分割板（黑色玻璃板）
    public static final int[] PANE_SLOTS = {0, 1, 2, 3, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 46, 52};

    private MenuUtil() {
    }

    public static ItemStack paneItem() {
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(" ");
        item.setItemMeta(meta);
        return item;
    }

    public static void fillPanes(Inventory inventory) {
        for (int slot : PANE_SLOTS) {
            inventory.setItem(slot, paneItem());
        }
    }

    public static ItemStack button(Material material, String name) {
        return button(material, name, null);
    }

    public static ItemStack button(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (lore != null && !lore.isEmpty()) {
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        return item;
    }

    // 解析图标材质
    public static Material iconMaterial(Warp warp) {
        Material material;
        try {
            material = Material.valueOf(warp.getIcon());
        } catch (Exception e) {
            material = Material.ENDER_PEARL;
        }
        if (material == Material.AIR) {
            material = Material.ENDER_PEARL;
        }
        return material;
    }

    // 地标展示物品：写入 PDC 以便点击时精确识别
    public static ItemStack warpItem(PinWarp plugin, Warp warp) {
        return warpItem(plugin, warp, plugin.getLang().color("menu.main.hint"));
    }

    // 带自定义提示语的地标展示物品
    public static ItemStack warpItem(PinWarp plugin, Warp warp, String hint) {
        ItemStack item = new ItemStack(iconMaterial(warp));
        ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&a" + warp.getName()));

        meta.getPersistentDataContainer().set(
                plugin.warpKey(),
                PersistentDataType.STRING,
                warp.getName()
        );

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(plugin.getLang().color("menu.main.visits") + warp.getVisits());
        lore.add(plugin.getLang().color("menu.main.owner") + plugin.getWarpManager().getOwnerName(warp));
        lore.add(plugin.getLang().color("menu.main.price") + warp.getPrice());
        lore.add("");
        lore.add(hint);

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    // 从物品读取地标名（优先 PDC，回退到展示名）
    public static String warpNameFromItem(PinWarp plugin, ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return null;
        }
        String name = item.getItemMeta().getPersistentDataContainer()
                .get(plugin.warpKey(), PersistentDataType.STRING);
        if (name == null) {
            String display = item.getItemMeta().getDisplayName();
            if (display != null) {
                name = ChatColor.stripColor(display);
            }
        }
        return name;
    }
}
