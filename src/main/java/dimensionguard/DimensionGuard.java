
package dimensionguard;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkCheckHandler;
import cpw.mods.fml.relauncher.Side;
import dimensionguard.handlers.EventHandler;
import dimensionguard.handlers.CommonEventHandler;
import dimensionguard.handlers.DisabledHandler;
import dimensionguard.network.MessageHandler;
import dimensionguard.reference.Metadata;
import dimensionguard.reference.Reference;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;
import java.util.Map;


@Mod(modid = Reference.ID, name = Reference.NAME, version = Reference.VERSION_FULL)
/**
 * DimensionGuard Mod
 * 
 * @author Charlie Paterson
 * @license GNU General Public License v3
 **/
public class DimensionGuard {
	public static File config;
	@Instance(Reference.ID)
	public static DimensionGuard instance = new DimensionGuard();

	@Mod.Metadata(Reference.ID)
	public static ModMetadata metadata;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event){
		config=event.getSuggestedConfigurationFile();
		metadata = Metadata.init(metadata);
		MessageHandler.init();
	}

	@NetworkCheckHandler
	public final boolean networkCheck(Map<String, String> remoteVersions, Side side)
	{
		if (side.isClient()) return true;
		else return remoteVersions.containsKey(Reference.ID);
	}
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent event){
		FMLCommonHandler.instance().bus().register(new CommonEventHandler());
		MinecraftForge.EVENT_BUS.register(new EventHandler());
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event){

	}

	@Mod.EventHandler
	public void loadComplete(FMLLoadCompleteEvent event)
	{
		DisabledHandler.init();
	}
}