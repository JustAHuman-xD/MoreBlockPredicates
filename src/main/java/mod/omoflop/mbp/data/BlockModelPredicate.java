package mod.omoflop.mbp.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import mod.omoflop.mbp.client.MBPClient;
import mod.omoflop.mbp.data.conditions.AdjacentBlock;
import mod.omoflop.mbp.data.conditions.CoordinateRange;
import mod.omoflop.mbp.data.conditions.InBiome;
import mod.omoflop.mbp.data.conditions.IsBlockState;
import mod.omoflop.mbp.data.conditions.IsContext;
import mod.omoflop.mbp.data.conditions.LightRange;
import mod.omoflop.mbp.data.logic.Not;
import mod.omoflop.mbp.data.logic.Or;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class BlockModelPredicate implements WorldViewCondition {
    private static final HashMap<String, Function<JsonElement, BlockModelPredicate>> HANDLERS = new HashMap<>();
    static {
        // Logical operators
        HANDLERS.put("or", Or::parse);
        HANDLERS.put("not", Not::parse);

        // Actual conditions
        HANDLERS.put("adjacent_block", AdjacentBlock::parse);
        HANDLERS.put("coordinate_range", CoordinateRange::parse);
        HANDLERS.put("biome", InBiome::parse);
        HANDLERS.put("state", IsBlockState::parse);
        HANDLERS.put("light_range", LightRange::parse);
        HANDLERS.put("is_context", IsContext::parse);
    }

    public static List<BlockModelPredicate> parseFromJson(JsonElement element) {
        ArrayList<JsonObject> objects = new ArrayList<>();
        if (element.isJsonArray()) {
            JsonArray arr = element.getAsJsonArray();
            for (JsonElement obj : arr) {
                objects.add(obj.getAsJsonObject());
            }
        } else {
            objects.add(element.getAsJsonObject());
        }

        ArrayList<BlockModelPredicate> predicates = new ArrayList<>();
        for(JsonObject curObject : objects) {
            for (Map.Entry<String, JsonElement> entries : curObject.entrySet()) {
                if (HANDLERS.containsKey(entries.getKey())) {
                    try {
                        predicates.add(HANDLERS.get(entries.getKey()).apply(entries.getValue()));
                    } catch (JsonParseException e) {
                        MBPClient.LOGGER.warn(String.format("Failed to load predicate \"%s\"! Reason: %s", entries.getKey(), e.getMessage()));
                    }
                } else {

                    MBPClient.LOGGER.warn(String.format("Unhandled predicate \"%s\"!", entries.getKey()));
                }
            }
        }

        return predicates;
    }



}
