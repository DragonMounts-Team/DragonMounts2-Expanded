package net.dragonmounts.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.Iterator;

import static net.dragonmounts.asm.DragonMountsCore.ASM_LOGGER;

public class LayerCustomHeadTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String raw, String clazz, byte[] asm) {
        if (!"net.minecraft.client.renderer.entity.layers.LayerCustomHead".equals(clazz)) return asm;
        try {
            ClassReader reader = new ClassReader(raw);
            ClassNode root = new ClassNode();
            reader.accept(root, 0);
            for (MethodNode method : root.methods) {
                String name = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(clazz, method.name, method.desc);
                if (!"func_177141_a".equals(name) && !"doRenderLayer".equals(name)) continue;
                Iterator<AbstractInsnNode> iterator = method.instructions.iterator();
                while (iterator.hasNext()) {
                    AbstractInsnNode node = iterator.next();
                    if (node instanceof VarInsnNode && node.getOpcode() == Opcodes.ALOAD) {
                        if (!iterator.hasNext()) break;
                        AbstractInsnNode next = iterator.next();
                        if (next instanceof FieldInsnNode && next.getOpcode() == Opcodes.GETSTATIC) {
                            FieldInsnNode field = (FieldInsnNode) next;
                            if (field.owner.equals("net/minecraft/init/Items")) {
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
        } catch (Exception e) {
            ASM_LOGGER.error("Failed to transform LayerCustomHead", e);
            return asm;
        }
    }
}
