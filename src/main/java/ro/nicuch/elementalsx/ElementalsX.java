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

import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import ro.nicuch.elementalsx.deathmessage.DeathMessageListener;
import ro.nicuch.elementalsx.elementals.ElementalsListener;
import ro.nicuch.elementalsx.elementals.ElementalsUtil;
import ro.nicuch.elementalsx.elementals.commands.*;
import ro.nicuch.elementalsx.protection.Field;
import ro.nicuch.elementalsx.protection.FieldListener;
import ro.nicuch.elementalsx.protection.FieldUtil;

public class ElementalsX extends JavaPlugin {

    private static Connection database;
    private final static ConcurrentMap<UUID, User> players = new ConcurrentHashMap<>();
    private static Economy vault;
    private static Permission perm;

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        getServer().setSpawnRadius(1);
        new File(this.getDataFolder() + File.separator + "regiuni").mkdirs();
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
        return Bukkit.getPluginManager().getPlugin("ElementalsX");
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
        TabExecutor te = new ProtectionCommand();
        this.getCommand("ps").setExecutor(te);
        this.getCommand("ps").setTabCompleter(te);
        this.getCommand("adminchat").setExecutor(new AdminChatCommand());
        this.getCommand("adminchat").setAliases(AdminChatCommand.getAliases());

        this.getCommand("chat").setExecutor(new ChatCommand());
        this.getCommand("randomtp").setExecutor(new RandomTpCommand());
        this.getCommand("randomtp").setAliases(RandomTpCommand.getAliases());
        te = new SoundCommand();
        this.getCommand("sound").setExecutor(te);
        this.getCommand("sound").setTabCompleter(te);
        this.getCommand("giveall").setExecutor(new GiveAllCommand());
        this.getCommand("test").setExecutor(new TestCommand());
        this.getCommand("points").setExecutor(te);
        this.getCommand("points").setTabCompleter(te);
        this.getCommand("admin").setExecutor(new AdminCommand());
        te = new SortCommand();
        this.getCommand("sort").setExecutor(te);
        this.getCommand("sort").setTabCompleter(te);
        this.getCommand("vote").setExecutor(new VoteCommand());
    }

    private void createDataBase() {
        File file = new File(this.getDataFolder() + File.separator + "database.db");
        String url = "jdbc:sqlite:" + file.getAbsolutePath();
        try {
            database = DriverManager.getConnection(url);
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
    }

    private void randomMsg() {
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () ->
                Bukkit.broadcastMessage(ElementalsUtil.color("&c&l>>&r " + ElementalsUtil.getAutoMessages()
                        .get(ElementalsUtil.nextInt(ElementalsUtil.getAutoMessages().size())))), 1L, 2 * 60 * 20L);
    }
}