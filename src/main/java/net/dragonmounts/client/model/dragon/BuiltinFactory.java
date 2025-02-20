package net.dragonmounts.client.model.dragon;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

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
}
