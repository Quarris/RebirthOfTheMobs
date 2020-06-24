package quarris.rotm.config;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class DeathSpawnType {
    public final ResourceLocation summon;   // Entity being summoned
    public final int minSpawn;              // Minimum amount to spawn per cycle
    public final int maxSpawn;              // Maximum amount to spawn per cycle
    public final boolean disableXP;         // Should XP be dropped by the summons on death
    public final boolean disableLoot;       // Should Loot be dropped by the summons on death
    public final ResourceLocation sound;    // The sound to be played when the summon happens
    public final NBTTagCompound nbt;        // NBT to apply to the summoned entity on spawn

    public DeathSpawnType(ResourceLocation summon, int minSpawn, int maxSpawn, boolean disableXP, boolean disableLoot, ResourceLocation sound, NBTTagCompound nbt) {
        this.summon = summon;
        this.minSpawn = minSpawn;
        this.maxSpawn = maxSpawn;
        this.disableXP = disableXP;
        this.disableLoot = disableLoot;
        this.sound = sound;
        this.nbt = nbt;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DeathSummonType{");
        sb.append("summon=").append(summon);
        sb.append(", minSpawn=").append(minSpawn);
        sb.append(", maxSpawn=").append(maxSpawn);
        sb.append(", disableXP=").append(disableXP);
        sb.append(", disableLoot=").append(disableLoot);
        sb.append(", sound=").append(sound);
        sb.append(", nbt=").append(nbt);
        sb.append('}');
        return sb.toString();
    }

    static class Builder {
        public ResourceLocation summon;   // Entity being summoned
        public int minSpawn;              // Minimum amount to spawn per cycle
        public int maxSpawn;              // Maximum amount to spawn per cycle
        public boolean disableXP;         // Should XP be dropped by the summons on death
        public boolean disableLoot;       // Should Loot be dropped by the summons on death
        public ResourceLocation sound;    // The sound to be played when the summon happens
        public NBTTagCompound nbt;

        public Builder() { }

        public Builder summon(ResourceLocation summon) {
            this.summon = summon;
            return this;
        }

        public Builder minSpawn(int minSpawn) {
            this.minSpawn = minSpawn;
            return this;
        }

        public Builder maxSpawn(int maxSpawn) {
            this.maxSpawn = maxSpawn;
            return this;
        }

        public Builder disableXP(boolean disableXP) {
            this.disableXP = disableXP;
            return this;
        }


        public Builder disableLoot(boolean disableLoot) {
            this.disableLoot = disableLoot;
            return this;
        }

        public Builder sound(ResourceLocation sound) {
            this.sound = sound;
            return this;
        }

        public Builder nbt(NBTTagCompound nbt) {
            this.nbt = nbt;
            return this;
        }

        public DeathSpawnType build() {
            return new DeathSpawnType(summon, minSpawn, maxSpawn, disableXP, disableLoot, sound, nbt);
        }
    }
}
