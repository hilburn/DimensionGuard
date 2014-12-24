package dimensionguard.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import dimensionguard.network.message.ClientMessage;
import dimensionguard.reference.Reference;

public class MessageHandler implements IMessageHandler
{
    public static SimpleNetworkWrapper INSTANCE = new SimpleNetworkWrapper(Reference.ID);

    public static void init()
    {
        INSTANCE.registerMessage(ClientMessage.class, ClientMessage.class, 0, Side.SERVER);
    }

    @Override
    public IMessage onMessage(IMessage message, MessageContext ctx)
    {
        return null;
    }

    public static void sendMessage(IMessage message)
    {
        INSTANCE.sendToServer(message);
    }
}