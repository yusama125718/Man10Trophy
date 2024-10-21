package yusama125718.man10Trophy;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import red.man10.man10score.ScoreDatabase;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import static java.lang.Integer.parseInt;
import static net.kyori.adventure.text.event.ClickEvent.suggestCommand;
import static yusama125718.man10Trophy.Man10Trophy.*;

public class Event implements Listener {
    public Event(Man10Trophy plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void GUIClick(InventoryClickEvent e) throws IOException {
        String title = e.getView().toString();
        if (title.startsWith("[Man10Trophy]")){
            if (e.getClick().equals(ClickType.NUMBER_KEY) || e.getClick().equals(ClickType.SWAP_OFFHAND)){
                e.setCancelled(true);
                return;
            }
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
                    if (target.score != 0){
                        int score =  ScoreDatabase.INSTANCE.getScore(e.getWhoClicked().getUniqueId());
                        if (score < target.score){
                            e.getWhoClicked().sendMessage(Component.text(prefix + "スコアが不足しています"));
                            return;
                        }
                    }
                    Thread th = new Thread(() -> {
                        MySQLManager mysql = new MySQLManager(trophy, "man10_trophy");
                        if (!mysql.execute("INSERT INTO man10_trophy (time, trophy_name, mcid, uuid) VALUES ('" + LocalDateTime.now() + ", " + target.name + ", " + e.getWhoClicked().getName() + ", " + e.getWhoClicked().getUniqueId() + "');")) {
                            e.getWhoClicked().sendMessage(Component.text(prefix + "DBの保存に失敗しました"));
                            e.getWhoClicked().closeInventory();
                            return;
                        }
                        Bukkit.getScheduler().runTask(trophy, () -> {
                            e.getWhoClicked().getInventory().removeItemAnySlot(target.cost);
                            ItemStack item = target.item.clone();
                            ItemMeta meta = item.getItemMeta();
                            meta.getPersistentDataContainer().set(new NamespacedKey(trophy, "Man10Trophy"), PersistentDataType.STRING, e.getWhoClicked().getUniqueId().toString());
                            item.setItemMeta(meta);
                            e.getWhoClicked().getInventory().addItem(item);
                        });
                        e.getWhoClicked().sendMessage(Component.text(prefix + "交換しました"));
                        return;
                    });
                    th.start();
                    try {
                        th.join();
                    } catch (InterruptedException ignored) {}
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
                    else {
                        target.state = true;
                        e.getInventory().setItem(31, GUI.GetItem(Material.EMERALD_BLOCK, "交換中 [クリックで交換停止]", 1));
                    }
                    File file = new File(configfile.getAbsoluteFile() + File.separator + target.name);
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                    config.set("state", target.state);
                    config.save(file);
                    e.getWhoClicked().sendMessage(Component.text(prefix + "販売状態を変更しました。現在の状態：" + target.state));
                    return;
                }
                else if (e.getRawSlot() == 33){
                    e.getWhoClicked().closeInventory();
                    e.getWhoClicked().sendMessage(Component.text(prefix + "必要を編集する§e§l[ここをクリックで自動入力する]").clickEvent(suggestCommand("/mtro editor score "+ id +" ")));
                    e.getWhoClicked().sendMessage(Component.text(prefix + "↑をクリックして[スコア]を入力で編集"));
                    return;
                }
                else if (e.getRawSlot() == 35){
                    e.getWhoClicked().closeInventory();
                    GUI.OpenDeleteGUI((Player) e.getWhoClicked(), id);
                    return;
                }
            }
            else if (title.startsWith("[Man10Trophy] トロフィー削除 ")){
                int id = parseInt(title.substring(22));
                Man10Trophy.Trophy target = trophies.get(id);
                if (e.getRawSlot() == 2){
                    if (new File(configfile + File.separator + target.name).delete()){
                        trophies.remove(target);
                        e.getWhoClicked().closeInventory();
                        e.getWhoClicked().sendMessage(Component.text(prefix + "削除しました"));
                    }
                    else {
                        e.getWhoClicked().closeInventory();
                        e.getWhoClicked().sendMessage(Component.text(prefix + "削除に失敗しました"));
                    }
                    return;
                }
                else if (e.getRawSlot() == 6){
                    e.getWhoClicked().closeInventory();
                    GUI.OpenEditMenu((Player) e.getWhoClicked(), id);
                    return;
                }
            }
        }
        else if (e.getView().title().toString().startsWith("[Man10TrophyEdit]")){
            if (e.getClick().equals(ClickType.NUMBER_KEY) || e.getClick().equals(ClickType.SWAP_OFFHAND)){
                e.setCancelled(true);
                return;
            }
            if (title.startsWith("[Man10TrophyEdit] 新規作成 ")){
                String name = title.substring(23);
                switch (e.getRawSlot()){
                    case 11, 15:
                        return;

                    case 31:
                        e.setCancelled(true);
                        Inventory inv = e.getInventory();
                        if (inv.getItem(11) == null || inv.getItem(15) == null){
                            e.getWhoClicked().sendMessage(Component.text(prefix + "アイテムが不足しています！"));
                            return;
                        }
                        File folder = new File(configfile.getAbsolutePath() + File.separator + name + ".yml");
                        YamlConfiguration yml = new YamlConfiguration();
                        yml.set("cost", inv.getItem(11));
                        yml.set("item", inv.getItem(15));
                        yml.set("display", inv.getItem(15));
                        yml.set("score", 0);
                        yml.set("state", false);
                        yml.save(folder);
                        trophies.add(new Trophy(inv.getItem(15), inv.getItem(11), inv.getItem(15), 0, false, name));
                        return;

                    default:
                        e.setCancelled(true);
                        return;
                }
            }
            else if (e.getView().title().toString().startsWith("[Man10TrophyEdit] アイテム編集 display ")){
                int id = parseInt(title.substring(33));
                Man10Trophy.Trophy target = trophies.get(id);
                switch(e.getRawSlot()){
                    case 0:
                        e.setCancelled(true);
                        e.getWhoClicked().closeInventory();
                        GUI.OpenEditMenu((Player) e.getWhoClicked(), id);
                        return;

                    case 12:
                        return;

                    case 15:
                        e.setCancelled(true);
                        e.getWhoClicked().closeInventory();
                        e.getWhoClicked().sendMessage(Component.text(prefix + "アイテム名を編集する§e§l[ここをクリックで自動入力する]").clickEvent(suggestCommand("/mtro editor display "+ id +" ")));
                        e.getWhoClicked().sendMessage(Component.text(prefix + "↑をクリックして[内容]を入力で編集"));
                        e.getWhoClicked().sendMessage(Component.text(prefix + "アイテム説明を編集する§e§l[ここをクリックで自動入力する]").clickEvent(suggestCommand("/mtro editor lore display "+ id +" ")));
                        e.getWhoClicked().sendMessage(Component.text(prefix + "↑をクリックして[行番号 内容]を入力で編集"));
                        return;

                    case 30:
                        e.setCancelled(true);
                        if (e.getInventory().getItem(12) == null){
                            e.getWhoClicked().sendMessage(Component.text(prefix + "アイテムが不足しています"));
                            return;
                        }
                        File file = new File(configfile + File.separator + target.name);
                        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                        config.set("display", e.getInventory().getItem(12));
                        config.save(file);
                        target.display = e.getInventory().getItem(12);
                        e.getWhoClicked().closeInventory();
                        e.getWhoClicked().sendMessage(Component.text(prefix + "変更しました"));
                        return;

                    default:
                        e.setCancelled(true);
                        return;
                }
            }
            else if (e.getView().title().toString().startsWith("[Man10TrophyEdit] アイテム編集 cost ")){
                int id = parseInt(title.substring(30));
                Man10Trophy.Trophy target = trophies.get(id);
                switch(e.getRawSlot()){
                    case 0:
                        e.setCancelled(true);
                        e.getWhoClicked().closeInventory();
                        GUI.OpenEditMenu((Player) e.getWhoClicked(), id);
                        return;

                    case 12:
                        return;

                    case 15:
                        e.setCancelled(true);
                        e.getWhoClicked().closeInventory();
                        e.getWhoClicked().sendMessage(Component.text(prefix + "アイテム名を編集する§e§l[ここをクリックで自動入力する]").clickEvent(suggestCommand("/mtro editor cost "+ id +" ")));
                        e.getWhoClicked().sendMessage(Component.text(prefix + "↑をクリックして[内容]を入力で編集"));
                        e.getWhoClicked().sendMessage(Component.text(prefix + "アイテム説明を編集する§e§l[ここをクリックで自動入力する]").clickEvent(suggestCommand("/mtro editor lore cost "+ id +" ")));
                        e.getWhoClicked().sendMessage(Component.text(prefix + "↑をクリックして[行番号 内容]を入力で編集"));
                        return;

                    case 30:
                        e.setCancelled(true);
                        if (e.getInventory().getItem(12) == null){
                            e.getWhoClicked().sendMessage(Component.text(prefix + "アイテムが不足しています"));
                            return;
                        }
                        File file = new File(configfile + File.separator + target.name);
                        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                        config.set("cost", e.getInventory().getItem(12));
                        config.save(file);
                        target.cost = e.getInventory().getItem(12);
                        e.getWhoClicked().closeInventory();
                        e.getWhoClicked().sendMessage(Component.text(prefix + "変更しました"));
                        return;

                    default:
                        e.setCancelled(true);
                        return;
                }
            }
            else if (e.getView().title().toString().startsWith("[Man10TrophyEdit] アイテム編集 item ")){
                int id = parseInt(title.substring(30));
                Man10Trophy.Trophy target = trophies.get(id);
                switch(e.getRawSlot()){
                    case 0:
                        e.setCancelled(true);
                        e.getWhoClicked().closeInventory();
                        GUI.OpenEditMenu((Player) e.getWhoClicked(), id);
                        return;

                    case 12:
                        return;

                    case 15:
                        e.setCancelled(true);
                        e.getWhoClicked().closeInventory();
                        e.getWhoClicked().sendMessage(Component.text(prefix + "アイテム名を編集する§e§l[ここをクリックで自動入力する]").clickEvent(suggestCommand("/mtro editor item "+ id +" ")));
                        e.getWhoClicked().sendMessage(Component.text(prefix + "↑をクリックして[内容]を入力で編集"));
                        e.getWhoClicked().sendMessage(Component.text(prefix + "アイテム説明を編集する§e§l[ここをクリックで自動入力する]").clickEvent(suggestCommand("/mtro editor lore item "+ id +" ")));
                        e.getWhoClicked().sendMessage(Component.text(prefix + "↑をクリックして[行番号 内容]を入力で編集"));
                        return;

                    case 30:
                        e.setCancelled(true);
                        if (e.getInventory().getItem(12) == null){
                            e.getWhoClicked().sendMessage(Component.text(prefix + "アイテムが不足しています"));
                            return;
                        }
                        File file = new File(configfile + File.separator + target.name);
                        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                        config.set("item", e.getInventory().getItem(12));
                        config.save(file);
                        target.item = e.getInventory().getItem(12);
                        e.getWhoClicked().closeInventory();
                        e.getWhoClicked().sendMessage(Component.text(prefix + "変更しました"));
                        return;

                    default:
                        e.setCancelled(true);
                        return;
                }
            }
        }
    }

    // 右クリックイベント
    @EventHandler
    public void PlayerInteractEvent(PlayerInteractEvent e){
        if (!e.getPlayer().hasPermission("mtro.p") || e.getHand().equals(EquipmentSlot.OFF_HAND) || e.getItem() == null) return;
        if (!e.getItem().hasItemMeta() || !e.getItem().getItemMeta().getPersistentDataContainer().has(new NamespacedKey(trophy, "Man10Trophy"), PersistentDataType.STRING)) return;
        if (!e.getItem().getItemMeta().getPersistentDataContainer().get(new NamespacedKey(trophy, "Man10Trophy"), PersistentDataType.STRING).equals(e.getPlayer().getUniqueId().toString())) return;
        e.getPlayer().sendMessage(Component.text(prefix + "アイテム名を編集する§e§l[ここをクリックで自動入力する]").clickEvent(suggestCommand("/mtro title ")));
        e.getPlayer().sendMessage(Component.text(prefix + "↑をクリックして[内容]を入力で編集"));
        e.getPlayer().sendMessage(Component.text(prefix + "アイテム説明を編集する§e§l[ここをクリックで自動入力する]").clickEvent(suggestCommand("/mtro lore ")));
        e.getPlayer().sendMessage(Component.text(prefix + "↑をクリックして[行番号 内容]を入力で編集"));
    }
}
