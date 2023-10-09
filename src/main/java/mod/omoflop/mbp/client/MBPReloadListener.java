package mod.omoflop.mbp.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import mod.omoflop.mbp.MBPData;
import mod.omoflop.mbp.data.logic.When;
import mod.omoflop.mbp.util.Utils;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.block.Block;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MBPReloadListener implements SimpleSynchronousResourceReloadListener {
    public static final Identifier IDENTIFIER = new Identifier("mbp:predicateloader");

    @Override
    public Identifier getFabricId() {
        return IDENTIFIER;
    }

    @Override
    public void reload(ResourceManager manager) {
        MBPData.PREDICATES.clear();

        Map<Identifier, Resource> map = manager.findResources("mbp", id -> id.getPath().endsWith(".json"));
        for (Identifier id : map.keySet()) {
            try {
                Identifier blockTarget = new Identifier(id.toString().substring(0,id.toString().length()-5).replace("mbp/", ""));
                JsonObject asset = JsonParser.parseReader(map.get(id).getReader()).getAsJsonObject();
                Optional<Block> block = Utils.getBlock(blockTarget);
                if (block.isPresent()) {
                    JsonArray overrides = asset.getAsJsonArray("overrides");
                    List<When> whenList = new ArrayList<>();
                    for(JsonElement overrideEntry : overrides) {
                        When when = When.parse(overrideEntry);
                        whenList.add(when);
                    }
                    MBPData.PREDICATES.put(block.get(), Collections.unmodifiableList(whenList));
                } else {
                    MBPClient.LOGGER.error("Block entry not found in file %s: %s ".formatted(id, blockTarget));
                }
            } catch (Exception e) {
                MBPClient.LOGGER.error("Error in file: %s".formatted(id));
                e.printStackTrace();
            }
        }
    }
}
