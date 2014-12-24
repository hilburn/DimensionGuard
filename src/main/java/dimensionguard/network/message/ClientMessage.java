package dimensionguard.network.message;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import dimensionguard.network.MessageHelper;
import dimensionguard.server.PlayerRegistry;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

public class ClientMessage implements IMessage, IMessageHandler<ClientMessage, IMessage>
{
    public String id;
    public boolean addPlayer;

    public ClientMessage()
    {
    }

    public ClientMessage(EntityPlayer player, boolean add)
    {
        this.id = player.getPersistentID().toString();
        this.addPlayer = add;
    }


    @Override
    public void fromBytes(ByteBuf buf)
    {
        byte[] bytes = null;
        buf.readBytes(bytes);
        id = MessageHelper.byteArrayToString(bytes);
        addPlayer = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeBytes(MessageHelper.stringToByeArray(id));
        buf.writeBoolean(addPlayer);
    }

    @Override
    public IMessage onMessage(ClientMessage message, MessageContext ctx)
    {
        UUID id = UUID.fromString(message.id);
        if (message.addPlayer)
            PlayerRegistry.addPlayer(id);
        else
            PlayerRegistry.removePlayer(id);
        return null;
    }
}
