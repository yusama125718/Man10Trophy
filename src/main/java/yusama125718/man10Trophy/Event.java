package yusama125718.man10Trophy;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.time.LocalDateTime;

import static java.lang.Integer.parseInt;
import static yusama125718.man10Trophy.Man10Trophy.*;

public class Event implements Listener {
    public Event(Man10Trophy plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void GUIClick(InventoryClickEvent e){
        String title = e.getView().toString();
        if (title.startsWith("[Man10Trophy]")){
            e.setCancelled(true);
            if (title.startsWith("[Man10Trophy] メインメニュー ")){
                if (!system){
                    e.getWhoClicked().sendMessage(Component.text(prefix + "現在システムはOffです"));
                    e.getWhoClicked().closeInventory();
                    return;
                }
                int page = parseInt(title.substring(22));
                if (51 <= e.getRawSlot() && e.getRawSlot() <= 53){    //次のページへ
                    if (trophies.size() > 45 * page) GUI.OpenMenu((Player) e.getWhoClicked(), false,page + 1);
                    return;
                }
                if (45 <= e.getRawSlot() && e.getRawSlot() <= 47){     //前のページへ
                    if (page != 1) GUI.OpenMenu((Player) e.getWhoClicked(), false, page - 1);
                    return;
                }
                if (e.getRawSlot() < 45 && e.getCurrentItem() != null){
                    int index = 45 * page + e.getRawSlot();
                    if (trophies.size() > index) GUI.OpenTradeMenu((Player) e.getWhoClicked(), index);
                    return;
                }
            }
            else if (title.startsWith("[Man10Trophy] 交換画面 ")){
                if (!system){
                    e.getWhoClicked().sendMessage(Component.text(prefix + "現在システムはOffです"));
                    e.getWhoClicked().closeInventory();
                    return;
                }
                int id = parseInt(title.substring(19));
                Man10Trophy.Trophy target = trophies.get(id);
                if (e.getRawSlot() == 31){
                    if (!target.state){
                        e.getWhoClicked().sendMessage(Component.text(prefix + "そのアイテムは現在交換停止中です"));
                        e.getWhoClicked().closeInventory();
                        return;
                    }
                    if (!e.getWhoClicked().getInventory().contains(target.cost, target.cost.getAmount())){
                        e.getWhoClicked().sendMessage(Component.text(prefix + "アイテムが不足しています"));
                        e.getWhoClicked().closeInventory();
                        return;
                    }
                    if (e.getWhoClicked().getInventory().firstEmpty() == -1){
                        e.getWhoClicked().sendMessage(Component.text(prefix + "インベントリがいっぱいです"));
                        e.getWhoClicked().closeInventory();
                        return;
                    }
                    MySQLManager mysql = new MySQLManager(trophy, "man10_trophy");
                    if (!mysql.execute("INSERT INTO man10_trophy (time, trophy_name, mcid, uuid) VALUES ('"+ LocalDateTime.now() +", "+ target.name +", "+ e.getWhoClicked().getName() +", "+ e.getWhoClicked().getUniqueId() +"');")){
                        e.getWhoClicked().sendMessage(Component.text(prefix + "DBの保存に失敗しました"));
                        e.getWhoClicked().closeInventory();
                        return;
                    }
                    e.getWhoClicked().getInventory().removeItemAnySlot(target.cost);
                    ItemStack item = target.item.clone();
                    ItemMeta meta = item.getItemMeta();
                    meta.getPersistentDataContainer().set(new NamespacedKey(trophy, "Man10Trophy"), PersistentDataType.STRING, e.getWhoClicked().getUniqueId().toString());
                    item.setItemMeta(meta);
                    e.getWhoClicked().getInventory().addItem(item);
                    e.getWhoClicked().sendMessage(Component.text(prefix + "交換しました"));
                    return;
                }
                else if (e.getRawSlot() == 0){
                    e.getWhoClicked().closeInventory();
                    GUI.OpenMenu((Player) e.getWhoClicked(), false, id / 45);
                    return;
                }
            }
            else if (title.startsWith("[Man10Trophy] 編集メニュー ")){
                int page = parseInt(title.substring(21));
                if (51 <= e.getRawSlot() && e.getRawSlot() <= 53){    //次のページへ
                    if (trophies.size() > 45 * page) GUI.OpenMenu((Player) e.getWhoClicked(), true,page + 1);
                    return;
                }
                if (45 <= e.getRawSlot() && e.getRawSlot() <= 47){     //前のページへ
                    if (page != 1) GUI.OpenMenu((Player) e.getWhoClicked(), true, page - 1);
                    return;
                }
                if (e.getRawSlot() < 45 && e.getCurrentItem() != null){
                    int index = 45 * page + e.getRawSlot();
                    if (trophies.size() > index) GUI.OpenEditMenu((Player) e.getWhoClicked(), index);
                    return;
                }
            }
            else if (title.startsWith("[Man10Trophy] 編集画面 ")){
                int id = parseInt(title.substring(19));
                Man10Trophy.Trophy target = trophies.get(id);
                if (e.getRawSlot() == 0){
                    e.getWhoClicked().closeInventory();
                    GUI.OpenMenu((Player) e.getWhoClicked(), true, id / 45);
                    return;
                }
                else if (e.getRawSlot() == 11) {
                    e.getWhoClicked().closeInventory();
                    GUI.OpenEditItemGUI((Player) e.getWhoClicked(),id, "cost");
                    return;
                }
                else if (e.getRawSlot() == 15) {
                    e.getWhoClicked().closeInventory();
                    GUI.OpenEditItemGUI((Player) e.getWhoClicked(),id, "item");
                    return;
                }
                else if (e.getRawSlot() == 29) {
                    e.getWhoClicked().closeInventory();
                    GUI.OpenEditItemGUI((Player) e.getWhoClicked(),id, "display");
                    return;
                }
                else if (e.getRawSlot() == 31) {
                    if (target.state){
                        target.state = false;
                        e.getInventory().setItem(31, GUI.GetItem(Material.REDSTONE_BLOCK, "交換停止中 [クリックで交換開始]", 1));
                    }
                }
            }
        }
        else if (e.getView().title().toString().startsWith("[Man10TrophyEdit]")){

        }
    }
}
