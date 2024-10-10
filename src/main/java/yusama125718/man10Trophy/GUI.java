package yusama125718.man10Trophy;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class GUI {
    // 通常メニュー展開
    public static void OpenMenu(Player p, Boolean isEdit){
        Inventory inv = Bukkit.createInventory(null,54, Component.text("[Man10Trophy] メインメニュー"));
    }

    private static ItemStack GetItem(Material mate, Integer amount, String name, Integer cmd){
        ItemStack item = new ItemStack(mate,amount);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(name));
        meta.setCustomModelData(cmd);
        item.setItemMeta(meta);
        return item;
    }
}
