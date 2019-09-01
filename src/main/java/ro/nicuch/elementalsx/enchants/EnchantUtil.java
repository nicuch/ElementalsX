package ro.nicuch.elementalsx.enchants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Dropper;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.util.Vector;

import ro.nicuch.elementalsx.elementals.ElementalsUtil;
import ro.nicuch.elementalsx.protection.FieldUtil;

public class EnchantUtil {

    public enum CCurseEnchType {
        // normal
        PROTECTION1("&fProtection I"), PROTECTION2("&fProtection II"), PROTECTION3("&fProtection III"), PROTECTION4(
                "&fProtection IV"), FIREPROTECTION1("&fFire Protection I"), FIREPROTECTION2(
                "&fFire Protection II"), FIREPROTECTION3("&fFire Protection III"), FIREPROTECTION4(
                "&fFire Protection IV"), FEATHERFALLING1("&fFeather Falling I"), FEATHERFALLING2(
                "&fFeather Falling II"), FEATHERFALLING3(
                "&fFeather Falling III"), FEATHERFALLING4(
                "&fFeather Falling IV"), BLASTPROTECTION1(
                "&fBlast Protection I"), BLASTPROTECTION2(
                "&fBlast Protection II"), BLASTPROTECTION3(
                "&fBlast Protection III"), BLASTPROTECTION4(
                "&fBlast Protection IV"), PROJPROTECTION1(
                "&fProjectile Protection I"), PROJPROTECTION2(
                "&fProjectile Protection II"), PROJPROTECTION3(
                "&fProjectile Protection III"), PROJPROTECTION4(
                "&fProjectile Protection IV"), RESPIRATION1(
                "&fRespiration I"), RESPIRATION2(
                "&fRespiration II"), RESPIRATION3(
                "&fRespiration III"), AQUAAFFINITY(
                "&fAqua Affinity"), THORNS1(
                "&fThorns I"), THORNS2(
                "&fThorns II"), THORNS3(
                "&fThorns III"), DEPTHSTRIDER1(
                "&fDepth Strider I"), DEPTHSTRIDER2(
                "&fDepth Strider II"), DEPTHSTRIDER3(
                "&fDepth Strider III"), FROSTWALKER1(
                "&fFrost Walker I"), FROSTWALKER2(
                "&fFrost Walker II"), SHARPNESS1(
                "&fSharpness I"), SHARPNESS2(
                "&fSharpness II"), SHARPNESS3(
                "&fSharpness III"), SHARPNESS4(
                "&fSharpness IV"), SHARPNESS5(
                "&fSharpness V"), SMITE1(
                "&fSmite I"), SMITE2(
                "&fSmite II"), SMITE3(
                "&fSmite III"), SMITE4(
                "&fSmite IV"), SMITE5(
                "&fSmite V"), BANEOFARTHROPODS1(
                "&fBane of Arthropods I"), BANEOFARTHROPODS2(
                "&fBane of Arthropods II"), BANEOFARTHROPODS3(
                "&fBane of Arthropods III"), BANEOFARTHROPODS4(
                "&fBane of Arthropods IV"), BANEOFARTHROPODS5(
                "&fBane of Arthropods V"), KNOCKBACK1(
                "&fKnockback I"), KNOCKBACK2(
                "&fKnockback II"), FIREASPECT1(
                "&fFire Aspect I"), FIREASPECT2(
                "&fFire Aspect II"), LOOTING1(
                "&fLooting I"), LOOTING2(
                "&fLooting II"), LOOTING3(
                "&fLooting III"), SWEEPINGEDGE1(
                "&fSweeping Edge I"), SWEEPINGEDGE2(
                "&fSweeping Edge II"), SWEEPINGEDGE3(
                "&fSweeping Edge III"), EFFICIENCY1(
                "&fEfficiency I"), EFFICIENCY2(
                "&fEfficiency II"), EFFICIENCY3(
                "&fEfficiency III"), EFFICIENCY4(
                "&fEfficiency IV"), EFFICIENCY5(
                "&fEfficiency V"), SILKTOUCH(
                "&fSilk Touch"), UNBREAKING1(
                "&fUnbreaking I"), UNBREAKING2(
                "&fUnbreaking II"), UNBREAKING3(
                "&fUnbreaking III"), FORTUNE1(
                "&fFortune I"), FORTUNE2(
                "&fFortune II"), FORTUNE3(
                "&fFortune III"), POWER1(
                "&fPower I"), POWER2(
                "&fPower II"), POWER3(
                "&fPower III"), POWER4(
                "&fPower IV"), POWER5(
                "&fPower V"), PUNCH1(
                "&fPunch I"), PUNCH2(
                "&fPunch II"), FLAME(
                "&fFlame"), INFINITY(
                "&fInfinity"), LUCKOFTHESEA1(
                "&fLuck of the Sea I"), LUCKOFTHESEA2(
                "&fLuck of the Sea II"), LUCKOFTHESEA3(
                "&fLuck of the Sea III"), LURE1(
                "&fLure I"), LURE2(
                "&fLure II"), LURE3(
                "&fLure III"), MENDING(
                "&fMending"),
        // custom
        STRIKE1("&fStrike I"), STRIKE2("&fStrike II"), REGENERATION1("&fRegeneration I"), REGENERATION2(
                "&fRegeneration II"), BLIND("&fBlindness I"), LIFESTEAL("&fLifesteal I"), GOOEY(
                "&fGooey I"), ICEASPECT1("&fIce Aspect I"), ICEASPECT2("&fIce Aspect II"), POISON1(
                "&fPoison I"), POISON2("&fPoison II"), WITHER1("&fWither I"), WITHER2(
                "&fWither II"), ENERGIZING1("&fEnergizing I"), ENERGIZING2(
                "&fEnergizing II"), FIREWORK("&fFirework I"), MOLOTOV(
                "&fMolotov I"), BLAZE("&fBlaze I"), MOLTEN1(
                "&fMolten I"), MOLTEN2("&fMolten II"), HARDENED1(
                "&fHardened I"), HARDENED2(
                "&fHardened II"), FROZEN(
                "&fFrozen I"), OBSIDIANSHIELD(
                "&fObsidian Shield I"), WHEELS1(
                "&fWheels I"), WHEELS2(
                "&fWheels II"), ROCKETS1(
                "&fRockets I"), ROCKETS2(
                "&fRockets II"), GLOWING(
                "&fGlowing I"),
        // new
        SHARPSHOOTER1("&fSharpshooter I"), SHARPSHOOTER2("&fSharpshooter II"), SHARPSHOOTER3(
                "&fSharpshooter III"), WISDOM1("&fWisdom I"), WISDOM2("&fWisdom II"), HUNTER1("&fHunter I"), HUNTER2(
                "&fHunter II"), HUNTER3("&fHunter III"), NECROMANCER1("&fNecromancer I"), NECROMANCER2(
                "&fNecromancer II"), NECROMANCER3("&fNecromancer III"), PESTICIDE1(
                "&fPesticide I"), PESTICIDE2("&fPesticide II"), PESTICIDE3(
                "&fPesticide III"), ANTIVENOM("&fAnti Venom I"), STALKER1(
                "&fStalker I"), STALKER2(
                "&fStalker II"), STALKER3("&fStalker III");

        private final String name;

        CCurseEnchType(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    public enum CEnchantType {
        STRIKE1("&7Strike I", 1.25), STRIKE2("&7Strike II", 2.5), REGENERATION1("&7Regeneration I", 100), REGENERATION2(
                "&7Regeneration II",
                100), BLIND("&7Blindness I", 33.3), LIFESTEAL("&7Lifesteal I", 33.3), GOOEY("&7Gooey I",
                33.3), ICEASPECT1("&7Ice Aspect I", 20), ICEASPECT2("&7Ice Aspect II", 40), POISON1(
                "&7Poison I",
                15), POISON2("&7Poison II", 30), WITHER1("&7Wither I", 10), WITHER2("&7Wither II",
                20), ENERGIZING1("&7Energizing I", 22.3), ENERGIZING2("&7Energizing II",
                44.6), FIREWORK("&7Firework I", 50), MOLOTOV("&7Molotov I",
                33.3), BLAZE("&7Blaze I", 2.5), MOLTEN1("&7Molten I",
                25), MOLTEN2("&7Molten II", 50), HARDENED1(
                "&7Hardened I",
                15), HARDENED2("&7Hardened II", 30), FROZEN(
                "&7Frozen I", 30), OBSIDIANSHIELD(
                "&7Obsidian Shield I",
                100), WHEELS1("&7Wheels I",
                100), WHEELS2(
                "&7Wheels II",
                100), ROCKETS1(
                "&7Rockets I",
                100), ROCKETS2(
                "&7Rockets II",
                100), GLOWING(
                "&7Glowing I",
                100),
        // new
        SHARPSHOOTER1("&7Sharpshooter I", 50), SHARPSHOOTER2("&7Sharpshooter II", 37.5), SHARPSHOOTER3(
                "&7Sharpshooter III",
                25), WISDOM1("&7Wisdom I", 66.6), WISDOM2("&7Wisdom II", 33.3), HUNTER1("&7Hunter I",
                100), HUNTER2("&7Hunter II", 100), HUNTER3("&7Hunter III", 100), NECROMANCER1("&7Necromancer I",
                75), NECROMANCER2("&7Necromancer II", 75), NECROMANCER3("&7Necromancer III",
                75), PESTICIDE1("&7Pesticide I", 75), PESTICIDE2("&7Pesticide II",
                75), PESTICIDE3("&7Pesticide III", 75), ANTIVENOM("&7Anti Venom I",
                30), STALKER1("&7Stalker I", 85), STALKER2("&7Stalker II",
                70), STALKER3("&7Stalker III", 55),
        // feature
        SLIMESHIELD("&7Slime Shield I", 66.6), WEIGHTED1("&7Wighted I", 100), WEIGHTED2("&7Wighted II",
                100), MAGNET("&7Magnet I", 100), HOMING("&7Homing I", 100);

        private final String name;
        private final double chance;

        CEnchantType(String name, double chance) {
            this.name = name;
            this.chance = chance;
        }

        public String getName() {
            return this.name;
        }

        public double getChance() {
            return this.chance;
        }

        public static List<CEnchantType> getEnchantsForEnderCase() {
            return Arrays.asList(CEnchantType.BLAZE, CEnchantType.BLIND,
                    CEnchantType.ENERGIZING1, CEnchantType.ENERGIZING2, CEnchantType.FIREWORK, CEnchantType.FROZEN,
                    CEnchantType.GLOWING, CEnchantType.GOOEY, CEnchantType.HARDENED1, CEnchantType.HARDENED2,
                    CEnchantType.ICEASPECT1, CEnchantType.ICEASPECT2, CEnchantType.LIFESTEAL, CEnchantType.MOLOTOV,
                    CEnchantType.MOLTEN1, CEnchantType.MOLTEN2, CEnchantType.OBSIDIANSHIELD, CEnchantType.POISON1,
                    CEnchantType.POISON2, CEnchantType.REGENERATION1, CEnchantType.REGENERATION2, CEnchantType.ROCKETS1,
                    CEnchantType.ROCKETS2, CEnchantType.STRIKE1, CEnchantType.STRIKE2, CEnchantType.WHEELS1,
                    CEnchantType.WHEELS2, CEnchantType.WITHER1, CEnchantType.WITHER2);
        }

    }

    public static int getEnchantCount(ItemStack item) {
        int i = 0;
        i += item.getEnchantments().keySet().size();
        for (CEnchantType type : CEnchantType.values())
            if (hasEnchant(item, type))
                i++;
        return i;
    }

    public static ItemStack changeEnchantTo(ItemStack item, CEnchantType one, CEnchantType two) {
        ItemMeta itemMeta = item.getItemMeta();
        List<String> lore = itemMeta.getLore();
        for (int i = 0; i < lore.size(); i++)
            if (lore.get(i).equals(one.getName()))
                lore.set(i, two.getName());
            else
                lore.set(i, lore.get(i));
        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack getBook(CEnchantType type) {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK, 1);
        ItemMeta meta = book.getItemMeta();
        meta.setDisplayName(ElementalsUtil.color("&eEnchanted Book"));
        List<String> lore = Collections.singletonList(ElementalsUtil.color(type.getName()));
        meta.setLore(lore);
        book.setItemMeta(meta);
        return book;
    }

    public static ItemStack getCurseBook(CCurseEnchType type) {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK, 1);
        ItemMeta meta = book.getItemMeta();
        meta.setDisplayName(ElementalsUtil.color("&eEnchanted Book"));
        meta.setLore(Arrays.asList(ElementalsUtil.color("&cCurse of Anti-Enchanting"), ElementalsUtil.color("&aRemoving: " + type.getName())));
        book.setItemMeta(meta);
        return book;
    }

    public static boolean isEnchantedBook(ItemStack item, CEnchantType type) {
        if (item == null)
            return false;
        if (!item.getType().equals(Material.ENCHANTED_BOOK))
            return false;
        if (!item.hasItemMeta())
            return false;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore())
            return false;
        if (meta.getLore().isEmpty())
            return false;
        if (meta.getLore().get(0) == null)
            return false;
        return meta.getLore().get(0).equals(type.getName());
    }

    public static boolean isCurseEnchBook(ItemStack item, CCurseEnchType type) {
        if (!item.getType().equals(Material.ENCHANTED_BOOK))
            return false;
        if (!item.hasItemMeta())
            return false;
        ItemMeta meta = item.getItemMeta();
        if (!meta.hasLore())
            return false;
        if (meta.getLore().isEmpty())
            return false;
        if (meta.getLore().size() < 2)
            return false;
        if (meta.getLore().get(1) == null)
            return false;
        return meta.getLore().get(1).equals("&aRemoving: " + type.getName());
    }

    public static ItemStack getEnchantedBook(CCurseEnchType type) {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
        switch (type) {
            case ANTIVENOM:
                return getBook(CEnchantType.ANTIVENOM);
            case BLAZE:
                return getBook(CEnchantType.BLAZE);
            case BLIND:
                return getBook(CEnchantType.BLIND);
            case ENERGIZING1:
                return getBook(CEnchantType.ENERGIZING1);
            case ENERGIZING2:
                return getBook(CEnchantType.ENERGIZING2);
            case FIREWORK:
                return getBook(CEnchantType.FIREWORK);
            case FROZEN:
                return getBook(CEnchantType.FROZEN);
            case GLOWING:
                return getBook(CEnchantType.GLOWING);
            case GOOEY:
                return getBook(CEnchantType.GOOEY);
            case HARDENED1:
                return getBook(CEnchantType.HARDENED1);
            case HARDENED2:
                return getBook(CEnchantType.HARDENED2);
            case HUNTER1:
                return getBook(CEnchantType.HUNTER1);
            case HUNTER2:
                return getBook(CEnchantType.HUNTER2);
            case HUNTER3:
                return getBook(CEnchantType.HUNTER3);
            case ICEASPECT1:
                return getBook(CEnchantType.ICEASPECT1);
            case ICEASPECT2:
                return getBook(CEnchantType.ICEASPECT2);
            case LIFESTEAL:
                return getBook(CEnchantType.LIFESTEAL);
            case MOLOTOV:
                return getBook(CEnchantType.MOLOTOV);
            case MOLTEN1:
                return getBook(CEnchantType.MOLTEN1);
            case MOLTEN2:
                return getBook(CEnchantType.MOLTEN2);
            case NECROMANCER1:
                return getBook(CEnchantType.NECROMANCER1);
            case NECROMANCER2:
                return getBook(CEnchantType.NECROMANCER2);
            case NECROMANCER3:
                return getBook(CEnchantType.NECROMANCER3);
            case OBSIDIANSHIELD:
                return getBook(CEnchantType.OBSIDIANSHIELD);
            case PESTICIDE1:
                return getBook(CEnchantType.PESTICIDE1);
            case PESTICIDE2:
                return getBook(CEnchantType.PESTICIDE2);
            case PESTICIDE3:
                return getBook(CEnchantType.PESTICIDE3);
            case POISON1:
                return getBook(CEnchantType.POISON1);
            case POISON2:
                return getBook(CEnchantType.POISON2);
            case REGENERATION1:
                return getBook(CEnchantType.REGENERATION1);
            case REGENERATION2:
                return getBook(CEnchantType.REGENERATION2);
            case ROCKETS1:
                return getBook(CEnchantType.ROCKETS1);
            case ROCKETS2:
                return getBook(CEnchantType.ROCKETS2);
            case SHARPSHOOTER1:
                return getBook(CEnchantType.SHARPSHOOTER1);
            case SHARPSHOOTER2:
                return getBook(CEnchantType.SHARPSHOOTER2);
            case SHARPSHOOTER3:
                return getBook(CEnchantType.SHARPSHOOTER3);
            case STALKER1:
                return getBook(CEnchantType.STALKER1);
            case STALKER2:
                return getBook(CEnchantType.STALKER2);
            case STALKER3:
                return getBook(CEnchantType.STALKER3);
            case STRIKE1:
                return getBook(CEnchantType.STRIKE1);
            case STRIKE2:
                return getBook(CEnchantType.STRIKE1);
            case WHEELS1:
                return getBook(CEnchantType.WHEELS1);
            case WHEELS2:
                return getBook(CEnchantType.WHEELS2);
            case WISDOM1:
                return getBook(CEnchantType.WISDOM1);
            case WISDOM2:
                return getBook(CEnchantType.WISDOM2);
            case WITHER1:
                return getBook(CEnchantType.WITHER1);
            case WITHER2:
                return getBook(CEnchantType.WITHER2);
            case UNBREAKING1:
                meta.addStoredEnchant(Enchantment.DURABILITY, 1, true);
                item.setItemMeta(meta);
                item.setItemMeta(meta);
                return item;
            case UNBREAKING2:
                meta.addStoredEnchant(Enchantment.DURABILITY, 2, true);
                item.setItemMeta(meta);
                return item;
            case UNBREAKING3:
                meta.addStoredEnchant(Enchantment.DURABILITY, 3, true);
                item.setItemMeta(meta);
                return item;
            case AQUAAFFINITY:
                meta.addStoredEnchant(Enchantment.WATER_WORKER, 1, true);
                item.setItemMeta(meta);
                return item;
            case BANEOFARTHROPODS1:
                meta.addStoredEnchant(Enchantment.DAMAGE_ARTHROPODS, 1, true);
                item.setItemMeta(meta);
                return item;
            case BANEOFARTHROPODS2:
                meta.addStoredEnchant(Enchantment.DAMAGE_ARTHROPODS, 2, true);
                item.setItemMeta(meta);
                return item;
            case BANEOFARTHROPODS3:
                meta.addStoredEnchant(Enchantment.DAMAGE_ARTHROPODS, 3, true);
                item.setItemMeta(meta);
                return item;
            case BANEOFARTHROPODS4:
                meta.addStoredEnchant(Enchantment.DAMAGE_ARTHROPODS, 4, true);
                item.setItemMeta(meta);
                return item;
            case BANEOFARTHROPODS5:
                meta.addStoredEnchant(Enchantment.DAMAGE_ARTHROPODS, 5, true);
                item.setItemMeta(meta);
                return item;
            case BLASTPROTECTION1:
                meta.addStoredEnchant(Enchantment.PROTECTION_EXPLOSIONS, 1, true);
                item.setItemMeta(meta);
                return item;
            case BLASTPROTECTION2:
                meta.addStoredEnchant(Enchantment.PROTECTION_EXPLOSIONS, 2, true);
                item.setItemMeta(meta);
                return item;
            case BLASTPROTECTION3:
                meta.addStoredEnchant(Enchantment.PROTECTION_EXPLOSIONS, 3, true);
                item.setItemMeta(meta);
                return item;
            case BLASTPROTECTION4:
                meta.addStoredEnchant(Enchantment.PROTECTION_EXPLOSIONS, 4, true);
                item.setItemMeta(meta);
                return item;
            case DEPTHSTRIDER1:
                meta.addStoredEnchant(Enchantment.DEPTH_STRIDER, 1, true);
                item.setItemMeta(meta);
                return item;
            case DEPTHSTRIDER2:
                meta.addStoredEnchant(Enchantment.DEPTH_STRIDER, 2, true);
                item.setItemMeta(meta);
                return item;
            case DEPTHSTRIDER3:
                meta.addStoredEnchant(Enchantment.DEPTH_STRIDER, 3, true);
                item.setItemMeta(meta);
                return item;
            case EFFICIENCY1:
                meta.addStoredEnchant(Enchantment.DIG_SPEED, 1, true);
                item.setItemMeta(meta);
                return item;
            case EFFICIENCY2:
                meta.addStoredEnchant(Enchantment.DIG_SPEED, 2, true);
                item.setItemMeta(meta);
                return item;
            case EFFICIENCY3:
                meta.addStoredEnchant(Enchantment.DIG_SPEED, 3, true);
                item.setItemMeta(meta);
                return item;
            case EFFICIENCY4:
                meta.addStoredEnchant(Enchantment.DIG_SPEED, 4, true);
                item.setItemMeta(meta);
                return item;
            case EFFICIENCY5:
                meta.addStoredEnchant(Enchantment.DIG_SPEED, 5, true);
                item.setItemMeta(meta);
                return item;
            case FEATHERFALLING1:
                meta.addStoredEnchant(Enchantment.PROTECTION_FALL, 1, true);
                item.setItemMeta(meta);
                return item;
            case FEATHERFALLING2:
                meta.addStoredEnchant(Enchantment.PROTECTION_FALL, 2, true);
                item.setItemMeta(meta);
                return item;
            case FEATHERFALLING3:
                meta.addStoredEnchant(Enchantment.PROTECTION_FALL, 3, true);
                item.setItemMeta(meta);
                return item;
            case FEATHERFALLING4:
                meta.addStoredEnchant(Enchantment.PROTECTION_FALL, 4, true);
                item.setItemMeta(meta);
                return item;
            case FIREASPECT1:
                meta.addStoredEnchant(Enchantment.FIRE_ASPECT, 1, true);
                item.setItemMeta(meta);
                return item;
            case FIREASPECT2:
                meta.addStoredEnchant(Enchantment.FIRE_ASPECT, 2, true);
                item.setItemMeta(meta);
                return item;
            case FIREPROTECTION1:
                meta.addStoredEnchant(Enchantment.PROTECTION_FIRE, 1, true);
                item.setItemMeta(meta);
                return item;
            case FIREPROTECTION2:
                meta.addStoredEnchant(Enchantment.PROTECTION_FIRE, 2, true);
                item.setItemMeta(meta);
                return item;
            case FIREPROTECTION3:
                meta.addStoredEnchant(Enchantment.PROTECTION_FIRE, 3, true);
                item.setItemMeta(meta);
                return item;
            case FIREPROTECTION4:
                meta.addStoredEnchant(Enchantment.PROTECTION_FIRE, 4, true);
                item.setItemMeta(meta);
                return item;
            case FLAME:
                meta.addStoredEnchant(Enchantment.ARROW_FIRE, 1, true);
                item.setItemMeta(meta);
                return item;
            case FORTUNE1:
                meta.addStoredEnchant(Enchantment.LOOT_BONUS_BLOCKS, 1, true);
                item.setItemMeta(meta);
                return item;
            case FORTUNE2:
                meta.addStoredEnchant(Enchantment.LOOT_BONUS_BLOCKS, 2, true);
                item.setItemMeta(meta);
                return item;
            case FORTUNE3:
                meta.addStoredEnchant(Enchantment.LOOT_BONUS_BLOCKS, 3, true);
                item.setItemMeta(meta);
                return item;
            case FROSTWALKER1:
                meta.addStoredEnchant(Enchantment.FROST_WALKER, 1, true);
                item.setItemMeta(meta);
                return item;
            case FROSTWALKER2:
                meta.addStoredEnchant(Enchantment.FROST_WALKER, 2, true);
                item.setItemMeta(meta);
                return item;
            case INFINITY:
                meta.addStoredEnchant(Enchantment.ARROW_INFINITE, 1, true);
                item.setItemMeta(meta);
                return item;
            case KNOCKBACK1:
                meta.addStoredEnchant(Enchantment.KNOCKBACK, 1, true);
                item.setItemMeta(meta);
                return item;
            case KNOCKBACK2:
                meta.addStoredEnchant(Enchantment.KNOCKBACK, 2, true);
                item.setItemMeta(meta);
                return item;
            case LOOTING1:
                meta.addStoredEnchant(Enchantment.LOOT_BONUS_MOBS, 1, true);
                item.setItemMeta(meta);
                return item;
            case LOOTING2:
                meta.addStoredEnchant(Enchantment.LOOT_BONUS_MOBS, 2, true);
                item.setItemMeta(meta);
                return item;
            case LOOTING3:
                meta.addStoredEnchant(Enchantment.LOOT_BONUS_MOBS, 3, true);
                item.setItemMeta(meta);
                return item;
            case LUCKOFTHESEA1:
                meta.addStoredEnchant(Enchantment.LUCK, 1, true);
                item.setItemMeta(meta);
                return item;
            case LUCKOFTHESEA2:
                meta.addStoredEnchant(Enchantment.LUCK, 2, true);
                item.setItemMeta(meta);
                return item;
            case LUCKOFTHESEA3:
                meta.addStoredEnchant(Enchantment.LUCK, 3, true);
                item.setItemMeta(meta);
                return item;
            case LURE1:
                meta.addStoredEnchant(Enchantment.LURE, 1, true);
                item.setItemMeta(meta);
                return item;
            case LURE2:
                meta.addStoredEnchant(Enchantment.LURE, 2, true);
                item.setItemMeta(meta);
                return item;
            case LURE3:
                meta.addStoredEnchant(Enchantment.LURE, 3, true);
                item.setItemMeta(meta);
                return item;
            case MENDING:
                meta.addStoredEnchant(Enchantment.MENDING, 1, true);
                item.setItemMeta(meta);
                return item;
            case POWER1:
                meta.addStoredEnchant(Enchantment.ARROW_DAMAGE, 1, true);
                item.setItemMeta(meta);
                return item;
            case POWER2:
                meta.addStoredEnchant(Enchantment.ARROW_DAMAGE, 2, true);
                item.setItemMeta(meta);
                return item;
            case POWER3:
                meta.addStoredEnchant(Enchantment.ARROW_DAMAGE, 3, true);
                item.setItemMeta(meta);
                return item;
            case POWER4:
                meta.addStoredEnchant(Enchantment.ARROW_DAMAGE, 4, true);
                item.setItemMeta(meta);
                return item;
            case POWER5:
                meta.addStoredEnchant(Enchantment.ARROW_DAMAGE, 5, true);
                item.setItemMeta(meta);
                return item;
            case PROJPROTECTION1:
                meta.addStoredEnchant(Enchantment.PROTECTION_PROJECTILE, 1, true);
                item.setItemMeta(meta);
                return item;
            case PROJPROTECTION2:
                meta.addStoredEnchant(Enchantment.PROTECTION_PROJECTILE, 2, true);
                item.setItemMeta(meta);
                return item;
            case PROJPROTECTION3:
                meta.addStoredEnchant(Enchantment.PROTECTION_PROJECTILE, 3, true);
                item.setItemMeta(meta);
                return item;
            case PROJPROTECTION4:
                meta.addStoredEnchant(Enchantment.PROTECTION_PROJECTILE, 4, true);
                item.setItemMeta(meta);
                return item;
            case PROTECTION1:
                meta.addStoredEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
                item.setItemMeta(meta);
                return item;
            case PROTECTION2:
                meta.addStoredEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true);
                item.setItemMeta(meta);
                return item;
            case PROTECTION3:
                meta.addStoredEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3, true);
                item.setItemMeta(meta);
                return item;
            case PROTECTION4:
                meta.addStoredEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 4, true);
                item.setItemMeta(meta);
                return item;
            case PUNCH1:
                meta.addStoredEnchant(Enchantment.ARROW_KNOCKBACK, 1, true);
                item.setItemMeta(meta);
                return item;
            case PUNCH2:
                meta.addStoredEnchant(Enchantment.ARROW_KNOCKBACK, 2, true);
                item.setItemMeta(meta);
                return item;
            case RESPIRATION1:
                meta.addStoredEnchant(Enchantment.OXYGEN, 1, true);
                item.setItemMeta(meta);
                return item;
            case RESPIRATION2:
                meta.addStoredEnchant(Enchantment.OXYGEN, 2, true);
                item.setItemMeta(meta);
                return item;
            case RESPIRATION3:
                meta.addStoredEnchant(Enchantment.OXYGEN, 3, true);
                item.setItemMeta(meta);
                return item;
            case SHARPNESS1:
                meta.addStoredEnchant(Enchantment.DAMAGE_ALL, 1, true);
                item.setItemMeta(meta);
                return item;
            case SHARPNESS2:
                meta.addStoredEnchant(Enchantment.DAMAGE_ALL, 2, true);
                item.setItemMeta(meta);
                return item;
            case SHARPNESS3:
                meta.addStoredEnchant(Enchantment.DAMAGE_ALL, 3, true);
                item.setItemMeta(meta);
                return item;
            case SHARPNESS4:
                meta.addStoredEnchant(Enchantment.DAMAGE_ALL, 4, true);
                item.setItemMeta(meta);
                return item;
            case SHARPNESS5:
                meta.addStoredEnchant(Enchantment.DAMAGE_ALL, 5, true);
                item.setItemMeta(meta);
                return item;
            case SILKTOUCH:
                meta.addStoredEnchant(Enchantment.SILK_TOUCH, 1, true);
                item.setItemMeta(meta);
                return item;
            case SMITE1:
                meta.addStoredEnchant(Enchantment.DAMAGE_UNDEAD, 1, true);
                item.setItemMeta(meta);
                return item;
            case SMITE2:
                meta.addStoredEnchant(Enchantment.DAMAGE_UNDEAD, 2, true);
                item.setItemMeta(meta);
                return item;
            case SMITE3:
                meta.addStoredEnchant(Enchantment.DAMAGE_UNDEAD, 3, true);
                item.setItemMeta(meta);
                return item;
            case SMITE4:
                meta.addStoredEnchant(Enchantment.DAMAGE_UNDEAD, 4, true);
                item.setItemMeta(meta);
                return item;
            case SMITE5:
                meta.addStoredEnchant(Enchantment.DAMAGE_UNDEAD, 5, true);
                item.setItemMeta(meta);
                return item;
            case SWEEPINGEDGE1:
                meta.addStoredEnchant(Enchantment.SWEEPING_EDGE, 1, true);
                item.setItemMeta(meta);
                return item;
            case SWEEPINGEDGE2:
                meta.addStoredEnchant(Enchantment.SWEEPING_EDGE, 2, true);
                item.setItemMeta(meta);
                return item;
            case SWEEPINGEDGE3:
                meta.addStoredEnchant(Enchantment.SWEEPING_EDGE, 3, true);
                item.setItemMeta(meta);
                return item;
            case THORNS1:
                meta.addStoredEnchant(Enchantment.THORNS, 1, true);
                item.setItemMeta(meta);
                return item;
            case THORNS2:
                meta.addStoredEnchant(Enchantment.THORNS, 2, true);
                item.setItemMeta(meta);
                return item;
            case THORNS3:
                meta.addStoredEnchant(Enchantment.THORNS, 3, true);
                item.setItemMeta(meta);
                return item;
            default:
                return item;
        }
    }

    public static ItemStack randomEnchant(ItemStack item) {
        return enchantItemWith(item, CCurseEnchType.values()[ElementalsUtil.nextInt(CCurseEnchType.values().length)]);
    }

    public static ItemStack enchantItemWith(ItemStack item, CCurseEnchType type) {
        switch (type) {
            case ANTIVENOM:
                return enchantItem(item, CEnchantType.ANTIVENOM);
            case BLAZE:
                return enchantItem(item, CEnchantType.BLAZE);
            case BLIND:
                return enchantItem(item, CEnchantType.BLIND);
            case ENERGIZING1:
                return enchantItem(item, CEnchantType.ENERGIZING1);
            case ENERGIZING2:
                return enchantItem(item, CEnchantType.ENERGIZING2);
            case FIREWORK:
                return enchantItem(item, CEnchantType.FIREWORK);
            case FROZEN:
                return enchantItem(item, CEnchantType.FROZEN);
            case GLOWING:
                return enchantItem(item, CEnchantType.GLOWING);
            case GOOEY:
                return enchantItem(item, CEnchantType.GOOEY);
            case HARDENED1:
                return enchantItem(item, CEnchantType.HARDENED1);
            case HARDENED2:
                return enchantItem(item, CEnchantType.HARDENED2);
            case HUNTER1:
                return enchantItem(item, CEnchantType.HUNTER1);
            case HUNTER2:
                return enchantItem(item, CEnchantType.HUNTER2);
            case HUNTER3:
                return enchantItem(item, CEnchantType.HUNTER3);
            case ICEASPECT1:
                return enchantItem(item, CEnchantType.ICEASPECT1);
            case ICEASPECT2:
                return enchantItem(item, CEnchantType.ICEASPECT2);
            case LIFESTEAL:
                return enchantItem(item, CEnchantType.LIFESTEAL);
            case MOLOTOV:
                return enchantItem(item, CEnchantType.MOLOTOV);
            case MOLTEN1:
                return enchantItem(item, CEnchantType.MOLTEN1);
            case MOLTEN2:
                return enchantItem(item, CEnchantType.MOLTEN2);
            case NECROMANCER1:
                return enchantItem(item, CEnchantType.NECROMANCER1);
            case NECROMANCER2:
                return enchantItem(item, CEnchantType.NECROMANCER2);
            case NECROMANCER3:
                return enchantItem(item, CEnchantType.NECROMANCER3);
            case OBSIDIANSHIELD:
                return enchantItem(item, CEnchantType.OBSIDIANSHIELD);
            case PESTICIDE1:
                return enchantItem(item, CEnchantType.PESTICIDE1);
            case PESTICIDE2:
                return enchantItem(item, CEnchantType.PESTICIDE2);
            case PESTICIDE3:
                return enchantItem(item, CEnchantType.PESTICIDE3);
            case POISON1:
                return enchantItem(item, CEnchantType.POISON1);
            case POISON2:
                return enchantItem(item, CEnchantType.POISON2);
            case REGENERATION1:
                return enchantItem(item, CEnchantType.REGENERATION1);
            case REGENERATION2:
                return enchantItem(item, CEnchantType.REGENERATION2);
            case ROCKETS1:
                return enchantItem(item, CEnchantType.ROCKETS1);
            case ROCKETS2:
                return enchantItem(item, CEnchantType.ROCKETS2);
            case SHARPSHOOTER1:
                return enchantItem(item, CEnchantType.SHARPSHOOTER1);
            case SHARPSHOOTER2:
                return enchantItem(item, CEnchantType.SHARPSHOOTER2);
            case SHARPSHOOTER3:
                return enchantItem(item, CEnchantType.SHARPSHOOTER3);
            case STALKER1:
                return enchantItem(item, CEnchantType.STALKER1);
            case STALKER2:
                return enchantItem(item, CEnchantType.STALKER2);
            case STALKER3:
                return enchantItem(item, CEnchantType.STALKER3);
            case STRIKE1:
                return enchantItem(item, CEnchantType.STRIKE1);
            case STRIKE2:
                return enchantItem(item, CEnchantType.STRIKE1);
            case WHEELS1:
                return enchantItem(item, CEnchantType.WHEELS1);
            case WHEELS2:
                return enchantItem(item, CEnchantType.WHEELS2);
            case WISDOM1:
                return enchantItem(item, CEnchantType.WISDOM1);
            case WISDOM2:
                return enchantItem(item, CEnchantType.WISDOM2);
            case WITHER1:
                return enchantItem(item, CEnchantType.WITHER1);
            case WITHER2:
                return enchantItem(item, CEnchantType.WITHER2);
            case UNBREAKING1:
                item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                return item;
            case UNBREAKING2:
                item.addUnsafeEnchantment(Enchantment.DURABILITY, 2);
                return item;
            case UNBREAKING3:
                item.addUnsafeEnchantment(Enchantment.DURABILITY, 3);
                return item;
            case AQUAAFFINITY:
                item.addUnsafeEnchantment(Enchantment.WATER_WORKER, 1);
                return item;
            case BANEOFARTHROPODS1:
                item.addUnsafeEnchantment(Enchantment.DAMAGE_ARTHROPODS, 1);
                return item;
            case BANEOFARTHROPODS2:
                item.addUnsafeEnchantment(Enchantment.DAMAGE_ARTHROPODS, 2);
                return item;
            case BANEOFARTHROPODS3:
                item.addUnsafeEnchantment(Enchantment.DAMAGE_ARTHROPODS, 3);
                return item;
            case BANEOFARTHROPODS4:
                item.addUnsafeEnchantment(Enchantment.DAMAGE_ARTHROPODS, 4);
                return item;
            case BANEOFARTHROPODS5:
                item.addUnsafeEnchantment(Enchantment.DAMAGE_ARTHROPODS, 5);
                return item;
            case BLASTPROTECTION1:
                item.addUnsafeEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 1);
                return item;
            case BLASTPROTECTION2:
                item.addUnsafeEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 2);
                return item;
            case BLASTPROTECTION3:
                item.addUnsafeEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 3);
                return item;
            case BLASTPROTECTION4:
                item.addUnsafeEnchantment(Enchantment.PROTECTION_EXPLOSIONS, 4);
                return item;
            case DEPTHSTRIDER1:
                item.addUnsafeEnchantment(Enchantment.DEPTH_STRIDER, 1);
                return item;
            case DEPTHSTRIDER2:
                item.addUnsafeEnchantment(Enchantment.DEPTH_STRIDER, 2);
                return item;
            case DEPTHSTRIDER3:
                item.addUnsafeEnchantment(Enchantment.DEPTH_STRIDER, 3);
                return item;
            case EFFICIENCY1:
                item.addUnsafeEnchantment(Enchantment.DIG_SPEED, 1);
                return item;
            case EFFICIENCY2:
                item.addUnsafeEnchantment(Enchantment.DIG_SPEED, 2);
                return item;
            case EFFICIENCY3:
                item.addUnsafeEnchantment(Enchantment.DIG_SPEED, 3);
                return item;
            case EFFICIENCY4:
                item.addUnsafeEnchantment(Enchantment.DIG_SPEED, 4);
                return item;
            case EFFICIENCY5:
                item.addUnsafeEnchantment(Enchantment.DIG_SPEED, 5);
                return item;
            case FEATHERFALLING1:
                item.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, 1);
                return item;
            case FEATHERFALLING2:
                item.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, 2);
                return item;
            case FEATHERFALLING3:
                item.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, 3);
                return item;
            case FEATHERFALLING4:
                item.addUnsafeEnchantment(Enchantment.PROTECTION_FALL, 4);
                return item;
            case FIREASPECT1:
                item.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 1);
                return item;
            case FIREASPECT2:
                item.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 2);
                return item;
            case FIREPROTECTION1:
                item.addUnsafeEnchantment(Enchantment.PROTECTION_FIRE, 1);
                return item;
            case FIREPROTECTION2:
                item.addUnsafeEnchantment(Enchantment.PROTECTION_FIRE, 2);
                return item;
            case FIREPROTECTION3:
                item.addUnsafeEnchantment(Enchantment.PROTECTION_FIRE, 3);
                return item;
            case FIREPROTECTION4:
                item.addUnsafeEnchantment(Enchantment.PROTECTION_FIRE, 4);
                return item;
            case FLAME:
                item.addUnsafeEnchantment(Enchantment.ARROW_FIRE, 1);
                return item;
            case FORTUNE1:
                item.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 1);
                return item;
            case FORTUNE2:
                item.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 2);
                return item;
            case FORTUNE3:
                item.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 3);
                return item;
            case FROSTWALKER1:
                item.addUnsafeEnchantment(Enchantment.FROST_WALKER, 1);
                return item;
            case FROSTWALKER2:
                item.addUnsafeEnchantment(Enchantment.FROST_WALKER, 2);
                return item;
            case INFINITY:
                item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
                return item;
            case KNOCKBACK1:
                item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
                return item;
            case KNOCKBACK2:
                item.addUnsafeEnchantment(Enchantment.KNOCKBACK, 2);
                return item;
            case LOOTING1:
                item.addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, 1);
                return item;
            case LOOTING2:
                item.addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, 2);
                return item;
            case LOOTING3:
                item.addUnsafeEnchantment(Enchantment.LOOT_BONUS_MOBS, 3);
                return item;
            case LUCKOFTHESEA1:
                item.addUnsafeEnchantment(Enchantment.LUCK, 1);
                return item;
            case LUCKOFTHESEA2:
                item.addUnsafeEnchantment(Enchantment.LUCK, 2);
                return item;
            case LUCKOFTHESEA3:
                item.addUnsafeEnchantment(Enchantment.LUCK, 3);
                return item;
            case LURE1:
                item.addUnsafeEnchantment(Enchantment.LURE, 1);
                return item;
            case LURE2:
                item.addUnsafeEnchantment(Enchantment.LURE, 2);
                return item;
            case LURE3:
                item.addUnsafeEnchantment(Enchantment.LURE, 3);
                return item;
            case MENDING:
                item.addUnsafeEnchantment(Enchantment.MENDING, 1);
                return item;
            case POWER1:
                item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
                return item;
            case POWER2:
                item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 2);
                return item;
            case POWER3:
                item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 3);
                return item;
            case POWER4:
                item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 4);
                return item;
            case POWER5:
                item.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 5);
                return item;
            case PROJPROTECTION1:
                item.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, 1);
                return item;
            case PROJPROTECTION2:
                item.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, 2);
                return item;
            case PROJPROTECTION3:
                item.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, 3);
                return item;
            case PROJPROTECTION4:
                item.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE, 4);
                return item;
            case PROTECTION1:
                item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
                return item;
            case PROTECTION2:
                item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
                return item;
            case PROTECTION3:
                item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
                return item;
            case PROTECTION4:
                item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
                return item;
            case PUNCH1:
                item.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 1);
                return item;
            case PUNCH2:
                item.addUnsafeEnchantment(Enchantment.ARROW_KNOCKBACK, 2);
                return item;
            case RESPIRATION1:
                item.addUnsafeEnchantment(Enchantment.OXYGEN, 1);
                return item;
            case RESPIRATION2:
                item.addUnsafeEnchantment(Enchantment.OXYGEN, 2);
                return item;
            case RESPIRATION3:
                item.addUnsafeEnchantment(Enchantment.OXYGEN, 3);
                return item;
            case SHARPNESS1:
                item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 1);
                return item;
            case SHARPNESS2:
                item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 2);
                return item;
            case SHARPNESS3:
                item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 3);
                return item;
            case SHARPNESS4:
                item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 4);
                return item;
            case SHARPNESS5:
                item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 5);
                return item;
            case SILKTOUCH:
                item.addUnsafeEnchantment(Enchantment.SILK_TOUCH, 1);
                return item;
            case SMITE1:
                item.addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, 1);
                return item;
            case SMITE2:
                item.addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, 2);
                return item;
            case SMITE3:
                item.addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, 3);
                return item;
            case SMITE4:
                item.addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, 4);
                return item;
            case SMITE5:
                item.addUnsafeEnchantment(Enchantment.DAMAGE_UNDEAD, 5);
                return item;
            case SWEEPINGEDGE1:
                item.addUnsafeEnchantment(Enchantment.SWEEPING_EDGE, 1);
                return item;
            case SWEEPINGEDGE2:
                item.addUnsafeEnchantment(Enchantment.SWEEPING_EDGE, 2);
                return item;
            case SWEEPINGEDGE3:
                item.addUnsafeEnchantment(Enchantment.SWEEPING_EDGE, 3);
                return item;
            case THORNS1:
                item.addUnsafeEnchantment(Enchantment.THORNS, 1);
                return item;
            case THORNS2:
                item.addUnsafeEnchantment(Enchantment.THORNS, 2);
                return item;
            case THORNS3:
                item.addUnsafeEnchantment(Enchantment.THORNS, 3);
                return item;
            default:
                return item;
        }
    }

    public static ItemStack unEnchantItem(ItemStack item, CEnchantType type) {
        ItemMeta itemMeta = item.getItemMeta();
        List<String> lore = itemMeta.getLore();
        for (String arg : lore)
            if (arg.equals(type.getName()))
                lore.remove(arg);
        itemMeta.setLore(lore);
        if (getEnchantCount(item) == 0 && itemMeta.hasDisplayName()
                && (!itemMeta.getDisplayName().startsWith(ElementalsUtil.color("&bNaNo"))))
            itemMeta.setDisplayName(null);
        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack unEnchantItem(ItemStack item, Enchantment type) {
        item.removeEnchantment(type);
        ItemMeta itemMeta = item.getItemMeta();
        if (getEnchantCount(item) == 0 && itemMeta.hasDisplayName()
                && (!itemMeta.getDisplayName().startsWith(ElementalsUtil.color("&bNaNo"))))
            itemMeta.setDisplayName(null);
        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack enchantItem(ItemStack item, CEnchantType type) {
        ItemMeta itemMeta = item.getItemMeta();
        List<String> itemLore = new ArrayList<>();
        itemLore.add(type.getName());
        if (itemMeta.hasLore())
            itemLore.addAll(itemMeta.getLore());
        itemMeta.setLore(itemLore);
        item.setItemMeta(itemMeta);
        return item;
    }

    public static void openEnchanter(Player player, Dropper block) {
        Inventory inv = Bukkit.createInventory(block, 9, "&5Enchanter");
        inv.setItem(0, new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1));
        inv.setItem(1, new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1));
        inv.setItem(3, new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1) /*TODO <item*/);
        ItemStack buttonItem = new ItemStack(Material.LIME_STAINED_GLASS_PANE, 1);
        ItemMeta buttonMeta = buttonItem.getItemMeta();
        buttonMeta.setDisplayName(ElementalsUtil.color("&aClick pentru a enchanta!"));
        buttonMeta.setLore(Collections.singletonList(ElementalsUtil.color("&cNecesita 10 nivele de experienta!")));
        buttonItem.setItemMeta(buttonMeta);
        inv.setItem(4, buttonItem);
        inv.setItem(5, new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1) /*TODO item>*/);
        inv.setItem(7, new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1));
        inv.setItem(8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1));
        player.openInventory(inv);
    }

    public static List<ItemStack> getHunterDrops(Entity entity, int level) {
        List<ItemStack> drops = new ArrayList<>();
        switch (entity.getType()) {
            case CHICKEN:
                drops.add(new ItemStack(Material.FEATHER, ElementalsUtil.nextInt(level)));
                break;
            case COW:
            case MUSHROOM_COW:
                drops.add(new ItemStack(Material.LEATHER, ElementalsUtil.nextInt(level)));
                if (entity.getFireTicks() > 0)
                    drops.add(new ItemStack(Material.COOKED_BEEF, ElementalsUtil.nextInt(level)));
                else
                    drops.add(new ItemStack(Material.BEEF, ElementalsUtil.nextInt(level)));
                break;
            case HORSE:
                drops.add(new ItemStack(Material.LEATHER, ElementalsUtil.nextInt(level)));
                break;
            case SKELETON_HORSE:
            case SKELETON:
                drops.add(new ItemStack(Material.BONE, ElementalsUtil.nextInt(level)));
                break;
            case ZOMBIE_HORSE:
                drops.add(new ItemStack(Material.ROTTEN_FLESH, ElementalsUtil.nextInt(level)));
                break;
            case PIG:
                if (entity.getFireTicks() > 0)
                    drops.add(new ItemStack(Material.COOKED_PORKCHOP, ElementalsUtil.nextInt(level)));
                else
                    drops.add(new ItemStack(Material.PORKCHOP, ElementalsUtil.nextInt(level)));
                break;
            case POLAR_BEAR:
                if (entity.getFireTicks() > 0)
                    drops.add(new ItemStack(Material.COOKED_SALMON, ElementalsUtil.nextInt(level)));
                else
                    drops.add(new ItemStack(Material.SALMON, ElementalsUtil.nextInt(level)));
                break;
            case RABBIT:
                drops.add(new ItemStack(Material.RABBIT_HIDE, ElementalsUtil.nextInt(level)));
                double rabbit_random = ElementalsUtil.nextDouble(100);
                if (rabbit_random <= 13)
                    drops.add(new ItemStack(Material.RABBIT_FOOT, ElementalsUtil.nextInt(level)));
                break;
            case SHEEP:
                if (entity.getFireTicks() > 0)
                    drops.add(new ItemStack(Material.COOKED_MUTTON, ElementalsUtil.nextInt(level)));
                else
                    drops.add(new ItemStack(Material.MUTTON, ElementalsUtil.nextInt(level)));
                break;
            case SQUID:
                drops.add(new ItemStack(Material.INK_SAC, ElementalsUtil.nextInt(level)));
                break;
            case BLAZE:
                drops.add(new ItemStack(Material.BLAZE_ROD, ElementalsUtil.nextInt(level)));
                break;
            case SPIDER:
            case CAVE_SPIDER:
                drops.add(new ItemStack(Material.STRING, ElementalsUtil.nextInt(level)));
                break;
            case CREEPER:
            case GHAST:
                drops.add(new ItemStack(Material.GUNPOWDER, ElementalsUtil.nextInt(level)));
                break;
            case ENDERMAN:
                drops.add(new ItemStack(Material.ENDER_PEARL, ElementalsUtil.nextInt(level)));
                break;
            case GUARDIAN:
                drops.add(new ItemStack(Material.PRISMARINE_SHARD, ElementalsUtil.nextInt(level)));
                drops.add(new ItemStack(Material.PRISMARINE_CRYSTALS, ElementalsUtil.nextInt(level)));
                break;
            case WITHER_SKELETON:
                drops.add(new ItemStack(Material.COAL, ElementalsUtil.nextInt(level)));
                break;
            case SLIME:
                drops.add(new ItemStack(Material.SLIME_BALL, ElementalsUtil.nextInt(level)));
                break;
            case MAGMA_CUBE:
                MagmaCube magma = (MagmaCube) entity;
                if (magma.getSize() > 1)
                    drops.add(new ItemStack(Material.MAGMA_CREAM, ElementalsUtil.nextInt(level)));
                break;
            case PIG_ZOMBIE:
                drops.add(new ItemStack(Material.ROTTEN_FLESH, ElementalsUtil.nextInt(level)));
                drops.add(new ItemStack(Material.GOLD_NUGGET, ElementalsUtil.nextInt(level)));
                break;
            default:
                break;
        }
        return drops;
    }

    public static void useEnchant(Event event, Entity ent_v, Entity ent_d, DamageCause cause) {
        if (!(ent_v instanceof LivingEntity))
            return;
        if (ent_d instanceof LivingEntity) {
            if (ent_v.getWorld().getName().equals("spawn"))
                return;
            if (ent_d.getWorld().getName().equals("spawn"))
                return;
            if (ent_d.isDead())
                return;
            if (FieldUtil.isFieldAtLocation(ent_v.getLocation()))
                return;
            if (FieldUtil.isFieldAtLocation(ent_d.getLocation()))
                return;
            LivingEntity victim = (LivingEntity) ent_v;
            ItemStack ih_v = new ItemStack(Material.AIR);
            ItemStack icp_v = new ItemStack(Material.AIR);
            ItemStack il_v = new ItemStack(Material.AIR);
            ItemStack ib_v = new ItemStack(Material.AIR);
            if (victim.getEquipment() != null) {
                if (victim.getEquipment().getHelmet() != null
                        && (!victim.getEquipment().getHelmet().getType().equals(Material.AIR)))
                    ih_v = victim.getEquipment().getHelmet();
                if (victim.getEquipment().getChestplate() != null
                        && (!victim.getEquipment().getChestplate().getType().equals(Material.AIR)))
                    icp_v = victim.getEquipment().getChestplate();
                if (victim.getEquipment().getLeggings() != null
                        && (!victim.getEquipment().getLeggings().getType().equals(Material.AIR)))
                    il_v = victim.getEquipment().getLeggings();
                if (victim.getEquipment().getBoots() != null
                        && (!victim.getEquipment().getBoots().getType().equals(Material.AIR)))
                    ib_v = victim.getEquipment().getBoots();
            }
            LivingEntity damager = (LivingEntity) ent_d;
            ItemStack ihand_d = new ItemStack(Material.AIR);
            if (damager.getEquipment() != null) {
                if (damager.getEquipment().getItemInMainHand() != null
                        && !damager.getEquipment().getItemInMainHand().getType().equals(Material.AIR))
                    ihand_d = damager.getEquipment().getItemInMainHand();
            }
            if (!icp_v.getType().equals(Material.AIR)) {
                if (hasEnchant(icp_v, CEnchantType.BLIND) && checkPotion(damager, PotionEffectType.BLINDNESS, 20 * 6)
                        && chanceEnchant(CEnchantType.BLIND))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 0), true);
                if (hasEnchant(icp_v, CEnchantType.POISON1) && checkPotion(damager, PotionEffectType.POISON, 20 * 6)
                        && chanceEnchant(CEnchantType.POISON1))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 5, 0), true);
                if (hasEnchant(icp_v, CEnchantType.POISON2) && checkPotion(damager, PotionEffectType.POISON, 20 * 11)
                        && chanceEnchant(CEnchantType.POISON2))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 10, 0), true);
                if (hasEnchant(icp_v, CEnchantType.WITHER1) && checkPotion(damager, PotionEffectType.WITHER, 20 * 6)
                        && chanceEnchant(CEnchantType.WITHER1))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 5, 0), true);
                if (hasEnchant(icp_v, CEnchantType.WITHER2) && checkPotion(damager, PotionEffectType.WITHER, 20 * 11)
                        && chanceEnchant(CEnchantType.WITHER2))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 10, 0), true);
                if (hasEnchant(icp_v, CEnchantType.MOLTEN1) && chanceEnchant(CEnchantType.MOLTEN1))
                    damager.setFireTicks(5 * 20);
                if (hasEnchant(icp_v, CEnchantType.MOLTEN2) && chanceEnchant(CEnchantType.MOLTEN2))
                    damager.setFireTicks(10 * 20);
                if (hasEnchant(icp_v, CEnchantType.HARDENED1) && checkPotion(damager, PotionEffectType.WEAKNESS, 20 * 6)
                        && chanceEnchant(CEnchantType.HARDENED1))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 5, 0), true);
                if (hasEnchant(icp_v, CEnchantType.HARDENED2)
                        && checkPotion(damager, PotionEffectType.WEAKNESS, 20 * 11)
                        && chanceEnchant(CEnchantType.HARDENED2))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 10, 0), true);
            }
            if (!ih_v.getType().equals(Material.AIR)) {
                if (hasEnchant(ih_v, CEnchantType.BLIND) && checkPotion(damager, PotionEffectType.BLINDNESS, 20 * 6)
                        && chanceEnchant(CEnchantType.BLIND))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 0), true);
                if (hasEnchant(ih_v, CEnchantType.POISON1) && checkPotion(damager, PotionEffectType.POISON, 20 * 6)
                        && chanceEnchant(CEnchantType.POISON1))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 5, 0), true);
                if (hasEnchant(ih_v, CEnchantType.POISON2) && checkPotion(damager, PotionEffectType.POISON, 20 * 11)
                        && chanceEnchant(CEnchantType.POISON2))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 10, 0), true);
                if (hasEnchant(ih_v, CEnchantType.WITHER1) && checkPotion(damager, PotionEffectType.WITHER, 20 * 6)
                        && chanceEnchant(CEnchantType.WITHER1))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 5, 0), true);
                if (hasEnchant(ih_v, CEnchantType.WITHER2) && checkPotion(damager, PotionEffectType.WITHER, 20 * 11)
                        && chanceEnchant(CEnchantType.WITHER2))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 10, 0), true);
                if (hasEnchant(ih_v, CEnchantType.MOLTEN1) && chanceEnchant(CEnchantType.MOLTEN1))
                    damager.setFireTicks(5 * 20);
                if (hasEnchant(ih_v, CEnchantType.MOLTEN2) && chanceEnchant(CEnchantType.MOLTEN2))
                    damager.setFireTicks(10 * 20);
                if (hasEnchant(ih_v, CEnchantType.HARDENED1) && checkPotion(damager, PotionEffectType.WEAKNESS, 20 * 6)
                        && chanceEnchant(CEnchantType.HARDENED1))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 5, 0), true);
                if (hasEnchant(ih_v, CEnchantType.HARDENED2) && checkPotion(damager, PotionEffectType.WEAKNESS, 20 * 11)
                        && chanceEnchant(CEnchantType.HARDENED2))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 10, 0), true);
            }
            if (!il_v.getType().equals(Material.AIR)) {
                if (hasEnchant(il_v, CEnchantType.BLIND) && checkPotion(damager, PotionEffectType.BLINDNESS, 20 * 6)
                        && chanceEnchant(CEnchantType.BLIND))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 0), true);
                if (hasEnchant(il_v, CEnchantType.POISON1) && checkPotion(damager, PotionEffectType.POISON, 20 * 6)
                        && chanceEnchant(CEnchantType.POISON1))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 5, 0), true);
                if (hasEnchant(il_v, CEnchantType.POISON2) && checkPotion(damager, PotionEffectType.POISON, 20 * 11)
                        && chanceEnchant(CEnchantType.POISON2))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 10, 0), true);
                if (hasEnchant(il_v, CEnchantType.WITHER1) && checkPotion(damager, PotionEffectType.WITHER, 20 * 6)
                        && chanceEnchant(CEnchantType.WITHER1))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 5, 0), true);
                if (hasEnchant(il_v, CEnchantType.WITHER2) && checkPotion(damager, PotionEffectType.WITHER, 20 * 11)
                        && chanceEnchant(CEnchantType.WITHER2))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 10, 0), true);
                if (hasEnchant(il_v, CEnchantType.MOLTEN1) && chanceEnchant(CEnchantType.MOLTEN1))
                    damager.setFireTicks(5 * 20);
                if (hasEnchant(il_v, CEnchantType.MOLTEN2) && chanceEnchant(CEnchantType.MOLTEN2))
                    damager.setFireTicks(10 * 20);
                if (hasEnchant(il_v, CEnchantType.HARDENED1) && checkPotion(damager, PotionEffectType.WEAKNESS, 20 * 6)
                        && chanceEnchant(CEnchantType.HARDENED1))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 5, 0), true);
                if (hasEnchant(il_v, CEnchantType.HARDENED2) && checkPotion(damager, PotionEffectType.WEAKNESS, 20 * 11)
                        && chanceEnchant(CEnchantType.HARDENED2))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 10, 0), true);
            }
            if (!ib_v.getType().equals(Material.AIR)) {
                if (hasEnchant(ib_v, CEnchantType.BLIND) && checkPotion(damager, PotionEffectType.BLINDNESS, 20 * 6)
                        && chanceEnchant(CEnchantType.BLIND))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 0), true);
                if (hasEnchant(ib_v, CEnchantType.POISON1) && checkPotion(damager, PotionEffectType.POISON, 20 * 6)
                        && chanceEnchant(CEnchantType.POISON1))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 5, 0), true);
                if (hasEnchant(ib_v, CEnchantType.POISON2) && checkPotion(damager, PotionEffectType.POISON, 20 * 11)
                        && chanceEnchant(CEnchantType.POISON2))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 10, 0), true);
                if (hasEnchant(ib_v, CEnchantType.WITHER1) && checkPotion(damager, PotionEffectType.WITHER, 20 * 6)
                        && chanceEnchant(CEnchantType.WITHER1))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 5, 0), true);
                if (hasEnchant(ib_v, CEnchantType.WITHER2) && checkPotion(damager, PotionEffectType.WITHER, 20 * 11)
                        && chanceEnchant(CEnchantType.WITHER2))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 10, 0), true);
                if (hasEnchant(ib_v, CEnchantType.MOLTEN1) && chanceEnchant(CEnchantType.MOLTEN1))
                    damager.setFireTicks(5 * 20);
                if (hasEnchant(ib_v, CEnchantType.MOLTEN2) && chanceEnchant(CEnchantType.MOLTEN2))
                    damager.setFireTicks(10 * 20);
                if (hasEnchant(ib_v, CEnchantType.HARDENED1) && checkPotion(damager, PotionEffectType.WEAKNESS, 20 * 6)
                        && chanceEnchant(CEnchantType.HARDENED1))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 5, 0), true);
                if (hasEnchant(ib_v, CEnchantType.HARDENED2) && checkPotion(damager, PotionEffectType.WEAKNESS, 20 * 11)
                        && chanceEnchant(CEnchantType.HARDENED2))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 10, 0), true);
            }
            if (!ihand_d.getType().equals(Material.AIR)) {
                if (hasEnchant(ihand_d, CEnchantType.GOOEY) && chanceEnchant(CEnchantType.GOOEY))
                    victim.setVelocity(new Vector(victim.getVelocity().getX(), 2, victim.getVelocity().getZ()));
                if (hasEnchant(ihand_d, CEnchantType.ICEASPECT1) && chanceEnchant(CEnchantType.ICEASPECT1)
                        && checkPotion(victim, PotionEffectType.SLOW, 20 * 6))
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 5, 0), true);
                if (hasEnchant(ihand_d, CEnchantType.ICEASPECT2) && chanceEnchant(CEnchantType.ICEASPECT2)
                        && checkPotion(victim, PotionEffectType.SLOW, 20 * 11))
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 10, 0), true);
                if (hasEnchant(ihand_d, CEnchantType.LIFESTEAL) && chanceEnchant(CEnchantType.LIFESTEAL)
                        && (damager.getHealth() < damager.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()))
                    damager.setHealth(Math.floor(damager.getHealth() + 1));
                if (hasEnchant(ihand_d, CEnchantType.POISON1) && chanceEnchant(CEnchantType.POISON1)
                        && checkPotion(victim, PotionEffectType.POISON, 20 * 6))
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 5, 0), true);
                if (hasEnchant(ihand_d, CEnchantType.POISON2) && chanceEnchant(CEnchantType.POISON2)
                        && checkPotion(victim, PotionEffectType.POISON, 20 * 11))
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 10, 0), true);
                if (hasEnchant(ihand_d, CEnchantType.WITHER1) && chanceEnchant(CEnchantType.WITHER1)
                        && checkPotion(victim, PotionEffectType.WITHER, 20 * 6))
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 5, 0), true);
                if (hasEnchant(ihand_d, CEnchantType.WITHER2) && chanceEnchant(CEnchantType.WITHER2)
                        && checkPotion(victim, PotionEffectType.WITHER, 20 * 11))
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 10, 0), true);
                if (hasEnchant(ihand_d, CEnchantType.BLIND) && chanceEnchant(CEnchantType.BLIND)
                        && checkPotion(victim, PotionEffectType.BLINDNESS, 20 * 6))
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 0), true);
                if (hasEnchant(ihand_d, CEnchantType.HARDENED1)
                        && checkPotion(victim, PotionEffectType.WEAKNESS, 20 * 6)
                        && chanceEnchant(CEnchantType.HARDENED1))
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 5, 0), true);
                if (hasEnchant(ihand_d, CEnchantType.HARDENED2)
                        && checkPotion(victim, PotionEffectType.WEAKNESS, 20 * 11)
                        && chanceEnchant(CEnchantType.HARDENED2))
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 10, 0), true);
                if (ent_v.isDead() && event instanceof EntityDeathEvent) {
                    EntityDeathEvent deathEvent = (EntityDeathEvent) event;
                    if (hasEnchant(ihand_d, CEnchantType.WISDOM1) && chanceEnchant(CEnchantType.WISDOM1))
                        deathEvent
                                .setDroppedExp(((deathEvent.getDroppedExp() * 30) / 100) + deathEvent.getDroppedExp());
                    if (hasEnchant(ihand_d, CEnchantType.WISDOM2) && chanceEnchant(CEnchantType.WISDOM2))
                        deathEvent
                                .setDroppedExp(((deathEvent.getDroppedExp() * 75) / 100) + deathEvent.getDroppedExp());
                }
            }
        } else if (ent_d instanceof Projectile) {
            if (ent_v.getWorld().getName().equals("spawn"))
                    return;
            if (ent_d.getWorld().getName().equals("spawn"))
                    return;
            if (ent_d.isDead())
                return;
            if (FieldUtil.isFieldAtLocation(ent_v.getLocation()))
                return;
            if (FieldUtil.isFieldAtLocation(ent_d.getLocation()))
                return;
            LivingEntity victim = (LivingEntity) ent_v;
            ItemStack ih_v = new ItemStack(Material.AIR);
            ItemStack icp_v = new ItemStack(Material.AIR);
            ItemStack il_v = new ItemStack(Material.AIR);
            ItemStack ib_v = new ItemStack(Material.AIR);
            if (victim.getEquipment() != null) {
                if (victim.getEquipment().getHelmet() != null
                        && (!victim.getEquipment().getHelmet().getType().equals(Material.AIR)))
                    ih_v = victim.getEquipment().getHelmet();
                if (victim.getEquipment().getChestplate() != null
                        && (!victim.getEquipment().getChestplate().getType().equals(Material.AIR)))
                    icp_v = victim.getEquipment().getChestplate();
                if (victim.getEquipment().getLeggings() != null
                        && (!victim.getEquipment().getLeggings().getType().equals(Material.AIR)))
                    il_v = victim.getEquipment().getLeggings();
                if (victim.getEquipment().getBoots() != null
                        && (!victim.getEquipment().getBoots().getType().equals(Material.AIR)))
                    ib_v = victim.getEquipment().getBoots();
            }
            Projectile arrow = (Projectile) ent_d;
            if (arrow.getShooter() == null)
                return;
            if (arrow.getShooter() instanceof BlockProjectileSource)
                return;
            LivingEntity damager = (LivingEntity) arrow.getShooter();
            if (arrow.hasMetadata("frozen1"))
                if (checkPotion(victim, PotionEffectType.SLOW, 20 * 6))
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 5, 0), true);
            if (event instanceof EntityDamageByEntityEvent) {
                EntityDamageByEntityEvent hitEvent = (EntityDamageByEntityEvent) event;
                if (arrow.hasMetadata("necromancer1") && isUndead(ent_v))
                    hitEvent.setDamage(hitEvent.getDamage() + 1.25);
                else if (arrow.hasMetadata("necromancer2") && isUndead(ent_v))
                    hitEvent.setDamage(hitEvent.getDamage() + 2.5);
                else if (arrow.hasMetadata("necromancer3") && isUndead(ent_v))
                    hitEvent.setDamage(hitEvent.getDamage() + 3.75);
                if (arrow.hasMetadata("pesticide1") && isArthropod(ent_v))
                    hitEvent.setDamage(hitEvent.getDamage() + 1.25);
                if (arrow.hasMetadata("pesticide2") && isArthropod(ent_v))
                    hitEvent.setDamage(hitEvent.getDamage() + 2.5);
                if (arrow.hasMetadata("pesticide3") && isArthropod(ent_v))
                    hitEvent.setDamage(hitEvent.getDamage() + 3.75);
            }
            if (event instanceof EntityDeathEvent) {
                EntityDeathEvent deathEvent = (EntityDeathEvent) event;
                if (arrow.hasMetadata("wishdom1"))
                    deathEvent.setDroppedExp(((deathEvent.getDroppedExp() * 30) / 100) + deathEvent.getDroppedExp());
                if (arrow.hasMetadata("wishdom2"))
                    deathEvent.setDroppedExp(((deathEvent.getDroppedExp() * 75) / 100) + deathEvent.getDroppedExp());
                if (arrow.hasMetadata("hunter1"))
                    deathEvent.getDrops().addAll(getHunterDrops(ent_v, 2));
                if (arrow.hasMetadata("hunter2"))
                    deathEvent.getDrops().addAll(getHunterDrops(ent_v, 3));
                if (arrow.hasMetadata("hunter3"))
                    deathEvent.getDrops().addAll(getHunterDrops(ent_v, 4));
            }
            if (!icp_v.getType().equals(Material.AIR)) {
                if (hasEnchant(icp_v, CEnchantType.BLIND) && checkPotion(damager, PotionEffectType.BLINDNESS, 20 * 6)
                        && chanceEnchant(CEnchantType.BLIND))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 0), true);
                if (hasEnchant(icp_v, CEnchantType.POISON1) && checkPotion(damager, PotionEffectType.POISON, 20 * 6)
                        && chanceEnchant(CEnchantType.POISON1))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 5, 0), true);
                if (hasEnchant(icp_v, CEnchantType.POISON2) && checkPotion(damager, PotionEffectType.POISON, 20 * 11)
                        && chanceEnchant(CEnchantType.POISON2))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 10, 0), true);
                if (hasEnchant(icp_v, CEnchantType.WITHER1) && checkPotion(damager, PotionEffectType.WITHER, 20 * 6)
                        && chanceEnchant(CEnchantType.WITHER1))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 5, 0), true);
                if (hasEnchant(icp_v, CEnchantType.WITHER2) && checkPotion(damager, PotionEffectType.WITHER, 20 * 11)
                        && chanceEnchant(CEnchantType.WITHER2))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 10, 0), true);
                if (hasEnchant(icp_v, CEnchantType.MOLTEN1) && chanceEnchant(CEnchantType.MOLTEN1))
                    damager.setFireTicks(5 * 20);
                if (hasEnchant(icp_v, CEnchantType.MOLTEN2) && chanceEnchant(CEnchantType.MOLTEN2))
                    damager.setFireTicks(10 * 20);
            }
            if (!ih_v.getType().equals(Material.AIR)) {
                if (hasEnchant(ih_v, CEnchantType.BLIND) && checkPotion(damager, PotionEffectType.BLINDNESS, 20 * 6)
                        && chanceEnchant(CEnchantType.BLIND))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 0), true);
                if (hasEnchant(ih_v, CEnchantType.POISON1) && checkPotion(damager, PotionEffectType.POISON, 20 * 6)
                        && chanceEnchant(CEnchantType.POISON1))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 5, 0), true);
                if (hasEnchant(ih_v, CEnchantType.POISON2) && checkPotion(damager, PotionEffectType.POISON, 20 * 11)
                        && chanceEnchant(CEnchantType.POISON2))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 10, 0), true);
                if (hasEnchant(ih_v, CEnchantType.WITHER1) && checkPotion(damager, PotionEffectType.WITHER, 20 * 6)
                        && chanceEnchant(CEnchantType.WITHER1))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 5, 0), true);
                if (hasEnchant(ih_v, CEnchantType.WITHER2) && checkPotion(damager, PotionEffectType.WITHER, 20 * 11)
                        && chanceEnchant(CEnchantType.WITHER2))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 10, 0), true);
                if (hasEnchant(ih_v, CEnchantType.MOLTEN1) && chanceEnchant(CEnchantType.MOLTEN1))
                    damager.setFireTicks(5 * 20);
                if (hasEnchant(ih_v, CEnchantType.MOLTEN2) && chanceEnchant(CEnchantType.MOLTEN2))
                    damager.setFireTicks(10 * 20);
            }
            if (!il_v.getType().equals(Material.AIR)) {
                if (hasEnchant(il_v, CEnchantType.BLIND) && checkPotion(damager, PotionEffectType.BLINDNESS, 20 * 6)
                        && chanceEnchant(CEnchantType.BLIND))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 0), true);
                if (hasEnchant(il_v, CEnchantType.POISON1) && checkPotion(damager, PotionEffectType.POISON, 20 * 6)
                        && chanceEnchant(CEnchantType.POISON1))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 5, 0), true);
                if (hasEnchant(il_v, CEnchantType.POISON2) && checkPotion(damager, PotionEffectType.POISON, 20 * 11)
                        && chanceEnchant(CEnchantType.POISON2))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 10, 0), true);
                if (hasEnchant(il_v, CEnchantType.WITHER1) && checkPotion(damager, PotionEffectType.WITHER, 20 * 6)
                        && chanceEnchant(CEnchantType.WITHER1))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 5, 0), true);
                if (hasEnchant(il_v, CEnchantType.WITHER2) && checkPotion(damager, PotionEffectType.WITHER, 20 * 11)
                        && chanceEnchant(CEnchantType.WITHER2))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 10, 0), true);
                if (hasEnchant(il_v, CEnchantType.MOLTEN1) && chanceEnchant(CEnchantType.MOLTEN1))
                    damager.setFireTicks(5 * 20);
                if (hasEnchant(il_v, CEnchantType.MOLTEN2) && chanceEnchant(CEnchantType.MOLTEN2))
                    damager.setFireTicks(10 * 20);
            }
            if (!ib_v.getType().equals(Material.AIR)) {
                if (hasEnchant(ib_v, CEnchantType.BLIND) && checkPotion(damager, PotionEffectType.BLINDNESS, 20 * 6)
                        && chanceEnchant(CEnchantType.BLIND))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 0), true);
                if (hasEnchant(ib_v, CEnchantType.POISON1) && checkPotion(damager, PotionEffectType.POISON, 20 * 6)
                        && chanceEnchant(CEnchantType.POISON1))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 5, 0), true);
                if (hasEnchant(ib_v, CEnchantType.POISON2) && checkPotion(damager, PotionEffectType.POISON, 20 * 11)
                        && chanceEnchant(CEnchantType.POISON2))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 10, 0), true);
                if (hasEnchant(ib_v, CEnchantType.WITHER1) && checkPotion(damager, PotionEffectType.WITHER, 20 * 6)
                        && chanceEnchant(CEnchantType.WITHER1))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 5, 0), true);
                if (hasEnchant(ib_v, CEnchantType.WITHER2) && checkPotion(damager, PotionEffectType.WITHER, 20 * 11)
                        && chanceEnchant(CEnchantType.WITHER2))
                    damager.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 10, 0), true);
                if (hasEnchant(ib_v, CEnchantType.MOLTEN1) && chanceEnchant(CEnchantType.MOLTEN1))
                    damager.setFireTicks(5 * 20);
                if (hasEnchant(ib_v, CEnchantType.MOLTEN2) && chanceEnchant(CEnchantType.MOLTEN2))
                    damager.setFireTicks(10 * 20);
            }
        }
    }

    public static boolean hasEnchant(ItemStack item, Enchantment type, int level) {
        if (!item.containsEnchantment(type))
            return false;
        return item.getEnchantmentLevel(type) == level;
    }

    public static boolean hasEnchant(ItemStack item, CEnchantType type) {
        if (!item.hasItemMeta())
            return false;
        if (!item.getItemMeta().hasLore())
            return false;
        if (item.getItemMeta().getLore().isEmpty())
            return false;
        return item.getItemMeta().getLore().contains(type.getName());
    }

    public static boolean chanceEnchant(CEnchantType type) {
        return chanceEnchant(ElementalsUtil.nextDouble(100), type);
    }

    public static boolean chanceEnchant(double random, CEnchantType type) {
        return (random <= type.getChance());
    }

    public static boolean hasItem(Inventory inv, ItemStack item) {
        for (ItemStack i : inv.getContents())
            if (isSimilar(i, item))
                return true;
        return false;
    }

    public static boolean isUndead(Entity entity) {
        return (entity.getType().equals(EntityType.ZOMBIE) || entity.getType().equals(EntityType.SKELETON)
                || entity.getType().equals(EntityType.WITHER) || entity.getType().equals(EntityType.PIG_ZOMBIE));
    }

    public static boolean isArthropod(Entity entity) {
        return (entity.getType().equals(EntityType.SPIDER) || entity.getType().equals(EntityType.CAVE_SPIDER)
                || entity.getType().equals(EntityType.SILVERFISH) || entity.getType().equals(EntityType.ENDERMITE));
    }

    public static boolean isSimilar(ItemStack i1, ItemStack i2) {
        if (!i1.getType().equals(i2.getType()))
            return false;
        if (i1.hasItemMeta() != i2.hasItemMeta())
            return false;
        if (i1.hasItemMeta()) {
            if (i1.getItemMeta().hasDisplayName() != i2.getItemMeta().hasDisplayName())
                return false;
            if (i1.getItemMeta().hasDisplayName())
                if (!i1.getItemMeta().getDisplayName().equals(i2.getItemMeta().getDisplayName()))
                    return false;
            if (i1.getItemMeta().hasLore() != i2.getItemMeta().hasLore())
                return false;
            if (i1.getItemMeta().hasLore())
                return i1.getItemMeta().getLore().equals(i2.getItemMeta().getLore());
        }
        return true;
    }

    public static boolean checkPotion(LivingEntity entity, PotionEffectType type, int duration) {
        for (PotionEffect effect : entity.getActivePotionEffects())
            if (effect.getType().equals(type))
                if (effect.getDuration() > duration)
                    return false;
        return true;
    }

    public static boolean checkPotion(LivingEntity entity, PotionEffectType type, int amplifier, int duration) {
        for (PotionEffect effect : entity.getActivePotionEffects())
            if (effect.getType().equals(type))
                if (effect.getDuration() > duration && effect.getAmplifier() != amplifier)
                    return false;
        return true;
    }
}
