package dimensionguard.handlers;

import com.google.common.collect.Table;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.ReflectionHelper;
import dimensionguard.DimensionGuard;
import dimensionguard.disabled.Disabled;
import dimensionguard.utils.Logger;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.regex.Pattern;

/**
 * DimensionGuard Mod
 * 
 * @author Charlie Paterson
 * @license GNU General Public License v3
 **/

public class DisabledHandler {
	public static Map<Item,ArrayList<Disabled>> disabledItemHash = new HashMap<Item, ArrayList<Disabled>>();
	public static Map<Class,ArrayList<Disabled>> disabledEntityHash = new HashMap<Class, ArrayList<Disabled>>();
	public static RenderItem renderItem = new RenderItem();

	DisabledHandler()
	{
		renderItem.zLevel = 500.0F;
	}

	public static void init(){
		ConfigHandler.init(DimensionGuard.config);

		Table<String,String,ItemStack> customStacks = ReflectionHelper.getPrivateValue(GameData.class, null, "customItemStacks");
		Map<String,ItemStack> customItemStacks = new LinkedHashMap<String, ItemStack>();

		for (Table.Cell<String, String,ItemStack> cell: customStacks.cellSet())
		{
			customItemStacks.put(cell.getColumnKey()+":"+cell.getRowKey(),cell.getValue());
		}

		addDisabledItems(true, customItemStacks);
		addDisabledItems(false, customItemStacks);
		addDisabledEntity(true);
		addDisabledEntity(false);
	}

	private static void addDisabledItems(boolean blacklist, Map<String,ItemStack> customItemStacks){
		ArrayList<String> strings = blacklist? ConfigHandler.blackList: ConfigHandler.whiteList;
		String[] splitString;
		String[] dimensions;
		for (String disableItem: strings){
			splitString = disableItem.split(",");
			if (splitString.length<2){
				Logger.log("Insufficient data for meaningful action: "+disableItem);
				continue;
			}
			dimensions = Arrays.copyOfRange(splitString, 1, splitString.length);
			String[] itemInfo = splitString[0].split(":");
			if (itemInfo.length<2){
				Logger.log("Invalid block: "+splitString[0]);
				continue;
			}
			String itemID=itemInfo[0]+":"+itemInfo[1];
			String damage=itemInfo.length==3?itemInfo[2]:"0"; 	//Defaults to 0 if no damage is declared
			if (damage.contains("*")) damage="-1";				//If wildcard is present set to -1 (any)

			ArrayList<Item> matches = new ArrayList<Item>();
			if (itemID.contains("*"))itemID=itemID.replaceAll("\\*", ".*");
			Pattern blockPattern = Pattern.compile(itemID,Pattern.CASE_INSENSITIVE);
			Disabled newDisabled = new Disabled(damage,dimensions,blacklist);

			for (Object key: GameData.getItemRegistry().getKeys()){
				if (blockPattern.matcher(key.toString()).matches())
				{
					matches.add(GameData.getItemRegistry().getObject(key.toString()));
				}
			}

			for (Map.Entry<String,ItemStack> entry:customItemStacks.entrySet())
			{
				if (blockPattern.matcher(entry.getKey()).matches())
				{
					ItemStack value = entry.getValue();
					if (value == null || value.getItem() == null) continue;
					Item stackItem = value.getItem();
					if (newDisabled.damageMatch(value.getItemDamage()) && !matches.contains(stackItem))
						matches.add(stackItem);
				}
			}

			if (matches.isEmpty())Logger.log(itemID+" has no registered matches");
			for (Item match:matches){
				ArrayList<Disabled> temp = new ArrayList<Disabled>(Arrays.asList(newDisabled));
				if (disabledItemHash.get(match)!=null)temp.addAll(disabledItemHash.get(match));
				disabledItemHash.put(match, temp);
			}
		}
	}

	private static void addDisabledEntity(boolean blacklist){
		ArrayList<String> strings = blacklist? ConfigHandler.entityBlacklist: ConfigHandler.entityWhitelist;
		String[] splitString;
		String[] dimensions;
		for (String disableEntity: strings){
			splitString = disableEntity.split(",");
			if (splitString.length<2){
				Logger.log("Insufficient data for meaningful action: "+disableEntity);
				continue;
			}
			dimensions = Arrays.copyOfRange(splitString, 1, splitString.length);
			String entityID=splitString[0];
			ArrayList<String> wildcardMatch=new ArrayList<String>();
			if (entityID.contains("*"))entityID=entityID.replaceAll("\\*", ".*");
			Pattern entityPattern = Pattern.compile(entityID,Pattern.CASE_INSENSITIVE);
			for (Object entity: EntityList.stringToClassMapping.keySet()){
				if (entityPattern.matcher((String)entity).matches()) wildcardMatch.add((String)entity);
			}
			Disabled newDisabled = new Disabled(dimensions,blacklist);
			for (String match:wildcardMatch){
				ArrayList<Disabled> temp = new ArrayList<Disabled>(Arrays.asList(newDisabled));
				if (disabledEntityHash.get(match)!=null)temp.addAll(disabledEntityHash.get(match));
				disabledEntityHash.put((Class) EntityList.stringToClassMapping.get(match), temp);
			}
		}
	}

	public static boolean isDisabledStack(ItemStack stack)
	{
		ArrayList<Disabled> disabled = disabledItemHash.get(stack.getItem());
		if (disabled==null) return false;
		for (Disabled check: disabled)
		{
			if (check.damageMatch(stack.getItemDamage())) return true;
		}
		return false;
	}

	public static boolean isDisabledStack(EntityPlayer entityPlayer)
	{
		return isDisabledStack(entityPlayer,entityPlayer.worldObj,entityPlayer.getCurrentEquippedItem());
	}

	public static boolean isDisabledStack(EntityPlayer player, ItemStack stack)
	{
		return isDisabledStack(player,player.worldObj.provider.dimensionId,stack);
	}

	public static boolean isDisabledStack(EntityPlayer player, World world, ItemStack stack)
	{
		return isDisabledStack(player,world.provider.dimensionId,stack);
	}

	public static boolean isDisabledStack(EntityPlayer player, int dim, ItemStack stack)
	{
		if (ConfigHandler.creativeOverride)
		{
			if (player.capabilities.isCreativeMode) return false;
		}
		return (isDisabledStack(stack, dim));
	}

	public static boolean isDisabledStack(ItemStack stack, int dim)
	{
		if (stack==null) return false;
		ArrayList<Disabled> disabled = disabledItemHash.get(stack.getItem());
		if (disabled==null) return false;
		for (Disabled check: disabled)
		{
			if (check.isDisabled(stack.getItemDamage(),dim)) return true;
		}
		return false;
	}

	public static boolean isValidArmour(ItemStack itemStack, Entity entity)
	{
		if (ConfigHandler.creativeOverride && entity instanceof EntityPlayer)
		{
			if (((EntityPlayer)entity).capabilities.isCreativeMode) return true;
		}
		return !isDisabledStack(itemStack, entity.worldObj.provider.dimensionId);
	}

	public static boolean isDisabledEntity(Class entityClass, int dim){
		ArrayList<Disabled> disabled = disabledEntityHash.get(entityClass);
		if (disabled!=null){
			for (Disabled set:disabled)if (set.dimensionMatch(dim))return true;
		}
		return false;
	}

	private static ResourceLocation texture = new ResourceLocation("dimensionguard","textures/items/lock.png");

	public static void disabledRender(RenderItem itemRender, ItemStack stack, int i, int j)
	{
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		GL11.glPushMatrix();

		if (!(stack.getItemSpriteNumber() == 0 && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(stack.getItem()).getRenderType())))
			GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glColor4f(1.0F,1.0F,1.0F,1.0F);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV((double)(i + 0), (double)(j + 16), 500F, 0, 1);
		tessellator.addVertexWithUV((double)(i + 16), (double)(j + 16), 500F, 1, 1);
		tessellator.addVertexWithUV((double)(i + 16), (double)(j + 0), 500F, 1, 0);
		tessellator.addVertexWithUV((double)(i + 0), (double)(j + 0), 500F, 0, 0);
		tessellator.draw();
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glPopMatrix();
	}
}
