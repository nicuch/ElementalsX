package ro.nicuch.elementalsx;

import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.minecraft.server.v1_14_R1.IChatBaseComponent;
import ro.nicuch.elementalsx.ElementalsX.Nano;
import ro.nicuch.elementalsx.elementals.ElementalsUtil;
import ro.nicuch.elementalsx.protection.FieldUtil;

public class User {
    private final Player base;
    private int points;
    private Nano nanoPick;
    private Nano nanoAxe;
    private Nano nanoSpade;
    private boolean ignorePlaceProtection;
    private int pvpTicks;
    private boolean pvp;
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
        this.points = 0;
        if (this.base.getLastDamageCause() == null)
            this.lastDamageCause = DamageCause.CUSTOM;
        else
            this.lastDamageCause = base.getLastDamageCause().getCause();
        this.nanoPick = ElementalsX.Nano.A;
        this.nanoSpade = ElementalsX.Nano.A;
        this.nanoAxe = ElementalsX.Nano.A;
        this.pvpTicks = 0;
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
                            + base.getUniqueId().toString() + "', '" + points + "');").executeUpdate();
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
                        ItemStack i13 = new ItemStack(Material.OAK_WOOD, 16);
                        ItemStack i14 = new ItemStack(Material.COBBLESTONE, 32);
                        ItemStack i15 = new ItemStack(Material.SADDLE);
                        ItemStack i16 = new ItemStack(Material.LEAD, 2);
                        ItemStack i17 = new ItemStack(Material.SHIELD);
                        ItemMeta m1 = i1.getItemMeta();
                        ItemMeta m2 = i2.getItemMeta();
                        ItemMeta m3 = i3.getItemMeta();
                        ItemMeta m4 = i4.getItemMeta();
                        ItemMeta m5 = i5.getItemMeta();
                        ItemMeta m6 = i6.getItemMeta();
                        ItemMeta m7 = i7.getItemMeta();
                        ItemMeta m8 = i8.getItemMeta();
                        ItemMeta m11 = i11.getItemMeta();
                        ItemMeta m12 = i12.getItemMeta();
                        List<String> lore = Collections.singletonList(ElementalsUtil.color("&a&l[&6PikaCraft&a&l]"));
                        m1.setDisplayName(ElementalsUtil.color("&7Casca de Piele"));
                        m2.setDisplayName(ElementalsUtil.color("&7Plosa de Piele"));
                        m3.setDisplayName(ElementalsUtil.color("&7Pantaloni de Piele"));
                        m4.setDisplayName(ElementalsUtil.color("&7Botosi de Piele"));
                        m5.setDisplayName(ElementalsUtil.color("&7Tarnacop de Piatra"));
                        m6.setDisplayName(ElementalsUtil.color("&7Sabie de Piatra"));
                        m7.setDisplayName(ElementalsUtil.color("&7Lopata de Piatra"));
                        m8.setDisplayName(ElementalsUtil.color("&7Topor de Piatra"));
                        m11.setDisplayName(ElementalsUtil.color("&bProtectie de Diamant &8(&a51x256x52&8)"));
                        m12.setDisplayName(ElementalsUtil.color("&bDiamond &aof &6Pikachu"));
                        m1.setLore(lore);
                        m2.setLore(lore);
                        m3.setLore(lore);
                        m4.setLore(lore);
                        m5.setLore(lore);
                        m6.setLore(lore);
                        m7.setLore(lore);
                        m8.setLore(lore);
                        m11.setLore(lore);
                        i1.setItemMeta(m1);
                        i2.setItemMeta(m2);
                        i3.setItemMeta(m3);
                        i4.setItemMeta(m4);
                        i5.setItemMeta(m5);
                        i6.setItemMeta(m6);
                        i7.setItemMeta(m7);
                        i8.setItemMeta(m8);
                        i11.setItemMeta(m11);
                        i12.setItemMeta(m12);
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

    public void addPoints(int i) {
        this.points += i;
    }

    public Player getBase() {
        return this.base;
    }

    public DamageCause getLastDamageCause() {
        return this.lastDamageCause;
    }

    public Nano getNanoAxeType() {
        return this.nanoAxe;
    }

    public Nano getNanoPickType() {
        return this.nanoPick;
    }

    public Nano getNanoSpadeType() {
        return this.nanoSpade;
    }

    public int getPoints() {
        return this.points;
    }

    public int getPvpTicks() {
        return this.pvpTicks;
    }

    public boolean hasPermission(String node) {
        if (this.base.isOp())
            return true;
        return ElementalsX.getPermission().has(this.base, node);
    }

    public boolean hasSounds() {
        return this.sounds;
    }

    public boolean isInPvp() {
        return this.pvp;
    }

    public boolean isIgnoringPlacingFields() {
        return this.ignorePlaceProtection;
    }

    public void removePoints(int i) {
        this.points -= i;
        if (this.points < 0)
            this.points = 0;
    }

    public void sendMessage(IChatBaseComponent icbc) {
        ((CraftPlayer) this.base).getHandle().sendMessage(icbc);
    }

    public void setLastDamageCause(DamageCause cause) {
        this.lastDamageCause = cause;
    }

    public void setNanoAxeType(Nano type) {
        this.nanoAxe = type;
    }

    public void setNanoPickType(Nano type) {
        this.nanoPick = type;
    }

    public void setNanoSpadeType(Nano type) {
        this.nanoSpade = type;
    }

    public void setIgnorePlacingFields(boolean b) {
        this.ignorePlaceProtection = b;
    }

    public void setPoints(int i) {
        this.points = i;
        if (this.points < 0)
            this.points = 0;
    }

    public void setPvpTicks(int i) {
        if (i >= 0)
            this.pvpTicks = i;
    }

    public void toggleField(boolean b) {
        this.field = b;
    }

    public void setLastFieldOwner(UUID owner) {
        this.lastFieldOwner = owner;
    }

    public void togglePvp(boolean b) {
        this.pvp = b;
    }

    public void toggleSounds(boolean b) {
        this.sounds = b;
    }

    public void save() {
        try {
            ElementalsX.getBase().prepareStatement(
                    "UPDATE pikapoints SET points='" + points + "' WHERE uuid='" + base.getUniqueId().toString() + "';")
                    .executeUpdate();
        } catch (Exception exception) {
            //TODO backup
            exception.printStackTrace();
        }
    }
}
