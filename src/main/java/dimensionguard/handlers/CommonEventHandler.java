package dimensionguard.handlers;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dimensionguard.network.MessageHandler;
import dimensionguard.network.message.ClientMessage;
import dimensionguard.reference.Names;
import dimensionguard.utils.StackUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;

import java.util.ArrayList;
import java.util.List;

public class CommonEventHandler
{
    @SubscribeEvent
    public void teleportEvent(PlayerEvent.PlayerChangedDimensionEvent event)
    {
        checkInventory(event.player, event.toDim, event.fromDim);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void playerConnect(PlayerEvent.PlayerLoggedInEvent event)
    {
        sendMessage(new ClientMessage(event.player, true));
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void playerDisconnect(PlayerEvent.PlayerLoggedOutEvent event)
    {
        sendMessage(new ClientMessage(event.player, false));
    }


    private void sendMessage(ClientMessage message)
    {
        MessageHandler.sendMessage(message);
    }

    @SubscribeEvent
    public void respawnPlayer(PlayerEvent.PlayerRespawnEvent event){
    	checkInventory(event.player, event.player.dimension, Integer.MIN_VALUE);
    }

    private void checkInventory(EntityPlayer player, int dim, int fromDim)
    {
        ArrayList<String> disabledItems = new ArrayList<String>();
        ArrayList<String> enabledItems = new ArrayList<String>();
        for (int i=0;i<player.inventory.getSizeInventory();i++)
        {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (stack==null || stack.getItem()==null) continue;
            boolean disabled = DisabledHandler.isDisabledStack(stack, dim);
            boolean wasDisabled = fromDim>Integer.MIN_VALUE? DisabledHandler.isDisabledStack(stack, fromDim):false;
            if (disabled!=wasDisabled)
            {
                String name = stack.getDisplayName();
                if (disabled && !disabledItems.contains(name)) disabledItems.add(name);
                else if (!disabled && !enabledItems.contains(name)) enabledItems.add(name);
            }
        }
        if (disabledItems.size()>0)
            player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal(Names.disabled)+ ": "+StackUtils.getConcatString(disabledItems)));
        if (enabledItems.size()>0)
            player.addChatComponentMessage(new ChatComponentText(StatCollector.translateToLocal(Names.enabled)+ ": "+StackUtils.getConcatString(enabledItems)));
        disableArmour(player,dim);
    }

    public void disableArmour(EntityPlayer player, int dim)
    {
        for (int i=player.inventory.mainInventory.length;i<player.inventory.getSizeInventory();i++)
        {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (stack!=null && DisabledHandler.isDisabledStack(player,dim,stack))
            {
                player.inventory.setInventorySlotContents(i,null);
                StackUtils.addStackToPlayer(player, stack);
            }
        }
    }
}
