package yusama125718.man10Trophy;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Man10Trophy extends JavaPlugin {

    public static JavaPlugin trophy;
    public static Boolean system;
    public static String prefix;
    public static List<Trophy> trophys;
    public static File configfile;

    private static final File folder = new File(trophy.getDataFolder().getAbsolutePath() + File.separator + "trophy");

    @Override
    public void onEnable() {
        this.trophy = this;
        SetupPL();
    }

    // setup
    private void SetupPL(){
        saveDefaultConfig();
        prefix = trophy.getConfig().getString("prefix");
        system = trophy.getConfig().getBoolean("system");
        // create trophy folder
        if (mserial.getDataFolder().listFiles() != null){
            for (File file : Objects.requireNonNull(mserial.getDataFolder().listFiles())) {
                if (file.getName().equals("trophy")) {
                    configfile = file;
                    GetTrophys(file);
                    return;
                }
            }
        }
        if (folder.mkdir()) {
            Bukkit.broadcast(prefix + "トロフィーフォルダを作成しました", "mtro.op");
            configfile = folder;
        } else {
            Bukkit.broadcast(prefix + "トロフィーフォルダの作成に失敗しました", "mtro.op");
        }
    }

    private void GetTrophys(File folder){

    }

    public class Trophy{
        public ItemStack item;
        public ItemStack cost;
        public ItemStack display;
    }
}
