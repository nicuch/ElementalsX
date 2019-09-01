package ro.nicuch.elementalsx.elementals;


import org.bukkit.*;
import org.bukkit.entity.Player;

public abstract class FakePlayer implements Player {
    private Location loc;

    public FakePlayer(Location loc) {
        this.loc = loc;
    }

    @Override
    public Location getLocation() {
        return this.loc;
    }
}
