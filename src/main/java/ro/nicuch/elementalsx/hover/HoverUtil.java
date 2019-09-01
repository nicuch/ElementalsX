package ro.nicuch.elementalsx.hover;

import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class HoverUtil {

    public static String rawMessage(ItemStack item) {
        return CraftItemStack.asNMSCopy(item).getOrCreateTag().toString();
    }
}
