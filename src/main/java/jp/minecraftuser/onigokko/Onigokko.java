
package jp.minecraftuser.onigokko;

import java.util.List;
import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 鬼ごっこクラス
 * @author ecolight
 */
public class Onigokko extends JavaPlugin {

    private static OnigokkoCommandExecutor myExecutor = null;
    private static OnigokkoEventListener myListener = null;
    private static GameManager gameMan = null;

    private static Logger log = null;
    private static boolean debug = false;

    /* コンフィグ値保持 */
    private static List<String> Worlds;
    private static boolean autoRun = true;
    private static boolean autoDefJoin = true;
    private static OniGameType gamemode = null;

    @Override
    public void onEnable(){

        // プラグイン初期化
        log = getLogger();                      // ログ用
        gameMan = new GameManager(this);

        // 設定値読み込み
        setConfig();
        log.info("Onigokko config loaded.");

        // 各種ハンドラ登録
        myExecutor = new OnigokkoCommandExecutor(this);
        myListener = new OnigokkoEventListener(this);
        log.info("Onigokko executor/listener registered.");
        
        if (isAutoRun()) {
             gameMan.Create(null);
        }
    }

    @Override
    public void onDisable(){
        if (gameMan.Get() != null) {
            gameMan.Delete(null);
        }
    }

    public void setConfig(){
        reloadConfig();
        // サーバー動作モードロード
        autoRun = getConfig().getBoolean("auto-run");
        autoDefJoin = getConfig().getBoolean("auto-defJoin");

        // ゲームモードロード
        String mode =  getConfig().getString("gamemode");
        if (mode.equalsIgnoreCase("addatk")) {
            gamemode = OniGameType.GAME_ADDATK;
        } else if (mode.equalsIgnoreCase("change")) {
            gamemode = OniGameType.GAME_CHANGE;
        } else if (mode.equalsIgnoreCase("tousou")) {
            gamemode = OniGameType.GAME_TOUSOU;
        } else {
            gamemode = OniGameType.GAME_ADDATK;
        }

        // 対象ワールドロード
        Worlds = getConfig().getStringList("Worlds");
        String worldList = "";
        for (String s : Worlds) worldList += " [" + s + "]";
        log.info("Onigokko worlds :" + worldList);

        getConfig().options().copyDefaults(true);
        saveConfig();

        // メッセージ読み込み
        OniUtl.msgLoad(null, this);
    }
    
    // コマンドからコンフィグ書き換えるよう
    public void changeMode(String mode) {
        // クラス変数への設定とコンフィグへの書き戻し
        if (mode.equalsIgnoreCase("addatk")) {
            gamemode = OniGameType.GAME_ADDATK;
            getConfig().set("gamemode", "ADDATK");
        } else if (mode.equalsIgnoreCase("change")) {
            gamemode = OniGameType.GAME_CHANGE;
            getConfig().set("gamemode", "CHANGE");
        } else if (mode.equalsIgnoreCase("tousou")) {
            gamemode = OniGameType.GAME_TOUSOU;
            getConfig().set("gamemode", "TOUSOU");
        } else {
            gamemode = OniGameType.GAME_ADDATK;
            getConfig().set("gamemode", "ADDATK");
        }
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public List<String> GetWorlds() { return Worlds; }
    public boolean isEnableWorld(String worldname){
        for (String str : Worlds){
            if (worldname.equals(str)) return true;
        }
        return false;
    }
    public boolean isAutoRun() { return autoRun; }
    public boolean isAutoDefJoin() { return autoDefJoin; }
    public OniGameType getGamemode() { return gamemode; }
    public boolean getDebug() { return debug; }
    public void setDebug(boolean flag) { this.debug = flag; }
    
    public GameManager getManager() { return gameMan; }
}
