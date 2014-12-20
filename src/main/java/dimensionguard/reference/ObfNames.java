package dimensionguard.reference;

import dimensionguard.asm.LoadingPlugin;

public enum ObfNames
{
    SEND_USE_ITEM("sendUseItem","func_78769_a","(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;)Z"),
    ON_PLAYER_RIGHT_CLICK("onPlayerRightClick","func_78760_a","(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIIILnet/minecraft/util/Vec3;)Z"),
    IS_VALID_ARMOR("isValidArmor","isValidArmor","(Lnet/minecraft/item/ItemStack;ILnet/minecraft/entity/Entity;)Z"),
    IS_VALID_ARMOUR("dimensionguard/handlers/DisabledHandler","dimensionguard/handlers/DisabledHandler","isValidArmour","(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/Entity;)Z"),
    IS_DISABLED_STACK("dimensionguard/handlers/DisabledHandler","dimensionguard/handlers/DisabledHandler","isDisabledStack","(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;)Z"),
    IS_DISABLED_PLAYER_STACK("dimensionguard/handlers/DisabledHandler","dimensionguard/handlers/DisabledHandler","isDisabledStack","(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/item/ItemStack;)Z"),
    DISABLED_RENDER("dimensionguard/handlers/DisabledHandler","dimensionguard/handlers/DisabledHandler","disabledRender","(Lnet/minecraft/client/renderer/entity/RenderItem;Lnet/minecraft/item/ItemStack;II)V"),
    RENDER_INVENTORY_SLOT("renderInventorySlot","func_73832_a","(IIIF)V"),
    DRAW_ITEM_STACK("drawItemStack","func_146982_a","(Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V"),
    THE_PLAYER("thePlayer","field_71439_g"),
    MINECRAFT("mc","field_73839_d"),
    MINECRAFT_CONTAINER("mc","field_146297_k"),
    ITEM_RENDERER("itemRenderer","field_73841_b"),
    ITEM_RENDER("itemRender","field_146296_j"),
    RENDER_SLOT("func_146977_a","func_146977_a","(Lnet/minecraft/inventory/Slot;)V"),
    RENDER_ITEM_OVERLAY("renderItemOverlayIntoGUI","func_77021_b");


    private String deObf;
    private String obf;
    private String args;
    private String method;

    ObfNames(String deObf, String obf)
    {
        this.deObf = deObf;
        this.obf = obf;
    }

    ObfNames(String deObf, String obf, String args)
    {
        this(deObf,obf);
        this.args = args;
    }

    ObfNames(String deObf, String obf, String method, String args)
    {
        this(deObf,obf,args);
        this.method=method;
    }

    public String getName()
    {
        return LoadingPlugin.runtimeDeobfEnabled?obf:deObf;
    }

    public String getArgs()
    {
        return args;
    }

    public String getMethod()
    {
        return method;
    }
}
