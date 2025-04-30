package net.dragonmounts.asm;

import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public abstract class DMClassTransformers {
    static boolean notMethod(ClassNode clazz, MethodNode method, String forge, String searge) {
        return !forge.equals(method.name) && !searge.equals(
                FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(clazz.name, method.name, method.desc)
        );
    }

    static boolean notMethod(MethodInsnNode method, String forge, String searge) {
        return !forge.equals(method.name) && !searge.equals(
                FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(method.owner, method.name, method.desc)
        );
    }

    /**
     * <pre>{@code
     * // insertion start
     * if (item instanceof DragonHeadItem) {
     *     DragonHeadBlockEntityRenderer.renderLayer((DragonHeadItem) item, limbSwing, flag);
     *     return;
     * }
     * // insertion end
     * if (item == Items.SKULL) // before here
     * }</pre>
     */
    public static byte[] transformLayerCustomHead(byte[] bytecodes) {
        ClassReader reader = new ClassReader(bytecodes);
        ClassNode root = new ClassNode();
        reader.accept(root, 0);
        for (MethodNode entry : root.methods) {
            if (notMethod(root, entry, "doRenderLayer", "func_177141_a")) continue;
            Iterator<AbstractInsnNode> iterator = entry.instructions.iterator();
            while (iterator.hasNext()) {
                AbstractInsnNode node = iterator.next();
                if (node instanceof VarInsnNode && node.getOpcode() == Opcodes.ALOAD && iterator.hasNext()) {
                    AbstractInsnNode next = iterator.next();
                    if (next instanceof FieldInsnNode && next.getOpcode() == Opcodes.GETSTATIC) {
                        FieldInsnNode field = (FieldInsnNode) next;
                        if ("net/minecraft/init/Items".equals(FMLDeobfuscatingRemapper.INSTANCE.map(field.owner))) {
                            int ref = ((VarInsnNode) node).var;
                            InsnList inject = new InsnList();
                            LabelNode back = new LabelNode();
                            inject.add(new TypeInsnNode(Opcodes.INSTANCEOF, "net/dragonmounts/item/DragonHeadItem"));
                            inject.add(new JumpInsnNode(Opcodes.IFEQ, back));
                            inject.add(new VarInsnNode(Opcodes.ALOAD, ref));
                            inject.add(new TypeInsnNode(Opcodes.CHECKCAST, "net/dragonmounts/item/DragonHeadItem"));
                            inject.add(new VarInsnNode(Opcodes.FLOAD, 2)); // limbSwing
                            inject.add(new VarInsnNode(Opcodes.ILOAD, 12)); // flag (isVillager)
                            inject.add(new MethodInsnNode(
                                    Opcodes.INVOKESTATIC,
                                    "net/dragonmounts/client/render/DragonHeadBlockEntityRenderer",
                                    "renderLayer",
                                    "(Lnet/dragonmounts/item/DragonHeadItem;FZ)V",
                                    false
                            ));
                            inject.add(new InsnNode(Opcodes.RETURN));
                            inject.add(back);
                            inject.add(new VarInsnNode(Opcodes.ALOAD, ref));
                            entry.instructions.insertBefore(next, inject);
                            break;
                        }
                    }
                }
            }
        }
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        root.accept(writer);
        return writer.toByteArray();
    }

    /**
     * <pre>{@code
     * this.setDead(); // after here
     * // insertion start
     * IEntityContainer.onItemDestroy(this);
     * // insertion end
     * }</pre>
     */
    public static byte[] transformEntityItem(byte[] bytecodes) {
        ClassReader reader = new ClassReader(bytecodes);
        ClassNode root = new ClassNode();
        reader.accept(root, 0);
        for (MethodNode entry : root.methods) {
            if (notMethod(root, entry, "attackEntityFrom", "func_70097_a")) continue;
            Iterator<AbstractInsnNode> iterator = entry.instructions.iterator();
            while (iterator.hasNext()) {
                AbstractInsnNode node = iterator.next();
                if (node instanceof MethodInsnNode && node.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                    MethodInsnNode method = (MethodInsnNode) node;
                    if (notMethod(method, "setDead", "func_70106_y")) continue;
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
                    break;
                }
            }
        }
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        root.accept(writer);
        return writer.toByteArray();
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
        ClassReader reader = new ClassReader(bytecodes);
        ClassNode root = new ClassNode();
        reader.accept(root, 0);
        for (MethodNode entry : root.methods) {
            if (!"lambda$write$3".equals(entry.name)) continue;
            boolean redirect = false;
            Iterator<AbstractInsnNode> iterator = entry.instructions.iterator();
            while (iterator.hasNext()) {
                AbstractInsnNode node = iterator.next();
                if (node instanceof MethodInsnNode && node.getOpcode() == Opcodes.INVOKEINTERFACE) {
                    MethodInsnNode method = (MethodInsnNode) node;
                    if ("java/util/Map$Entry".equals(method.owner) && "getKey".equals(method.name)) {
                        if (redirect) {
                            method.name = "getValue";
                            break;
                        }
                        redirect = true;
                    }
                }
            }
        }
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        root.accept(writer);
        return writer.toByteArray();
    }

    private DMClassTransformers() {}
}
