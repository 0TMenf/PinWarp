package top.pinkoc.pinwarp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class MeMenu {

    private static final int PAGE_SIZE = MenuUtil.WARP_SLOTS.length;

    private final PinWarp plugin;

    private final Player player;

    private final Inventory inventory;

    private final int page;

    public MeMenu(PinWarp plugin, Player player, int page) {

        this.plugin = plugin;
        this.player = player;
        this.page = page;

        this.inventory =
                Bukkit.createInventory(
                        player,
                        54,
                        ChatColor.BLACK +
                                plugin.getLang().get("menu.me.title") +
                                " " +
                                (page + 1)
                );

        populate();
    }

    private void populate() {

        inventory.clear();

        MenuUtil.fillPanes(inventory);

        List<Warp> warps = plugin.getWarpManager().getMyWarps(player);

        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, warps.size());

        int idx = 0;
        for (int i = start; i < end; i++) {
            if (idx >= MenuUtil.WARP_SLOTS.length) {
                break;
            }
            inventory.setItem(
                    MenuUtil.WARP_SLOTS[idx],
                    MenuUtil.warpItem(plugin, warps.get(i))
            );
            idx++;
        }

        int totalPages = (int) Math.ceil((double) warps.size() / PAGE_SIZE);
        if (totalPages < 1) {
            totalPages = 1;
        }

        if (page > 0) {
            inventory.setItem(
                    47,
                    navButton(
                            Material.PAPER,
                            plugin.getLang().color("menu.main.previous"),
                            page + 1,
                            totalPages
                    )
            );
        }

        if (end < warps.size()) {
            inventory.setItem(
                    51,
                    navButton(
                            Material.PAPER,
                            plugin.getLang().color("menu.main.next"),
                            page + 1,
                            totalPages
                    )
            );
        }

        // 返回主菜单
        inventory.setItem(
                49,
                MenuUtil.button(
                        Material.PLAYER_HEAD,
                        plugin.getLang().color("menu.me.back")
                )
        );
    }

    private ItemStack navButton(Material material, String name, int displayPage, int totalPages) {
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.translateAlternateColorCodes(
                '&',
                "&8▪ &7当前页码 &a" + displayPage
        ));
        lore.add(ChatColor.translateAlternateColorCodes(
                '&',
                "&8▪ &7总页码数 &a" + totalPages
        ));
        return MenuUtil.button(material, name, lore);
    }

    public void open(Player player) {
        player.openInventory(inventory);
    }
}
