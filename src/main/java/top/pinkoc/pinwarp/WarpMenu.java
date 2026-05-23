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

public class WarpMenu {

    private final PinWarp plugin;

    private final Inventory inventory;

    private final int page;

    public WarpMenu(
            PinWarp plugin,
            Player player,
            int page
    ) {

        this.plugin = plugin;

        this.page = page;

        this.inventory =
                Bukkit.createInventory(
                        player,
                        54,
                        ChatColor.GREEN +
                                "地标菜单 - " + (page + 1)
                );

        populateMenu();
    }

    // =========================
    // 填充菜单
    // =========================

    private void populateMenu() {

        inventory.clear();

        // =========================
        // 边框
        // =========================

        ItemStack glass =
                new ItemStack(
                        Material.GRAY_STAINED_GLASS_PANE
                );

        ItemMeta glassMeta =
                glass.getItemMeta();

        glassMeta.setDisplayName(" ");

        glass.setItemMeta(glassMeta);

        for (int i = 0; i < 9; i++) {

            inventory.setItem(i, glass);

        }

        // =========================
        // 标题
        // =========================

        ItemStack info =
                new ItemStack(Material.BEACON);

        ItemMeta infoMeta =
                info.getItemMeta();

        infoMeta.setDisplayName(
                ChatColor.AQUA +
                        "PinWarp 地标"
        );

        List<String> infoLore =
                new ArrayList<>();

        infoLore.add(
                ChatColor.GRAY +
                        "点击地标即可传送"
        );

        infoLore.add(
                ChatColor.GRAY +
                        "当前页数: " +
                        (page + 1)
        );

        infoMeta.setLore(infoLore);

        info.setItemMeta(infoMeta);

        inventory.setItem(4, info);

        // =========================
        // 地标分页
        // =========================

        List<Warp> warps =
                new ArrayList<>(
                        plugin.getWarpManager()
                                .getWarps()
                                .values()
                );

        int start =
                page * 21;

        int end =
                Math.min(
                        start + 21,
                        warps.size()
                );

        int[] slots = {

                10,11,12,13,14,15,16,
                19,20,21,22,23,24,25,
                28,29,30,31,32,33,34
        };

        int index = 0;

        for (int i = start;
             i < end;
             i++) {

            Warp warp =
                    warps.get(i);

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

            String ownerName =
                    plugin.getServer()
                            .getOfflinePlayer(
                                    java.util.UUID.fromString(
                                            warp.getOwner()
                                    )
                            )
                            .getName();

            lore.add(
                    ChatColor.GRAY +
                            "创建者: " +
                            ownerName
            );

            lore.add(
                    ChatColor.GREEN +
                            "点击传送"
            );

            meta.setLore(lore);

            item.setItemMeta(meta);

            if (index >= slots.length) {
                break;
            }

            inventory.setItem(
                    slots[index],
                    item
            );

            index++;
        }

        // =========================
        // 上一页
        // =========================

        if (page > 0) {

            ItemStack previous =
                    new ItemStack(
                            Material.ARROW
                    );

            ItemMeta previousMeta =
                    previous.getItemMeta();

            previousMeta.setDisplayName(
                    ChatColor.YELLOW +
                            "上一页"
            );

            previous.setItemMeta(
                    previousMeta
            );

            inventory.setItem(
                    45,
                    previous
            );
        }

        // =========================
        // 下一页
        // =========================

        if (end < warps.size()) {

            ItemStack next =
                    new ItemStack(
                            Material.ARROW
                    );

            ItemMeta nextMeta =
                    next.getItemMeta();

            nextMeta.setDisplayName(
                    ChatColor.YELLOW +
                            "下一页"
            );

            next.setItemMeta(nextMeta);

            inventory.setItem(
                    53,
                    next
            );
        }

        // =========================
        // 创建地标
        // =========================

        ItemStack create =
                new ItemStack(
                        Material.ANVIL
                );

        ItemMeta createMeta =
                create.getItemMeta();

        createMeta.setDisplayName(
                ChatColor.GREEN +
                        "创建地标"
        );

        create.setItemMeta(createMeta);

        inventory.setItem(47, create);

        // =========================
        // 更改图标
        // =========================

        ItemStack iconEdit =
                new ItemStack(
                        Material.OAK_SIGN
                );

        ItemMeta iconMeta =
                iconEdit.getItemMeta();

        iconMeta.setDisplayName(
                ChatColor.AQUA +
                        "更改地标图案"
        );

        iconEdit.setItemMeta(iconMeta);

        inventory.setItem(51, iconEdit);

        // =========================
        // 关闭按钮
        // =========================

        ItemStack close =
                new ItemStack(
                        Material.BARRIER
                );

        ItemMeta closeMeta =
                close.getItemMeta();

        closeMeta.setDisplayName(
                ChatColor.RED +
                        "关闭菜单"
        );

        close.setItemMeta(closeMeta);

        inventory.setItem(49, close);
    }

    // =========================
    // 打开菜单
    // =========================

    public void open(Player player) {

        player.openInventory(inventory);

    }
}