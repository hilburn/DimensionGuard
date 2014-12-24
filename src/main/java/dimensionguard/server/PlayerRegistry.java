package dimensionguard.server;

import net.minecraft.entity.player.EntityPlayer;

import java.util.*;

public class PlayerRegistry
{
    private static Set<UUID> playerSet = new HashSet<UUID>();

    public static void addPlayer(UUID id)
    {
        playerSet.add(id);
    }

    public static void removePlayer(UUID id)
    {
        playerSet.remove(id);
    }

    public static boolean hasClient(EntityPlayer player)
    {
        return playerSet.contains(player.getPersistentID());
    }

    public PlayerRegistry()
    {
    }
}
