
package jp.minecraftuser.onigokko;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * 鬼ごっこユーザー状態クラス
 * @author ecolight
 */
public class OniUserStatus {
    private OniUserType type = null;
    private Player player = null;
    private boolean teleportLock = false; 
    public OniUserStatus(Player p) {
        player = p;
    }
    public void setManager() {
        if (type == null) type = OniUserType.USER_MANAGER;
        switch (type) {
            case USER_ATK:
                type = OniUserType.USER_MANATK;
                break;
            case USER_DEF:
                type = OniUserType.USER_MANDEF;
                break;
            default:
                type = OniUserType.USER_MANAGER;
                break;
        }
    }
    public void setManagerOnly() {
        type = OniUserType.USER_MANAGER;
    }
    public void setAtk(){ 
        player.getInventory().setHelmet(null);
        player.getInventory().setHelmet(new ItemStack(Material.GOLD_HELMET));
        if ((type == OniUserType.USER_MANAGER) || (type == OniUserType.USER_MANDEF) || (type == OniUserType.USER_MANATK)) {        
            type = OniUserType.USER_MANATK;
        } else {
            type = OniUserType.USER_ATK;
        }
    }
    public void setDef() {
        player.getInventory().setHelmet(null);
        if ((type == OniUserType.USER_MANAGER) || (type == OniUserType.USER_MANATK) || (type == OniUserType.USER_MANDEF)) {
            type = OniUserType.USER_MANDEF;
        } else {
            type = OniUserType.USER_DEF;
        }
    }
    public void disableTeleport() { teleportLock = true; }
    public void enableTeleport() { teleportLock = false; }

    public Player getPlayer() { return player; }
    public OniUserType getType() { return type; }
    public boolean isTeleportLock() { return teleportLock; }
}
