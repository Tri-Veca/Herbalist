package com.herbalist.init.custom;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
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
import java.util.List;

public class TeaItem extends Item {
    private static final int DRINK_DURATION = 32;
    private int Nutrition;
    private float SaturationModifier;

    private MobEffectInstance[] EffectInstance;

    public TeaItem(Properties pProperties, int pNutrition, float pSaturationModifier, @Nullable MobEffectInstance... pInstance) {
        super(pProperties);
        this.Nutrition = pNutrition;
        this.SaturationModifier = pSaturationModifier;
        this.EffectInstance = pInstance;
    }

    public static void addEffectsToItemStack(ItemStack itemStack, MobEffectInstance... pInstance) {
        CompoundTag pTag = itemStack.getOrCreateTag();
        ListTag pEffectList = pTag.getList("CustomPotionEffects", 10);

        for (MobEffectInstance pEffect : pInstance) {
            // Check if the effect is already present in the list
            boolean effectFound = false;
            for (int i = 0; i < pEffectList.size(); i++) {
                MobEffectInstance existingEffect = MobEffectInstance.load(pEffectList.getCompound(i));
                if (existingEffect.getEffect() == pEffect.getEffect()) {
                    effectFound = true;
                    break;
                }
            }

            // If the effect is not already present, add it to the list
            if (!effectFound) {
                pEffectList.add(pEffect.save(new CompoundTag()));
            }
        }

        pTag.put("CustomPotionEffects", pEffectList);
    }

    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving) {

        Player player = pEntityLiving instanceof Player ? (Player)pEntityLiving : null;
        if (player instanceof ServerPlayer) {
            CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)player, pStack);
        }

        if (!pLevel.isClientSide) {
            for(MobEffectInstance mobeffectinstance : PotionUtils.getMobEffects(pStack)) {
                if (mobeffectinstance.getEffect().isInstantenous()) {
                    mobeffectinstance.getEffect().applyInstantenousEffect(player, player, pEntityLiving, mobeffectinstance.getAmplifier(), 1.0D);
                } else {
                    pEntityLiving.addEffect(new MobEffectInstance(mobeffectinstance));
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
                return new ItemStack(Items.GLASS_BOTTLE);
            }

            if (player != null) {
                player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE));
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

    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
        PotionUtils.addPotionTooltip(pStack, pTooltip, 1.0F);
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
    public MobEffectInstance[] getEffectInstance() {
        return EffectInstance;
    }
}

