package yusama125718.man10Trophy;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

import static yusama125718.man10Trophy.Man10Trophy.trophies;
import static yusama125718.man10Trophy.Man10Trophy.trophy;

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
        for (int i = 0; (i < 45 && trophies.size() > i + 45 * (page - 1)); i++){
            inv.setItem(i, trophies.get(i + 45 * (page - 1)).display.clone());
        }
        p.openInventory(inv);
    }

    // 通常メニュー交換画面
    public static void OpenTradeMenu(Player p, Integer index){
        Man10Trophy.Trophy t = trophies.get(index);
        Inventory inv = Bukkit.createInventory(null,36, Component.text("[Man10Trophy] 交換画面 " + index));
        for (int i = 0; i < 36; i++){
            switch (i){
                // 戻る
                case 0:
                    inv.setItem(i, GetItem(Material.BLACK_STAINED_GLASS_PANE, "戻る",1));
                    break;

                // 交換元
                case 11:
                    ItemStack item = t.cost.clone();
                    ItemMeta meta = item.getItemMeta();
                    byte b = 0;
                    // 取られても交換に使えないように
                    meta.getPersistentDataContainer().set(new NamespacedKey(trophy, "mtro"), PersistentDataType.BYTE, b);
                    item.setItemMeta(meta);
                    inv.setItem(i, item);
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


    public static void OpenEditMenu(Player p, Integer id){
        Man10Trophy.Trophy t = trophies.get(id);
        Inventory inv = Bukkit.createInventory(null,36, Component.text("[Man10Trophy] 編集画面 " + id));
        for (int i = 0; i < 36; i++){
            switch (i){
                // 戻る
                case 0:
                    inv.setItem(i, GetItem(Material.BLACK_STAINED_GLASS_PANE, "戻る",1));
                    break;

                // 交換元
                case 11:
                    ItemStack item = t.cost.clone();
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

                // score
                case 33:
                    inv.setItem(i, GetItem(Material.EMERALD, "スコアを編集する 必要スコア：" + t.score, 1));
                    break;

                // delete
                case 35:
                    inv.setItem(i, GetItem(Material.RED_STAINED_GLASS, "削除する", 1));
                    break;

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

    // mode = <display/cost/item>
    public static void OpenEditItemGUI(Player p, Integer id, String mode){
        Man10Trophy.Trophy t = trophies.get(id);
        Inventory inv = Bukkit.createInventory(null,36, Component.text("[Man10TrophyEdit] アイテム編集 " + mode + " " + id ));
        for (int i = 0; i < 36; i++){
            switch (i){
                // 戻る
                case 0:
                    inv.setItem(i, GetItem(Material.BLACK_STAINED_GLASS_PANE, "戻る",1));
                    break;

                // アイテムスロット
                case 12:
                    ItemStack item;
                    if (mode.equals("display")) item = t.display;
                    else if (mode.equals("cost")) item = t.cost;
                    else item = t.item;
                    inv.setItem(i, item);
                    break;

                case 15:
                    inv.setItem(i, GetItem(Material.RED_STAINED_GLASS_PANE, "コマンドを表示",1));
                    break;

                // 決定ボタン
                case 30:
                    inv.setItem(i, GetItem(Material.EMERALD_BLOCK, "保存", 1));
                    break;

                default:
                    inv.setItem(i, GetItem(Material.WHITE_STAINED_GLASS_PANE, "",1));
                    break;
            }

        }
        p.openInventory(inv);
    }

    public static void OpenDeleteGUI(Player p, int id){
        Man10Trophy.Trophy t = trophies.get(id);
        Inventory inv = Bukkit.createInventory(null,36, Component.text("[Man10Trophy] トロフィー削除 " + id ));
        for (int i = 0; i < 36; i++){
            switch (i){
                // 戻る
                case 2:
                    inv.setItem(i, GetItem(Material.RED_STAINED_GLASS_PANE, "削除",1));
                    break;

                case 6:
                    inv.setItem(i, GetItem(Material.BLUE_STAINED_GLASS_PANE, "キャンセル",1));
                    break;

                default:
                    inv.setItem(i, GetItem(Material.WHITE_STAINED_GLASS_PANE, "",1));
                    break;
            }

        }
        p.openInventory(inv);
    }

    public static ItemStack GetItem(Material mate, String name, Integer cmd){
        ItemStack item = new ItemStack(mate, 1);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(name));
        meta.setCustomModelData(cmd);
        item.setItemMeta(meta);
        return item;
    }
}
