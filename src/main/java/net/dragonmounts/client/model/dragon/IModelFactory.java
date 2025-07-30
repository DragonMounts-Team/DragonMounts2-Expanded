package net.dragonmounts.client.model.dragon;

import net.dragonmounts.util.ITextureOffsetDefiner;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

import static net.dragonmounts.client.model.dragon.BuiltinFactory.*;
import static net.dragonmounts.entity.DragonModelContracts.NECK_SIZE;
import static net.dragonmounts.entity.DragonModelContracts.TAIL_SIZE;

public interface IModelFactory {
    default void defineTextures(ModelBase model, ITextureOffsetDefiner definer) {
        model.textureWidth = 256;
        model.textureHeight = 256;
        definer.set("body.body", 0, 0);
        definer.set("body.scale", 0, 32);
        definer.set("saddle.cushion", 184, 98);
        definer.set("saddle.front", 214, 120);
        definer.set("saddle.back", 214, 120);
        definer.set("saddle.tie", 220, 100);
        definer.set("saddle.metal", 224, 132);
        definer.set("chest.left", 192, 132);
        definer.set("chest.right", 224, 132);
        definer.set("head.nostril", 48, 0);
        definer.set("head.mainhead", 0, 0);
        definer.set("head.upperjaw", 56, 88);
        definer.set("head.lowerjaw", 0, 88);
        definer.set("head.horn", 28, 32);
        definer.set("foreLeg.thigh", 112, 0);
        definer.set("foreLeg.shank", 148, 0);
        definer.set("foreLeg.foot", 210, 0);
        definer.set("foreLeg.toe", 176, 0);
        definer.set("hindLeg.thigh", 112, 29);
        definer.set("hindLeg.shank", 152, 29);
        definer.set("hindLeg.foot", 180, 29);
        definer.set("hindLeg.toe", 215, 29);
        definer.set("neck.box", 112, 88);
        definer.set("neck.scale", 0, 0);
        definer.set("tail.box", 152, 88);
        definer.set("tail.scale", 0, 0);
        definer.set("tail.horn", 0, 117);
        definer.set("wingarm.bone", 0, 152);
        definer.set("wingarm.skin", 116, 232);
        definer.set("wingfinger.bone", 0, 172);
        definer.set("wingfinger.shortskin", -32, 224);
        definer.set("wingfinger.skin", -49, 176);
        definer.set("wingforearm.bone", 0, 164);
    }

    default HeadPart makeHead(ModelBase base) {
        HeadPart head = new HeadPart(base, "head", new ModelRenderer(base, "head")
                .addBox("lowerjaw", -6, 0, -16, 12, 4, 16)
        );
        head.addBox("upperjaw", -6, -1, -8 + HEAD_OFS, 12, 5, 16);
        head.addBox("mainhead", -8, -8, 6 + HEAD_OFS, 16, 16, 16);
        head.addBox("nostril", -5, -3, -6 + HEAD_OFS, 2, 2, 4);
        head.mirror = true;
        head.addBox("nostril", 3, -3, -6 + HEAD_OFS, 2, 2, 4);
        head.jaw.setRotationPoint(0, 4, 8 + HEAD_OFS);
        head.addChild(makeHorn(base, head, false));
        head.addChild(makeHorn(base, head, true));
        return head;
    }

    default NeckPart makeNeck(ModelBase base) {
        NeckPart neck = new NeckPart(
                new ScalablePart(base, "neck"),
                new ScalablePart(base, "neck"),
                // hide the first and every second scale
                index -> (index & 1) == 0 && index != 0
        );
        neck.normal.addBox("box", -5, -5, -5, NECK_SIZE, NECK_SIZE, NECK_SIZE);
        neck.scaled.addBox("box", -5, -5, -5, NECK_SIZE, NECK_SIZE, NECK_SIZE)
                .addBox("scale", -1, -7, -3, 2, 4, 6);
        return neck;
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

    default TailPart makeTail(ModelBase base) {
        SimpleTailPart tail = new SimpleTailPart(base, "tail");
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
