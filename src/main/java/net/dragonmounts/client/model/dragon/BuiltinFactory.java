package net.dragonmounts.client.model.dragon;

import net.dragonmounts.util.ITextureOffsetDefiner;
import net.dragonmounts.util.math.MathX;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

import static net.dragonmounts.client.ClientUtil.withRotation;
import static net.dragonmounts.client.model.dragon.DragonModel.*;

public enum BuiltinFactory implements IModelFactory {
    DEFAULT,
    TAIL_HORNED {
        @Override
        public IModelPart makeTail(ModelBase base) {
            HornedTailPart tail = new HornedTailPart(base, "tail", HornedTailPart::makeDefaultedSnapshot);
            tail.addBox("box", -5, -5, -5, TAIL_SIZE, TAIL_SIZE, TAIL_SIZE);
            return tail;
        }
    },
    TAIL_SCALE_INCLINED {
        @Override
        public IModelPart makeTail(ModelBase base) {
            SimpleTailPart tail = new SimpleTailPart(base, "tail");
            float rot = MathX.toRadians(45);
            ModelRenderer scale = new ModelRenderer(base, "tail")
                    .addBox("scale", -1, -8, -3, 2, 4, 6);
            scale.rotateAngleZ = rot;
            tail.addBox("box", -5, -5, -5, TAIL_SIZE, TAIL_SIZE, TAIL_SIZE).addChild(scale);
            scale = new ModelRenderer(base, "tail")
                    .addBox("scale", -1, -8, -3, 2, 4, 6);
            scale.rotateAngleZ = -rot;
            tail.addChild(scale);
            return tail;
        }
    },
    SKELETON {
        @Override
        public IModelPart makeTail(ModelBase base) {
            HornedTailPart tail = new HornedTailPart(base, "tail", HornedTailPart::makeDefaultedSnapshot);
            tail.addBox("box", -5, -5, -5, TAIL_SIZE, TAIL_SIZE, TAIL_SIZE);
            return tail;
        }

        @Override
        public LegPart makeForeLeg(ModelBase base) {
            LegPart leg = makeLeg(
                    base,
                    "foreLeg",
                    SKELETON_LEG_BASE_WIDTH,
                    SKELETON_LEG_BASE_WIDTH,
                    (int) (LEG_BASE_LENGTH * 0.77F),
                    (int) (LEG_BASE_LENGTH * 0.80F),
                    (int) (LEG_BASE_LENGTH * 0.34F),
                    (int) (LEG_BASE_LENGTH * 0.33F)
            );
            leg.setRotationPoint(-11, 18, 4);
            return leg;
        }

        @Override
        public LegPart makeHindLeg(ModelBase base) {
            LegPart leg = makeLeg(
                    base,
                    "hindLeg",
                    SKELETON_LEG_BASE_WIDTH,
                    SKELETON_LEG_BASE_WIDTH + 1,
                    (int) (LEG_BASE_LENGTH * 0.90F),
                    (int) (LEG_BASE_LENGTH * 0.70F) - 2,
                    (int) (LEG_BASE_LENGTH * 0.67F),
                    (int) (LEG_BASE_LENGTH * 0.27F)
            );
            leg.setRotationPoint(-11, 13, 46);
            return leg;
        }
    };
    public static final int DEFAULT_LEG_BASE_WIDTH = 9;
    public static final int SKELETON_LEG_BASE_WIDTH = 7;
    public static final int LEG_BASE_LENGTH = 26;
    public static final int FOOT_HEIGHT = 4;

    @Override
    public void defineTextures(ITextureOffsetDefiner definer) {
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

    @Override
    public HeadPart makeHead(ModelBase base) {
        HeadPart head = new HeadPart(base, "head", new ModelRenderer(base, "head")
                .addBox("lowerjaw", -6, 0, -16, 12, 4, 16)
        );
        head.addBox("upperjaw", -6, -1, -8 + HEAD_OFS, 12, 5, 16);
        head.addBox("mainhead", -8, -8, 6 + HEAD_OFS, 16, 16, 16);
        head.addBox("nostril", -5, -3, -6 + HEAD_OFS, 2, 2, 4);
        head.mirror = true;
        head.addBox("nostril", 3, -3, -6 + HEAD_OFS, 2, 2, 4);
        head.jaw.setRotationPoint(0, 4, 8 + HEAD_OFS);
        head.addChild(this.makeHorn(base, head, false));
        head.addChild(this.makeHorn(base, head, true));
        return head;
    }

    @Override
    public IModelPart makeNeck(ModelBase base) {
        NeckPart neck = new NeckPart(
                new ScalablePart(base, "neck"),
                new ScalablePart(base, "neck"),
                // hide the first and every second scale
                index -> (index & 1) == 0 || index != 0
        );
        neck.normal.addBox("box", -5, -5, -5, NECK_SIZE, NECK_SIZE, NECK_SIZE);
        neck.scaled.addBox("box", -5, -5, -5, NECK_SIZE, NECK_SIZE, NECK_SIZE)
                .addBox("scale", -1, -7, -3, 2, 4, 6);
        return neck;
    }

    @Override
    public BodyPart makeBody(ModelBase base) {
        BodyPart body = new BodyPart(base, "body", new ModelRenderer(base, "body")
                .addBox("scale", -1, -6, -10, 2, 6, 12)
        );
        body.addBox("body", -12, 0, -16, 24, 24, 64)
                .addBox("scale", -1, -6, 10, 2, 6, 12)
                .addBox("scale", -1, -6, 30, 2, 6, 12)
                .setRotationPoint(0, 4, 8);
        return body;
    }

    @Override
    public WingPart makeWing(ModelBase base) {
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

    @Override
    public IModelPart makeTail(ModelBase base) {
        SimpleTailPart tail = new SimpleTailPart(base, "tail");
        tail.addBox("box", -5, -5, -5, TAIL_SIZE, TAIL_SIZE, TAIL_SIZE)
                .addBox("scale", -1, -8, -3, 2, 4, 6);
        return tail;
    }

    @Override
    public ModelRenderer makeHorn(ModelBase base, HeadPart head, boolean mirror) {
        final float rad30 = MathX.toRadians(30);
        final int hornThick = 3;
        final int hornLength = 12;
        final float hornOfs = -0.5F * hornThick;
        ModelRenderer horn = new ModelRenderer(base, "head")
                .addBox("horn", hornOfs, hornOfs, hornOfs, hornThick, hornThick, hornLength);
        horn.setRotationPoint(mirror ? 5 : -5, -8, 0);
        return withRotation(horn, rad30, mirror ? rad30 : -rad30, 0);
    }

    @Override
    public LegPart makeForeLeg(ModelBase base) {
        LegPart leg = makeLeg(
                base,
                "foreLeg",
                DEFAULT_LEG_BASE_WIDTH,
                DEFAULT_LEG_BASE_WIDTH,
                (int) (LEG_BASE_LENGTH * 0.77F),
                (int) (LEG_BASE_LENGTH * 0.80F),
                (int) (LEG_BASE_LENGTH * 0.34F),
                (int) (LEG_BASE_LENGTH * 0.33F)
        );
        leg.setRotationPoint(-11, 18, 4);
        return leg;
    }

    @Override
    public LegPart makeHindLeg(ModelBase base) {
        LegPart leg = makeLeg(
                base,
                "hindLeg",
                DEFAULT_LEG_BASE_WIDTH,
                DEFAULT_LEG_BASE_WIDTH + 1,
                (int) (LEG_BASE_LENGTH * 0.90F),
                (int) (LEG_BASE_LENGTH * 0.70F) - 2,
                (int) (LEG_BASE_LENGTH * 0.67F),
                (int) (LEG_BASE_LENGTH * 0.27F)
        );
        leg.setRotationPoint(-11, 13, 46);
        return leg;
    }

    @Override
    public ModelRenderer makeChest(ModelBase base) {
        return new ModelRenderer(base, "chest")
                .addBox("left", 12, 0, 21, 4, 12, 12)
                .addBox("right", -16, 0, 21, 4, 12, 12);
    }

    @Override
    public ModelRenderer makeSaddle(ModelBase base) {
        return new ModelRenderer(base, "saddle")
                .addBox("cushion", -7, -2, -15, 15, 3, 20)
                .addBox("tie", 12, 0, -14, 1, 14, 2) // left
                .addBox("tie", -13, 0, -14, 1, 10, 2) // right
                .addBox("metal", 12, 14, -15, 1, 5, 4) // left
                .addBox("metal", -13, 10, -15, 1, 5, 4) // right
                .addBox("front", -3, -3, -14, 6, 1, 2)
                .addBox("back", -6, -4, 2, 13, 2, 2);
    }

    public static LegPart makeLeg(
            ModelBase base,
            String name,
            int width,
            int thighWidth,
            int thighLength,
            int shankLength,
            int footLength,
            int toeLength
    ) {
        int shankWidth = width - 2;
        float thighOffset = thighWidth * -0.5F;
        float shankOffset = shankWidth * -0.5F;
        float footOffsetY = FOOT_HEIGHT * -0.5F;
        float footOffsetZ = footLength * -0.75F;
        LegPart leg = new LegPart(
                base,
                name,
                new ModelRenderer(base, name).addBox(
                        "shank",
                        shankOffset,
                        shankOffset,
                        shankOffset,
                        shankWidth,
                        shankLength,
                        shankWidth
                ),
                new ModelRenderer(base, name).addBox(
                        "foot",
                        thighOffset,
                        footOffsetY,
                        footOffsetZ,
                        width,
                        FOOT_HEIGHT,
                        footLength
                ),
                new ModelRenderer(base, name).addBox(
                        "toe",
                        thighOffset,
                        footOffsetY,
                        -toeLength,
                        width,
                        FOOT_HEIGHT,
                        toeLength
                )
        );
        leg.shank.rotationPointY = thighLength + thighOffset;
        leg.foot.rotationPointY = shankLength + shankOffset * 0.5F;
        leg.toe.rotationPointZ = footOffsetZ - footOffsetY * 0.5F;
        leg.addBox(
                "thigh",
                thighOffset,
                thighOffset,
                thighOffset,
                thighWidth,
                thighLength,
                thighWidth
        );
        return leg;
    }

    public static ModelRenderer makeFinger(ModelBase base, boolean small) {
        ModelRenderer finger = new ModelRenderer(base, "wingfinger")
                .addBox("bone", -70, -1, -1, 70, 2, 2);
        finger.setRotationPoint(-47, 0, 0);
        if (small) {
            finger.addBox("shortskin", -70, 0, 1, 70, 0, 32);
        } else {
            finger.addBox("skin", -70, 0, 1, 70, 0, 48);
        }
        return finger;
    }
}
