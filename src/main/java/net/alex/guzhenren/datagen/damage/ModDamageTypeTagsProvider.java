package net.alex.guzhenren.datagen.damage;

import java.util.concurrent.CompletableFuture;
import net.alex.guzhenren.Guzhenren;
import net.alex.guzhenren.registry.damage.ModDamageTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Writes the damage type tags.
 *
 * <p>Extends {@link net.minecraft.data.tags.TagsProvider} for {@link net.minecraft.world.damagesource.DamageType}.
 * All six mod damage types carry the same six bypass tags (armor, effects, enchantments, resistance,
 * shield, no knockback) but deliberately NOT {@code BYPASSES_INVULNERABILITY} -- creative stays
 * unkillable. The aperture pressure explosion also carries {@code IS_EXPLOSION}. Takes the datapack
 * provider's registry lookup so it sees the types generated this run.
 *
 * @author Alex
 * @version 1.0.0
 * @see net.alex.guzhenren.datagen.ModDatapackProvider
 * @since 1.0.0
 */

public class ModDamageTypeTagsProvider extends TagsProvider<DamageType> {

    public ModDamageTypeTagsProvider(PackOutput output,
                                     CompletableFuture<HolderLookup.Provider> lookupProvider,
                                     @Nullable ExistingFileHelper existingFileHelper) {
        super(output, Registries.DAMAGE_TYPE, lookupProvider, Guzhenren.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        unstoppable(DamageTypeTags.BYPASSES_ARMOR);
        unstoppable(DamageTypeTags.BYPASSES_EFFECTS);
        unstoppable(DamageTypeTags.BYPASSES_ENCHANTMENTS);
        unstoppable(DamageTypeTags.BYPASSES_RESISTANCE);
        unstoppable(DamageTypeTags.BYPASSES_SHIELD);
        unstoppable(DamageTypeTags.NO_KNOCKBACK);
        tag(DamageTypeTags.IS_EXPLOSION).add(ModDamageTypes.APERTURE_PRESSURE_EXPLOSION,
                ModDamageTypes.TEN_EXTREME_DISASTER);
    }

    private void unstoppable(TagKey<DamageType> tag) {
        tag(tag).add(ModDamageTypes.LIFESPAN_EXHAUSTED,
                ModDamageTypes.SOUL_COLLAPSE,
                ModDamageTypes.MIND_OCEAN_SHATTERED,
                ModDamageTypes.APERTURE_PRESSURE_EXPLOSION,
                ModDamageTypes.TEN_EXTREME_DISASTER,
                ModDamageTypes.VITAL_GU_LOST);
    }
}
