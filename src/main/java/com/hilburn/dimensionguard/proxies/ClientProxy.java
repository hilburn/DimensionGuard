package com.hilburn.dimensionguard.proxies;

import net.minecraftforge.client.MinecraftForgeClient;

import com.hilburn.dimensionguard.client.DisabledRenderer;
import com.hilburn.dimensionguard.items.ModItems;


public class ClientProxy extends CommonProxy {
	
	@Override
	public void initSounds() {
		
		
	}

	@Override
	public void initRenderers() {
		MinecraftForgeClient.registerItemRenderer(ModItems.disable, new DisabledRenderer());
	}
}
