
package jp.minecraftuser.onigokko;

import java.util.logging.Logger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * 鬼ごっこコマンドクラス
 * @author ecolight
 */
public class OnigokkoCommandExecutor implements CommandExecutor {

    private static Onigokko plg = null;
    private static Logger log = null;
    private static GameManager man = null;
    private static int paracnt = 0;

    public OnigokkoCommandExecutor(Onigokko plugin) {
        plg = plugin;
        log = plugin.getLogger();
        man = plugin.getManager();
        plugin.getCommand("oni").setExecutor(this);
        plugin.getCommand("onic").setExecutor(this);
        plugin.getCommand("atc").setExecutor(this);
        plugin.getCommand("dec").setExecutor(this);
        plugin.getCommand("mac").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
        paracnt = 0;
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        // 各処理関数に振り分け
        boolean rtn = false;
        if (cmd.getName().equalsIgnoreCase("onic")) rtn = cmdChat(player, cmd, commandLabel, args, ChatType.CHAT_ALL);
        else if (cmd.getName().equalsIgnoreCase("atc")) rtn = cmdChat(player, cmd, commandLabel, args, ChatType.CHAT_ATK);
        else if (cmd.getName().equalsIgnoreCase("dec")) rtn = cmdChat(player, cmd, commandLabel, args, ChatType.CHAT_DEF);
        else if (cmd.getName().equalsIgnoreCase("mac")) rtn = cmdChat(player, cmd, commandLabel, args, ChatType.CHAT_MANAGER);
        else if(cmd.getName().equalsIgnoreCase("oni")) {
            if (args.length >= 1) {
                paracnt = 1;
                if (args[0].equalsIgnoreCase("enable")) rtn = cmdEnable(player, cmd, commandLabel, args);
                else if (args[0].equalsIgnoreCase("disable")) rtn = cmdDisable(player, cmd, commandLabel, args);
                else if (args[0].equalsIgnoreCase("atk")) rtn = cmdAtk(player, cmd, commandLabel, args);
                else if (args[0].equalsIgnoreCase("def")) rtn = cmdDef(player, cmd, commandLabel, args);
                else if (args[0].equalsIgnoreCase("manager")) rtn = cmdManager(player, cmd, commandLabel, args);
                else if (args[0].equalsIgnoreCase("leave")) rtn = cmdLeave(player, cmd, commandLabel, args);
                else if (args[0].equalsIgnoreCase("start")) rtn = cmdStart(player, cmd, commandLabel, args);
                else if (args[0].equalsIgnoreCase("stop")) rtn = cmdStop(player, cmd, commandLabel, args);
                else if (args[0].equalsIgnoreCase("list")) rtn = cmdList(player, cmd, commandLabel, args);
                else if (args[0].equalsIgnoreCase("tpall")) rtn = cmdTpall(player, cmd, commandLabel, args);
                else if (args[0].equalsIgnoreCase("tphere")) rtn = cmdTphere(player, cmd, commandLabel, args);
                else if (args[0].equalsIgnoreCase("alldef")) rtn = cmdAlldef(player, cmd, commandLabel, args);
                else if (args[0].equalsIgnoreCase("allatk")) rtn = cmdAllatk(player, cmd, commandLabel, args);
                else if (args[0].equalsIgnoreCase("tpset")) rtn = cmdTpset(player, cmd, commandLabel, args);
                else if (args[0].equalsIgnoreCase("spawnset")) rtn = cmdSpawnset(player, cmd, commandLabel, args);
                else if (args[0].equalsIgnoreCase("tp")) rtn = cmdTp(player, cmd, commandLabel, args);
                else if (args[0].equalsIgnoreCase("mode")) rtn = cmdMode(player, cmd, commandLabel, args);
                else if (args[0].equalsIgnoreCase("reload")) {
                    // コンソール許可＋パーミッションチェック
                    if ((player == null) || (!player.isOp())) {
                        log.info("[" + player.getName() + "]" + "EcGames not Permissions : eco.ecg.reload");
                        player.sendMessage(OniUtl.mss("game_sys_notperm","ecg reload"));
                        return false;
                    }

                    // 設定ファイルのリロード
                    plg.setConfig();
                    if (player == null){
                        log.info("Onigokko config reloaded.");
                    } else {
                        log.info("[" + player.getName() + "]" + "Onigokko config reloaded.");
                        player.sendMessage(OniUtl.ms("game_sys_reload"));
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("debug")) {
                    // コンソール許可＋パーミッションチェック
                    if ((player == null) || (!player.isOp())) {
                        log.info("[" + player.getName() + "]" + "EcGames not Permissions : eco.ecg.reload");
                        player.sendMessage(OniUtl.mss("game_sys_notperm","ecg reload"));
                        return false;
                    }
                    
                    if (plg.getDebug()) {
                        plg.setDebug(false);
                        player.sendMessage("デバッグモードをOFFにしました");
                    } else {
                        plg.setDebug(true);
                        player.sendMessage("デバッグモードをONにしました");
                    }

                    return true;
                } else {
                    if (player == null){
                        log.info("Onigokko unknown command.(oni " + args[0] + ")");
                    } else {
                        log.info("[" + player.getName() + "]" + "Onigokko unknown command.(oni " + args[0] + ")");
                        player.sendMessage(OniUtl.ms("game_sys_invalid"));
                    }
                }
            } else {
                    if (player == null){
                        log.info("Onigokko unknown command.(not parameter)");
                    } else {
                        log.info("[" + player.getName() + "]" + "Onigokko unknown command.(not parameter)");
                        player.sendMessage(OniUtl.ms("game_sys_invalid"));
                    }
            }
	}
        return true;
    }

    private boolean cmdEnable(Player player, Command cmd, String commandLabel, String[] args) {
        if ((!player.isOp()) && (!player.getName().equals("GravelSmith"))) {
            log.info("[" + player.getName() + "]" + OniUtl.ms("game_sys_notowner"));
            player.sendMessage(OniUtl.ms("game_sys_notowner"));
            return true;
        }
        man.Create(player);
        plg.getServer().broadcastMessage(OniUtl.ms("game_sys_enable"));
        return true;
    }
    private boolean cmdDisable(Player player, Command cmd, String commandLabel, String[] args) {
        if ((!player.isOp()) && (!player.getName().equals("GravelSmith"))) {
            log.info("[" + player.getName() + "]" + OniUtl.ms("game_sys_notowner"));
            player.sendMessage(OniUtl.ms("game_sys_notowner"));
            return true;
        }
        if (man.Get() != null) man.Delete(player);
        plg.getServer().broadcastMessage(OniUtl.ms("game_sys_disable"));
        return true;
    }
    private boolean cmdAtk(Player player, Command cmd, String commandLabel, String[] args) {
        // ゲームの存在チェック
        if (man.Get() == null) {
            player.sendMessage(OniUtl.ms("game_sys_notfound"));
            return true;
        }
        if ((man.Get().checkExec()) && (!man.Get().isManager(player))) {
            log.info("[" + player.getName() + "]" + OniUtl.ms("game_sys_exec"));
            player.sendMessage(OniUtl.ms("game_sys_exec"));
            return true;
        }
        if (args.length <= 1) {
            man.Get().OnigokkoAttack(player);
            return true;
        } else {
            if (!man.Get().isManager(player)) {
                log.info("[" + player.getName() + "]" + OniUtl.ms("game_sys_exec"));
                player.sendMessage(OniUtl.ms("game_sys_exec"));
                return true;                
            }
            if (plg.getServer().getPlayer(args[1]) != null) {
                man.Get().OnigokkoAttack(plg.getServer().getPlayer(args[1]));
            } else {
                player.sendMessage(OniUtl.ms("game_sys_invalid"));
            }
        }
        return true;
    }
    private boolean cmdDef(Player player, Command cmd, String commandLabel, String[] args) {
        // ゲームの存在チェック
        if (man.Get() == null) {
            player.sendMessage(OniUtl.ms("game_sys_notfound"));
            return true;
        }
        if ((man.Get().checkExec()) && (!man.Get().isManager(player))) {
            log.info("[" + player.getName() + "]" + OniUtl.ms("game_sys_exec"));
            player.sendMessage(OniUtl.ms("game_sys_exec"));
            return true;
        }
        if (args.length <= 1) {
            man.Get().OnigokkoDefense(player);
            return true;
        } else {
            if (!man.Get().isManager(player)) {
                log.info("[" + player.getName() + "]" + OniUtl.ms("game_sys_exec"));
                player.sendMessage(OniUtl.ms("game_sys_exec"));
                return true;                
            }
            if (plg.getServer().getPlayer(args[1]) != null) {
                man.Get().OnigokkoDefense(plg.getServer().getPlayer(args[1]));
            } else {
                player.sendMessage(OniUtl.ms("game_sys_invalid"));
            }
        }
        return true;
    }
    private boolean cmdManager(Player player, Command cmd, String commandLabel, String[] args) {
        // ゲームの存在チェック
        if (man.Get() == null) {
            player.sendMessage(OniUtl.ms("game_sys_notfound"));
            return true;
        }
        if (!man.Get().isManager(player)) {
            log.info("[" + player.getName() + "]" + OniUtl.ms("game_sys_notowner"));
            player.sendMessage(OniUtl.ms("game_sys_notowner"));
            return true;
        }
/*        if (!player.isOp()) {
            log.info("[" + player.getName() + "]" + OniUtl.ms("game_sys_notowner"));
            player.sendMessage(OniUtl.ms("game_sys_notowner"));
            return true;
        }*/
        // コマンド長が足りているか
        if (args.length <= 1) {
            man.Get().OnigokkoManager(player);
            player.sendMessage("プレイヤー["+player.getName()+"をマネージャーに追加しました");
            return true;
        } else {
            if (plg.getServer().getPlayer(args[1]) != null) {
                man.Get().OnigokkoManager(plg.getServer().getPlayer(args[1]));
                player.sendMessage("プレイヤー["+plg.getServer().getPlayer(args[1]).getName()+"をマネージャーに追加しました");
            } else {
                player.sendMessage(OniUtl.ms("game_sys_invalid"));
            }
        }
        return true;
    }
    private boolean cmdLeave(Player player, Command cmd, String commandLabel, String[] args) {
        // ゲームの存在チェック
        if (man.Get() == null) {
            player.sendMessage(OniUtl.ms("game_sys_notfound"));
            return true;
        }
        // コマンド長が足りているか
        if (args.length <= 1) {
            man.Get().leave(player);
            return false;
        } else {
            if (!man.Get().isManager(player)) {
                log.info("[" + player.getName() + "]" + OniUtl.ms("game_sys_notowner"));
                player.sendMessage(OniUtl.ms("game_sys_notowner"));
                return true;
            }
            if (plg.getServer().getPlayer(args[1]) != null) {
                man.Get().leave(plg.getServer().getPlayer(args[1]));
            } else {
                player.sendMessage(OniUtl.ms("game_sys_invalid"));
            }
        }
        return true;
    }
    private boolean cmdList(Player player, Command cmd, String commandLabel, String[] args) {
        // ゲームの存在チェック
        if (man.Get() == null) {
            player.sendMessage(OniUtl.ms("game_sys_notfound"));
            return true;
        }
        man.Get().list(player);
        return true;
    }
    private boolean cmdStart(Player player, Command cmd, String commandLabel, String[] args) {
        // ゲームの存在チェック
        if (man.Get() == null) {
            player.sendMessage(OniUtl.ms("game_sys_notfound"));
            return true;
        }
        if (!man.Get().isManager(player)) {
            log.info("[" + player.getName() + "]" + OniUtl.ms("game_sys_notowner"));
            player.sendMessage(OniUtl.ms("game_sys_notowner"));
            return true;
        }
        man.Get().start(player);
        return true;
    }
    private boolean cmdStop(Player player, Command cmd, String commandLabel, String[] args) {
        // ゲームの存在チェック
        if (man.Get() == null) {
            player.sendMessage(OniUtl.ms("game_sys_notfound"));
            return true;
        }
        if (!man.Get().isManager(player)) {
            log.info("[" + player.getName() + "]" + OniUtl.ms("game_sys_notowner"));
            player.sendMessage(OniUtl.ms("game_sys_notowner"));
            return true;
        }
        man.Get().stop(player);
        return true;
    }
    private boolean cmdTpall(Player player, Command cmd, String commandLabel, String[] args) {
        // ゲームの存在チェック
        if (man.Get() == null) {
            player.sendMessage(OniUtl.ms("game_sys_notfound"));
            return true;
        }
        if (!man.Get().isManager(player)) {
            log.info("[" + player.getName() + "]" + OniUtl.ms("game_sys_notowner"));
            player.sendMessage(OniUtl.ms("game_sys_notowner"));
            return true;
        }
        // コマンド長が足りているか
        if (args.length <= 1) {
            player.sendMessage(OniUtl.ms("game_sys_invalid"));
            return false;
        }
        if (args[1].equalsIgnoreCase("all")) {
            man.Get().tphereAll(player.getLocation());
        } else if (args[1].equalsIgnoreCase("atk")) {
            man.Get().tphereAtk(player.getLocation());
        } else if (args[1].equalsIgnoreCase("def")) {
            man.Get().tphereDef(player.getLocation());
        } else if (args[1].equalsIgnoreCase("manager")) {
            man.Get().tphereManager(player.getLocation());
        } else {
            player.sendMessage(OniUtl.ms("game_sys_invalid"));
            return false;
        }

        return true;
    }
    private boolean cmdTphere(Player player, Command cmd, String commandLabel, String[] args) {
        // ゲームの存在チェック
        if (man.Get() == null) {
            player.sendMessage(OniUtl.ms("game_sys_notfound"));
            return true;
        }
        if (!man.Get().isManager(player)) {
            log.info("[" + player.getName() + "]" + OniUtl.ms("game_sys_notowner"));
            player.sendMessage(OniUtl.ms("game_sys_notowner"));
            return true;
        }
        // コマンド長が足りているか
        if (args.length <= 1) {
            player.sendMessage(OniUtl.ms("game_sys_invalid"));
            return false;
        }
        // プレイヤーチェック
        Player p = plg.getServer().getPlayer(args[1]);
        if (p == null) {
            player.sendMessage(OniUtl.ms("game_sys_invalid"));
            return false;
        }
        // てれぽ
        p.teleport(player);
        player.sendMessage("プレイヤー["+p.getDisplayName()+"]を呼び出しました");

        return true;
    }
    private boolean cmdTpset(Player player, Command cmd, String commandLabel, String[] args) {
        // ゲームの存在チェック
        if (man.Get() == null) {
            player.sendMessage(OniUtl.ms("game_sys_notfound"));
            return true;
        }
        if (!man.Get().isManager(player)) {
            log.info("[" + player.getName() + "]" + OniUtl.ms("game_sys_notowner"));
            player.sendMessage(OniUtl.ms("game_sys_notowner"));
            return true;
        }
        man.Get().tpset(player);
        player.sendMessage("現在位置にケイドロモードの救出地点位置を設定しました");

        return true;
    }
    private boolean cmdSpawnset(Player player, Command cmd, String commandLabel, String[] args) {
        // ゲームの存在チェック
        if (man.Get() == null) {
            player.sendMessage(OniUtl.ms("game_sys_notfound"));
            return true;
        }
        if (!man.Get().isManager(player)) {
            log.info("[" + player.getName() + "]" + OniUtl.ms("game_sys_notowner"));
            player.sendMessage(OniUtl.ms("game_sys_notowner"));
            return true;
        }
        man.Get().respawnset(player);
        player.sendMessage("現在位置に捕まった後のテレポート位置を設定しました");

        return true;
    }
    private boolean cmdTp(Player player, Command cmd, String commandLabel, String[] args) {
        // ゲームの存在チェック
        if (man.Get() == null) {
            player.sendMessage(OniUtl.ms("game_sys_notfound"));
            return true;
        }
        if (!man.Get().isManager(player)) {
            log.info("[" + player.getName() + "]" + OniUtl.ms("game_sys_notowner"));
            player.sendMessage(OniUtl.ms("game_sys_notowner"));
            return true;
        }
        // コマンド長が足りているか
        if (args.length <= 1) {
            player.sendMessage(OniUtl.ms("game_sys_invalid"));
            return false;
        }
        // プレイヤーチェック
        Player p = plg.getServer().getPlayer(args[1]);
        if (p == null) {
            player.sendMessage(OniUtl.ms("game_sys_invalid"));
            return false;
        }
        // てれぽ
        player.teleport(p);
        player.sendMessage("プレイヤー["+p.getDisplayName()+"]の位置にテレポートしました");

        return true;
    }
    private boolean cmdAlldef(Player player, Command cmd, String commandLabel, String[] args) {
        // ゲームの存在チェック
        if (man.Get() == null) {
            player.sendMessage(OniUtl.ms("game_sys_notfound"));
            return true;
        }
        if (!man.Get().isManager(player)) {
            log.info("[" + player.getName() + "]" + OniUtl.ms("game_sys_notowner"));
            player.sendMessage(OniUtl.ms("game_sys_notowner"));
            return true;
        }
        man.Get().setAllDefense();
        player.sendMessage("全プレイヤーを逃走役に変更しました");

        return true;
    }
    private boolean cmdAllatk(Player player, Command cmd, String commandLabel, String[] args) {
        // ゲームの存在チェック
        if (man.Get() == null) {
            player.sendMessage(OniUtl.ms("game_sys_notfound"));
            return true;
        }
        if (!man.Get().isManager(player)) {
            log.info("[" + player.getName() + "]" + OniUtl.ms("game_sys_notowner"));
            player.sendMessage(OniUtl.ms("game_sys_notowner"));
            return true;
        }
        man.Get().setAllAttack();
        player.sendMessage("全プレイヤーを逃走役に変更しました");

        return true;
    }
     private boolean cmdMode(Player player, Command cmd, String commandLabel, String[] args) {
        // ゲームの存在チェック
        if (man.Get() == null) {
            player.sendMessage(OniUtl.ms("game_sys_notfound"));
            return true;
        }
        if (man.Get().checkExec()) {
            log.info("[" + player.getName() + "]" + OniUtl.ms("game_sys_exec"));
            player.sendMessage(OniUtl.ms("game_sys_exec"));
            return true;
        }
        // コマンド長が足りているか
        if (args.length <= 1) {
            player.sendMessage(OniUtl.ms("game_sys_invalid"));
            return false;
        }
        plg.changeMode(args[1]);
        switch (plg.getGamemode()) {
            case GAME_ADDATK:
                man.Get().broadcastMessageAll(OniUtl.ms("game_mode_addatk")+"("+ player.getName() +")");
                break;
            case GAME_CHANGE:
                man.Get().broadcastMessageAll(OniUtl.ms("game_mode_change")+"("+ player.getName() +")");
                break;
            case GAME_TOUSOU:
                man.Get().broadcastMessageAll(OniUtl.ms("game_mode_tousou")+"("+ player.getName() +")");
                break;
        }

        return true;
    }
    private boolean cmdChat(Player player, Command cmd, String commandLabel, String[] args, ChatType type) {
        // コンソール不許可
        if (player == null) {
            log.info("Console chat not supported.");
            return true;
        }
        // ゲームの存在チェック
        if (man.Get() == null) {
            player.sendMessage(OniUtl.ms("game_sys_notfound"));
            return true;
        }
        String msg = "";
        for (int cnt = 0; (paracnt + cnt) < args.length ; cnt++){
            msg += args[paracnt + cnt] + " ";
        }
        switch (type) {
            case CHAT_ALL:
                man.Get().broadcastChatAll(player, msg);
                break;
            case CHAT_ATK:
                man.Get().broadcastChatAtk(player, msg);
                break;
            case CHAT_DEF:
                man.Get().broadcastChatDef(player, msg);
                break;
            case CHAT_MANAGER:
                man.Get().broadcastChatManager(player, msg);
                break;
        }
        return true;
    }
}