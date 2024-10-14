package yusama125718.man10Trophy;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

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
                if (!target.state){
                    e.getWhoClicked().sendMessage(Component.text(prefix + "そのアイテムは現在交換停止中です"));
                    e.getWhoClicked().closeInventory();
                    return;
                }
                if (e.getRawSlot() == 31){

                }
            }
        }
        else if (e.getView().title().toString().startsWith("[Man10TrophyEdit]")){

        }
    }
}
