package quarris.rotm.config.types;

import net.minecraft.util.ResourceLocation;

public class HealthRegainType {

    public final ResourceLocation target;
    public final float healthPercentage;
    public final boolean lastManStanding;
    public final float radius;

    public HealthRegainType(ResourceLocation target, float healthPercentage, boolean lastManStanding, float radius) {
        this.target = target;
        this.healthPercentage = healthPercentage;
        this.lastManStanding = lastManStanding;
        this.radius = radius;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HealthRegainType{");
        sb.append("target=").append(target);
        sb.append(", healthPercentage=").append(healthPercentage);
        sb.append(", lastManStanding=").append(lastManStanding);
        sb.append(", radius=").append(radius);
        sb.append('}');
        return sb.toString();
    }

    public static class Builder {

        public ResourceLocation target;
        public float healthPercentage;
        public boolean lastManStanding;
        public float radius;

        public Builder target(ResourceLocation target) {
            this.target = target;
            return this;
        }

        public Builder healthPercentage(float healthPercentage) {
            this.healthPercentage = healthPercentage/100f;
            return this;
        }

        public Builder lastManStanding(boolean lastManStanding) {
            this.lastManStanding = lastManStanding;
            return this;
        }

        public Builder radius(float radius) {
            this.radius = radius;
            return this;
        }

        public HealthRegainType build() {
            return new HealthRegainType(this.target, this.healthPercentage, this.lastManStanding, this.radius);
        }

    }
}
