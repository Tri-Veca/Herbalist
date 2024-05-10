package com.herbalist.init.custom.plant;
import com.herbalist.init.ItemInit;
import com.herbalist.util.PropertyUtil;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
public class AlfalfaPlant extends CropBlock {
    public AlfalfaPlant(Properties p_52247_) {
        super(p_52247_);
    }
    public static final IntegerProperty AGE = PropertyUtil.AGE_4;



    @Override
    public int getMaxAge() {
        return 4;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return ItemInit.ALFALFA_SEEDS.get();
    }

    public IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(AGE);
    }
}
