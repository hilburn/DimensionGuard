package com.hilburn.dimensionguard.client;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class DisabledRenderer implements IItemRenderer {
    private static RenderItem renderItem = new RenderItem();
    private static ResourceLocation lockTexture = new ResourceLocation("dimensionguard","textures/items/lock.png");
    private static Minecraft mc = Minecraft.getMinecraft();
    //private static FontRenderer fontRenderer=mc.fontRenderer;
    private static RenderBlocks renderBlocksIr = new RenderBlocks();


    @Override
    public boolean handleRenderType (ItemStack item, ItemRenderType type)
    {
        if (!item.hasTagCompound())
            return false;

        switch (type)
        {
        case ENTITY:
            return true;
        case EQUIPPED:
            GL11.glTranslatef(0.03f, 0F, -0.09375F);
        case EQUIPPED_FIRST_PERSON:
            return true;
        case INVENTORY:
            return true;
        default:
        case FIRST_PERSON_MAP:
            return false;
        }
    }
    
    @Override
    public void renderItem (ItemRenderType type, ItemStack itemStack, Object... data)
    {
    	ItemStack thisStack = ItemStack.loadItemStackFromNBT((NBTTagCompound) itemStack.stackTagCompound.getTag("ItemStack"));

        boolean isInventory = type == ItemRenderType.INVENTORY;
        Entity ent = null;
        if (data.length > 1)
            ent = (Entity) data[1];
        Tessellator tess = Tessellator.instance;
        
        TextureManager texturemanager = mc.getTextureManager();
    	GL11.glPushMatrix();
        Item item = thisStack.getItem();
        Block block = Block.getBlockFromItem(item);
//        Entity ent = null;
//        if (data.length > 1)
//            ent = (Entity) data[1];
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
				IIcon icon = thisStack.getIconIndex();
				float xMax;
		        float yMin;
		        float xMin;
		        float yMax;
		        float depth = 1f / 16f;

		        float width;
		        float height;
		        float xDiff;
		        float yDiff;
		        float xSub;
		        float ySub;

		        xMin = icon.getMinU();
		        xMax = icon.getMaxU();
		        yMin = icon.getMinV();
		        yMax = icon.getMaxV();
		        width = icon.getIconWidth();
		        height = icon.getIconHeight();
		        xDiff = xMin - xMax;
		        yDiff = yMin - yMax;
		        xSub = 0.5f * (xMax - xMin) / width;
		        ySub = 0.5f * (yMax - yMin) / height;
		        

		        if (isInventory)
		        {
		            texturemanager.getResourceLocation(thisStack.getItemSpriteNumber());
		            TextureUtil.func_152777_a(false, false, 1.0F);

		            GL11.glDisable(GL11.GL_LIGHTING);
		            //GL11.glEnable(GL11.GL_ALPHA_TEST);
		            //GL11.glAlphaFunc(GL11.GL_GREATER, 0.5F);
		            GL11.glDisable(GL11.GL_BLEND);

		            tess.startDrawingQuads();

	                //tess.setColorOpaque_I(color[i]);
	                tess.addVertexWithUV(0, 16, 0, xMin, yMax);
	                tess.addVertexWithUV(16, 16, 0, xMax, yMax);
	                tess.addVertexWithUV(16, 0, 0, xMax, yMin);
	                tess.addVertexWithUV(0, 0, 0, xMin, yMin);

		            tess.draw();
		            GL11.glEnable(GL11.GL_LIGHTING);
		            //GL11.glDisable(GL11.GL_ALPHA_TEST);

		            //GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		            texturemanager.bindTexture(texturemanager.getResourceLocation(thisStack.getItemSpriteNumber()));
		            TextureUtil.func_147945_b();
		        }
		        else
		        {
		            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		            switch (type)
		            {
		            case EQUIPPED_FIRST_PERSON:
		                break;
		            case EQUIPPED:
		                GL11.glTranslatef(0, -4 / 16f, 0);
		                break;
		            case ENTITY:
		                GL11.glTranslatef(-0.5f, 0f, depth); // correction of the rotation point when items lie on the ground
		            default:
		            }

		            //=====Sides=====
		            tess.startDrawingQuads();
		            tess.setNormal(0, 0, 1);
	                tess.addVertexWithUV(0, 0, 0, xMax, yMax);
	                tess.addVertexWithUV(1, 0, 0, xMin, yMax);
	                tess.addVertexWithUV(1, 1, 0, xMin, yMin);
	                tess.addVertexWithUV(0, 1, 0, xMax, yMin);
		            tess.draw();

		            tess.startDrawingQuads();
		            tess.setNormal(0, 0, -1);
	                tess.addVertexWithUV(0, 1, -depth, xMax, yMin);
	                tess.addVertexWithUV(1, 1, -depth, xMin, yMin);
	                tess.addVertexWithUV(1, 0, -depth, xMin, yMax);
	                tess.addVertexWithUV(0, 0, -depth, xMax, yMax);
		            tess.draw();

		            // ========3Derize=============
		            tess.startDrawingQuads();
		            tess.setNormal(-1, 0, 0);
		            float pos;
		            float iconPos;
	                float w = width, m = xMax, d = xDiff, s = xSub;
	                for (int k = 0, e = (int) w; k < e; ++k)
	                {
	                    pos = k / w;
	                    iconPos = m + d * pos - s;
	                    tess.addVertexWithUV(pos, 0, -depth, iconPos, yMax);
	                    tess.addVertexWithUV(pos, 0, 0, iconPos, yMax);
	                    tess.addVertexWithUV(pos, 1, 0, iconPos, yMin);
	                    tess.addVertexWithUV(pos, 1, -depth, iconPos, yMin);
	                }
		            tess.draw();
		            tess.startDrawingQuads();
		            tess.setNormal(1, 0, 0);
		            float posEnd;
	                w = width; 
	                m = xMax;
	                d = xDiff;
	                s = xSub;
	                float d2 = 1f / w;
	                for (int k = 0, e = (int) w; k < e; ++k)
	                {
	                    pos = k / w;
	                    iconPos = m + d * pos - s;
	                    posEnd = pos + d2;
	                    tess.addVertexWithUV(posEnd, 1, -depth, iconPos, yMin);
	                    tess.addVertexWithUV(posEnd, 1, 0, iconPos, yMin);
	                    tess.addVertexWithUV(posEnd, 0, 0, iconPos, yMax);
	                    tess.addVertexWithUV(posEnd, 0, -depth, iconPos, yMax);
	                }
		            tess.draw();
		            tess.startDrawingQuads();
		            tess.setNormal(0, 1, 0);
	                float h = height;
	                m = yMax;
	                d = yDiff;
	                s = ySub;
	                d2 = 1f / h;
	                for (int k = 0, e = (int) h; k < e; ++k)
	                {
	                    pos = k / h;
	                    iconPos = m + d * pos - s;
	                    posEnd = pos + d2;
	                    tess.addVertexWithUV(0, posEnd, 0, xMax, iconPos);
	                    tess.addVertexWithUV(1, posEnd, 0, xMin, iconPos);
	                    tess.addVertexWithUV(1, posEnd, -depth, xMin, iconPos);
	                    tess.addVertexWithUV(0, posEnd, -depth, xMax, iconPos);
		            }
		            tess.draw();
		            tess.startDrawingQuads();
		            tess.setNormal(0, -1, 0);
	            	h = height; 
	            	m = yMax;
	            	d = yDiff;
	            	s = ySub;
	                for (int k = 0, e = (int) h; k < e; ++k)
	                {
	                    pos = k / h;
	                    iconPos = m + d * pos - s;
	                    tess.addVertexWithUV(1, pos, 0, xMin, iconPos);
	                    tess.addVertexWithUV(0, pos, 0, xMax, iconPos);
	                    tess.addVertexWithUV(0, pos, -depth, xMax, iconPos);
	                    tess.addVertexWithUV(1, pos, -depth, xMin, iconPos);
	                }
		            tess.draw();
		            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		        }
			}
		}
		
		if (thisStack != null && block != null && block.getRenderBlockPass() != 0)
        {
            GL11.glDisable(GL11.GL_BLEND);
        }
        
        GL11.glPopMatrix();

        
    }

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,ItemRendererHelper helper) {
		return true;
	}
    
   /* @Override
    public void renderItem(ItemRenderType type, ItemStack itemStack, Object... data) {
    	ItemStack thisStack = ItemStack.loadItemStackFromNBT((NBTTagCompound) itemStack.stackTagCompound.getTag("ItemStack"));
    	Tessellator tessellator=Tessellator.instance;
    	TextureManager texturemanager = mc.getTextureManager();
    	GL11.glPushMatrix();
        Item item = thisStack.getItem();
        Block block = Block.getBlockFromItem(item);
//        Entity ent = null;
//        if (data.length > 1)
//            ent = (Entity) data[1];
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
				//IIcon icon = thisStack.getIconIndex();
                if (type==ItemRenderType.INVENTORY){
                	//renderItem.renderItemIntoGUI(mc.fontRenderer, texturemanager, thisStack, 0, 0,true);
                }
			}
		}
		
		if (thisStack != null && block != null && block.getRenderBlockPass() != 0)
        {
            GL11.glDisable(GL11.GL_BLEND);
        }

		
		
		if(type==ItemRenderType.INVENTORY){
			IIcon icon = itemStack.getIconIndex();
			TextureUtil.func_152777_a(false, false, 1.0F);
			 GL11.glDisable(GL11.GL_LIGHTING);
	            GL11.glEnable(GL11.GL_ALPHA_TEST);
	            GL11.glAlphaFunc(GL11.GL_GREATER, 0.5F);
	            GL11.glDisable(GL11.GL_BLEND);
			//renderItem.renderEffect(texturemanager, 0, 0);
			
			
			tessellator.startDrawingQuads();
			GL11.
			GL11.glVertex2d(x, y)
			tessellator.addVertexWithUV(0,1,0,icon.getMinU(),icon.getMaxV());
			tessellator.addVertexWithUV(1,1,0,icon.getMaxU(),icon.getMaxV());
			tessellator.addVertexWithUV(1,0,0,icon.getMaxU(),icon.getMinV());
			tessellator.addVertexWithUV(0,0,0,icon.getMinU(),icon.getMinV());
			tessellator.draw();
			GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_ALPHA_TEST);

            //GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            texturemanager.bindTexture(lockTexture);
            TextureUtil.func_147945_b();
		}
		GL11.glPopMatrix(); 
    }*/
}
