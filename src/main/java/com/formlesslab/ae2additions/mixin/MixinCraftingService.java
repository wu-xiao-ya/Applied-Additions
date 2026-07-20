package com.formlesslab.ae2additions.mixin;

import ae2.api.networking.IGrid;
import ae2.api.networking.IGridNode;
import ae2.crafting.CraftingLink;
import ae2.me.cluster.implementations.CraftingCPUCluster;
import ae2.me.service.CraftingService;
import com.formlesslab.ae2additions.me.cluster.AdvCraftingCPU;
import com.formlesslab.ae2additions.me.cluster.AdvCraftingCPUCluster;
import com.formlesslab.ae2additions.me.service.QuantumCraftingServiceBridge;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = CraftingService.class, remap = false)
public abstract class MixinCraftingService {
    @Unique
    private final Set<AdvCraftingCPUCluster> ae2additions$quantumCpuClusters = new HashSet<>();

    @Final
    @Shadow
    private ObjectOpenHashSet<CraftingCPUCluster> craftingCPUClusters;

    @Final
    @Shadow
    private IGrid grid;

    @Shadow
    private boolean updateList;

    @Shadow
    public abstract void addLink(CraftingLink link);

    @Inject(method = "addNode", at = @At("TAIL"))
    private void ae2additions$addQuantumNode(IGridNode gridNode, NBTTagCompound savedData, CallbackInfo ci) {
        if (QuantumCraftingServiceBridge.ownsQuantumCpuNode(gridNode)) {
            this.updateList = true;
        }
    }

    @Inject(method = "removeNode", at = @At("TAIL"))
    private void ae2additions$removeQuantumNode(IGridNode gridNode, CallbackInfo ci) {
        if (QuantumCraftingServiceBridge.ownsQuantumCpuNode(gridNode)) {
            this.updateList = true;
        }
    }

    @Inject(method = "updateCPUClusters", at = @At("TAIL"))
    private void ae2additions$registerQuantumCpus(CallbackInfo ci) {
        this.ae2additions$quantumCpuClusters.clear();
        this.ae2additions$quantumCpuClusters.addAll(QuantumCraftingServiceBridge.collectClusters(this.grid));

        for (AdvCraftingCPUCluster cluster : this.ae2additions$quantumCpuClusters) {
            for (AdvCraftingCPU cpu : cluster.getActiveCPUs()) {
                this.craftingCPUClusters.add(cpu);
                if (cpu.craftingLogic.getLastLink() instanceof CraftingLink link) {
                    this.addLink(link);
                }
            }
            this.craftingCPUClusters.add(cluster.getRemainingCapacityCPU());
        }
    }

    @Inject(method = "onServerEndTick", at = @At("HEAD"))
    private void ae2additions$removeFinishedQuantumCpus(CallbackInfo ci) {
        for (AdvCraftingCPUCluster cluster : this.ae2additions$quantumCpuClusters) {
            cluster.getActiveCPUs();
        }
    }
}
