package top.pinkoc.pinwarp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DeleteMenu {

    private final PinWarp plugin;

    private final Inventory inventory;

    public DeleteMenu(PinWarp plugin, Player player) {

        this.plugin = plugin;

        this.inventory =
                Bukkit.createInventory(
                        player,
                        54,
                        ChatColor.BLUE +
                                plugin.getLang().get("menu.delete.title")
                );

        populate(player);
    }

    private void populate(Player player) {

        inventory.clear();

        MenuUtil.fillPanes(inventory);

        List<Warp> warps = plugin.getWarpManager().getMyWarps(player);

        int idx = 0;
        for (Warp warp : warps) {
            if (idx >= MenuUtil.WARP_SLOTS.length) {
                break;
            }
            ItemStack item = MenuUtil.warpItem(
                    plugin,
                    warp,
                    plugin.getLang().color("menu.delete.click")
            );
            inventory.setItem(MenuUtil.WARP_SLOTS[idx], item);
            idx++;
        }
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }
}
