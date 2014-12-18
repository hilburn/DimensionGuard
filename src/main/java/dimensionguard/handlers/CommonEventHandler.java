package dimensionguard.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
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
    public void teleportEvent(PlayerEvent.PlayerChangedDimensionEvent event){
        guardInventory(event.player, event.toDim);
    }
    
    @SubscribeEvent
    public void respawnPlayer(PlayerEvent.PlayerRespawnEvent event){
    	guardInventory(event.player, event.player.dimension);
    }

    @SubscribeEvent
    public void playerTickHandler(TickEvent.PlayerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
        {
            EntityPlayer player = event.player;
            if (player.openContainer!=null) guardInventory(player,player.dimension,event.side,true);
        }
    }

    private void guardInventory(EntityPlayer player, int dimension)
    {
        guardInventory(player,dimension,Side.SERVER,false);
    }

    private void guardInventory(EntityPlayer player, int dimension, Side side, boolean removeOnly){
    	NBTTagCompound persisted = player.getEntityData().getCompoundTag(player.PERSISTED_NBT_TAG);
        NBTTagList disabled = removeOnly? persisted.getTagList(Names.NBTTag, 10):new NBTTagList();
        if (!persisted.hasNoTags() && !removeOnly)
        {
            ArrayList<String> enabledItems = new ArrayList<String>();
            NBTTagList disabledItems = persisted.getTagList(Names.NBTTag, 10);
            for (int i = 0; i<disabledItems.tagCount(); i++)
            {
                NBTTagCompound stackCompound = disabledItems.getCompoundTagAt(i);
                ItemStack stack = ItemStack.loadItemStackFromNBT(stackCompound);
                if (DisabledHandler.isDisabledStack(stack, dimension))
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
            if (DisabledHandler.isDisabledStack(stack, dimension))
            {
                if (side == Side.SERVER)
                {
                    disabled.appendTag(stack.writeToNBT(new NBTTagCompound()));
                    if (!disabledItems.contains(stack.getDisplayName())) disabledItems.add(stack.getDisplayName());
                }
                player.inventory.setInventorySlotContents(i,null);
            }
        }
        if (disabledItems.size()>0)
            player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal(Names.disabled)+ ": "+StackUtils.getConcatString(disabledItems)));

        persisted.setTag(Names.NBTTag, disabled);
        player.getEntityData().setTag(player.PERSISTED_NBT_TAG,persisted);
    }
}
