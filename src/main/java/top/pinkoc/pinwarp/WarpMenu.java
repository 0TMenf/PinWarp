package top.pinkoc.pinwarp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class WarpMenu {

    private static final int PAGE_SIZE = MenuUtil.WARP_SLOTS.length;

    private final PinWarp plugin;

    private final Inventory inventory;

    private final int page;

    public WarpMenu(PinWarp plugin, Player player, int page) {

        this.plugin = plugin;
        this.page = page;

        this.inventory =
                Bukkit.createInventory(
                        player,
                        54,
                        ChatColor.BLACK +
                                plugin.getLang().get("menu.main.title") +
                                " " +
                                (page + 1)
                );

        populate();
    }

    private void populate() {

        inventory.clear();

        // 边框
        MenuUtil.fillPanes(inventory);

        // 地标介绍（slot 4）
        ItemStack info =
                MenuUtil.button(
                        Material.PAINTING,
                        plugin.getLang().color("menu.main.info"),
                        plugin.getLang().colorList("menu.main.infoLore")
                );
        inventory.setItem(4, info);

        // 地标（按访问量从高到低排序）
        List<Warp> warps = plugin.getWarpManager().getSortedWarps();

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

        // 上一页
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

        // 下一页
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

        // 创建地标
        inventory.setItem(
                45,
                MenuUtil.button(
                        Material.NETHER_WART,
                        plugin.getLang().color("menu.main.create")
                )
        );

        // 重命名地标
        inventory.setItem(
                48,
                MenuUtil.button(
                        Material.OAK_SIGN,
                        plugin.getLang().color("menu.main.rename")
                )
        );

        // 更改图标
        inventory.setItem(
                49,
                MenuUtil.button(
                        Material.ITEM_FRAME,
                        plugin.getLang().color("menu.main.icon")
                )
        );

        // 我的地标
        inventory.setItem(
                50,
                MenuUtil.button(
                        Material.PLAYER_HEAD,
                        plugin.getLang().color("menu.main.me")
                )
        );

        // 删除地标
        inventory.setItem(
                53,
                MenuUtil.button(
                        Material.BARRIER,
                        plugin.getLang().color("menu.main.delete")
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
