package dimensionguard.asm;

import dimensionguard.reference.ObfNames;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DimensionGuardTransformer implements IClassTransformer
{
    private enum ClassName
    {
        //ENTITY_PLAYER("net.minecraft.entity.player.EntityPlayer","yz"),
        //ITEM_STACK("net.minecraft.item.ItemStack","add"),
        INGAME("net.minecraft.client.gui.GuiIngame","bbv", ObfNames.RENDER_INVENTORY_SLOT),
        CONTAINER("net.minecraft.client.gui.inventory.GuiContainer","bex",ObfNames.DRAW_ITEM_STACK,ObfNames.RENDER_SLOT),
        ITEM("net.minecraft.item.Item","adb",ObfNames.IS_VALID_ARMOR),
        PLAYER_CONTROLLER_MP("net.minecraft.client.multiplayer.PlayerControllerMP","bje",ObfNames.ON_PLAYER_RIGHT_CLICK, ObfNames.SEND_USE_ITEM),
        ITEM_WORLD_MANAGER("net.minecraft.server.management.ItemInWorldManager","mx",ObfNames.TRY_USE_ITEM);

        private String deObf;
        private String obf;
        private ObfNames[] methods;

        ClassName(String deObf, String obf)
        {
            this.deObf = deObf;
            this.obf = obf;
        }

        ClassName(String deObf, String obf, ObfNames... methods)
        {
            this(deObf,obf);
            this.methods = methods;
        }

        public String getName()
        {
            return LoadingPlugin.runtimeDeobfEnabled?obf:deObf;
        }

        public String getASMName()
        {
            return deObf.replace(".","/");
        }

        public ObfNames[] getMethods()
        {
            return methods;
        }
    }

    private static Map<String,ClassName> classMap = new HashMap<String, ClassName>();

    static
    {
        for (ClassName className: ClassName.values()) classMap.put(className.getName(),className);
    }

    @Override
    public byte[] transform(String className, String className2, byte[] bytes)
    {
        ClassName clazz = classMap.get(className);
        if (clazz!=null)
        {
            switch (clazz)
            {
                case ITEM_WORLD_MANAGER:
                case PLAYER_CONTROLLER_MP:
                    bytes= insertDisabledCheck(clazz, bytes);
                    break;
                case ITEM:
                    bytes=insertArmourCheck(clazz,bytes);
                    break;
                case CONTAINER:
                case INGAME:
                    bytes=insertDisabledRender(clazz,bytes);
                    break;
            }
            classMap.remove(className);
        }

        return bytes;
    }

    private byte[] insertDisabledRender(ClassName className, byte[] data)
    {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(data);
        classReader.accept(classNode, ClassReader.EXPAND_FRAMES);

        for (ObfNames method:className.getMethods())
        {
            MethodNode methodNode = getMethodByName(classNode, method);
            AbstractInsnNode pos = findMethodNode(ObfNames.RENDER_ITEM_OVERLAY, methodNode).getNext();

            LabelNode l4 = new LabelNode(new Label());
            methodNode.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ALOAD, 0));
            int stackPos = 1;
            int iPos = 2;
            int jPos = 3;
            switch (method)
            {
                case RENDER_INVENTORY_SLOT:
                    methodNode.instructions.insertBefore(pos, new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/gui/GuiIngame", ObfNames.MINECRAFT.getName(), "Lnet/minecraft/client/Minecraft;"));
                    stackPos = 5;
                    break;
                case RENDER_SLOT:
                    stackPos = 4;
                case DRAW_ITEM_STACK:
                    methodNode.instructions.insertBefore(pos, new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/gui/inventory/GuiContainer", ObfNames.MINECRAFT_CONTAINER.getName(), "Lnet/minecraft/client/Minecraft;"));
                    break;


            }
            methodNode.instructions.insertBefore(pos, new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", ObfNames.THE_PLAYER.getName(), "Lnet/minecraft/client/entity/EntityClientPlayerMP;"));
            int player = methodNode.localVariables.size();

            methodNode.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ASTORE, player));
            if (method==ObfNames.RENDER_SLOT)
            {
                methodNode.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ALOAD, stackPos));
                methodNode.instructions.insertBefore(pos, new JumpInsnNode(Opcodes.IFNULL, l4));
            }
            methodNode.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ALOAD, player));
            methodNode.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ALOAD, stackPos));
            methodNode.instructions.insertBefore(pos, new MethodInsnNode(Opcodes.INVOKESTATIC, ObfNames.IS_DISABLED_PLAYER_STACK.getName(), ObfNames.IS_DISABLED_PLAYER_STACK.getMethod(), ObfNames.IS_DISABLED_PLAYER_STACK.getArgs(), false));
            methodNode.instructions.insertBefore(pos, new JumpInsnNode(Opcodes.IFEQ, l4));
            switch (method)
            {
                case RENDER_INVENTORY_SLOT:
                    methodNode.instructions.insertBefore(pos, new FieldInsnNode(Opcodes.GETSTATIC,className.getASMName(),ObfNames.ITEM_RENDERER.getName(),"Lnet/minecraft/client/renderer/entity/RenderItem;"));
                    break;
                default:
                    methodNode.instructions.insertBefore(pos, new FieldInsnNode(Opcodes.GETSTATIC,className.getASMName(),ObfNames.ITEM_RENDER.getName(),"Lnet/minecraft/client/renderer/entity/RenderItem;"));
            }
            methodNode.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ALOAD, stackPos));
            methodNode.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ILOAD, iPos));
            methodNode.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ILOAD, jPos));
            methodNode.instructions.insertBefore(pos, new MethodInsnNode(Opcodes.INVOKESTATIC, ObfNames.DISABLED_RENDER.getName(), ObfNames.DISABLED_RENDER.getMethod(), ObfNames.DISABLED_RENDER.getArgs(), false));
            methodNode.instructions.insertBefore(pos, l4);
        }
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    private static AbstractInsnNode findMethodNode(ObfNames name, MethodNode methodNode)
    {
        for (Iterator<AbstractInsnNode> itr = methodNode.instructions.iterator(); itr.hasNext();)
        {
            AbstractInsnNode node = itr.next();
            if (node instanceof MethodInsnNode)
            {
                if (((MethodInsnNode)node).name.equals(name.getName())) return node;
            }
        }
        return methodNode.instructions.getLast();
    }

    public byte[] insertArmourCheck(ClassName className, byte[] data)
    {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(data);
        classReader.accept(classNode, ClassReader.EXPAND_FRAMES);

        MethodNode methodNode = getMethodByName(classNode, className.getMethods()[0]);
        AbstractInsnNode pos = methodNode.instructions.getFirst();
        LabelNode l4 = new LabelNode(new Label());
        methodNode.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ALOAD, 1));
        methodNode.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ALOAD, 3));
        methodNode.instructions.insertBefore(pos, new MethodInsnNode(Opcodes.INVOKESTATIC, ObfNames.IS_VALID_ARMOUR.getName(), ObfNames.IS_VALID_ARMOUR.getMethod(), ObfNames.IS_VALID_ARMOUR.getArgs(),false));
        methodNode.instructions.insertBefore(pos, new JumpInsnNode(Opcodes.IFNE, l4));
        methodNode.instructions.insertBefore(pos, new InsnNode(Opcodes.ICONST_0));
        methodNode.instructions.insertBefore(pos, new InsnNode(Opcodes.IRETURN));
        methodNode.instructions.insertBefore(pos, l4);

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    public byte[] insertDisabledCheck(ClassName className, byte[] data)
    {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(data);
        classReader.accept(classNode, ClassReader.EXPAND_FRAMES);

        for (ObfNames method:className.getMethods())
        {
            MethodNode methodNode = getMethodByName(classNode, method);
            AbstractInsnNode pos = methodNode.instructions.getFirst();
            LabelNode l4 = new LabelNode(new Label());
            methodNode.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ALOAD, 1));
            methodNode.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ALOAD, 2));
            methodNode.instructions.insertBefore(pos, new VarInsnNode(Opcodes.ALOAD, 3));
            methodNode.instructions.insertBefore(pos, new MethodInsnNode(Opcodes.INVOKESTATIC, ObfNames.IS_DISABLED_STACK.getName(), ObfNames.IS_DISABLED_STACK.getMethod(), ObfNames.IS_DISABLED_STACK.getArgs(),false));
            methodNode.instructions.insertBefore(pos, new JumpInsnNode(Opcodes.IFEQ, l4));
            methodNode.instructions.insertBefore(pos, new InsnNode(Opcodes.ICONST_0));
            methodNode.instructions.insertBefore(pos, new InsnNode(Opcodes.IRETURN));
            methodNode.instructions.insertBefore(pos, l4);
        }
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    public static MethodNode getMethodByName(ClassNode classNode, ObfNames obfName) {
        List<MethodNode> methods = classNode.methods;
        for (int k = 0; k < methods.size(); k++) {
            MethodNode method = methods.get(k);
            if (method.name.equals(obfName.getName()) && method.desc.equals(obfName.getArgs())) {
                return method;
            }
        }
        return null;
    }
}
