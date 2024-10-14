package yusama125718.man10Trophy;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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
                }
                else if(sender.hasPermission("mtro.op") && args[0].equals("edit")){
                    if(trophies.isEmpty()){
                        sender.sendMessage(Component.text(prefix + "トロフィーが1つも登録されていません"));
                        return true;
                    }
                    GUI.OpenMenu((Player) sender, true, 1);
                    return true;
                }
                break;

            case 2:
                if (sender.hasPermission("mtro.op") && args[0].equals("create")){
                    for(Trophy t: trophies){
                        if(t.name.equals(args[1])){
                            sender.sendMessage(Component.text(prefix + args[1] + "は既に存在しています"));
                            return true;
                        }
                    }
                    GUI.OpenCreateGUI((Player) sender, args[1]);
                    return true;
                }
                break;
        }
        sender.sendMessage(Component.text(prefix + "/mtro help でhelpを表示"));
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission("mtro.p")) return null;
        return null;
    }
}
