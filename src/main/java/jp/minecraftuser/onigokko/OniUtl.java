
package jp.minecraftuser.onigokko;

import java.util.HashMap;
import java.util.logging.Logger;
import org.bukkit.entity.Player;

/**
 * 鬼ごっこ系ユーティリティ
 * @author ecolight
 */
public class OniUtl {

    private static HashMap<String, String> msgMap = null;
    private static Onigokko plg = null;
    private static Logger logger = null;

    public static String msss(String format, String p1, String p2){
        if (msgMap == null) return "";
        String msgbuf = msgMap.get(format);
        if (msgbuf == null) return "MessageLoadErr";
        return "§d[鬼]§f" + msgParser(msgbuf, p1, p2);
    }
    public static String mss(String format, String p1){
        if (msgMap == null) return "";
        String msgbuf = msgMap.get(format);
        if (msgbuf == null) return "MessageLoadErr";
        return "§d[鬼]§f" + msgParser(msgbuf, p1);
    }
    public static String ms(String format){
        if (msgMap == null) return "";
        String msgbuf = msgMap.get(format);
        if (msgbuf == null) return "MessageLoadErr";
        return "§d[鬼]§f" + msgParser(msgbuf);
    }
    private static String msgParser(String msg){
        // そのうちmsg内に値埋め込める記述追加
        return msg;
    }
    private static String msgParser(String msg, String p1){
        return msg.replace("{p1}", p1);
    }
    private static String msgParser(String msg, String p1, String p2){
        return msg.replace("{p1}", p1).replace("{p2}", p2);
    }

    public static boolean msgLoad(String loc, Onigokko plugin){
        if (msgMap == null) msgMap = new HashMap<String, String>();
        if (plg == null) plg = plugin;
        if (logger == null) logger = plugin.getLogger();
        msgMap.clear();
        // そのうちファイル読み込み実装

        // システムj系
        msgMap.put("game_sys_notperm", "コマンドの実行権限がありません({p1})");
        msgMap.put("game_sys_reload", "設定ファイルを読み込み直しました");
        msgMap.put("game_sys_invalid", "パラメータの指定が不正です");
        msgMap.put("game_sys_many", "パラメータの個数が多いです");
        msgMap.put("game_sys_fewer", "パラメータの個数が少ないです");
        msgMap.put("game_sys_notowner", "このコマンドはゲームの運営ユーザーまたはOPのみ使用可能です");
        msgMap.put("game_sys_enderdragon", "[{p1}]がエンダードラゴンを撃破");
        msgMap.put("game_sys_pickenderdragon", "[{p1}]がエンダードラゴンエッグを取得");
        msgMap.put("game_sys_enable", "onigokko SYSTEMを起動しました");
        msgMap.put("game_sys_disable", "onigokko SYSTEMを停止しました");
        msgMap.put("game_sys_notfound", "onigokko SYSTEMが起動していません");
        msgMap.put("game_sys_exec", "ゲームが実行中のためコマンドが受け付けられません");

        // ゲーム制御系
        msgMap.put("game_create_already", "既にゲームを作成済みです");
        msgMap.put("game_create_invtype", "ゲームタイプの指定が誤っています");
        msgMap.put("game_create_invjoin", "ゲームの参加指定が誤っています(join or show)");
        msgMap.put("game_create", "新規ゲームが作成されました");
        msgMap.put("game_delete_notfound", "対象ゲームが存在しません");
        msgMap.put("game_delete_nonparticipation", "ゲームに参加していません");
        msgMap.put("game_delete", "参加していたゲームが削除されました");
        msgMap.put("game_join_notfound", "対象のゲームが存在しません");
        msgMap.put("game_join_exec", "ゲームが実行中のため参加できません");
        msgMap.put("game_join_other", "ゲームには同時に一つまでしか参加できません");
        msgMap.put("game_join_already", "既に参加中です");
        msgMap.put("game_joinAtk", "[{p1}]が鬼役に参加しました");
        msgMap.put("game_joinDef", "[{p1}]が逃走役に参加しました");
        msgMap.put("game_joinManager", "[{p1}]が運営に参加しました");
        msgMap.put("game_show", "[{p1}]がゲームの観戦に参加しました");
        msgMap.put("game_leave", "[{p1}]がゲームから離脱しました");
        msgMap.put("game_user_notfound", "ユーザーが見つかりませんでした");
        msgMap.put("game_info_Atk", "鬼役ユーザーの一覧");
        msgMap.put("game_info_Def", "逃走役ユーザーの一覧");
        msgMap.put("game_info_Manager", "運営の一覧");
        msgMap.put("game_kick_master", "ゲーム作成者によりゲームから[{p1}]が強制離脱されました");
        msgMap.put("game_kick_op", "サーバー管理者によりゲームから[{p1}]が強制離脱されました");
        msgMap.put("game_kick_reject", "ゲームの参加／実行中には使用できません");
        msgMap.put("game_start_already", "既にゲームは開始しています");
        msgMap.put("game_start_impossible", "ゲームの開始条件を満たしていません");
        msgMap.put("game_start_world", "コンフィグから対象ワールドが見つかりませんでした、記載後に\"/oni reload\"を実行して下さい");
        msgMap.put("game_start_world_reject", "鬼ごっこ管理対象ワールド設定ミスの可能性を考慮し管理対象ワールド内でしかゲームをスタートできません");
        msgMap.put("game_start", "ゲーム開始！");
        msgMap.put("game_end_already", "既にゲームは終了しています");
        msgMap.put("                                                                                                                      ", "参加者不足のためゲームを終了しました");
        msgMap.put("game_end", "ゲームを終了しました");
        msgMap.put("game_utl_pset", "[{p1}]マップをPvP設定しました");
        msgMap.put("game_utl_preset", "[{p1}]マップをPvP設定解除しました");
        msgMap.put("game_utl_onaka", "全ユーザーの空腹度をリセットしました");

        msgMap.put("game_oni_change", "[{p1}]が[{p2}]を捕まえた");
        msgMap.put("game_oni_chat", "このチームに所属していないため発言権がありません");
        msgMap.put("game_oni_chat_atk", "§d[ATK]§f<{p1}> {p2}");
        msgMap.put("game_oni_chat_def", "§d[DEF]§f<{p1}> {p2}");
        msgMap.put("game_oni_chat_man", "§d[MANAGER]§f<{p1}> {p2}");
        msgMap.put("game_oni_chat_all", "§d[ALL]§f<{p1}> {p2}");
        msgMap.put("game_oni_tphere", "管理者に召集されました");
        msgMap.put("game_oni_alldef", "参加者全員を逃走役にセットしました");
        msgMap.put("game_oni_atkdef", "参加者全員を鬼役にセットしました");
        msgMap.put("game_oni_thunder", "鬼には雷がお似合いだよね！どこからともなく雷が！");
 
        msgMap.put("game_mode_addatk", "鬼追加モードに変更されました");
        msgMap.put("game_mode_change", "鬼交換モードに変更されました");
        msgMap.put("game_mode_tousou", "ケイドロモードに変更されました");


        // ゲーム別メッセージ
        msgMap.put("game_battleroyal_cancel", "ゲームの実行条件を満たさないためゲームは中止されました");
        msgMap.put("game_battleroyal_unknown", "不明な理由によりゲームが中止されました、プラグイン開発者に連絡して下さい");
        msgMap.put("game_battleroyal_win", "プレイヤー[{p1}]の勝利です");

        return true;
    }
    
    public static Logger log(){
        return logger;
    }

}
