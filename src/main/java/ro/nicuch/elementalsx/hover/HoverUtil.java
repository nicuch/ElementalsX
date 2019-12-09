package ro.nicuch.elementalsx.hover;

import net.minecraft.server.v1_14_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class HoverUtil {

    public static String rawMessage(ItemStack item) {
        net.minecraft.server.v1_14_R1.ItemStack nms = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = new NBTTagCompound();
        nms.save(tag);
        return tag.toString();
    }
}
