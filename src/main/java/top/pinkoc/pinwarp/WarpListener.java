package top.pinkoc.pinwarp;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

public class WarpListener implements Listener {

    private final PinWarp plugin;

    public WarpListener(PinWarp plugin) {
        this.plugin = plugin;
    }

    // 从标题提取页码（标题格式："标题 N"，返回索引 0 起）
    private int getPage(String title) {
        String clean = ChatColor.stripColor(title);
        if (clean == null) {
            return 0;
        }
        clean = clean.trim();
        int lastSpace = clean.lastIndexOf(' ');
        if (lastSpace >= 0) {
            try {
                return Integer.parseInt(clean.substring(lastSpace + 1).trim()) - 1;
            } catch (NumberFormatException ignored) {
            }
        }
        return 0;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();

        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType().isAir()) {
            return;
        }

        String title = event.getView().getTitle();
        int slot = event.getRawSlot();

        // =========================
        // 重命名菜单
        // =========================

        if (title.equals(
                ChatColor.BLUE +
                        plugin.getLang().get("menu.rename.title"))) {

            event.setCancelled(true);

            String warpName = MenuUtil.warpNameFromItem(plugin, clickedItem);

            if (warpName == null || plugin.getWarpManager().getWarp(warpName) == null) {
                return;
            }

            plugin.getEditingName().put(player, plugin.getWarpManager().getWarp(warpName));

            player.closeInventory();
            player.sendMessage(ChatColor.YELLOW + plugin.getLang().get("listener.rename.input"));

            return;
        }

        // =========================
        // 删除菜单
        // =========================

        if (title.equals(
                ChatColor.BLUE +
                        plugin.getLang().get("menu.delete.title"))) {

            event.setCancelled(true);

            String warpName = MenuUtil.warpNameFromItem(plugin, clickedItem);

            if (warpName == null || plugin.getWarpManager().getWarp(warpName) == null) {
                return;
            }

            plugin.getDeletingWarp().put(player, plugin.getWarpManager().getWarp(warpName));

            player.closeInventory();
            player.sendMessage(ChatColor.RED + plugin.getLang().get("listener.delete.confirm"));

            return;
        }

        // =========================
        // 图标菜单
        // =========================

        if (title.startsWith(
                ChatColor.BLUE +
                        plugin.getLang().get("menu.icon.title"))) {

            event.setCancelled(true);

            String warpName = MenuUtil.warpNameFromItem(plugin, clickedItem);

            if (warpName == null || plugin.getWarpManager().getWarp(warpName) == null) {
                return;
            }

            plugin.getEditingIcon().put(player, plugin.getWarpManager().getWarp(warpName));

            player.closeInventory();
            player.sendMessage(ChatColor.YELLOW + plugin.getLang().get("listener.icon.input"));

            return;
        }

        // =========================
        // 我的地标菜单
        // =========================

        if (title.startsWith(
                ChatColor.BLACK +
                        plugin.getLang().get("menu.me.title"))) {

            event.setCancelled(true);

            if (slot == 49) {
                plugin.openWarpMenu(player);
                return;
            }

            if (slot == 47 && clickedItem.getType() == Material.PAPER) {
                plugin.openMeMenu(player, getPage(title) - 1);
                return;
            }

            if (slot == 51 && clickedItem.getType() == Material.PAPER) {
                plugin.openMeMenu(player, getPage(title) + 1);
                return;
            }

            String warpName = MenuUtil.warpNameFromItem(plugin, clickedItem);

            if (warpName == null) {
                return;
            }

            plugin.getWarpManager().teleportToWarp(player, warpName);
            player.closeInventory();
            return;
        }

        // =========================
        // 主菜单
        // =========================

        if (!title.startsWith(
                ChatColor.BLACK +
                        plugin.getLang().get("menu.main.title"))) {
            return;
        }

        event.setCancelled(true);

        // 创建地标
        if (slot == 45 && clickedItem.getType() == Material.NETHER_WART) {
            player.closeInventory();
            player.sendMessage(ChatColor.YELLOW + plugin.getLang().get("listener.create.help"));
            return;
        }

        // 上一页
        if (slot == 47 && clickedItem.getType() == Material.PAPER) {
            plugin.openWarpMenu(player, getPage(title) - 1);
            return;
        }

        // 下一页
        if (slot == 51 && clickedItem.getType() == Material.PAPER) {
            plugin.openWarpMenu(player, getPage(title) + 1);
            return;
        }

        // 重命名
        if (slot == 48 && clickedItem.getType() == Material.OAK_SIGN) {
            new RenameMenu(plugin, player).open(player);
            return;
        }

        // 更改图标
        if (slot == 49 && clickedItem.getType() == Material.ITEM_FRAME) {
            new IconMenu(plugin, player, 0).open(player);
            return;
        }

        // 我的地标
        if (slot == 50 && clickedItem.getType() == Material.PLAYER_HEAD) {
            plugin.openMeMenu(player);
            return;
        }

        // 删除地标
        if (slot == 53 && clickedItem.getType() == Material.BARRIER) {
            new DeleteMenu(plugin, player).open(player);
            return;
        }

        // 点击地标 -> 传送
        String warpName = MenuUtil.warpNameFromItem(plugin, clickedItem);

        if (warpName == null) {
            return;
        }

        plugin.getWarpManager().teleportToWarp(player, warpName);
        player.closeInventory();
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();
        String msg = event.getMessage();

        // 图标修改
        if (plugin.getEditingIcon().containsKey(player)) {

            if (!msg.equalsIgnoreCase("T")) {
                return;
            }

            event.setCancelled(true);

            Warp warp = plugin.getEditingIcon().remove(player);

            ItemStack hand = player.getInventory().getItemInMainHand();

            if (hand == null || hand.getType().isAir()) {
                player.sendMessage(plugin.getLang().get("listener.icon.hand"));
                return;
            }

            warp.setIcon(hand.getType().name());
            plugin.getWarpManager().saveWarps();

            player.sendMessage(plugin.getLang().get("listener.icon.success"));
            return;
        }

        // 重命名
        if (plugin.getEditingName().containsKey(player)) {

            event.setCancelled(true);

            Warp warp = plugin.getEditingName().remove(player);

            boolean ok = plugin.getWarpManager().renameWarp(warp.getName(), msg);

            if (!ok) {
                player.sendMessage(ChatColor.RED + plugin.getLang().get("listener.rename.exists"));
                return;
            }

            player.sendMessage(plugin.getLang().get("listener.rename.success"));
            return;
        }

        // 删除
        if (plugin.getDeletingWarp().containsKey(player)) {

            event.setCancelled(true);

            if (!msg.equalsIgnoreCase("YES")) {
                player.sendMessage(plugin.getLang().get("listener.delete.cancel"));
                plugin.getDeletingWarp().remove(player);
                return;
            }

            Warp warp = plugin.getDeletingWarp().remove(player);

            plugin.getWarpManager().deleteWarp(warp.getName());

            player.sendMessage(plugin.getLang().get("listener.delete.success"));
        }
    }
}
