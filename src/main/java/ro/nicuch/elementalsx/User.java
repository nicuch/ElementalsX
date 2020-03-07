package ro.nicuch.elementalsx;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ro.nicuch.elementalsx.elementals.ElementalsUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class User {
    private final UUID uuid;
    private boolean ignorePlaceProtection;
    private boolean sounds;
    private DamageCause lastDamageCause;
    private boolean field;
    private UUID lastFieldOwner;
    private int thirstLevel;
    private boolean pack;
    private boolean messages;
    private final AtomicLong lastRandomTeleport = new AtomicLong(0);

    public boolean hasMsgActive() {
        return this.messages;
    }

    public boolean canRandomTeleport() {
        return (this.lastRandomTeleport.get() + TimeUnit.MILLISECONDS.convert(30, TimeUnit.MINUTES) < System.currentTimeMillis());
    }

    public void toggleRandomTeleport() {
        this.lastRandomTeleport.set(System.currentTimeMillis());
    }

    public void toggleMsgActive(boolean b) {
        this.messages = b;
    }

    public boolean hasResourcePack() {
        return this.pack;
    }

    public void toggleResourcePack(boolean pack) {
        this.pack = pack;
    }

    public void setThirstLevel(int i) {
        this.thirstLevel = i;
    }

    public int getThirstLevel() {
        return this.thirstLevel;
    }

    public boolean isInField() {
        return this.field;
    }

    public UUID getLastFieldOwner() {
        return this.lastFieldOwner;
    }

    public User(Player base) {
        this.uuid = base.getUniqueId();
        if (base.getLastDamageCause() == null)
            this.lastDamageCause = DamageCause.CUSTOM;
        else
            this.lastDamageCause = base.getLastDamageCause().getCause();
        this.sounds = true;
        this.pack = false;
        if (!base.hasPlayedBefore()) {
            ItemStack i1 = new ItemStack(Material.LEATHER_HELMET);
            ItemStack i2 = new ItemStack(Material.LEATHER_CHESTPLATE);
            ItemStack i3 = new ItemStack(Material.LEATHER_LEGGINGS);
            ItemStack i4 = new ItemStack(Material.LEATHER_BOOTS);
            ItemStack i5 = new ItemStack(Material.STONE_PICKAXE);
            ItemStack i6 = new ItemStack(Material.STONE_SWORD);
            ItemStack i7 = new ItemStack(Material.STONE_SHOVEL);
            ItemStack i8 = new ItemStack(Material.STONE_AXE);
            ItemStack i9 = new ItemStack(Material.TORCH, 32);
            ItemStack i10 = new ItemStack(Material.APPLE, 16);
            ItemStack i11 = new ItemStack(Material.DIAMOND_BLOCK);
            ItemStack i12 = new ItemStack(Material.DIAMOND, 2);
            ItemStack i13 = new ItemStack(Material.OAK_LOG, 16);
            ItemStack i14 = new ItemStack(Material.COBBLESTONE, 32);
            ItemStack i15 = new ItemStack(Material.SADDLE);
            ItemStack i16 = new ItemStack(Material.LEAD, 2);
            ItemStack i17 = new ItemStack(Material.SHIELD);
            ItemMeta prot = i11.getItemMeta();
            prot.setDisplayName(ElementalsUtil.color("&bProtectie de Diamant &8(&a51x256x52&8)"));
            i11.setItemMeta(prot);
            base.getInventory().setItem(0, i5);
            base.getInventory().setItem(1, i6);
            base.getInventory().setItem(2, i7);
            base.getInventory().setItem(3, i8);
            base.getInventory().setItem(4, i9);
            base.getInventory().setItem(5, i10);
            base.getInventory().setItem(6, i16);
            base.getInventory().setItem(9, i14);
            base.getInventory().setItem(10, i13);
            base.getInventory().setItem(11, i15);
            base.getInventory().setItem(27, i11);
            base.getInventory().setItem(28, i12);
            base.getInventory().setHelmet(i1);
            base.getInventory().setChestplate(i2);
            base.getInventory().setLeggings(i3);
            base.getInventory().setBoots(i4);
            base.getInventory().setItemInOffHand(i17);
        }

        ElementalsX.newChain().async(() -> {
            try (Statement statement = ElementalsX.getDatabase().createStatement()) {
                try (ResultSet resultSet = statement.executeQuery("SELECT next FROM randomtp WHERE uuid='" + base.getUniqueId().toString() + "';")) {
                    if (resultSet.next()) {
                        this.lastRandomTeleport.set(resultSet.getLong("next"));
                    } else {
                        try (Statement statement1 = ElementalsX.getDatabase().createStatement()) {
                            statement1.executeUpdate("INSERT INTO randomtp (uuid, next) VALUES ('" + this.uuid.toString() + "', '" + this.lastRandomTeleport.get() + "');");
                        }
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }).execute();
    }

    public Player getBase() {
        return Bukkit.getPlayer(this.uuid);
    }

    public DamageCause getLastDamageCause() {
        return this.lastDamageCause;
    }

    public boolean hasPermission(String node) {
        if (this.getBase().isOp())
            return true;
        return ElementalsX.getPermission().has(this.getBase(), node);
    }

    public boolean hasSounds() {
        return this.sounds;
    }

    public boolean isIgnoringPlacingFields() {
        return this.ignorePlaceProtection;
    }

    public void setLastDamageCause(DamageCause cause) {
        this.lastDamageCause = cause;
    }

    public void setIgnorePlacingFields(boolean b) {
        this.ignorePlaceProtection = b;
    }

    public void toggleField(boolean b) {
        this.field = b;
    }

    public void setLastFieldOwner(UUID owner) {
        this.lastFieldOwner = owner;
    }

    public void toggleSounds(boolean b) {
        this.sounds = b;
    }

    public void save(boolean disable) {
        if (disable) {
            try (Statement statement = ElementalsX.getDatabase().createStatement()) {
                statement.executeUpdate(
                        "UPDATE randomtp SET next='" + this.lastRandomTeleport + "' WHERE uuid='" + this.uuid.toString() + "';");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } else {
            ElementalsX.newChain().async(() -> {
                try (Statement statement = ElementalsX.getDatabase().createStatement()) {
                    statement.executeUpdate(
                            "UPDATE randomtp SET next='" + this.lastRandomTeleport + "' WHERE uuid='" + this.uuid.toString() + "';");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }).execute();
        }
    }

    public UUID getUUID() {
        return this.uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof User))
            return false;
        User oUser = (User) o;
        return this.uuid.equals(oUser.getUUID());
    }

    @Override
    public int hashCode() {
        return this.uuid.hashCode() * 757;
    }
}
