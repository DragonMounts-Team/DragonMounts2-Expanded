package net.dragonmounts.asm;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.function.Predicate;

public abstract class DMClassTransformers {
    static byte[] export(ClassNode clazz) {
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        clazz.accept(writer);
        return writer.toByteArray();
    }

    @Nonnull
    static MethodNode findMethod(ClassNode clazz, byte[] bytecodes, String forge, String searge) {
        new ClassReader(bytecodes).accept(clazz, 0);
        for (MethodNode method : clazz.methods) {
            if (forge.equals(method.name) || searge.equals(
                    FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(clazz.name, method.name, method.desc)
            )) return method;
        }
        return new MethodNode(); // just to avoid nullptr
    }

    static boolean notMember(MethodInsnNode method, String forge, String searge) {
        return !forge.equals(method.name) && !searge.equals(
                FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(method.owner, method.name, method.desc)
        );
    }

    static boolean notMember(FieldInsnNode field, String forge, String searge) {
        return !forge.equals(field.name) && !searge.equals(
                FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(field.owner, field.name, field.desc)
        );
    }

    static boolean notFieldOperation(AbstractInsnNode load, Predicate<AbstractInsnNode> predicate) {
        if (load.getOpcode() != Opcodes.ALOAD) return true;
        AbstractInsnNode node = load.getNext();
        if (node == null || node.getOpcode() != Opcodes.GETFIELD) return true;
        node = node.getNext();
        return node == null || predicate.test(node);
    }

    /**
     * <pre>{@code
     * while (this.gameSettings.keyBindInventory.isPressed()) {
     *     // insertion start
     *     if (COpenInventoryPacket.sendIfAvailable(this.player)) continue;
     *     // insertion end
     *     if (this.playerController.isRidingHorse()) // before here
     *     ...
     * }
     * }</pre>
     *
     * @see net.dragonmounts.client.ClientDragonEntity#openInventoryIfAvailable(Minecraft)
     */
    public static byte[] transformMinecraft(byte[] bytecodes) {
        ClassNode clazz = new ClassNode();
        MethodNode entry = findMethod(clazz, bytecodes, "processKeyBinds", "func_184117_aA");
        Iterator<AbstractInsnNode> iterator = entry.instructions.iterator();
        while (iterator.hasNext()) {
            AbstractInsnNode condition = iterator.next();
            if (notFieldOperation(condition, node ->
                    node.getOpcode() != Opcodes.GETFIELD || !(node instanceof FieldInsnNode) || notMember(
                            (FieldInsnNode) node,
                            "keyBindInventory",
                            "field_151445_Q"
                    )
            )) continue;
            while (iterator.hasNext()) {
                AbstractInsnNode self = iterator.next(); // this
                if (notFieldOperation(self, node ->
                        node.getOpcode() != Opcodes.INVOKEVIRTUAL || !(node instanceof MethodInsnNode) || notMember(
                                (MethodInsnNode) node,
                                "isRidingHorse",
                                "func_110738_j"
                        )
                )) continue;
                LabelNode back = new LabelNode();
                LabelNode loop = new LabelNode();
                entry.instructions.insertBefore(condition, loop); // while
                InsnList inject = new InsnList();
                inject.add(new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "net/dragonmounts/client/ClientDragonEntity",
                        "openInventoryIfAvailable",
                        "(Lnet/minecraft/client/Minecraft;)Z",
                        false
                ));
                inject.add(new JumpInsnNode(Opcodes.IFEQ, back));
                inject.add(new JumpInsnNode(Opcodes.GOTO, loop)); // continue
                inject.add(back);
                inject.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
                entry.instructions.insert(self, inject);
                return export(clazz);
            }
        }
        return bytecodes;
    }

    /**
     * <pre>{@code
     * // insertion start
     * if (DragonHeadBlockEntityRenderer.renderIfAvailable(item, limbSwing, flag)) return;
     * // insertion end
     * if (item == Items.SKULL) // before here
     * }</pre>
     * @see net.dragonmounts.client.render.DragonHeadBlockEntityRenderer#renderIfAvailable(Item, float, boolean)
     */
    public static byte[] transformLayerCustomHead(byte[] bytecodes) {
        ClassNode clazz = new ClassNode();
        MethodNode entry = findMethod(clazz, bytecodes, "doRenderLayer", "func_177141_a");
        Iterator<AbstractInsnNode> iterator = entry.instructions.iterator();
        while (iterator.hasNext()) {
            AbstractInsnNode node = iterator.next();
            if (node.getOpcode() == Opcodes.ALOAD && node instanceof VarInsnNode && iterator.hasNext()) {
                AbstractInsnNode next = iterator.next();
                if (next.getOpcode() == Opcodes.GETSTATIC && next instanceof FieldInsnNode && "net/minecraft/init/Items".equals(
                        FMLDeobfuscatingRemapper.INSTANCE.map(((FieldInsnNode) next).owner)
                )) {
                    InsnList inject = new InsnList();
                    LabelNode back = new LabelNode();
                    inject.add(new VarInsnNode(Opcodes.FLOAD, 2)); // limbSwing
                    inject.add(new VarInsnNode(Opcodes.ILOAD, 12)); // flag (isVillager)
                    inject.add(new MethodInsnNode(
                            Opcodes.INVOKESTATIC,
                            "net/dragonmounts/client/render/DragonHeadBlockEntityRenderer",
                            "renderIfAvailable",
                            "(Lnet/minecraft/item/Item;FZ)Z",
                            false
                    ));
                    inject.add(new JumpInsnNode(Opcodes.IFEQ, back));
                    inject.add(new InsnNode(Opcodes.RETURN));
                    inject.add(back);
                    inject.add(new VarInsnNode(Opcodes.ALOAD, ((VarInsnNode) node).var));
                    entry.instructions.insertBefore(next, inject);
                    return export(clazz);
                }
            }
        }
        return bytecodes;
    }

    /**
     * <pre>{@code
     * this.setDead(); // after here
     * // insertion start
     * IEntityContainer.onItemDestroy(this);
     * // insertion end
     * }</pre>
     * @see net.dragonmounts.item.IEntityContainer#onItemDestroy(EntityItem)
     */
    public static byte[] transformEntityItem(byte[] bytecodes) {
        ClassNode clazz = new ClassNode();
        MethodNode entry = findMethod(clazz, bytecodes, "attackEntityFrom", "func_70097_a");
        Iterator<AbstractInsnNode> iterator = entry.instructions.iterator();
        while (iterator.hasNext()) {
            AbstractInsnNode node = iterator.next();
            if (node.getOpcode() == Opcodes.INVOKEVIRTUAL && node instanceof MethodInsnNode) {
                MethodInsnNode method = (MethodInsnNode) node;
                if (notMember(method, "setDead", "func_70106_y")) continue;
                InsnList inject = new InsnList();
                inject.add(new VarInsnNode(Opcodes.ALOAD, 0)); // this
                inject.add(new MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        "net/dragonmounts/item/IEntityContainer",
                        "onItemDestroy",
                        "(Lnet/minecraft/entity/item/EntityItem;)V",
                        true
                ));
                entry.instructions.insert(method, inject);
                return export(clazz);
            }
        }
        return bytecodes;
    }

    /**
     * Before:
     * <pre>{@code
     * tag.setString("K", e.getKey().toString());
     * tag.setString("V", e.getKey().toString()); // here
     * }</pre>
     * After:
     * <pre>{@code
     * tag.setString("K", e.getKey().toString());
     * tag.setString("V", e.getValue().toString()); // here
     * }</pre>
     */
    public static byte[] transformRegistrySnapshot(byte[] bytecodes) {
        ClassNode clazz = new ClassNode();
        new ClassReader(bytecodes).accept(clazz, 0);
        for (MethodNode entry : clazz.methods) {
            if (!"lambda$write$3".equals(entry.name)) continue;
            boolean redirect = false;
            Iterator<AbstractInsnNode> iterator = entry.instructions.iterator();
            while (iterator.hasNext()) {
                AbstractInsnNode node = iterator.next();
                if (node.getOpcode() == Opcodes.INVOKEINTERFACE && node instanceof MethodInsnNode) {
                    MethodInsnNode method = (MethodInsnNode) node;
                    if ("java/util/Map$Entry".equals(method.owner) && "getKey".equals(method.name)) {
                        if (redirect) {
                            method.name = "getValue";
                            return export(clazz);
                        }
                        redirect = true;
                    }
                }
            }
        }
        return bytecodes;
    }

    private DMClassTransformers() {}
}
