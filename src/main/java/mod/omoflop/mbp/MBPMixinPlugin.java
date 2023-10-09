package mod.omoflop.mbp;

import mod.omoflop.mbp.client.MBPClient;
import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class MBPMixinPlugin implements IMixinConfigPlugin {
    private static boolean hasSodium = false;

    @Override
    public void onLoad(String mixinPackage) {
        hasSodium = FabricLoader.getInstance().isModLoaded("sodium");
        StringBuilder builder = new StringBuilder();
        if (hasSodium) {
            builder.append("sodium, ");
        }
        if (FabricLoader.getInstance().isModLoaded("worldmesher")) {
            builder.append("worldmesher, ");
        }
        String str = "Starting MBP";
        if (builder.length() > 0) {
            str = str + " with " + builder.substring(0, builder.length()-2);
        }
        MBPClient.LOGGER.debug(str);
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.equals("compat.sodium.ChunkBuilderMeshingTaskMixin")) {
            return hasSodium;
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
        // No need for anything here
    }

    @Override
    public List<String> getMixins() {
        return List.of("compat.sodium.ChunkBuilderMeshingTaskMixin");
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // No need for anything here
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // No need for anything here
    }
}
