package top.pinkoc.pinwarp;

import org.bukkit.Location;

public class Warp {

    private final String name;

    private final String owner;

    private final Location location;

    private double price;

    // 新增：图标材质
    private String icon;

    public Warp(String name,
                String owner,
                Location location,
                double price) {

        this.name = name;

        this.owner = owner;

        this.location = location;

        this.price = price;

        // 默认图标
        this.icon = "ENDER_PEARL";
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public Location getLocation() {
        return location;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    // ===== 图标 =====

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}