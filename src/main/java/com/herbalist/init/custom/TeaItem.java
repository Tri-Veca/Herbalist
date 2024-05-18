package com.herbalist.init.custom;

import com.herbalist.init.ItemInit;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.dedicated.Settings;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;


import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TeaItem extends Item {
    private static final int DRINK_DURATION = 32;
    private int Nutrition;
    private float SaturationModifier;
    private static final String TAG_EFFECTS = "Effects";

    private List<MobEffectInstance> EffectInstances;

    public TeaItem(Properties pProperties, int pNutrition, float pSaturationModifier, List<MobEffectInstance> pInstances) {
        super(pProperties);
        this.Nutrition = pNutrition;
        this.SaturationModifier = pSaturationModifier;
        this.EffectInstances = pInstances;
    }

    public static void addEffectsToItemStack(ItemStack itemStack, List<MobEffectInstance> pInstances) {
        CompoundTag pTag = itemStack.getOrCreateTag();
        ListTag pEffectList = new ListTag();

        for (MobEffectInstance pEffect : pInstances) {
            pEffectList.add(pEffect.save(new CompoundTag()));
            System.out.println("Added effect to item stack: " + pEffect);
        }

        pTag.put("Effects", pEffectList);
        System.out.println("Effects in tea: " + pEffectList);
    }

    // Method to get the effects from the tea item's NBT data
    public List<MobEffectInstance> getEffects(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTag();
        ListTag effects = nbt.getList("Effects", 10);
        List<MobEffectInstance> effectInstances = new ArrayList<>();
        for (int i = 0; i < effects.size(); i++) {
            effectInstances.add(MobEffectInstance.load(effects.getCompound(i)));
        }
        return effectInstances;
    }



    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving) {
        Player player = pEntityLiving instanceof Player ? (Player)pEntityLiving : null;
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)player, pStack);
        }

        if (!pLevel.isClientSide) {
            // Get the effects from the tea item's NBT data
            List<MobEffectInstance> effects = getEffects(pStack);
            for (MobEffectInstance effect : effects) {
                if (effect.getEffect().isInstantenous()) {
                    effect.getEffect().applyInstantenousEffect(player, player, pEntityLiving, effect.getAmplifier(), 1.0D);
                } else {
                    pEntityLiving.addEffect(new MobEffectInstance(effect));
                }
            }
            FoodData foodData = player.getFoodData();
            foodData.setFoodLevel(foodData.getFoodLevel() + getNutrition());
            foodData.setSaturation(foodData.getSaturationLevel() + getSaturationModifier());
        }

        if (player != null) {
            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild) {
                pStack.shrink(1);
            }
        }

        if (player == null || !player.getAbilities().instabuild) {
            if (pStack.isEmpty()) {
                return new ItemStack(ItemInit.EMPTY_TEA.get());
            }

            if (player != null) {
                player.getInventory().add(new ItemStack(ItemInit.EMPTY_TEA.get()));
            }
        }

        pLevel.gameEvent(pEntityLiving, GameEvent.DRINKING_FINISH, pEntityLiving.eyeBlockPosition());
        return pStack;
    }






    public int getUseDuration(ItemStack pStack) {
        return 32;
    }

    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.DRINK;
    }

    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        return ItemUtils.startUsingInstantly(pLevel, pPlayer, pHand);
    }

    public String getDescriptionId(ItemStack pStack) {
        if (getEffectInstance() != null) {
            addEffectsToItemStack(pStack, getEffectInstance());
        }
        return PotionUtils.getPotion(pStack).getName(this.getDescriptionId() + ".effect.");
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        PotionUtils.addPotionTooltip(stack, tooltip, 1.0F);
        // Debug output

    }



    public boolean isFoil(ItemStack pStack) {
        return false;
    }

    public int getNutrition() {
        return Nutrition;
    }

    public void setSaturationModifier(float saturationModifier) {
        this.SaturationModifier = saturationModifier;
    }

    public float getSaturationModifier() {
        return SaturationModifier;
    }

    @Nullable
    public List<MobEffectInstance> getEffectInstance() {
        return EffectInstances;
    }
}

