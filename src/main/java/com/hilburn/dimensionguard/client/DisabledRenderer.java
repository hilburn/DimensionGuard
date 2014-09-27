package com.hilburn.dimensionguard.client;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;

public class DisabledRenderer implements IItemRenderer {
    private static RenderItem renderItem = new RenderItem();
    private static ResourceLocation lockTexture = new ResourceLocation("DimensionGuard","textures/items/lock.png");
    private static Minecraft mc = Minecraft.getMinecraft();
    private static FontRenderer fontRenderer=mc.fontRenderer;
    private static RenderBlocks renderBlocksIr = new RenderBlocks();

    @Override
    public boolean handleRenderType(ItemStack itemStack, ItemRenderType type) {
            return true;//type == ItemRenderType.INVENTORY;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,ItemRendererHelper helper) {
           return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack itemStack, Object... data) {
    	ItemStack thisStack = ItemStack.loadItemStackFromNBT((NBTTagCompound) itemStack.stackTagCompound.getTag("ItemStack"));
    	//Logger.log(thisStack.getDisplayName());
    	//renderItem.renderItemAndEffectIntoGUI(fontRenderer, textureManager, thisStack, 0, 0);
    	GL11.glPushMatrix();
    	TextureManager texturemanager = mc.getTextureManager();
        Item item = thisStack.getItem();
        Block block = Block.getBlockFromItem(item);

        if (thisStack != null && block != null && block.getRenderBlockPass() != 0){
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_CULL_FACE);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        }
		IItemRenderer storedRenderer = MinecraftForgeClient.getItemRenderer(thisStack, type);
		if (storedRenderer!=null){
			storedRenderer.renderItem(type, thisStack, data);
		}
		else{
			if (thisStack.getItemSpriteNumber() == 0 && item instanceof ItemBlock && RenderBlocks.renderItemIn3d(block.getRenderType()))
	        {
	            texturemanager.bindTexture(texturemanager.getResourceLocation(0));

	            if (thisStack != null && block != null && block.getRenderBlockPass() != 0)
	            {
	                GL11.glDepthMask(false);
	                renderBlocksIr.renderBlockAsItem(block, thisStack.getItemDamage(), 1.0F);
	                GL11.glDepthMask(true);
	            }
	            else
	            {
	                renderBlocksIr.renderBlockAsItem(block, thisStack.getItemDamage(), 1.0F);
	            }
	        }
			else{
				//Render Item
			}
		}
		
		if (thisStack != null && block != null && block.getRenderBlockPass() != 0)
        {
            GL11.glDisable(GL11.GL_BLEND);
        }

        GL11.glPopMatrix();  
		
		if (type==ItemRenderType.INVENTORY){
			GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDepthMask(false);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            
            Tessellator tessellator = Tessellator.instance;
            FMLClientHandler.instance().getClient().renderEngine.bindTexture(lockTexture);
            tessellator.startDrawing(GL11.GL_QUADS);
            //tessellator.setColorRGBA(0, 0, 0, 128);
           
            tessellator.addVertex(0, 0, 0);
            tessellator.addVertex(0, 16, 0);
            tessellator.addVertex(16, 16, 0);
            tessellator.addVertex(16, 0, 0);
            tessellator.draw();
            
            GL11.glDepthMask(true);
            GL11.glDisable(GL11.GL_BLEND);
			GL11.glPopMatrix();
		}
		
            
    }
}
