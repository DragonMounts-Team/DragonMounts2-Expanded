package net.dragonmounts.client.model.dragon;

import net.dragonmounts.util.IUVRegistry;
import net.dragonmounts.util.math.MathX;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

import static net.dragonmounts.client.model.dragon.ComplexSegmentedPart.dualSegmented;
import static net.dragonmounts.client.model.dragon.HeadPart.buildBasicHead;
import static net.dragonmounts.client.model.dragon.ModelMagic.*;
import static net.dragonmounts.entity.DragonModelContracts.*;

public enum BuiltinFactory implements IModelFactory {
    NORMAL,
    @Deprecated
    COMPAT {
        @Override
        public void defineTextures(ModelBase model, IUVRegistry registry) {
            super.defineTextures(model, registry);
            registry.set("wingfinger.skin", -49, 176);
        }
    },
    @Deprecated
    COMPAT_TAIL_HORNED {
        @Override
        public void defineTextures(ModelBase model, IUVRegistry registry) {
            super.defineTextures(model, registry);
            registry.set("wingfinger.skin", -49, 176);
        }

        @Override
        public ISegmentedPart makeTail(ModelBase base) {
            return buildHornedTail(base);
        }
    },
    TAIL_HORNED {
        @Override
        public ISegmentedPart makeTail(ModelBase base) {
            return buildHornedTail(base);
        }
    },
    TAIL_SCALE_INCLINED {
        @Override
        public ISegmentedPart makeTail(ModelBase base) {
            SimpleSegmentedPart tail = new SimpleSegmentedPart(base, "tail", TAIL_SEGMENTS, BuiltinFactory::makeNormalTailSnapshot);
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
    SCALE_SHARPENED {
        @Override
        public BodyPart makeBody(ModelBase root) {
            BodyPart body = new BodyPart(root, "body", attachSharpenedBackScale(new ModelRenderer(root, "back"), -22.0F));
            attachSharpenedBackScale(body, 5.0F)
                    .addBox("body", -12, 0, -16, 24, 24, 64)
                    .setRotationPoint(0, 4, 8);
            body.back.rotationPointZ = 5.0F + 9.0F * TAN_10_DEG;
            body.back.rotateAngleX = MathX.toRadians(10.0F);
            ModelRenderer scale = attachSharpenedBackScale(new ModelRenderer(root), 0.0F);
            scale.rotationPointZ = 27.0F - 9.0F * TAN_15_DEG;
            scale.rotateAngleX = MathX.toRadians(-15.0F);
            body.addChild(scale);
            return body;
        }

        @Override
        public ISegmentedPart makeNeck(ModelBase base) {
            ScalablePart normal = new ScalablePart(base, "neck");
            ScalablePart scaled = new ScalablePart(base, "neck");
            ScalablePart scale = new ScalablePart(base, null);
            scale.renderScaleY = 0.6F;
            scale.setTextureOffset(0, 29)
                    .addBox(0, -10, -5, 0, 9, NECK_SIZE)
                    .rotationPointY = -4.0F;
            scaled.addBox("box", -5, -5, -5, NECK_SIZE, NECK_SIZE, NECK_SIZE)
                    .addChild(scale);
            normal.addBox("box", -5, -5, -5, NECK_SIZE, NECK_SIZE, NECK_SIZE);
            return dualSegmented(normal, scaled, NECK_SEGMENTS, BuiltinFactory::makeNormalNeckSnapshot, index -> index == 3 || index == 5);
        }

        @Override
        public ISegmentedPart makeTail(ModelBase root) {
            ScalablePart[] parts = new ScalablePart[TAIL_SEGMENTS];
            ModelRenderer scale = new ModelRenderer(root);
            attachSharpenedTailScale(scale);
            scale.rotationPointY = 4.0F;
            scale.rotateAngleX = MathX.toRadians(12.5F);
            (parts[0] = buildTailSegment(root)).addChild(scale);
            scale = new ModelRenderer(root);
            attachSharpenedTailScale(scale);
            scale.rotationPointY = 2.0F;
            scale.rotateAngleX = MathX.toRadians(2.5F);
            (parts[1] = buildTailSegment(root)).addChild(scale);
            ScalablePart segment = buildTailSegment(root);
            attachSharpenedTailScale(segment);
            for (int i = 2; i < TAIL_SEGMENTS; ++i) {
                parts[i] = segment;
            }
            parts[6] = buildHornedTailSegmentWithSharpenScale(root, 7, 26, TAIL_HORN_OFFSET + 9.0F, 206, 192);
            parts[7] = buildHornedTailSegmentWithSharpenScale(root, 5, 22, TAIL_HORN_OFFSET + 10.0F, 200, 192);
            parts[8] = buildHornedTailSegmentWithSharpenScale(root, 15, 22, TAIL_HORN_OFFSET + 10.0F, 170, 192);
            return new ComplexSegmentedPart(parts, BuiltinFactory::makeNormalTailSnapshot);
        }
    },
    SKELETON {
        @Override
        public BodyPart makeBody(ModelBase base) {
            BodyPart body = new BodyPart(base, "body", new ModelRenderer(base)
                    .setTextureOffset(0, 32)
                    .addBox(-1, -6, -10, 2, 6, 12/*, ATTACHED_TO_BOTTOM*/)
            );
            body.setTextureOffset(0, 0)
                    .addBox(-12, 0, -16, 24, 24, 64, 0.1F);
            // scales
            body.setTextureOffset(0, 32)
                    .addBox(-1, -6, 10, 2, 6, 12/*, ATTACHED_TO_BOTTOM*/)
                    .addBox(-1, -6, 30, 2, 6, 12/*, ATTACHED_TO_BOTTOM*/)
                    // ribs
                    .setTextureOffset(128, 112)
                    .addBox(-11.5F, 1.5F, -13, 23, 18, 40)
                    // spines
                    .setTextureOffset(144, 176)
                    .addBox(-4, 0, -15.5F, 8, 8, 21/*, ATTACHED_TO_SOUTH*/)
                    .addBox(-4, 0, 5.5F, 8, 8, 21/*, SPINE_SURFACE*/)
                    .addBox(-4, 0, 26.5F, 8, 8, 21/*, ATTACHED_TO_NORTH*/)
                    // sternum
                    .setTextureOffset(176, 192)
                    .addBox(-1.5F, 18, -15, 3, 5, 29)
                    .setTextureOffset(176, 52)
                    .addBox(-3.5F, 17, -13, 7, 4, 29)
                    // heart
                    .setTextureOffset(112, 128)
                    .addBox(-4, 12, -9, 8, 6, 15)
                    // shoulders
                    .setTextureOffset(112, 112)
                    .addBox(-11, 0, -14, 7, 3, 13/*, ATTACHED_TO_EAST*/)
                    .mirror = true;
            body.addBox(4, 0, -14, 7, 3, 13/*, ATTACHED_TO_EAST*/)
                    .setTextureOffset(72, 110)
                    .addBox(7, 1, -15, 5, 12, 10)
                    .mirror = false;
            body.addBox(-12, 1, -15, 5, 12, 10)
                    // hips
                    .setTextureOffset(72, 132)
                    .addBox(-11, 0, 32, 7, 12, 13)
                    .mirror = true;
            body.addBox(4, 0, 32, 7, 12, 13)
                    .setRotationPoint(0, 4, 8);
            return body;
        }

        @Override
        public HeadPart makeHead(ModelBase root) {
            return buildBasicHead(root);
        }

        @Override
        public ISegmentedPart makeNeck(ModelBase root) {
            ScalablePart scaled = buildSkeletonNeckSegment(root);
            scaled.setTextureOffset(0, 10)
                    .addBox(-1, -7, -3, 2, 2, 3, false/*, ATTACHED_TO_BOTTOM*/);
            return dualSegmented(buildSkeletonNeckSegment(root), scaled, NECK_SEGMENTS, BuiltinFactory::makeSkeletonNeckSnapshot, index -> index == 2 || index == 4);
        }

        @Override
        public ISegmentedPart makeTail(ModelBase root) {
            ScalablePart[] parts = new ScalablePart[TAIL_SEGMENTS];
            ScalablePart segment = buildSkeletonTailSegment(root);
            segment.setTextureOffset(152, 88).addBox(-5.5F, -5, -5, 11, 9, 10, 0.1F);
            attachSkeletonSideTailBone(segment, 2.0F);
            segment.addChild(buildSkeletonTailBone(root, 0.0F));
            for (int i = 0; i < 5; ++i) {
                parts[i] = segment;
            }
            (parts[5] = buildSkeletonTailSegment(root)).addChild(buildSkeletonTailBone(root, -0.5F));
            (parts[6] = segment = buildSkeletonTailSegment(root)).addChild(buildSkeletonTailBone(root, -1.0F));
            segment.setTextureOffset(152, 88)
                    .addBox(-5.5F, -5, -5, 11, 9, 10, 0.1F);
            attachSkeletonSideTailBone(segment, 1.0F);
            for (int i = 7; i < 10; ++i) {
                parts[i] = segment = buildSkeletonTailSegment(root);
                attachSkeletonSideTailBone(segment, 1.0F);
                segment.setTextureOffset(38, 123)
                        .addBox(-3.5F, -5, -5, 7, 6, 10, 0.1F);
            }
            parts[7].addChild(buildSkeletonTailBone(root, -1.5F));
            parts[8].addChild(buildSkeletonTailBone(root, -2.0F));
            (parts[10] = buildSkeletonTailSegment(root))
                    .setTextureOffset(38, 123)
                    .addBox(-3.5F, -5, -5, 7, 6, 10, 0.1F);
            parts[11] = buildSkeletonTailSegment(root);
            return new ComplexSegmentedPart(parts, BuiltinFactory::makeNormalTailSnapshot);
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
    public static final int HEAD_OFS = -16;
    public static final int HORN_THICK = 3;
    public static final int TAIL_HORN_LENGTH = 32;
    static final float TAN_10_DEG = (float) Math.tan(10 * MathX.DEGREES_TO_RADIANS);
    static final float TAN_15_DEG = (float) Math.tan(15 * MathX.DEGREES_TO_RADIANS);

    public static ScalablePart.Snapshot makeNormalNeckSnapshot(int index) {
        float scale = MathX.lerp(1.6F, 1.0F, (index + 1) / (float) NECK_SEGMENTS);
        return new ScalablePart.Snapshot(scale, scale, 0.6F);
    }

    public static ScalablePart.Snapshot makeNormalTailSnapshot(int index) {
        float scale = MathX.lerp(1.5F, 0.3F, (index + 1) / (float) TAIL_SEGMENTS);
        return new ScalablePart.Snapshot(scale, scale, scale);
    }

    public static ScalablePart.Snapshot makeSkeletonNeckSnapshot(int index) {
        float scale = MathX.lerp(1.6F, 1.0F, (index + 1) / (float) NECK_SEGMENTS);
        return new ScalablePart.Snapshot(scale, scale, 1.0F);
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

    public static ScalablePart buildTailSegment(ModelBase root) {
        ScalablePart segment = new ScalablePart(root, "tail");
        segment.addBox("box", -5, -5, -5, TAIL_SIZE, TAIL_SIZE, TAIL_SIZE);
        return segment;
    }

    /// @param mirror invoked with true if it is left, false otherwise
    public static ModelRenderer buildTailHorn(ModelBase root, boolean mirror) {
        ModelRenderer horn = new ModelRenderer(root, "tail");
        horn.mirror = mirror;
        float hornOfs = horn.rotationPointY = -0.5F * HORN_THICK;
        horn.rotationPointZ = 0.5F * TAIL_SIZE;
        horn.rotateAngleX = TAIL_HORN_ROT_X;
        horn.rotateAngleY = mirror ? TAIL_HORN_ROT_Y : -TAIL_HORN_ROT_Y;
        return horn.addBox("horn", hornOfs, hornOfs, hornOfs, HORN_THICK, HORN_THICK, TAIL_HORN_LENGTH);
    }

    public static ComplexSegmentedPart buildHornedTail(ModelBase root) {
        ScalablePart normal = new ScalablePart(root, "tail");
        ScalablePart horned = buildTailSegment(root);
        horned.addChild(buildTailHorn(root, true));
        horned.addChild(buildTailHorn(root, false));
        return dualSegmented(
                buildTailSegment(root),
                horned,
                TAIL_SEGMENTS,
                BuiltinFactory::makeNormalTailSnapshot,
                index -> index + 7 > TAIL_SEGMENTS && index + 3 < TAIL_SEGMENTS
        );
    }

    public static ModelRenderer attachSharpenedBackScale(ModelRenderer bone, float offset) {
        return bone.setTextureOffset(0, 27).addBox(0, -12, offset, 0, 12, 22);
    }

    public static void attachSharpenedTailScale(ModelRenderer bone) {
        bone.setTextureOffset(0, 29).addBox(0, -14, -5, 0, 9, 10);
    }

    public static ScalablePart buildHornedTailSegmentWithSharpenScale(ModelBase root, int width, int length, float offset, int u, int v) {
        ScalablePart segment = buildTailSegment(root);
        attachSharpenedTailScale(segment);
        // left horn
        ModelRenderer horn = new ModelRenderer(root);
        horn.mirror = true;
        horn.rotationPointY = TAIL_HORN_OFFSET;
        horn.rotationPointZ = -HALF_TAIL_SIZE;
        horn.rotateAngleX = TAIL_HORN_ROT_X;
        horn.rotateAngleY = TAIL_HORN_ROT_Y;
        segment.addChild(horn.setTextureOffset(u, v)
                .addBox(TAIL_HORN_OFFSET - width, TAIL_HORN_OFFSET + 1.5F, offset, width, 0, length/*, TOP_SURFACE*/)
                .setTextureOffset(0, 117)
                .addBox(TAIL_HORN_OFFSET, TAIL_HORN_OFFSET, TAIL_HORN_OFFSET, HORN_THICK, HORN_THICK, TAIL_HORN_LENGTH/*, ATTACHED_TO_NORTH*/));
        // right horn
        horn = new ModelRenderer(root);
        horn.rotationPointY = TAIL_HORN_OFFSET;
        horn.rotationPointZ = -HALF_TAIL_SIZE;
        horn.rotateAngleX = TAIL_HORN_ROT_X;
        horn.rotateAngleY = -TAIL_HORN_ROT_Y;
        segment.addChild(horn.setTextureOffset(u, v)
                .addBox(TAIL_HORN_OFFSET + 3.0F, TAIL_HORN_OFFSET + 1.5F, offset, width, 0, length/*, TOP_SURFACE*/)
                .setTextureOffset(0, 117)
                .addBox(TAIL_HORN_OFFSET, TAIL_HORN_OFFSET, TAIL_HORN_OFFSET, HORN_THICK, HORN_THICK, TAIL_HORN_LENGTH/*, ATTACHED_TO_NORTH*/));
        return segment;
    }

    public static ScalablePart buildSkeletonNeckSegment(ModelBase root) {
        ScalablePart segment = new ScalablePart(root, null);
        segment.setTextureOffset(112, 88)
                .addBox(-5, -5, -5, 10, 9, 6, 0.1F);
        segment.setTextureOffset(0, 108)
                .addBox(-3, -5, -5, 6, 7, 6)
                .setTextureOffset(0, 108)
                .addBox(3, -2, -3, 1, 2, 2/*, ATTACHED_TO_WEST*/)
                .mirror = true;
        segment.addBox(-4, -2, -3, 1, 2, 2/*, ATTACHED_TO_WEST*/);
        return segment;
    }

    public static ScalablePart buildSkeletonTailSegment(ModelBase root) {
        ScalablePart segment = new ScalablePart(root, null);
        segment.setTextureOffset(24, 108)
                .addBox(-3, -4, -5, 6, 5, 10)
                .setTextureOffset(0, 0)
                .addBox(-1, -8, -3, 2, 4, 6/*, ATTACHED_TO_BOTTOM*/);
        return segment;
    }

    public static ModelRenderer buildSkeletonTailBone(ModelBase root, float offset) {
        ModelRenderer bone = new ModelRenderer(root)
                .setTextureOffset(24, 108)
                .addBox(-1, 0, -1, 2, 4, 2/*, ATTACHED_TO_TOP*/);
        bone.rotateAngleX = -10.0F * MathX.DEGREES_TO_RADIANS;
        bone.rotationPointY = offset;
        return bone;
    }

    public static void attachSkeletonSideTailBone(ModelRenderer segment, float offset) {
        segment.mirror = true;
        segment.setTextureOffset(48, 110)
                .addBox(-3 - offset, -2, -2, 2, 2, 4/*, ATTACHED_TO_WEST*/)
                .mirror = false;
        segment.addBox(1 + offset, -2, -2, 2, 2, 4/*, ATTACHED_TO_WEST*/);
    }
}
