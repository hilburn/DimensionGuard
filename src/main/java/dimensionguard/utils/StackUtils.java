package dimensionguard.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;

public class StackUtils
{
    public static void addStackToPlayer(EntityPlayer player, ItemStack stack)
    {
        if (!player.inventory.addItemStackToInventory(stack))
        {
            player.func_146097_a(stack, false,true);
        }
    }

    public static String getConcatString(ArrayList<String> items)
    {
        String result = "";
        for (Iterator itr = items.iterator();itr.hasNext();)
        {
            result+=itr.next()+(itr.hasNext()?", ":"");
        }
        return result;
    }
}
