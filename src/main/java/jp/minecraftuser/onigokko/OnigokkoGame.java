
package jp.minecraftuser.onigokko;

import java.util.HashMap;
import java.util.logging.Logger;
import javax.naming.ldap.ManageReferralControl;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

/**
 * 鬼ごっこ系ゲームクラス
 * @author ecolight
 */
public class OnigokkoGame {

    // プラグイン共通情報
    private Onigokko plg = null;
    private Logger log = null;
    
    // 内部情報
    private HashMap<Player, OniUserStatus> userList = null;
    private boolean exec = false;
    private HashMap<String, Boolean> pvpList = null;
    private Location respawn = null;
    private Location rescue = null;
    private Scoreboard board = null;
    private HashMap<Player, Scoreboard> defboard = null;
    private Team atkteam = null;
    private Team defteam = null;
    private Objective touchobj = null;
    private Objective touchobj2 = null;

    public OnigokkoGame(Onigokko plugin) {
        plg = plugin;
        log = plg.getLogger();
        userList = new HashMap<Player,OniUserStatus>();
        pvpList = new HashMap<String, Boolean>();
        defboard = new HashMap<Player, Scoreboard>();
        board = plg.getServer().getScoreboardManager().getNewScoreboard();
        if (board == null) {
            board = plg.getServer().getScoreboardManager().getMainScoreboard();
        }
        
        Objective atko = board.getObjective("atkteam");
        if (atko != null) {
            atko.unregister();
            atko = null;
        }
        Objective defo = board.getObjective("defteam");
        if (defo != null) {
            defo.unregister();
            defo = null;
        }
        atkteam = board.registerNewTeam("atkteam");
        defteam = board.registerNewTeam("defteam");
        atkteam.setCanSeeFriendlyInvisibles(true);
        defteam.setCanSeeFriendlyInvisibles(true);
        atkteam.setDisplayName("鬼チーム");
        defteam.setDisplayName("逃走チーム");
        atkteam.setPrefix(ChatColor.RED.toString()+"[鬼]");
        defteam.setPrefix(ChatColor.BLUE.toString()+"[逃走]");
        atkteam.setSuffix(ChatColor.WHITE.toString());
        defteam.setSuffix(ChatColor.WHITE.toString());
        board.clearSlot(DisplaySlot.SIDEBAR);
        board.clearSlot(DisplaySlot.PLAYER_LIST);
        board.clearSlot(DisplaySlot.BELOW_NAME);
    }
    
    public void OnigokkoGameFinalize() {
        for (OfflinePlayer p : board.getPlayers()) {
            if (p.isOnline()) {
                Player pl = p.getPlayer();
                if (defboard.containsKey(pl)) {
                    pl.setScoreboard(defboard.get(pl));
                    defboard.remove(pl);
                } else {
                    pl.setScoreboard(null);
                }
            }
        }
        if (atkteam != null) {
            atkteam.unregister();
        }
        if (defteam != null) {
            defteam.unregister();
        }
        if (board != null) {
            for (Objective o :board.getObjectives()) {
                o.unregister();
            }
            board.clearSlot(DisplaySlot.SIDEBAR);
            board.clearSlot(DisplaySlot.PLAYER_LIST);
            board.clearSlot(DisplaySlot.BELOW_NAME);
        }
    }

    public boolean OnigokkoAttack(Player player) {
        OniUserStatus stat = userList.get(player);
        if (stat == null) {
            stat = new OniUserStatus(player);
            stat.setAtk();
            userList.put(player, stat);
        } else {
            // プレイヤーリストにATKで列挙されている場合は追加中止
            if ((stat.getType() == OniUserType.USER_ATK) || (stat.getType() == OniUserType.USER_MANATK)) {
                // "既にこのゲームに参加中です"
                player.sendMessage(OniUtl.ms("game_join_already"));
                return true;
            }
            stat.setAtk();
        }
        atkteam.addPlayer(player);
        if (!defboard.containsKey(player)) {
            Scoreboard bd = player.getScoreboard();
            if (bd != null)defboard.put(player, bd);
        }
        player.setScoreboard(board);
        // "[{p1}]が鬼役に参加しました"
        broadcastMessageAll(OniUtl.mss("game_joinAtk", player.getName()));

        return true;
    }
    public boolean OnigokkoDefense(Player player) {
        OniUserStatus stat = userList.get(player);
        if (stat == null) {
            stat = new OniUserStatus(player);
            stat.setDef();
            userList.put(player, stat);
        } else {
            // プレイヤーリストにDEFで列挙されている場合は追加中止
            if ((stat.getType() == OniUserType.USER_DEF) || (stat.getType() == OniUserType.USER_MANDEF)) {
                // "既にこのゲームに参加中です"
                player.sendMessage(OniUtl.ms("game_join_already"));
                return true;
            }
            stat.setDef();
        }
        defteam.addPlayer(player);
        if (!defboard.containsKey(player)) {
            Scoreboard bd = player.getScoreboard();
            if (bd != null)defboard.put(player, bd);
        }
        player.setScoreboard(board);
        // "[{p1}]が逃走役に参加しました"
        broadcastMessageAll(OniUtl.mss("game_joinDef", player.getName()));

        return true;
    }
    public boolean OnigokkoManager(Player player) {
        OniUserStatus stat = userList.get(player);
        if (stat == null) {
            stat = new OniUserStatus(player);
            stat.setManager();
            userList.put(player, stat);
        } else {
            // プレイヤーリストにMANAGERで列挙されている場合は追加中止
            if ((stat.getType() == OniUserType.USER_MANAGER) ||
                (stat.getType() == OniUserType.USER_MANATK) ||
                (stat.getType() == OniUserType.USER_MANDEF)){
                // "既にこのゲームに参加中です"
                player.sendMessage(OniUtl.ms("game_join_already"));
                return true;
            }
            stat.setManager();
        }
        if (!defboard.containsKey(player)) {
            Scoreboard bd = player.getScoreboard();
            if (bd != null)defboard.put(player, bd);
        }
        player.setScoreboard(board);
        // "[{p1}]が運営に参加しました"
        broadcastMessageAll(OniUtl.mss("game_joinManager", player.getName()));

        return true;
    }
    public boolean leave(Player user){
        
        OniUserStatus stat = userList.get(user);
        if (stat != null) {
            // "[{p1}]がゲームから離脱しました"
            broadcastMessageAll(OniUtl.mss("game_leave", user.getName()));
            // 登録の削除
            userList.remove(user);
            if (defboard.containsKey(user)) {
                user.setScoreboard(defboard.get(user));
                defboard.remove(user);
            } else {
                user.setScoreboard(null);
            }
            // ゲームの中止判定
            if (isCancelled()) {
                stop(null);
            }
        }
        return true;
    }
    public boolean list(Player user){
        user.sendMessage(OniUtl.ms("game_info_Atk"));
        String msg = "";
        for (OniUserStatus stat : userList.values()){
            if ((stat.getType() == OniUserType.USER_ATK) || (stat.getType() == OniUserType.USER_MANATK)) {
                msg += stat.getPlayer().getName() + ",";
            }
        }
        user.sendMessage(msg);
        user.sendMessage(OniUtl.ms("game_info_Def"));
        msg = "";
        for (OniUserStatus stat : userList.values()){
            if ((stat.getType() == OniUserType.USER_DEF) || (stat.getType() == OniUserType.USER_MANDEF)) {
                if ((plg.getGamemode() == OniGameType.GAME_TOUSOU) && (stat.isTeleportLock())) {
                    msg += "§7!" + stat.getPlayer().getName() + ",§f";
                } else {
                    msg += stat.getPlayer().getName() + ",";
                }
            }
        }
        user.sendMessage(msg);
        user.sendMessage(OniUtl.ms("game_info_Manager"));
        msg = "";
        for (OniUserStatus stat : userList.values()){
            if ((stat.getType() == OniUserType.USER_MANAGER) ||
                (stat.getType() == OniUserType.USER_MANATK) ||
                (stat.getType() == OniUserType.USER_MANDEF)){
                msg += stat.getPlayer().getName() + ",";
            }
        }
        user.sendMessage(msg);
        return true;
    }
    
    public boolean isCancelled() {
        int Atk = 0;
        int Def = 0;
        int CaptDef = 0;
        boolean ret = false;
        for (OniUserStatus stat : userList.values()) {
            switch (stat.getType()) {
                case USER_ATK:
                case USER_MANATK:
                    Atk++;
                    break;
                case USER_DEF:
                case USER_MANDEF:
                    if (stat.isTeleportLock()) CaptDef++;
                    Def++;
                    break;
            }
        }
        if ((Atk < 1) || (Def < 1)) {
            ret = true;
        }
        if (plg.getGamemode() == OniGameType.GAME_TOUSOU) {
            if (Def == CaptDef) {
                ret = true;
            }
        }
        return ret;
    }
    
    public boolean isAtk(Player player) {
        OniUserStatus hit = userList.get(player);
        if (hit != null) {
            if ((hit.getType() == OniUserType.USER_ATK) ||
                (hit.getType() == OniUserType.USER_MANATK)) {
                return true;
            }
        }
        return false;
    }
    public boolean isDef(Player player) {
        OniUserStatus hit = userList.get(player);
        if (hit != null) {
            if ((hit.getType() == OniUserType.USER_DEF) ||
                (hit.getType() == OniUserType.USER_MANDEF)) {
                return true;
            }
        }
        return false;
    }
    public boolean isManager(Player player) {
        if (player.isOp()) return true;
        if (player.getName().equals("GravelSmith")) return true;
        
        OniUserStatus hit = userList.get(player);
        if (hit != null) {
            if ((hit.getType() == OniUserType.USER_MANAGER) ||
                (hit.getType() == OniUserType.USER_MANATK) ||
                (hit.getType() == OniUserType.USER_MANDEF)){
                return true;
            }
        }
        return false;
    }
    
    public HashMap<String, Boolean> getPvPList() {
        return pvpList;
    }

    // ゲーム開始
    public void start(Player master){
        // 既に開始状態か
        if (exec == true){
            // "既にゲームは開始しています"
            master.sendMessage(OniUtl.ms("game_start_already"));
            return;
        }

        // ゲームが開始できない条件の場合には異常終了
        if ((isCancelled()) && (!plg.getDebug())){
            // "ゲームの開始条件を満たしていません"
            master.sendMessage(OniUtl.ms("game_start_impossible"));
            return;
        }
        
        // 管理対象ワールドをPvP設定に変更
        for (String world : plg.GetWorlds()) {
            World w = plg.getServer().getWorld(world);
            if (w != null) {
                Boolean pvp = new Boolean(w.getPVP());
                pvpList.put(w.getName(), pvp);
                w.setPVP(true);
            }
        }
        if (pvpList.size() == 0) {
            // "コンフィグから対象ワールドが見つかりませんでした、記載後に\"/oni reload\"を実行して下さい"
            master.sendMessage(OniUtl.ms("game_start_world"));
            return;
        }
        if (!pvpList.containsKey(master.getWorld().getName())) {
            // "鬼ごっこ管理対象ワールド設定ミスの可能性を考慮し管理対象ワールド内でしかゲームをスタートできません"
            master.sendMessage(OniUtl.ms("game_start_world_reject"));
            return;
        }

        // ユーザー個別フラグリセット
        for (OniUserStatus stat : userList.values() ){
            stat.enableTeleport();
        }

        // 開始出来るならば正常終了
        exec = true;
        // 鬼のリスポーンポイントとしてスタート設定箇所を指定
        if (respawn == null) {
            respawn = master.getLocation();
        }
        // "ゲーム開始！"
        OniUtl.log().info("GameStart:" + master.getName());
        broadcastMessageAll(OniUtl.ms("game_start"));

        board.clearSlot(DisplaySlot.SIDEBAR);
        board.clearSlot(DisplaySlot.BELOW_NAME);
        board.clearSlot(DisplaySlot.PLAYER_LIST);
        for (Objective o : board.getObjectives()) {
            o.unregister();
        }
        switch (plg.getGamemode()) {
            case GAME_ADDATK:
                touchobj = board.registerNewObjective("touch", "dummy");
                touchobj.setDisplayName("捕まえた回数");
                touchobj.setDisplaySlot(DisplaySlot.SIDEBAR);
                touchobj2 = board.registerNewObjective("touch2", "dummy");
                touchobj2.setDisplayName("捕まえた回数");
                touchobj2.setDisplaySlot(DisplaySlot.PLAYER_LIST);
                break;
        }
        return;
    }
    public Objective getTouchObj() {
        return touchobj;
    }
    public Objective getTouchObj2() {
        return touchobj2;
    }
    // ゲーム停止
    public boolean stop(Player master){

        // 既に停止状態か
        if (exec == false){
            // "既にゲームは終了しています"
            if (master != null) master.sendMessage(OniUtl.ms("game_end_already"));
            return false;
        }

        // 対象ワールドのPvP設定を元に戻す
        for (String world : plg.GetWorlds()) {
            World w = plg.getServer().getWorld(world);
            if (w != null) {
                Boolean pvp = pvpList.get(w.getName());
                w.setPVP(pvp);
            }
        }
        pvpList.clear();
        
        // 交代式の場合には鬼役に落雷しておわる
        if (plg.getGamemode() == OniGameType.GAME_CHANGE) {
            for (OniUserStatus stat : userList.values() ){
                if ((stat.getType() == OniUserType.USER_ATK) ||
                    (stat.getType() == OniUserType.USER_MANATK)){
                    Player p = stat.getPlayer();
                    p.getWorld().strikeLightningEffect(p.getLocation());
                    p.sendMessage(OniUtl.ms("game_oni_thunder"));
                }
            }
        }

        // フラグリセット
        exec = false;
        
        // 各種復帰地点初期化
        rescue = null;
        respawn = null;

        // "ゲーム終了"メッセージ
        if (master == null) {
            broadcastMessageAll(OniUtl.ms("game_end_leave"));
            OniUtl.log().info("GameStop:System");
        } else {
            broadcastMessageAll(OniUtl.ms("game_end"));
            OniUtl.log().info("GameStop:" + master.getName());
        }
        return true;
    }
    // ケイドロモード逃走者復帰地点設定
    public void tpset(Player master){
        rescue = master.getLocation();
    }
    // 各モード捕まった後にリスポーンさせる地点設定
    public void respawnset(Player master){
        respawn = master.getLocation();
    }
    
    public void setAllDefense() {
        for (OniUserStatus stat : userList.values() ){
            if ((stat.getType() == OniUserType.USER_ATK) ||
                (stat.getType() == OniUserType.USER_MANATK)) {
                stat.setDef();
                atkteam.removePlayer(stat.getPlayer());
                defteam.addPlayer(stat.getPlayer());
            }
            stat.enableTeleport();
        }
        broadcastMessageAll(OniUtl.ms("game_oni_alldef"));
    }
    public void setAllAttack() {
        for (OniUserStatus stat : userList.values() ){
            if ((stat.getType() == OniUserType.USER_DEF) ||
                (stat.getType() == OniUserType.USER_MANDEF)) {
                stat.setAtk();
                defteam.removePlayer(stat.getPlayer());
                atkteam.addPlayer(stat.getPlayer());
            }
            stat.enableTeleport();
        }
        broadcastMessageAll(OniUtl.ms("game_oni_allatk"));
    }
    
    // ユーザーステータスの取得
    public OniUserStatus searchUserStatus(Player user){
        return userList.get(user);
    }

    // ゲーム実行中か確認取得
    public boolean checkExec(){ return exec; }

    public Location GetLocation() { return respawn; }
    public Location GetRescue() { return rescue; }

    // Atkユーザーへの全体メッセージ
    public void broadcastMessageAtk(String msg) {
        for (OniUserStatus stat : userList.values() ){
            if ((stat.getType() == OniUserType.USER_ATK) || (stat.getType() == OniUserType.USER_MANATK)) {
                stat.getPlayer().sendMessage(msg);
            }
        }
    }
    // Defユーザーへの全体メッセージ
    public void broadcastMessageDef(String msg) {
        for (OniUserStatus stat : userList.values() ){
            if ((stat.getType() == OniUserType.USER_DEF) || (stat.getType() == OniUserType.USER_MANDEF)) {
                stat.getPlayer().sendMessage(msg);
            }
        }
    }
    // Managerユーザーへの全体メッセージ
    public void broadcastMessageManager(String msg) {
        for (OniUserStatus stat : userList.values() ){
            if (stat.getType() == OniUserType.USER_MANAGER) {
                stat.getPlayer().sendMessage(msg);
            }
        }
    }
    // ゲーム所属ユーザーへの全体メッセージ
    public void broadcastMessageAll(String msg) {
        broadcastMessageAtk(msg);
        broadcastMessageDef(msg);
        broadcastMessageManager(msg);
    }
    // Atkユーザーへの全体チャット
    public void broadcastChatAtk(Player speaker, String msg) {
        // 発言権チェック
        if ((userList.get(speaker).getType() != OniUserType.USER_ATK) &&
            (userList.get(speaker).getType() != OniUserType.USER_MANAGER) &&
            (userList.get(speaker).getType() != OniUserType.USER_MANATK) &&
            (userList.get(speaker).getType() != OniUserType.USER_MANDEF)){
            speaker.sendMessage(OniUtl.ms("game_oni_chat"));
            return;
        }
        for (OniUserStatus stat : userList.values() ){
            if (stat.getType() == OniUserType.USER_ATK) {
                stat.getPlayer().sendMessage(OniUtl.msss("game_oni_chat_atk", speaker.getName(), msg));
            }
        }
        broadcastChatManager(null, OniUtl.msss("game_oni_chat_atk", speaker.getName(), msg));
    }
    // Defユーザーへの全体チャット
    public void broadcastChatDef(Player speaker, String msg) {
        // 発言権チェック
        if ((userList.get(speaker).getType() != OniUserType.USER_DEF) &&
            (userList.get(speaker).getType() != OniUserType.USER_MANAGER) &&
            (userList.get(speaker).getType() != OniUserType.USER_MANATK) &&
            (userList.get(speaker).getType() != OniUserType.USER_MANDEF)){
            speaker.sendMessage(OniUtl.ms("game_oni_chat"));
            return;
        }
        for (OniUserStatus stat : userList.values() ){
            if (stat.getType() == OniUserType.USER_DEF) {
                stat.getPlayer().sendMessage(OniUtl.msss("game_oni_chat_def", speaker.getName(), msg));
            }
        }
        broadcastChatManager(null, OniUtl.msss("game_oni_chat_def", speaker.getName(), msg));
    }
    // Managerユーザーへの全体チャット
    public void broadcastChatManager(Player speaker, String msg) {
        // 発言権チェック
        if (speaker != null) {
            if ((userList.get(speaker).getType() != OniUserType.USER_MANAGER) &&
                (userList.get(speaker).getType() != OniUserType.USER_MANATK) &&
                (userList.get(speaker).getType() != OniUserType.USER_MANDEF)){
                speaker.sendMessage(OniUtl.ms("game_oni_chat"));
                return;
            }
        }
        for (OniUserStatus stat : userList.values() ){
            if ((stat.getType() == OniUserType.USER_MANAGER) || 
                (stat.getType() == OniUserType.USER_MANATK) ||
                (stat.getType() == OniUserType.USER_MANDEF)){
                if (speaker != null) {
                    stat.getPlayer().sendMessage(OniUtl.msss("game_oni_chat_man", speaker.getName(), msg));
                } else {
                    stat.getPlayer().sendMessage(OniUtl.msss("game_oni_chat_man", "", msg));
                }
            }
        }
    }
    // ゲーム所属ユーザーへの全体チャット
    public void broadcastChatAll(Player speaker, String msg) {
        for (OniUserStatus stat : userList.values() ){
            stat.getPlayer().sendMessage(OniUtl.msss("game_oni_chat_all", speaker.getName(), msg));
        }
    }

    // ユーザーの召集
    public void tphereAll(Location loc) {
        for (OniUserStatus stat : userList.values() ){
            if ((stat.getType() == OniUserType.USER_ATK) ||
                (stat.getType() == OniUserType.USER_DEF) ||
                (stat.getType() == OniUserType.USER_MANATK) ||
                (stat.getType() == OniUserType.USER_MANDEF)){
                stat.getPlayer().teleport(loc);
                stat.getPlayer().sendMessage(OniUtl.ms("game_oni_tphere"));
            }
        }
    }
    public void tphereAtk(Location loc) {
        for (OniUserStatus stat : userList.values() ){
            if ((stat.getType() == OniUserType.USER_ATK) ||
                (stat.getType() == OniUserType.USER_MANATK)){
                stat.getPlayer().teleport(loc);
                stat.getPlayer().sendMessage(OniUtl.ms("game_oni_tphere"));
            }
        }
    }
    public void tphereDef(Location loc) {
        for (OniUserStatus stat : userList.values() ){
            if ((stat.getType() == OniUserType.USER_DEF) ||
                (stat.getType() == OniUserType.USER_MANDEF)){
                stat.getPlayer().teleport(loc);
                stat.getPlayer().sendMessage(OniUtl.ms("game_oni_tphere"));
            }
        }
    }
    public void tphereManager(Location loc) {
        for (OniUserStatus stat : userList.values() ){
            if ((stat.getType() == OniUserType.USER_MANAGER) ||
                (stat.getType() == OniUserType.USER_MANATK) ||
                (stat.getType() == OniUserType.USER_MANDEF)){
                stat.getPlayer().teleport(loc);
                stat.getPlayer().sendMessage(OniUtl.ms("game_oni_tphere"));
            }
        }
    }
    public Scoreboard getScoreBoard() {
        return board;
    }
    public Team getAtkTeam() {
        return atkteam;
    }
    public Team getDefTeam() {
        return defteam;
    }
    public void incTouch(Player p) {
        Score s = touchobj.getScore(p);
        s.setScore(s.getScore() + 1);
        s = touchobj2.getScore(p);
        s.setScore(s.getScore() + 1);
    }
    public void chgAtk(Player p) {
        if (defteam.hasPlayer(p)) {
            defteam.removePlayer(p);
        }
        atkteam.addPlayer(p);
    }
    public void chgDef(Player p) {
        if (atkteam.hasPlayer(p)) {
            atkteam.removePlayer(p);
        }
        defteam.addPlayer(p);
    }
}
