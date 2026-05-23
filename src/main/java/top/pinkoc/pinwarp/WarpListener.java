package top.pinkoc.pinwarp;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

public class WarpListener implements Listener {

    private final PinWarp plugin;

    public WarpListener(PinWarp plugin) {

        this.plugin = plugin;

    }

    // =========================
    // 获取页数
    // =========================

    private int getPage(String title) {

        try {

            String clean =
                    ChatColor.stripColor(title);

            String[] split =
                    clean.split("-");

            return Integer.parseInt(
                    split[1].trim()
            ) - 1;

        } catch (Exception e) {

            return 0;

        }
    }

    // =========================
    // GUI 点击事件
    // =========================

    @EventHandler
    public void onInventoryClick(
            InventoryClickEvent event
    ) {

        Player player =
                (Player) event.getWhoClicked();

        ItemStack clickedItem =
                event.getCurrentItem();

        if (clickedItem == null
                || clickedItem.getType().isAir()) {
            return;
        }

        String title =
                event.getView().getTitle();

        // =========================
        // 主菜单
        // =========================

        if (title.startsWith(
                ChatColor.GREEN +
                        "地标菜单")) {

            event.setCancelled(true);

            int slot =
                    event.getRawSlot();

            // =========================
            // 上一页
            // =========================

            if (slot == 45
                    && clickedItem.getType()
                    == Material.ARROW) {

                WarpMenu menu =
                        new WarpMenu(
                                plugin,
                                player,
                                getPage(title) - 1
                        );

                menu.open(player);

                return;
            }

            // =========================
            // 下一页
            // =========================

            if (slot == 53
                    && clickedItem.getType()
                    == Material.ARROW) {

                WarpMenu menu =
                        new WarpMenu(
                                plugin,
                                player,
                                getPage(title) + 1
                        );

                menu.open(player);

                return;
            }

            // =========================
            // 关闭按钮
            // =========================

            if (slot == 49
                    && clickedItem.getType()
                    == Material.BARRIER) {

                player.closeInventory();

                return;
            }

            // =========================
            // 创建地标
            // =========================

            if (slot == 47
                    && clickedItem.getType()
                    == Material.ANVIL) {

                player.closeInventory();

                player.sendMessage(
                        ChatColor.YELLOW +
                                "使用 /pinwarp create 名称 创建地标"
                );

                return;
            }

            // =========================
            // 更改图标
            // =========================

            if (slot == 51
                    && clickedItem.getType()
                    == Material.OAK_SIGN) {

                IconMenu menu =
                        new IconMenu(
                                plugin,
                                player
                        );

                menu.open(player);

                return;
            }

            // =========================
            // 点击地标传送
            // =========================

            if (clickedItem.getItemMeta()
                    == null) {
                return;
            }

            String warpName =
                    ChatColor.stripColor(
                            clickedItem
                                    .getItemMeta()
                                    .getDisplayName()
                    );

            Warp warp =
                    plugin.getWarpManager()
                            .getWarps()
                            .get(warpName);

            if (warp == null) {
                return;
            }

            plugin.getWarpManager()
                    .teleportToWarp(
                            player,
                            warpName
                    );

            player.closeInventory();

            return;
        }

        // =========================
        // 图标编辑菜单
        // =========================

        if (title.equals(
                ChatColor.BLUE +
                        "选择要修改图标的地标")) {

            event.setCancelled(true);

            if (clickedItem.getItemMeta()
                    == null) {
                return;
            }

            String warpName =
                    ChatColor.stripColor(
                            clickedItem
                                    .getItemMeta()
                                    .getDisplayName()
                    );

            Warp warp =
                    plugin.getWarpManager()
                            .getWarps()
                            .get(warpName);

            if (warp == null) {
                return;
            }

            // 只能修改自己的地标

            if (!warp.getOwner().equals(
                    player.getUniqueId().toString()
            )) {

                player.sendMessage(
                        ChatColor.RED +
                                "你只能修改自己的地标图案!"
                );

                return;
            }

            plugin.getEditingIcon()
                    .put(player, warp);

            player.closeInventory();

            player.sendMessage(
                    ChatColor.YELLOW +
                            "手拿想设置的物品后发送 T"
            );
        }
    }

    // =========================
    // 聊天输入监听
    // =========================

    @EventHandler
    public void onChat(
            AsyncPlayerChatEvent event
    ) {

        Player player =
                event.getPlayer();

        if (!plugin.getEditingIcon()
                .containsKey(player)) {
            return;
        }

        if (!event.getMessage()
                .equalsIgnoreCase("T")) {
            return;
        }

        event.setCancelled(true);

        Warp warp =
                plugin.getEditingIcon()
                        .remove(player);

        ItemStack hand =
                player.getInventory()
                        .getItemInMainHand();

        if (hand == null
                || hand.getType().isAir()) {

            player.sendMessage(
                    ChatColor.RED +
                            "请手持一个物品!"
            );

            return;
        }

        warp.setIcon(
                hand.getType().name()
        );

        player.sendMessage(
                ChatColor.GREEN +
                        "已修改地标图标!"
        );
    }

    // =========================
    // GUI关闭事件
    // =========================

    @EventHandler
    public void onInventoryClose(
            InventoryCloseEvent event
    ) {

        if (event.getView()
                .getTitle()
                .startsWith(
                        ChatColor.GREEN +
                                "地标菜单")) {

            // 可选逻辑

        }
    }
}