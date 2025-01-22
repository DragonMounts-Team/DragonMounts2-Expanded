package net.dragonmounts.asm;

import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

public abstract class DMClassTransformers {
    public static byte[] transformLayerCustomHead(byte[] bytecodes) {
        ClassReader reader = new ClassReader(bytecodes);
        ClassNode root = new ClassNode();
        reader.accept(root, 0);
        for (MethodNode method : root.methods) {
            if (!"doRenderLayer".equals(method.name) && !"func_177141_a".equals(
                    FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(root.name, method.name, method.desc)
            )) continue;
            Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
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
                            inject.add(new VarInsnNode(Opcodes.ILOAD, 12)); // isVillager
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
                            method.instructions.insertBefore(next, inject);
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

    private DMClassTransformers() {}
}
