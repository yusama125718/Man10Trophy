package yusama125718.man10Trophy;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MainHand;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Integer.parseInt;
import static yusama125718.man10Trophy.Man10Trophy.*;

public class Command implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("mtro.p")) return true;
        switch (args.length){
            // 0の時は購入メニューオープン
            case 0:
                if(!system){
                    sender.sendMessage(Component.text(prefix + "現在システムは無効です"));
                    return true;
                }
                if(trophies.isEmpty()){
                    sender.sendMessage(Component.text(prefix + "トロフィーが1つも登録されていません"));
                    return true;
                }
                GUI.OpenMenu((Player) sender, false, 1);
                return true;

            case 1:
                if(args[0].equals("help")){
                    sender.sendMessage(Component.text(prefix + "§8/mtro §7: §rトロフィー交換メニューを開きます"));
                    sender.sendMessage(Component.text(prefix + "§8/mtro title [タイトル] §7: §r手に持っているトロフィーのアイテム名を編集します（自身が発行した物のみ）"));
                    sender.sendMessage(Component.text(prefix + "§8/mtro lore [行番号] [内容] §7: §r手に持っているトロフィーの説明を編集します。:blankで空行を設定（自身が発行した物のみ）"));
                    if (sender.hasPermission("mtro.op")){
                        sender.sendMessage(Component.text(prefix + "§8/mtro [on/off] §7: §rシステムを[起動/停止]します"));
                        sender.sendMessage(Component.text(prefix + "§8/mtro create [内部名] §7: §rトロフィー交換メニューを開きます"));
                        sender.sendMessage(Component.text(prefix + "§8/mtro edit §7: §rトロフィー編集メニューを開きます"));
                        sender.sendMessage(Component.text("=========================="));
                        sender.sendMessage(Component.text("※編集系コマンドはシステムをOFFにしてから行ってください"));
                        sender.sendMessage(Component.text(prefix + "システム：" + system));
                    }
                    return true;
                }
                else if(sender.hasPermission("mtro.op") && args[0].equals("on")){
                    if(system){
                        sender.sendMessage(Component.text(prefix + "システムは既に有効です"));
                        return true;
                    }
                    system = true;
                    trophy.getConfig().set("system", system);
                    trophy.saveConfig();
                    sender.sendMessage(Component.text(prefix + "システムを有効にしました"));
                    return true;
                }
                else if(sender.hasPermission("mtro.op") && args[0].equals("off")){
                    if(!system){
                        sender.sendMessage(Component.text(prefix + "システムは既に無効です"));
                        return true;
                    }
                    system = false;
                    trophy.getConfig().set("system", system);
                    trophy.saveConfig();
                    sender.sendMessage(Component.text(prefix + "システムを無効にしました"));
                    return true;
                }
                else if(sender.hasPermission("mtro.op") && args[0].equals("edit")){
                    if(system){
                        sender.sendMessage(Component.text(prefix + "システムを無効にしてから実施してください"));
                        return true;
                    }
                    if(trophies.isEmpty()){
                        sender.sendMessage(Component.text(prefix + "トロフィーが1つも登録されていません"));
                        return true;
                    }
                    editor = sender.getName();
                    GUI.OpenMenu((Player) sender, true, 1);
                    return true;
                }
                break;

            case 2:
                if (sender.hasPermission("mtro.op") && args[0].equals("create")){
                    if((args[1] + ".yml").length() > 20){
                        sender.sendMessage(Component.text(prefix + "16文字以内で指定してください"));
                        return true;
                    }
                    for(Trophy t: trophies){
                        if(t.name.equals(args[1] + ".yml")){
                            sender.sendMessage(Component.text(prefix + args[1] + "は既に存在しています"));
                            return true;
                        }
                    }
                    editor = sender.getName();
                    GUI.OpenCreateGUI((Player) sender, args[1]);
                    return true;
                }
                if (args[0].equals("title")){
                    ItemStack item = ((Player) sender).getInventory().getItem(EquipmentSlot.HAND);
                    if (!item.hasItemMeta() || !item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(trophy, "Man10Trophy"), PersistentDataType.STRING) || !((Player) sender).getUniqueId().toString().equals(item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(trophy, "Man10Trophy"), PersistentDataType.STRING))){
                        sender.sendMessage(Component.text(prefix + "そのアイテムは編集できません"));
                        return true;
                    }
                    ItemMeta meta = item.getItemMeta();
                    meta.displayName(Component.text(args[1]));
                    item.setItemMeta(meta);
                    sender.sendMessage(Component.text(prefix + "アイテム名を変更しました"));
                    return true;
                }
                break;

            case 3:
                if (args[0].equals("lore")) {
                    ItemStack item = ((Player) sender).getInventory().getItem(EquipmentSlot.HAND);
                    if (!item.hasItemMeta() || !item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(trophy, "Man10Trophy"), PersistentDataType.STRING) || !((Player) sender).getUniqueId().toString().equals(item.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(trophy, "Man10Trophy"), PersistentDataType.STRING))) {
                        sender.sendMessage(Component.text(prefix + "そのアイテムは編集できません"));
                        return true;
                    }
                    int row;
                    try {
                        row = parseInt(args[1]);
                    } catch (Exception e) {
                        sender.sendMessage(Component.text(prefix + "行番号が不正です"));
                        return true;
                    }
                    ItemMeta meta = item.getItemMeta();
                    if (meta.hasLore()){
                        List<Component> lore = meta.lore();
                        if (args[2].equals(":blank")) {
                            lore.remove(row);
                        }
                        else if(lore.size() > row){
                            lore.set(row, Component.text(args[2]));
                        } else {
                            while (lore.size() < row) {
                                lore.add(Component.text(""));
                            }
                            lore.add(Component.text(args[2]));
                        }
                        meta.lore(lore);
                    }
                    else {
                        List<Component> lore = new ArrayList<>();
                        if (args[2].equals(":blank")){
                            sender.sendMessage(prefix + "説明が存在しません");
                        }
                        else {
                            while (lore.size() < row) {
                                lore.add(Component.text(""));
                            }
                            lore.add(Component.text(args[2]));
                        }
                        meta.lore(lore);
                    }
                    item.setItemMeta(meta);
                    sender.sendMessage(Component.text(prefix + "説明を変更しました"));
                    return true;
                }
                break;

            case 4:
                if (args[0].equals("editor") && sender.hasPermission("mtro.op")){
                    if(system){
                        sender.sendMessage(Component.text(prefix + "システムを無効にしてから実施してください"));
                        return true;
                    }
                    if (args[1].equals("score")){
                        int id;
                        int score;
                        try {
                            id = parseInt(args[2]);
                            score = parseInt(args[3]);
                        } catch (Exception e){
                            sender.sendMessage(Component.text(prefix + "数値が不正です"));
                            return true;
                        }
                        if (trophies.size() - 1 < id){
                            sender.sendMessage(Component.text(prefix + "指定したトロフィーは存在しません"));
                            return true;
                        }
                        Trophy target = trophies.get(id);
                        File file = new File(configfile + File.separator + target.name);
                        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                        config.set("score", score);
                        try {
                            config.save(file);
                        } catch (IOException e) {
                            sender.sendMessage(Component.text(prefix + "保存に失敗しました"));
                            return true;
                        }
                        trophies.get(id).score = score;
                        sender.sendMessage(Component.text(prefix + "変更しました"));
                        return true;
                    }
                    else if (args[1].equals("display")){
                        int id;
                        try {
                            id = parseInt(args[2]);
                        } catch (Exception e){
                            sender.sendMessage(Component.text(prefix + "数値が不正です"));
                            return true;
                        }
                        if (trophies.size() - 1 < id){
                            sender.sendMessage(Component.text(prefix + "指定したトロフィーは存在しません"));
                            return true;
                        }
                        Trophy target = trophies.get(id);
                        ItemStack item = target.display;
                        ItemMeta meta = item.getItemMeta();
                        meta.displayName(Component.text(args[3]));
                        item.setItemMeta(meta);
                        File file = new File(configfile + File.separator + target.name);
                        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                        config.set("display", item);
                        try {
                            config.save(file);
                        } catch (IOException e) {
                            sender.sendMessage(Component.text(prefix + "保存に失敗しました"));
                            return true;
                        }
                        sender.sendMessage(Component.text(prefix + "変更しました"));
                        return true;
                    }
                    else if (args[1].equals("item")){
                        int id;
                        try {
                            id = parseInt(args[2]);
                        } catch (Exception e){
                            sender.sendMessage(Component.text(prefix + "数値が不正です"));
                            return true;
                        }
                        if (trophies.size() - 1 < id){
                            sender.sendMessage(Component.text(prefix + "指定したトロフィーは存在しません"));
                            return true;
                        }
                        Trophy target = trophies.get(id);
                        ItemStack item = target.item;
                        ItemMeta meta = item.getItemMeta();
                        meta.displayName(Component.text(args[3]));
                        item.setItemMeta(meta);
                        File file = new File(configfile + File.separator + target.name);
                        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                        config.set("item", item);
                        try {
                            config.save(file);
                        } catch (IOException e) {
                            sender.sendMessage(Component.text(prefix + "保存に失敗しました"));
                            return true;
                        }
                        sender.sendMessage(Component.text(prefix + "変更しました"));
                        return true;
                    }
                    else if (args[1].equals("cost")){
                        int id;
                        try {
                            id = parseInt(args[2]);
                        } catch (Exception e){
                            sender.sendMessage(Component.text(prefix + "数値が不正です"));
                            return true;
                        }
                        if (trophies.size() - 1 < id){
                            sender.sendMessage(Component.text(prefix + "指定したトロフィーは存在しません"));
                            return true;
                        }
                        Trophy target = trophies.get(id);
                        ItemStack item = target.cost;
                        ItemMeta meta = item.getItemMeta();
                        meta.displayName(Component.text(args[3]));
                        item.setItemMeta(meta);
                        File file = new File(configfile + File.separator + target.name);
                        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                        config.set("cost", item);
                        try {
                            config.save(file);
                        } catch (IOException e) {
                            sender.sendMessage(Component.text(prefix + "保存に失敗しました"));
                            return true;
                        }
                        sender.sendMessage(Component.text(prefix + "変更しました"));
                        return true;
                    }
                }
                break;

            case 6:
                if (sender.hasPermission("mtro.op") && args[0].equals("editor") && args[1].equals("lore")) {
                    if(system){
                        sender.sendMessage(Component.text(prefix + "システムを無効にしてから実施してください"));
                        return true;
                    }
                    if (args[2].equals("display")) {
                        int id;
                        int row;
                        try {
                            id = parseInt(args[3]);
                            row = parseInt(args[4]);
                        } catch (Exception e) {
                            sender.sendMessage(Component.text(prefix + "数字が不正です"));
                            return true;
                        }
                        Trophy target = trophies.get(id);
                        ItemStack item = target.display;
                        ItemMeta meta = item.getItemMeta();
                        if (meta.hasLore()) {
                            List<Component> lore = meta.lore();
                            if (args[5].equals(":blank")) {
                                lore.remove(row);
                            }
                            else if(lore.size() > row){
                                lore.set(row, Component.text(args[5]));
                            } else {
                                while (lore.size() < row) {
                                    lore.add(Component.text(""));
                                }
                                lore.add(Component.text(args[5]));
                            }
                            meta.lore(lore);
                        } else {
                            List<Component> lore = new ArrayList<>();
                            if (args[5].equals(":blank")) {
                                sender.sendMessage(prefix + "説明が存在しません");
                            } else {
                                while (lore.size() < row) {
                                    lore.add(Component.text(""));
                                }
                                lore.add(Component.text(args[5]));
                            }
                            meta.lore(lore);
                        }
                        item.setItemMeta(meta);
                        File file = new File(configfile + File.separator + target.name);
                        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                        config.set("display", item);
                        try {
                            config.save(file);
                        } catch (IOException e) {
                            sender.sendMessage(Component.text(prefix + "保存に失敗しました"));
                            return true;
                        }
                        sender.sendMessage(Component.text(prefix + "説明を変更しました"));
                        return true;
                    }
                    else if (args[2].equals("item")){
                        int id;
                        int row;
                        try {
                            id = parseInt(args[3]);
                            row = parseInt(args[4]);
                        } catch (Exception e) {
                            sender.sendMessage(Component.text(prefix + "数字が不正です"));
                            return true;
                        }
                        Trophy target = trophies.get(id);
                        ItemStack item = target.item;
                        ItemMeta meta = item.getItemMeta();
                        if (meta.hasLore()){
                            List<Component> lore = meta.lore();
                            if (args[5].equals(":blank")) {
                                lore.remove(row);
                            }
                            else if(lore.size() > row){
                                lore.set(row, Component.text(args[5]));
                            } else {
                                while (lore.size() < row) {
                                    lore.add(Component.text(""));
                                }
                                lore.add(Component.text(args[5]));
                            }
                            meta.lore(lore);
                        }
                        else {
                            List<Component> lore = new ArrayList<>();
                            if (args[5].equals(":blank")){
                                sender.sendMessage(prefix + "説明が存在しません");
                            }
                            else {
                                while (lore.size() < row) {
                                    lore.add(Component.text(""));
                                }
                                lore.add(Component.text(args[5]));
                            }
                            meta.lore(lore);
                        }
                        item.setItemMeta(meta);
                        File file = new File(configfile + File.separator + target.name);
                        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                        config.set("item", item);
                        try {
                            config.save(file);
                        } catch (IOException e) {
                            sender.sendMessage(Component.text(prefix + "保存に失敗しました"));
                            return true;
                        }
                        sender.sendMessage(Component.text(prefix + "説明を変更しました"));
                        return true;
                    }
                    else if (args[2].equals("cost")){
                        int id;
                        int row;
                        try {
                            id = parseInt(args[3]);
                            row = parseInt(args[4]);
                        } catch (Exception e) {
                            sender.sendMessage(Component.text(prefix + "数字が不正です"));
                            return true;
                        }
                        Trophy target = trophies.get(id);
                        ItemStack item = target.cost;
                        ItemMeta meta = item.getItemMeta();
                        if (meta.hasLore()){
                            List<Component> lore = meta.lore();
                            if (args[5].equals(":blank")) {
                                lore.remove(row);
                            }
                            else if(lore.size() > row){
                                lore.set(row, Component.text(args[5]));
                            } else {
                                while (lore.size() < row) {
                                    lore.add(Component.text(""));
                                }
                                lore.add(Component.text(args[5]));
                            }
                            meta.lore(lore);
                        }
                        else {
                            List<Component> lore = new ArrayList<>();
                            if (args[5].equals(":blank")){
                                sender.sendMessage(prefix + "説明が存在しません");
                            }
                            else {
                                while (lore.size() < row) {
                                    lore.add(Component.text(""));
                                }
                                lore.add(Component.text(args[5]));
                            }
                            meta.lore(lore);
                        }
                        item.setItemMeta(meta);
                        File file = new File(configfile + File.separator + target.name);
                        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                        config.set("cost", item);
                        try {
                            config.save(file);
                        } catch (IOException e) {
                            sender.sendMessage(Component.text(prefix + "保存に失敗しました"));
                            return true;
                        }
                        sender.sendMessage(Component.text(prefix + "説明を変更しました"));
                        return true;
                    }
                }

                break;
        }
        sender.sendMessage(Component.text(prefix + "/mtro help でhelpを表示"));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission("mtro.p")) {
            if (args.length == 1) {
                if (args[0].isEmpty() && sender.hasPermission("mtro.op")) return Arrays.asList("edit", "create");
            }
        }
        return null;
    }
}
