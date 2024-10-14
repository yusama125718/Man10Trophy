package yusama125718.man10Trophy;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static yusama125718.man10Trophy.Man10Trophy.trophies;

public class GUI {
    // 通常メニュー展開
    public static void OpenMenu(Player p, Boolean isEdit, Integer page){
        String title;
        if(isEdit) title = "[Man10Trophy] 編集メニュー " + page;
        else title = "[Man10Trophy] メインメニュー " + page;
        Inventory inv = Bukkit.createInventory(null,54, Component.text(title));
        for (int i = 51; i < 54; i++){
            inv.setItem(i,GetItem(Material.BLUE_STAINED_GLASS_PANE, "次のページへ",1));
            inv.setItem(i - 3,GetItem(Material.WHITE_STAINED_GLASS_PANE, "",1));
            inv.setItem(i - 6,GetItem(Material.RED_STAINED_GLASS_PANE, "前のページへ",1));
        }
        for (int i = 0; (i <= 45 || trophies.size() >= i + 45 * (page - 1)); i++){
            inv.setItem(i, trophies.get(i + 45 * (page - 1)).display.clone());
        }
        p.openInventory(inv);
    }

    // 通常メニュー交換画面
    public static void OpenTradeMenu(Player p, Integer index){
        Man10Trophy.Trophy t = trophies.get(index);
        Inventory inv = Bukkit.createInventory(null,36, Component.text("[Man10Trophy] 交換画面 " + t.name));
        for (int i = 0; i < 36; i++){
            switch (i){
                // 戻る
                case 0:
                    inv.setItem(i, GetItem(Material.WHITE_STAINED_GLASS_PANE, "戻る",1));
                    break;

                // 交換元
                case 11:
                    inv.setItem(i, t.display.clone());
                    break;

                // 矢印
                case 13:
                    inv.setItem(i, GetItem(Material.QUARTZ, "→", 62));
                    break;

                // 交換先
                case 15:
                    inv.setItem(i, t.item.clone());
                    break;

                // 交換ボタン
                case 31:
                    if(t.state) inv.setItem(i, GetItem(Material.EMERALD_BLOCK, "交換する", 1));
                    else inv.setItem(i, GetItem(Material.REDSTONE_BLOCK, "交換停止中", 1));
                    break;

                default:
                    inv.setItem(i, GetItem(Material.WHITE_STAINED_GLASS_PANE, "",1));
                    break;
            }

        }
        p.openInventory(inv);
    }


    public static void OpenEditMenu(Player p, Man10Trophy.Trophy t){
        Inventory inv = Bukkit.createInventory(null,36, Component.text("[Man10Trophy] 編集画面 " + t.name));
        for (int i = 0; i < 36; i++){
            switch (i){
                // 戻る
                case 0:
                    inv.setItem(i, GetItem(Material.WHITE_STAINED_GLASS_PANE, "戻る",1));
                    break;

                // 交換元
                case 11:
                    ItemStack item = t.display.clone();
                    ItemMeta meta = item.getItemMeta();
                    if(meta.hasLore()) meta.lore().add(Component.text("[クリックで編集]"));
                    else {
                        meta.lore(new ArrayList<>() {{
                            add(Component.text("[クリックで編集]"));
                        }});
                    }
                    item.setItemMeta(meta);
                    inv.setItem(i, item);
                    break;

                // 矢印
                case 13:
                    inv.setItem(i, GetItem(Material.QUARTZ, "→", 62));
                    break;

                // 交換先
                case 15:
                    ItemStack d_item = t.display.clone();
                    ItemMeta d_meta = d_item.getItemMeta();
                    if(d_meta.hasLore()) d_meta.lore().add(Component.text("[クリックで編集]"));
                    else {
                        d_meta.lore(new ArrayList<>() {{
                            add(Component.text("[クリックで編集]"));
                        }});
                    }
                    d_item.setItemMeta(d_meta);
                    inv.setItem(i, d_item);
                    break;

                // 交換ボタン
                case 31:
                    if(t.state) inv.setItem(i, GetItem(Material.EMERALD_BLOCK, "交換中 [クリックで交換停止]", 1));
                    else inv.setItem(i, GetItem(Material.REDSTONE_BLOCK, "交換停止中 [クリックで交換開始]", 1));
                    break;

                // display
                case 29:
                    inv.setItem(i, GetItem(Material.ITEM_FRAME, "アイコンを編集する", 1));
                    break;

                case 30:


                default:
                    inv.setItem(i, GetItem(Material.WHITE_STAINED_GLASS_PANE, "",1));
                    break;
            }

        }
        p.openInventory(inv);
    }

    public static void OpenCreateGUI(Player p, String name){
        Inventory inv = Bukkit.createInventory(null,36, Component.text("[Man10TrophyEdit] 新規作成 " + name));
        for (int i = 0; i < 36; i++){
            switch (i){
                // 戻る
                case 0:
                    inv.setItem(i, GetItem(Material.WHITE_STAINED_GLASS_PANE, "戻る",1));
                    break;

                // 交換元、交換先
                case 11, 15:
                    break;

                // 矢印
                case 13:
                    inv.setItem(i, GetItem(Material.QUARTZ, "→", 62));
                    break;

                // 決定ボタン
                case 31:
                    inv.setItem(i, GetItem(Material.EMERALD_BLOCK, "保存", 1));
                    break;

                default:
                    inv.setItem(i, GetItem(Material.WHITE_STAINED_GLASS_PANE, "",1));
                    break;
            }

        }
        p.openInventory(inv);
    }

    private static ItemStack GetItem(Material mate, String name, Integer cmd){
        ItemStack item = new ItemStack(mate, 1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(name));
        meta.setCustomModelData(cmd);
        item.setItemMeta(meta);
        return item;
    }
}