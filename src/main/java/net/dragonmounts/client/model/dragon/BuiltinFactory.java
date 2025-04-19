package net.dragonmounts.client.model.dragon;

import net.dragonmounts.util.math.MathX;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

import static net.dragonmounts.client.model.dragon.DragonModel.TAIL_SIZE;

public enum BuiltinFactory implements IModelFactory {
    NORMAL,
    TAIL_HORNED {
        @Override
        public TailPart makeTail(ModelBase base) {
            HornedTailPart tail = new HornedTailPart(base, "tail", HornedTailPart::makeDefaultedSnapshot);
            tail.addBox("box", -5, -5, -5, TAIL_SIZE, TAIL_SIZE, TAIL_SIZE);
            return tail;
        }
    },
    TAIL_SCALE_INCLINED {
        @Override
        public TailPart makeTail(ModelBase base) {
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
        public TailPart makeTail(ModelBase base) {
            HornedTailPart tail = new HornedTailPart(base, "tail", HornedTailPart::makeDefaultedSnapshot);
            tail.addBox("box", -5, -5, -5, TAIL_SIZE, TAIL_SIZE, TAIL_SIZE);
            return tail;
        }

        @Override
        public LegPart makeForeLeg(ModelBase base) {
            LegPart leg = makeLeg(
                    base,
                    "foreLeg",
                    SKELETON_LEG_WIDTH,
                    SKELETON_LEG_WIDTH,
                    (int) (LEG_LENGTH * 0.77F),
                    (int) (LEG_LENGTH * 0.80F),
                    (int) (LEG_LENGTH * 0.34F),
                    (int) (LEG_LENGTH * 0.33F)
            );
            leg.setRotationPoint(-11, 18, 4);
            return leg;
        }

        @Override
        public LegPart makeHindLeg(ModelBase base) {
            LegPart leg = makeLeg(
                    base,
                    "hindLeg",
                    SKELETON_LEG_WIDTH,
                    SKELETON_LEG_WIDTH + 1,
                    (int) (LEG_LENGTH * 0.90F),
                    (int) (LEG_LENGTH * 0.70F) - 2,
                    (int) (LEG_LENGTH * 0.67F),
                    (int) (LEG_LENGTH * 0.27F)
            );
            leg.setRotationPoint(-11, 13, 46);
            return leg;
        }
    };
    public static final int NORMAL_LEG_WIDTH = 9;
    public static final int SKELETON_LEG_WIDTH = 7;
    public static final int LEG_LENGTH = 26;
    public static final int FOOT_HEIGHT = 4;

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

    public static ModelRenderer makeHorn(ModelBase base, HeadPart head, boolean mirror) {
        final float rad30 = MathX.toRadians(30);
        final int hornThick = 3;
        final int hornLength = 12;
        final float hornOfs = -0.5F * hornThick;
        ModelRenderer horn = new ModelRenderer(base, "head")
                .addBox("horn", hornOfs, hornOfs, hornOfs, hornThick, hornThick, hornLength);
        horn.setRotationPoint(mirror ? 5 : -5, -8, 0);
        horn.rotateAngleX = rad30;
        horn.rotateAngleY = mirror ? rad30 : -rad30;
        horn.rotateAngleZ = 0.0F;
        return horn;
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
