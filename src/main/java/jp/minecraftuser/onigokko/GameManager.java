
package jp.minecraftuser.onigokko;

import java.util.logging.Logger;
import org.bukkit.entity.Player;

/**
 * ゲーム制御クラス
 * @author ecolight
 */
public class GameManager {

    private static Onigokko plg = null;
    private Logger log = null;
    private OnigokkoGame game = null;

    public GameManager (Onigokko plugin){
        plg = plugin;
        log = plg.getLogger();
    }
    
    public boolean Create (Player player) {
        if (game != null) {
            if (player != null) player.sendMessage(OniUtl.ms("game_create_already"));
            log.info(OniUtl.ms("game_create_already"));
            return true;
        }
        game = new OnigokkoGame(plg);
        return true;
    }
    public boolean Delete (Player player) {
        if (game == null) {
            if (player != null) player.sendMessage(OniUtl.ms("game_delete_notfound"));
            log.info(OniUtl.ms("game_delete_notfound"));
            return true;
        }
        game.OnigokkoGameFinalize();
        game = null;
        return true;
    }
    public OnigokkoGame Get() {
        return game;
    }
}
