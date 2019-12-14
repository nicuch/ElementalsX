package ro.nicuch.elementalsx;

import net.minecraft.server.v1_14_R1.IChatBaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ro.nicuch.elementalsx.elementals.ElementalsUtil;
import ro.nicuch.elementalsx.protection.FieldUtil;

import java.sql.ResultSet;
import java.util.Objects;
import java.util.UUID;

public class User {
    private final Player base;
    private boolean ignorePlaceProtection;
    private boolean sounds;
    private DamageCause lastDamageCause;
    private boolean field;
    private UUID lastFieldOwner;
    private int thirstLevel;
    private boolean pack;
    private boolean messages;

    public boolean hasMsgActive() {
        return this.messages;
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
        this.base = base;
        if (this.base.getLastDamageCause() == null)
            this.lastDamageCause = DamageCause.CUSTOM;
        else
            this.lastDamageCause = base.getLastDamageCause().getCause();
        this.sounds = true;
        this.pack = false;
        if (FieldUtil.isFieldAtLocation(this.base.getLocation())) {
            this.lastFieldOwner = FieldUtil.getFieldByLocation(this.base.getLocation()).getOwner();
            this.field = true;
            this.base.setCollidable(false);
        } else {
            this.field = false;
            this.base.setCollidable(true);
        }
        Bukkit.getScheduler().runTaskAsynchronously(ElementalsX.get(), () -> {
            try {
                ResultSet rs = ElementalsX.getBase()
                        .prepareStatement(
                                "SELECT points FROM pikapoints WHERE uuid='" + base.getUniqueId().toString() + "';")
                        .executeQuery();
                if (!rs.next()) {
                    ElementalsX.getBase().prepareStatement("INSERT INTO pikapoints(uuid, points) VALUES('"
                            + base.getUniqueId().toString() + "', '" + 0 + "');").executeUpdate();
                    Bukkit.getScheduler().runTask(ElementalsX.get(), () -> {
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
                        this.base.getInventory().setItem(0, i5);
                        this.base.getInventory().setItem(1, i6);
                        this.base.getInventory().setItem(2, i7);
                        this.base.getInventory().setItem(3, i8);
                        this.base.getInventory().setItem(4, i9);
                        this.base.getInventory().setItem(5, i10);
                        this.base.getInventory().setItem(6, i16);
                        this.base.getInventory().setItem(9, i14);
                        this.base.getInventory().setItem(10, i13);
                        this.base.getInventory().setItem(11, i15);
                        this.base.getInventory().setItem(27, i11);
                        this.base.getInventory().setItem(28, i12);
                        this.base.getInventory().setHelmet(i1);
                        this.base.getInventory().setChestplate(i2);
                        this.base.getInventory().setLeggings(i3);
                        this.base.getInventory().setBoots(i4);
                        this.base.getInventory().setItemInOffHand(i17);
                    });
                }
            } catch (Exception exception) {
                exception.printStackTrace();
                base.sendMessage(ElementalsUtil.color("&a&l[&6PikaCraft&a] &eEroare! Contacteaza un admin!"));
            }
        });
    }

    public Player getBase() {
        return this.base;
    }

    public DamageCause getLastDamageCause() {
        return this.lastDamageCause;
    }

    public boolean hasPermission(String node) {
        if (this.base.isOp())
            return true;
        return ElementalsX.getPermission().has(this.base, node);
    }

    public boolean hasSounds() {
        return this.sounds;
    }

    public boolean isIgnoringPlacingFields() {
        return this.ignorePlaceProtection;
    }

    public void sendMessage(IChatBaseComponent icbc) {
        ((CraftPlayer) this.base).getHandle().sendMessage(icbc);
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

    public void save() {
        try {
            ElementalsX.getBase().prepareStatement(
                    "UPDATE pikapoints SET points='" + 0 + "' WHERE uuid='" + base.getUniqueId().toString() + "';")
                    .executeUpdate();
        } catch (Exception exception) {
            //TODO backup
            exception.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(base.getUniqueId(), user.base.getUniqueId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(base.getUniqueId());
    }
}
