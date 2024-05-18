package com.herbalist.init.custom.Kettle;

import com.herbalist.init.ItemInit;
import com.herbalist.init.custom.InfuserItem;
import com.herbalist.init.custom.TeaItem;
import com.herbalist.inventory.WrappedHandler;
import com.herbalist.networking.ModMessages;
import com.herbalist.networking.packet.FluidSyncS2CPacket;
import com.herbalist.util.BlockEntityUtil;
import com.herbalist.util.InitUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class KettleEntity extends BlockEntity implements MenuProvider {


    private final ItemStackHandler itemHandler = new ItemStackHandler(3) {


        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case 0 -> true;
                case 1 -> true;
                case 2 -> stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent();
                default -> super.isItemValid(slot, stack);
            };
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;

    private int maxProgress = 120;
    boolean isboiling = false;



    public KettleEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(BlockEntityUtil.KETTLE_ENTITY.get(), pWorldPosition, pBlockState);
        this.data = new ContainerData() {
            public int get(int index) {
                switch (index) {
                    case 0:
                        return KettleEntity.this.progress;
                    case 1:
                        return KettleEntity.this.maxProgress;
                    default:
                        return 0;
                }
            }

            public void set(int index, int value) {
                switch (index) {
                    case 0:
                        KettleEntity.this.progress = value;
                        break;
                    case 1:
                        KettleEntity.this.maxProgress = value;
                        break;
                }
            }

            public int getCount() {
                return 2;
            }
        };
    }


    @Override
    public Component getDisplayName() {
        return Component.nullToEmpty("Kettle");
    }



    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @javax.annotation.Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (side == null) {
                return lazyItemHandler.cast();
            }
        }
        if (directionWrappedHandlerMap.containsKey(side)) {
            Direction localDir = this.getBlockState().getValue(KettleBlock.FACING);

            if (side == Direction.UP || side == Direction.DOWN) {
                return directionWrappedHandlerMap.get(side).cast();
            }

            return switch (localDir) {
                default -> directionWrappedHandlerMap.get(side.getOpposite()).cast();
                case EAST -> directionWrappedHandlerMap.get(side.getClockWise()).cast();
                case SOUTH -> directionWrappedHandlerMap.get(side).cast();
                case WEST -> directionWrappedHandlerMap.get(side.getCounterClockWise()).cast();
            };
        }

        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return lazyFluidHandler.cast();
        }

        return super.getCapability(cap, side);
    }

    private final FluidTank FLUID_TANK = new FluidTank(3000) {
        @Override
        protected void onContentsChanged() {
            setChanged();
            if(!level.isClientSide()) {
                ModMessages.sendToClients(new FluidSyncS2CPacket(this.fluid, worldPosition));
            }

        }




        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == Fluids.WATER;
        }
    };

    public void setFluid(FluidStack stack) {
        this.FLUID_TANK.setFluid(stack);


    }

    public FluidStack getFluidStack() {
        return this.FLUID_TANK.getFluid();
    }

    private final Map<Direction, LazyOptional<WrappedHandler>> directionWrappedHandlerMap =
            Map.of(Direction.DOWN, LazyOptional.of(() -> new WrappedHandler(itemHandler, (i) -> i == 2, (i, s) -> false)),
                    Direction.NORTH, LazyOptional.of(() -> new WrappedHandler(itemHandler, (index) -> index == 1,
                            (index, stack) -> itemHandler.isItemValid(1, stack))),
                    Direction.SOUTH, LazyOptional.of(() -> new WrappedHandler(itemHandler, (i) -> i == 2, (i, s) -> false)),
                    Direction.EAST, LazyOptional.of(() -> new WrappedHandler(itemHandler, (i) -> i == 1,
                            (index, stack) -> itemHandler.isItemValid(1, stack))),
                    Direction.WEST, LazyOptional.of(() -> new WrappedHandler(itemHandler, (index) -> index == 0 || index == 1,
                            (index, stack) -> itemHandler.isItemValid(0, stack) || itemHandler.isItemValid(1, stack))));
    private LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyFluidHandler = LazyOptional.of(() -> FLUID_TANK);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyFluidHandler.invalidate();

    }

    @Override
    protected void saveAdditional( CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("kettle.progress", progress);
        tag = FLUID_TANK.writeToNBT(tag);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        setChanged();
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        progress = nbt.getInt("kettle.progress");
        FLUID_TANK.readFromNBT(nbt);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        ModMessages.sendToClients(new FluidSyncS2CPacket(this.getFluidStack(), worldPosition));
        return new KettleMenu(pContainerId, pInventory, this, this.data);


    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, KettleEntity pBlockEntity) {
        if (pLevel != null && !pLevel.isClientSide) {
            BlockPos below = pPos.below();
            BlockState stateBelow = pLevel.getBlockState(below);

            if (stateBelow.getBlock() instanceof CampfireBlock) {
                pBlockEntity.isboiling = true;
                // There's a campfire below the kettle

            } else {
                pBlockEntity.isboiling = false;
            }
        }
        if (hasRecipe(pBlockEntity) && pBlockEntity.isboiling) {
            pBlockEntity.progress++;
            setChanged(pLevel, pPos, pState);
            if (pBlockEntity.progress > pBlockEntity.maxProgress) {
                craftItem(pBlockEntity);
            }
        } else {
            pBlockEntity.resetProgress();
            setChanged(pLevel, pPos, pState);
        }
        if (hasFluidItemInSourceSlot(pBlockEntity)) {
            transferItemFluidToFluidTank(pBlockEntity);
        }
    }

    private static boolean hasEnoughFluid(KettleEntity pBlockEntity){
        return pBlockEntity.FLUID_TANK.getFluidAmount() >= 1000;
    }

    public boolean checkIfKettleIsBoiling() {
        if (level != null) {
            BlockPos below = worldPosition.below();
            BlockState stateBelow = level.getBlockState(below);
            return stateBelow.getBlock() instanceof CampfireBlock;
        }
        return false;
    }



    private static void transferItemFluidToFluidTank(KettleEntity pBlockEntity) {
        pBlockEntity.itemHandler.getStackInSlot(2).getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(handler -> {
            int drainAmount = Math.min(pBlockEntity.FLUID_TANK.getSpace(), 1000);

            FluidStack stack = handler.drain(drainAmount, IFluidHandler.FluidAction.SIMULATE);
            if (pBlockEntity.FLUID_TANK.isFluidValid(stack)) {
                stack = handler.drain(drainAmount, IFluidHandler.FluidAction.EXECUTE);
                fillTankWithFluid(pBlockEntity, stack, handler.getContainer());
            }
        });
    }

    private static void fillTankWithFluid(KettleEntity pBLockEntity, FluidStack stack, ItemStack container) {
        pBLockEntity.FLUID_TANK.fill(stack, IFluidHandler.FluidAction.EXECUTE);

        pBLockEntity.itemHandler.extractItem(2, 1, false);
        pBLockEntity.itemHandler.insertItem(2, container, false);
    }

    private static boolean hasFluidItemInSourceSlot(KettleEntity pBlockEntity) {
        return pBlockEntity.itemHandler.getStackInSlot(2).getCount() > 0;
    }


    private static boolean hasRecipe(KettleEntity entity) {
        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        Optional<KettleRecipe> match = level.getRecipeManager()
                .getRecipeFor(KettleRecipe.Type.INSTANCE, inventory, level);

        return match.isPresent() && canInsertAmountIntoOutputSlot(inventory)
                && canInsertItemIntoOutputSlot(inventory, match.get().getResultItem())
                && hasCorrectFluidInTank(entity, match) && hasCorrectFluidAmountInTank(entity, match);

    }

    private static boolean hasCorrectFluidAmountInTank(KettleEntity entity, Optional<KettleRecipe> match) {
        return entity.FLUID_TANK.getFluidAmount() >= entity.FLUID_TANK.getFluid().getAmount();
    }

    private static boolean hasCorrectFluidInTank(KettleEntity entity, Optional<KettleRecipe> match) {
        return entity.FLUID_TANK.getFluid().equals(entity.FLUID_TANK.getFluid());
    }

    //&& hasWaterInWaterSlot(entity) && hasToolsInToolSlot(entity);


    /*private static boolean hasWaterInWaterSlot(KettleEntity entity) {
        return PotionUtils.getPotion(entity.itemHandler.getStackInSlot(0)) == Potions.WATER;
    }

    private static boolean hasToolsInToolSlot(KettleEntity entity) {
        return entity.itemHandler.getStackInSlot(2).getItem() == Items.DIAMOND_AXE;
    }

     */
      /*
             entity.itemHandler.extractItem(1,1, false);
             entity.itemHandler.getStackInSlot(2).hurt(1, new Random(), null);
             */



    private static void craftItem(KettleEntity entity) {
        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        Optional<KettleRecipe> match = level.getRecipeManager()
                .getRecipeFor(KettleRecipe.Type.INSTANCE, inventory, level);

        if (hasRecipe(entity)) {
            entity.FLUID_TANK.drain(entity.FLUID_TANK.getFluid().getAmount(), IFluidHandler.FluidAction.EXECUTE);
            entity.itemHandler.extractItem(1, 1, false);

            entity.itemHandler.setStackInSlot(0, new ItemStack(match.get().getResultItem().getItem(),
                    entity.itemHandler.getStackInSlot(0).getCount() + 1));

            // Get the infuser and its herbs
            ItemStack infuserStack = entity.itemHandler.getStackInSlot(1);
            System.out.println("Item in slot 1: " + infuserStack.getItem());  // Debug line

            // Check if the infuser is present and the necessary conditions are met
            if (!(infuserStack.getItem() instanceof InfuserItem)) {
                System.out.println("Crafting conditions not met. Exiting craftItem method.");
                return;
            }
            if (infuserStack.getItem() instanceof InfuserItem) {
                InfuserItem infuserItem = (InfuserItem) infuserStack.getItem();
                List<String> herbs = infuserItem.getHerbs(entity.itemHandler.getStackInSlot(1));
                System.out.println("HERB_EFFECT_MAP: " + InitUtil.HERB_EFFECT_MAP);
                // Create a new tea item with the effects from the herbs
                ItemStack tea = new ItemStack(ItemInit.TEA.get()); // Replace with actual method to create a new tea item
                List<MobEffectInstance> effects = new ArrayList<>();
                for (String herb : herbs) {
                    // Add the effect from the herb to the tea
                    Item herbItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(herb));
                    System.out.println("Herb item: " + herbItem);
                    MobEffectInstance effectInstance = InitUtil.HERB_EFFECT_MAP.get(herbItem);
                    if (effectInstance != null) {
                        effects.add(effectInstance);
                        System.out.println("Added effect to tea: " + effectInstance);
                    }
                }
                System.out.println("Attempting to add effects to tea...");
                TeaItem.addEffectsToItemStack(tea, effects);
                System.out.println("Effects added to tea: " + effects);

                // Set the output to the new tea item
                entity.itemHandler.setStackInSlot(0, tea);
                entity.resetProgress();
            }
        }
    }

    private void resetProgress() {
        this.progress = 0;
    }

    private static boolean canInsertItemIntoOutputSlot(SimpleContainer inventory, ItemStack output) {
        return inventory.getItem(0).getItem() == output.getItem() || inventory.getItem(0).isEmpty();
    }

    private static boolean canInsertAmountIntoOutputSlot(SimpleContainer inventory) {
        return inventory.getItem(0).getMaxStackSize() > inventory.getItem(0).getCount();
    }
}
