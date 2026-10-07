package top.pinkoc.pinwarp;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.ChatColor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LangManager {

    private final PinWarp plugin;

    private FileConfiguration lang;

    public LangManager(PinWarp plugin) {
        this.plugin = plugin;
    }

    public void load() {

        String language =
                plugin.getConfig().getString("language", "zh_CN");

        // 语言文件实际存放在 jar 的 lang/ 目录下，
        // 与资源路径保持一致，避免首次运行时 saveResource 找不到资源。
        File file =
                new File(
                        plugin.getDataFolder(),
                        "lang" + File.separator + language + ".yml"
                );

        if (!file.exists()) {
            plugin.saveResource("lang/" + language + ".yml", false);
        }

        // 若仍不存在（自定义语言名未内置），回退到简体中文
        if (!file.exists()) {
            file = new File(
                    plugin.getDataFolder(),
                    "lang" + File.separator + "zh_CN.yml"
            );
            plugin.saveResource("lang/zh_CN.yml", false);
        }

        lang = YamlConfiguration.loadConfiguration(file);
    }

    public String get(String path) {
        return lang.getString(path, path);
    }

    public String color(String key) {
        return ChatColor.translateAlternateColorCodes('&', get(key));
    }

    public List<String> getList(String path) {
        return lang.getStringList(path);
    }

    public List<String> colorList(String path) {
        List<String> out = new ArrayList<>();
        for (String s : lang.getStringList(path)) {
            out.add(ChatColor.translateAlternateColorCodes('&', s));
        }
        return out;
    }
}
