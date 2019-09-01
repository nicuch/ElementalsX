package ro.nicuch.elementalsx;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

import org.anjocaido.groupmanager.GroupManager;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Difficulty;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import ro.nicuch.elementalsx.deathmessage.DeathMessageListener;
import ro.nicuch.elementalsx.elementals.ElementalsListener;
import ro.nicuch.elementalsx.elementals.ElementalsUtil;
import ro.nicuch.elementalsx.elementals.commands.AdminChatCommand;
import ro.nicuch.elementalsx.elementals.commands.AdminCommand;
import ro.nicuch.elementalsx.elementals.commands.ChatCommand;
import ro.nicuch.elementalsx.elementals.commands.CurseEnchantCommand;
import ro.nicuch.elementalsx.elementals.commands.CustomEnchantCommand;
import ro.nicuch.elementalsx.elementals.commands.GiveAllCommand;
import ro.nicuch.elementalsx.elementals.commands.PointsCommand;
import ro.nicuch.elementalsx.elementals.commands.ProtectionCommand;
import ro.nicuch.elementalsx.elementals.commands.RandomTpCommand;
import ro.nicuch.elementalsx.elementals.commands.ShowCommand;
import ro.nicuch.elementalsx.elementals.commands.SortCommand;
import ro.nicuch.elementalsx.elementals.commands.SoundCommand;
import ro.nicuch.elementalsx.elementals.commands.TestCommand;
import ro.nicuch.elementalsx.enchants.EnchantListener;
import ro.nicuch.elementalsx.enchants.EnchantUtil;
import ro.nicuch.elementalsx.enchants.EnchantUtil.CEnchantType;
import ro.nicuch.elementalsx.protection.Field;
import ro.nicuch.elementalsx.protection.FieldListener;
import ro.nicuch.elementalsx.protection.FieldUtil;

public class ElementalsX extends JavaPlugin {
    public enum Nano {
        A, B, C, D
    }

    private static Connection database;
    private final static ConcurrentMap<UUID, User> players = new ConcurrentHashMap<>();
    private static Economy vault;
    private static Permission perm;
    private static GroupManager gm;


    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        getServer().setSpawnRadius(1);
        new File(this.getDataFolder() + File.separator + "regiuni").mkdirs();
        gm = (GroupManager) Bukkit.getPluginManager().getPlugin("GroupManager");
        vault = this.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
        perm = this.getServer().getServicesManager().getRegistration(Permission.class).getProvider();
        new WorldCreator("spawn").environment(Environment.NORMAL).generateStructures(false).createWorld();
        createDataBase();
        Bukkit.getWorlds().forEach((World world) -> {
            world.setDifficulty(Difficulty.HARD);
            for (Chunk chunk : world.getLoadedChunks())
                if (chunk.isLoaded())
                    FieldUtil.loadFieldsInChunk(chunk);
        });
        this.commandsCreator();
        this.eventCreator();
        this.randomMsg();
        this.timer();
        this.enchantament();
        this.mobMerger();
        Bukkit.getOnlinePlayers().forEach((Consumer<Player>) ElementalsX::createUser);
        sendConsoleMessage("&bPluginul a pornit! (" + (System.currentTimeMillis() - start) + "ms)");
        Bukkit.broadcastMessage(ElementalsUtil.color("&bElementals a pornit! (" + (System.currentTimeMillis() - start) + "ms)"));
    }

    @Override
    public void onDisable() {
        FieldUtil.getLoadedFields().forEach(Field::save);
        getOnlineUsers().forEach(User::save);
        try {
            database.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        sendConsoleMessage("&bPluginul s-a oprit!");
    }

    public static GroupManager getGM() {
        return gm;
    }

    public static void createUser(Player player) {
        players.putIfAbsent(player.getUniqueId(), new User(player));
    }

    public static boolean existUser(UUID uuid) {
        return players.containsKey(uuid);
    }

    public static boolean existUser(Player player) {
        return existUser(player.getUniqueId());
    }

    public static Plugin get() {
        return Bukkit.getPluginManager().getPlugin("Elementals");
    }

    public static Connection getBase() {
        return database;
    }

    public static Collection<User> getOnlineUsers() {
        return players.values();
    }

    public static User getUser(Player player) {
        return getUser(player.getUniqueId());
    }

    public static User getUser(UUID uuid) {
        if (existUser(uuid))
            return players.get(uuid);
        else
            throw new NullPointerException("This user is offline or null.");
    }

    public static Economy getVault() {
        return vault;
    }

    public static Permission getPermission() {
        return perm;
    }

    public static void removeUser(Player player) {
        removeUser(player.getUniqueId());
    }

    public static void removeUser(UUID uuid) {
        if (existUser(uuid)) {
            User user = getUser(uuid);
            players.remove(uuid);
            user.save();
        }
    }

    public static void sendConsoleMessage(String arg) {
        Bukkit.getServer().getConsoleSender().sendMessage(ElementalsUtil.color(arg));
    }

    private void commandsCreator() {
        this.getCommand("ps").setExecutor(new ProtectionCommand());
        this.getCommand("ps").setTabCompleter(new ProtectionCommand());
        this.getCommand("adminchat").setExecutor(new AdminChatCommand());
        this.getCommand("adminchat").setAliases(AdminChatCommand.getAliases());
        this.getCommand("chat").setExecutor(new ChatCommand());
        this.getCommand("randomtp").setExecutor(new RandomTpCommand());
        this.getCommand("randomtp").setAliases(RandomTpCommand.getAliases());
        this.getCommand("sound").setExecutor(new SoundCommand());
        this.getCommand("sound").setTabCompleter(new SoundCommand());
        this.getCommand("giveall").setExecutor(new GiveAllCommand());
        this.getCommand("test").setExecutor(new TestCommand());
        this.getCommand("points").setExecutor(new PointsCommand());
        this.getCommand("points").setTabCompleter(new PointsCommand());
        this.getCommand("admin").setExecutor(new AdminCommand());
        this.getCommand("show").setExecutor(new ShowCommand());
        this.getCommand("ce").setExecutor(new CustomEnchantCommand());
        this.getCommand("ce").setTabCompleter(new CustomEnchantCommand());
        this.getCommand("sort").setExecutor(new SortCommand());
        this.getCommand("sort").setExecutor(new SortCommand());
        this.getCommand("curse").setExecutor(new CurseEnchantCommand());
    }

    private void createDataBase() {
        FileConfiguration cfg = this.getConfig();
        String username = cfg.getString("db_user");
        String password = cfg.getString("db_pass");
        String ip = cfg.getString("db_ip");
        String db_name = cfg.getString("db_name");
        String url = "jdbc:mysql://" + ip + "/" + db_name + "?autoReconnect=true";
        try {
            database = DriverManager.getConnection(url, username, password);
            database.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS protection(id VARCHAR(50) PRIMARY KEY, x INT, y INT, z INT, world VARCHAR(50), owner VARCHAR(50), maxx INT, maxz INT, minx INT, minz INT, chunkx INT, chunkz INT);")
                    .executeUpdate();
            database.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS pikapoints(uuid VARCHAR(50) PRIMARY KEY, points INT);").executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private void eventCreator() {
        PluginManager manager = this.getServer().getPluginManager();
        manager.registerEvents(new ElementalsListener(), this);
        manager.registerEvents(new FieldListener(), this);
        manager.registerEvents(new DeathMessageListener(), this);
        manager.registerEvents(new EnchantListener(), this);
    }

    // TODO we have a lag-hole here
    private void mobMerger() {
        Bukkit.getScheduler().runTaskTimer(this, () -> Bukkit.getWorlds().forEach((World world) -> {
            if (!world.getName().equals("spawn"))
                ElementalsUtil.procesWorld(world);
        }), 10 * 20L, 10 * 20L);
    }


    // TODO You can do in other ways here.
    private void enchantament() {
        Bukkit.getScheduler().runTaskTimer(this, () -> Bukkit.getOnlinePlayers().forEach((Player player) -> {
            ItemStack ih = player.getInventory().getHelmet();
            ItemStack icp = player.getInventory().getChestplate();
            ItemStack il = player.getInventory().getLeggings();
            ItemStack ib = player.getInventory().getBoots();
            if (!(ih == null || ih.getType().equals(Material.AIR))) {
                if (EnchantUtil.checkPotion(player, PotionEffectType.NIGHT_VISION, 300)
                        && EnchantUtil.hasEnchant(ih, CEnchantType.GLOWING))
                    player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 305, 0), true);
            }
            if (!(icp == null || icp.getType().equals(Material.AIR))) {
                if (EnchantUtil.checkPotion(player, PotionEffectType.REGENERATION, 30)
                        && EnchantUtil.hasEnchant(icp, CEnchantType.REGENERATION1))
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 25, 0), true);
                if (EnchantUtil.checkPotion(player, PotionEffectType.REGENERATION, 30)
                        && EnchantUtil.hasEnchant(icp, CEnchantType.REGENERATION2))
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 25, 1), true);
            }
            if (!(il == null || il.getType().equals(Material.AIR))) {
                if (EnchantUtil.checkPotion(player, PotionEffectType.FIRE_RESISTANCE, 30)
                        && EnchantUtil.hasEnchant(il, CEnchantType.OBSIDIANSHIELD))
                    player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 25, 0), true);
                if (EnchantUtil.checkPotion(player, PotionEffectType.JUMP, 30)
                        && EnchantUtil.hasEnchant(il, CEnchantType.ROCKETS1))
                    player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 25, 0), true);
                if (EnchantUtil.checkPotion(player, PotionEffectType.JUMP, 30)
                        && EnchantUtil.hasEnchant(il, CEnchantType.ROCKETS2))
                    player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 25, 1), true);
            }
            if (!(ib == null || ib.getType().equals(Material.AIR))) {
                if (EnchantUtil.checkPotion(player, PotionEffectType.SPEED, 30)
                        && EnchantUtil.hasEnchant(ib, CEnchantType.WHEELS1))
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 25, 0), true);
                if (EnchantUtil.checkPotion(player, PotionEffectType.SPEED, 30)
                        && EnchantUtil.hasEnchant(ib, CEnchantType.WHEELS2))
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 25, 1), true);
            }
        }), 20L, 20L);
    }

    private void randomMsg() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () ->
                Bukkit.broadcastMessage(ElementalsUtil.color("&c&l>>&r " + ElementalsUtil.getAutoMessages()
                        .get(ElementalsUtil.nextInt(ElementalsUtil.getAutoMessages().size())))), 1L, 2 * 60 * 20L);
    }

    private void timer() {
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            getOnlineUsers().forEach((User user) -> {
                if (user.getPvpTicks() > 1)
                    user.setPvpTicks(user.getPvpTicks() - 1);
                else if (user.getPvpTicks() == 1) {
                    user.togglePvp(false);
                    user.getBase().sendMessage(ElementalsUtil.color("&6Nu mai esti in &cPvP&6! Te poti deconecta."));
                    user.setPvpTicks(user.getPvpTicks() - 1);
                }
                // Fuck you Spigot!
                //if (user.getBase().getHealth() > user.getBase().getMaxHealth())
                //    user.getBase().setHealth(user.getBase().getMaxHealth());
            });
            ElementalsUtil.tickMotd();
        }, 20L, 20L);
    }
}