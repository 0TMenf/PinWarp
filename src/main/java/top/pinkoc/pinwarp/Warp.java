package top.pinkoc.pinwarp;

import org.bukkit.Location;

public class Warp {

    private final String name;

    private final String owner;

    private final Location location;

    private double price;

    // 图标材质
    private String icon;

    // 地标访问量（地标流量）
    private int visits;

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
        this.visits = 0;
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

    // ===== 访问量 =====

    public int getVisits() {
        return visits;
    }

    public void setVisits(int visits) {
        this.visits = visits;
    }

    public void incrementVisits() {
        this.visits++;
    }
}
