
package jp.minecraftuser.onigokko;

import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

/**
 * 鬼ごっこイベントクラス
 * @author ecolight
 */
public class OnigokkoEventListener implements Listener {

    private static Onigokko plg = null;
    private static Logger log = null;
    private static GameManager man = null;

    public OnigokkoEventListener(Onigokko plugin) {
        plg = plugin;
        log = plugin.getLogger();
        man = plugin.getManager();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void PlayerLogin(PlayerLoginEvent event) {
        if (plg.isAutoDefJoin()) {
            man.Get().OnigokkoDefense(event.getPlayer());
        }
    } 
    @EventHandler(priority = EventPriority.LOWEST)
    public void PlayerQuit(PlayerQuitEvent event) {
       OnigokkoGame game = man.Get();
       if (game != null) {
           game.leave(event.getPlayer());
       }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void PlayerDeath(PlayerDeathEvent event) {

        if (man.Get() != null) {
            if ((plg.isEnableWorld(event.getEntity().getWorld().getName())) &&
                (man.Get().checkExec()) &&
                (event.getEntity() instanceof Player)) {

                Player user = (Player) event.getEntity();
                switch (plg.getGamemode()) {
                    case GAME_ADDATK:
                        if (man.Get().isDef(user)) {
                            man.Get().getDefTeam().removePlayer(user);
                            man.Get().getAtkTeam().addPlayer(user);
                            man.Get().searchUserStatus(user).setAtk();
                        }
                        break;
                    case GAME_CHANGE:
                        if ((man.Get().isAtk(user)) ||
                            (man.Get().isDef(user))) {
                            if (man.Get().isManager(user)) {
                                if (man.Get().getDefTeam().hasPlayer(user)) {
                                    man.Get().getDefTeam().removePlayer(user);
                                } else {
                                    man.Get().getAtkTeam().removePlayer(user);
                                }
                                man.Get().searchUserStatus(user).setManagerOnly();
                            } else {
                                man.Get().leave(user);
                            }
                        }
                        break;
                    case GAME_TOUSOU:
                        break;
                }
            }
        }
    }


    @EventHandler(priority = EventPriority.LOWEST)
    public void EntityDamageByEntity(EntityDamageByEntityEvent event) {
        
        switch (event.getCause()) {
            case ENTITY_ATTACK:
                if (man.Get() == null) break; 
                // まず対象ワールドかどうか
                if (!plg.isEnableWorld(event.getEntity().getWorld().getName())) break;
                // ゲームが実行中かどうか
                if (!man.Get().checkExec()) break;
                ///-------------------------------------------------------------
                // 鬼から逃走役のダメージのみ有効
                ///-------------------------------------------------------------
                // 攻撃者はPlayerか
                if (!(event.getDamager() instanceof Player)) break;
                // 被ダメ者はPlayerか
                if (!(event.getEntity() instanceof Player)) break;

                Player atk = (Player) event.getDamager();
                Player def = (Player) event.getEntity();
                // 逃走役から逃走役のダメージか？
                if ((man.Get().isDef(atk)) &&
                    (man.Get().isDef(def))) {
                    // ダメージキャンセル
                    event.setCancelled(true);
                    
                    switch (plg.getGamemode()) {
                        case GAME_ADDATK:
                            break;
                        case GAME_CHANGE:
                            break;
                        case GAME_TOUSOU:
                            // 捕まってるユーザーは他ユーザーをテレポさせられない
                            if (man.Get().searchUserStatus(atk).isTeleportLock()) break;

                            // ケイドロ形式の場合は、逃走者をテレポ
                            Location loc = man.Get().GetRescue();
                            if (loc == null) {
                                def.teleport(atk.getLocation());
                            } else {
                                def.teleport(man.Get().GetRescue());
                            }
                            man.Get().searchUserStatus(def).enableTeleport();
                            break;
                    }
                }
                
                // 鬼から逃走役のダメージか？
                else if ((man.Get().isAtk(atk)) &&
                         (man.Get().isDef(def))) {
                    //ATK->DEFの場合、ダメージキャンセル
                    event.setCancelled(true);
                    
                    switch (plg.getGamemode()) {
                        case GAME_ADDATK:
                            // 鬼が追加形式の場合は逃走者を鬼にする、新鬼をテレポ
                            man.Get().searchUserStatus(def).setAtk();
                            def.teleport(man.Get().GetLocation());
                            man.Get().incTouch(atk);
                            man.Get().chgAtk(def);
                            break;
                        case GAME_CHANGE:
                            // 鬼が交代形式の場合は鬼と逃走者を入れ替え、新鬼をテレポ
                            man.Get().searchUserStatus(def).setAtk();
                            man.Get().searchUserStatus(atk).setDef();
                            def.teleport(man.Get().GetLocation());
                            man.Get().chgAtk(def);
                            man.Get().chgDef(atk);
                            break;
                        case GAME_TOUSOU:
                            // 既に捕まってるユーザーへの攻撃か？
                            if (man.Get().searchUserStatus(def).isTeleportLock()) return;
                            // ケイドロ形式の場合は、逃走者をテレポ
                            def.teleport(man.Get().GetLocation());
                            // 逃走者の攻撃判定を抑止
                            man.Get().searchUserStatus(def).disableTeleport();
                            break;
                    }
                    
                    // 通知メッセージ
                    log.info(OniUtl.msss("game_oni_change", atk.getName(), def.getName()));
                    man.Get().broadcastMessageAll(OniUtl.msss("game_oni_change", atk.getName(), def.getName()));
                    
                    // ゲームの最低終了条件の判定
                    if (man.Get().isCancelled()) {
                        man.Get().stop(null);
                    }
                } else {
                    // ゲーム参加者へのダメージはキャンセルする
                    OniUserStatus atkstat = man.Get().searchUserStatus(atk);
                    OniUserStatus defstat = man.Get().searchUserStatus(def);
                    if (atkstat != null || defstat != null) {
                        if (((atkstat == null) && (defstat.getType() != OniUserType.USER_MANAGER)) ||
                            ((defstat == null) && (atkstat.getType() == OniUserType.USER_MANAGER))) {
                            event.setCancelled(true);
                        }
                    }
                    
                    // 場所がもともとPvP無効のワールドならキャンセルする
                    if (!man.Get().getPvPList().containsKey(atk.getWorld().getName())) return;
                    if (!man.Get().getPvPList().get(atk.getWorld().getName())) event.setCancelled(true);
                }
                break;
        }
        
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void EntityDamage(EntityDamageEvent event) {
        if (plg.isEnableWorld(event.getEntity().getWorld().getName())) {
            // 攻撃者はPlayerか
            if (event.getEntityType() != EntityType.PLAYER) return;
            if (man.Get() == null) return;

            if ((man.Get().isAtk((Player)event.getEntity())) ||
                (man.Get().isDef((Player)event.getEntity())) ||
                (man.Get().isManager((Player)event.getEntity()))){
                if ((event.getCause() == EntityDamageEvent.DamageCause.LIGHTNING) ||
                    (event.getCause() == EntityDamageEvent.DamageCause.FIRE) ||
                    (event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK)){
                        event.setCancelled(true);
                }
            }
        }
    }

}