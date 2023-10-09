package mod.omoflop.mbp.data.logic;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import mod.omoflop.mbp.data.BlockModelPredicate;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

import java.util.List;

public class And extends BlockModelPredicate {

    final List<BlockModelPredicate> predicates;

    public And(List<BlockModelPredicate> predicates) {
        this.predicates = predicates;
    }

    @Override
    public boolean meetsCondition(BlockView world, BlockPos pos, BlockState state, Identifier renderContext) {
        for (BlockModelPredicate action : predicates) {
            if (!action.meetsCondition(world, pos, state, renderContext)) return false;
        }
        return true;
    }

    public static And parse(JsonElement arg) {
        return new And(ImmutableList.copyOf(BlockModelPredicate.parseFromJson(arg)));
    }
}
