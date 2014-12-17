package dimensionguard.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import dimensionguard.reference.Names;
import dimensionguard.utils.StackUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;

import java.util.ArrayList;

public class CommonEventHandler
{
    @SubscribeEvent
    public void teleportEvent(PlayerEvent.PlayerChangedDimensionEvent event)
    {
        EntityPlayer player = event.player;
        NBTTagCompound persisted = event.player.getEntityData().getCompoundTag(player.PERSISTED_NBT_TAG);
        NBTTagList disabled = new NBTTagList();
        if (!persisted.hasNoTags())
        {
            ArrayList<String> enabledItems = new ArrayList<String>();
            NBTTagList disabledItems = persisted.getTagList(Names.NBTTag, 10);
            for (int i = 0; i<disabledItems.tagCount(); i++)
            {
                NBTTagCompound stackCompound = disabledItems.getCompoundTagAt(i);
                ItemStack stack = ItemStack.loadItemStackFromNBT(stackCompound);
                if (DisabledHandler.isDisabledStack(stack, event.toDim))
                {
                    disabled.appendTag(stackCompound);
                }
                else
                {
                    StackUtils.addStackToPlayer(player,stack);
                    if (!enabledItems.contains(stack.getDisplayName())) enabledItems.add(stack.getDisplayName());
                }
            }
            if (enabledItems.size()>0)
                player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal(Names.enabled)+": "+StackUtils.getConcatString(enabledItems)));
        }
        ArrayList<String> disabledItems = new ArrayList<String>();
        for (int i = 0; i<player.inventory.getSizeInventory(); i++)
        {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (stack==null || stack.getItem()==null) continue;
            if (DisabledHandler.isDisabledStack(stack, event.toDim))
            {
                disabled.appendTag(stack.writeToNBT(new NBTTagCompound()));
                player.inventory.setInventorySlotContents(i,null);
                if (!disabledItems.contains(stack.getDisplayName())) disabledItems.add(stack.getDisplayName());
            }
        }
        if (disabledItems.size()>0)
            player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal(Names.disabled)+ ": "+StackUtils.getConcatString(disabledItems)));

        persisted.setTag(Names.NBTTag, disabled);
        player.getEntityData().setTag(player.PERSISTED_NBT_TAG,persisted);
    }
}
