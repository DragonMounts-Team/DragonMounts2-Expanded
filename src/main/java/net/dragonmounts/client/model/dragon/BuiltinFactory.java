package net.dragonmounts.client.model.dragon;

import net.dragonmounts.util.math.MathX;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

import static net.dragonmounts.client.ClientUtil.withRotation;
import static net.dragonmounts.client.model.dragon.DragonModel.HEAD_OFS;

public enum BuiltinFactory implements IModelFactory {
    DEFAULT {
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
    },
    SKELETON {
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
        float thighOffset = thighWidth / -2F;
        float shankOffset = shankWidth / -2F;
        float footOffsetY = FOOT_HEIGHT / -2F;
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
        leg.foot.rotationPointY = shankLength + shankOffset / 2F;
        leg.toe.rotationPointZ = footOffsetZ - footOffsetY / 2F;
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
    public NeckPart makeNeck(ModelBase base) {
        return new NeckPart(base, "neck");
    }

    @Override
    public TailPart makeTail(ModelBase base) {
        return new TailPart(base, "tail");
    }

    @Override
    public ModelRenderer makeHorn(ModelBase base, HeadPart head, boolean mirror) {
        final float rad30 = MathX.toRadians(30);
        final int hornThick = 3;
        final int hornLength = 12;
        final float hornOfs = -(hornThick / 2f);
        ModelRenderer horn = new ModelRenderer(base, "head")
                .addBox("horn", hornOfs, hornOfs, hornOfs, hornThick, hornThick, hornLength);
        horn.setRotationPoint(mirror ? 5 : -5, -8, 0);
        return withRotation(horn, rad30, mirror ? rad30 : -rad30, 0);
    }
}
