package dimensionguard.utils;

import dimensionguard.reference.Names;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;

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

    public static void addDisabledStack(EntityPlayer player, ItemStack stack)
    {
        NBTTagCompound persisted = player.getEntityData().getCompoundTag(player.PERSISTED_NBT_TAG);
        NBTTagList disabled = persisted.hasKey(Names.NBTTag)? persisted.getTagList(Names.NBTTag,10): new NBTTagList();
        if (stack==null || stack.getItem()==null) return;
        disabled.appendTag(stack.writeToNBT(new NBTTagCompound()));
        player.addChatComponentMessage(new ChatComponentText(stack.getDisplayName() + " " + StatCollector.translateToLocal(Names.dimensionDisabled)));
        persisted.setTag(Names.NBTTag, disabled);
        player.getEntityData().setTag(player.PERSISTED_NBT_TAG,persisted);
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
