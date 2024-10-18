package yusama125718.man10Trophy;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.Objects;

public final class Man10Trophy extends JavaPlugin {

    public static JavaPlugin trophy;
    public static Boolean system;
    public static String prefix;
    public static List<Trophy> trophies;
    public static File configfile;

    private static final File folder = new File(trophy.getDataFolder().getAbsolutePath() + File.separator + "trophy");

    @Override
    public void onEnable() {
        trophy = this;
        new Event(this);
        getCommand("mtro").setExecutor(new Command());
        SetupPL();
        MySQLManager mysql = new MySQLManager(trophy, "man10_trophy");
        mysql.execute("create table if not exists man10_trophy_data(id int auto_increment,time varchar(35),trophy_name varchar(20),mcid varchar(16),uuid varchar(36),primary key(id))");
    }

    // setup
    private void SetupPL(){
        saveDefaultConfig();
        prefix = trophy.getConfig().getString("prefix");
        system = trophy.getConfig().getBoolean("system");
        // create trophy folder
        if (trophy.getDataFolder().listFiles() != null){
            for (File file : Objects.requireNonNull(trophy.getDataFolder().listFiles())) {
                if (file.getName().equals("trophy")) {
                    configfile = file;
                    GetTrophies(file);
                    return;
                }
            }
        }
        if (folder.mkdir()) {
            Bukkit.broadcast(Component.text(prefix + "トロフィーフォルダを作成しました"), "mtro.op");
            configfile = folder;
        } else {
            Bukkit.broadcast(Component.text(prefix + "トロフィーフォルダの作成に失敗しました"), "mtro.op");
        }
    }

    private void GetTrophies(File folder){
        if (configfile.listFiles() != null){
            for (File file : configfile.listFiles()){
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                if (config.isItemStack("item") || config.isItemStack("cost") || config.isItemStack("display") || config.isInt("score") || config.isBoolean("state")){
                    Bukkit.broadcast(Component.text(prefix + file.getName() + "の読み込みに失敗しました"), "mtro.op");
                    continue;
                }
                trophies.add(new Trophy(config.getItemStack("item"), config.getItemStack("cost"), config.getItemStack("display"), config.getInt("score"), config.getBoolean("state"), folder.getName()));
            }
        }
    }

    public static class Trophy{
        public ItemStack item;
        public ItemStack cost;
        public ItemStack display;
        // 必要スコア
        public Integer score;
        // 販売中か
        public Boolean state;
        public String name;

        public Trophy(ItemStack ITEM, ItemStack COST, ItemStack DISPLAY, Integer SCORE, Boolean STATE, String NAME){
            item = ITEM;
            cost = COST;
            display = DISPLAY;
            score = SCORE;
            state = STATE;
            name = NAME;
        }
    }
}
