package net.dragonmounts.client.model.dragon;

import net.dragonmounts.util.IUVRegistry;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

import static net.dragonmounts.client.model.dragon.BuiltinFactory.*;
import static net.dragonmounts.client.model.dragon.ComplexSegmentedPart.dualSegmented;
import static net.dragonmounts.client.model.dragon.HeadPart.buildBasicHead;
import static net.dragonmounts.entity.DragonModelContracts.*;

public interface IModelFactory {
    default void defineTextures(ModelBase model, IUVRegistry registry) {
        model.textureWidth = 256;
        model.textureHeight = 256;
        registry.set("body.body", 0, 0);
        registry.set("body.scale", 0, 32);
        registry.set("saddle.cushion", 184, 98);
        registry.set("saddle.front", 214, 120);
        registry.set("saddle.back", 214, 120);
        registry.set("saddle.tie", 220, 100);
        registry.set("saddle.metal", 224, 132);
        registry.set("chest.left", 192, 132);
        registry.set("chest.right", 224, 132);
        registry.set("head.nostril", 48, 0);
        registry.set("head.mainhead", 0, 0);
        registry.set("head.upperjaw", 56, 88);
        registry.set("head.lowerjaw", 0, 88);
        registry.set("head.horn", 28, 32);
        registry.set("foreLeg.thigh", 112, 0);
        registry.set("foreLeg.shank", 148, 0);
        registry.set("foreLeg.foot", 210, 0);
        registry.set("foreLeg.toe", 176, 0);
        registry.set("hindLeg.thigh", 112, 29);
        registry.set("hindLeg.shank", 152, 29);
        registry.set("hindLeg.foot", 180, 29);
        registry.set("hindLeg.toe", 215, 29);
        registry.set("neck.box", 112, 88);
        registry.set("neck.scale", 0, 0);
        registry.set("tail.box", 152, 88);
        registry.set("tail.scale", 0, 0);
        registry.set("tail.horn", 0, 117);
        registry.set("wingarm.bone", 0, 152);
        registry.set("wingarm.skin", 116, 232);
        registry.set("wingfinger.bone", 0, 172);
        registry.set("wingfinger.shortskin", -32, 224);
        registry.set("wingfinger.skin", -48, 176);
        registry.set("wingforearm.bone", 0, 164);
    }

    default HeadPart makeHead(ModelBase root) {
        HeadPart head = buildBasicHead(root);
        head.addBox("nostril", -5, -3, -6 + HEAD_OFS, 2, 2, 4);
        head.mirror = true;
        head.addBox("nostril", 3, -3, -6 + HEAD_OFS, 2, 2, 4);
        return head;
    }

    default ISegmentedPart makeNeck(ModelBase base) {
        ScalablePart normal = new ScalablePart(base, "neck");
        ScalablePart scaled = new ScalablePart(base, "neck");
        scaled.addBox("box", -5, -5, -5, NECK_SIZE, NECK_SIZE, NECK_SIZE)
                .addBox("scale", -1, -7, -3, 2, 4, 6);
        normal.addBox("box", -5, -5, -5, NECK_SIZE, NECK_SIZE, NECK_SIZE);
        // hide the first and every second scale
        return dualSegmented(normal, scaled, NECK_SEGMENTS, BuiltinFactory::makeNormalNeckSnapshot, index -> (index & 1) == 0 && index != 0);
    }

    default BodyPart makeBody(ModelBase base) {
        BodyPart body = new BodyPart(base, "body", new ModelRenderer(base, "body")
                .addBox("scale", -1, -6, -10, 2, 6, 12)
        );
        body.addBox("body", -12, 0, -16, 24, 24, 64)
                .addBox("scale", -1, -6, 10, 2, 6, 12)
                .addBox("scale", -1, -6, 30, 2, 6, 12)
                .setRotationPoint(0, 4, 8);
        return body;
    }

    default WingPart makeWing(ModelBase base) {
        WingPart wing = new WingPart(
                base,
                "wingarm",
                new ModelRenderer(base, "wingforearm")
                        .addBox("bone", -48, -2, -2, 48, 4, 4),
                makeFinger(base, false),
                makeFinger(base, false),
                makeFinger(base, false),
                makeFinger(base, true)
        );
        wing.forearm.setRotationPoint(-28, 0, 0);
        wing.addBox("bone", -28, -3, -3, 28, 6, 6)
                .addBox("skin", -28, 0, 2, 28, 0, 24)
                .setRotationPoint(-10, 5, 4);
        return wing;
    }

    default ISegmentedPart makeTail(ModelBase base) {
        SimpleSegmentedPart tail = new SimpleSegmentedPart(base, "tail", TAIL_SEGMENTS, BuiltinFactory::makeNormalTailSnapshot);
        tail.addBox("box", -5, -5, -5, TAIL_SIZE, TAIL_SIZE, TAIL_SIZE)
                .addBox("scale", -1, -8, -3, 2, 4, 6);
        return tail;
    }

    default LegPart makeForeLeg(ModelBase base) {
        LegPart leg = makeLeg(
                base,
                "foreLeg",
                NORMAL_LEG_WIDTH,
                NORMAL_LEG_WIDTH,
                (int) (LEG_LENGTH * 0.77F),
                (int) (LEG_LENGTH * 0.80F),
                (int) (LEG_LENGTH * 0.34F),
                (int) (LEG_LENGTH * 0.33F)
        );
        leg.setRotationPoint(-11, 18, 4);
        return leg;
    }

    default LegPart makeHindLeg(ModelBase base) {
        LegPart leg = makeLeg(
                base,
                "hindLeg",
                NORMAL_LEG_WIDTH,
                NORMAL_LEG_WIDTH + 1,
                (int) (LEG_LENGTH * 0.90F),
                (int) (LEG_LENGTH * 0.70F) - 2,
                (int) (LEG_LENGTH * 0.67F),
                (int) (LEG_LENGTH * 0.27F)
        );
        leg.setRotationPoint(-11, 13, 46);
        return leg;
    }

    default ModelRenderer makeChest(ModelBase base) {
        return new ModelRenderer(base, "chest")
                .addBox("left", 12, 0, 21, 4, 12, 12)
                .addBox("right", -16, 0, 21, 4, 12, 12);
    }

    default ModelRenderer makeSaddle(ModelBase base) {
        return new ModelRenderer(base, "saddle")
                .addBox("cushion", -7, -2, -15, 15, 3, 20)
                .addBox("tie", 12, 0, -14, 1, 14, 2) // left
                .addBox("tie", -13, 0, -14, 1, 10, 2) // right
                .addBox("metal", 12, 14, -15, 1, 5, 4) // left
                .addBox("metal", -13, 10, -15, 1, 5, 4) // right
                .addBox("front", -3, -3, -14, 6, 1, 2)
                .addBox("back", -6, -4, 2, 13, 2, 2);
    }
}
