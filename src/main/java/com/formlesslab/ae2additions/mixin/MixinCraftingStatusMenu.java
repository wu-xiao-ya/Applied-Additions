package com.formlesslab.ae2additions.mixin;

import ae2.api.networking.crafting.ICraftingCPU;
import ae2.container.guisync.GuiSync;
import ae2.container.implementations.ContainerCraftingCPU;
import ae2.container.implementations.ContainerCraftingStatus;
import com.formlesslab.ae2additions.client.util.CraftingStatusCpuGrouping;
import com.formlesslab.ae2additions.client.util.CraftingStatusCpuMetadata;
import com.formlesslab.ae2additions.client.util.CraftingStatusCpuMetadataList;
import com.formlesslab.ae2additions.client.util.CraftingStatusCpuMetadataProvider;
import com.formlesslab.ae2additions.me.cluster.AdvCraftingCPU;
import com.formlesslab.ae2additions.me.cluster.ClusterAdvCraftingCPU;
import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.entity.player.InventoryPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

@Mixin(value = ContainerCraftingStatus.class, remap = false)
public abstract class MixinCraftingStatusMenu extends ContainerCraftingCPU implements CraftingStatusCpuMetadataProvider {
    @Shadow
    public ContainerCraftingStatus.CraftingCpuList cpuList;

    @Shadow
    private ImmutableSet<ICraftingCPU> lastCpuSet;

    @Shadow
    @Final
    private WeakHashMap<ICraftingCPU, Integer> cpuSerialMap;

    @GuiSync(10)
    @Unique
    private CraftingStatusCpuMetadataList ae2additions$cpuMetadata = CraftingStatusCpuMetadataList.EMPTY;

    protected MixinCraftingStatusMenu(InventoryPlayer playerInventory, Object host) {
        super(playerInventory, host);
    }

    @Inject(method = "detectAndSendChanges", at = @At(value = "INVOKE", target = "Lae2/container/implementations/ContainerCraftingCPU;detectAndSendChanges()V", shift = At.Shift.BEFORE))
    private void ae2additions$groupQuantumComputerCpus(CallbackInfo ci) {
        if (!this.isServerSide()) {
            return;
        }

        Map<Integer, CraftingStatusCpuMetadata> metadataBySerial = ae2additions$createMetadataBySerial();
        this.ae2additions$cpuMetadata = new CraftingStatusCpuMetadataList(List.copyOf(metadataBySerial.values()));
        if (metadataBySerial.isEmpty() || this.cpuList.cpus().isEmpty()) {
            return;
        }

        Map<Integer, ContainerCraftingStatus.CraftingCpuListEntry> entriesBySerial = new HashMap<>();
        ObjectArrayList<Integer> serials = new ObjectArrayList<>(this.cpuList.cpus().size());
        for (ContainerCraftingStatus.CraftingCpuListEntry cpu : this.cpuList.cpus()) {
            entriesBySerial.put(cpu.serial(), cpu);
            serials.add(cpu.serial());
        }

        List<Integer> orderedSerials = CraftingStatusCpuGrouping.orderSerials(serials, metadataBySerial);
        ObjectArrayList<ContainerCraftingStatus.CraftingCpuListEntry> orderedEntries = new ObjectArrayList<>(orderedSerials.size());
        for (Integer serial : orderedSerials) {
            ContainerCraftingStatus.CraftingCpuListEntry entry = entriesBySerial.get(serial);
            if (entry != null) {
                orderedEntries.add(entry);
            }
        }

        this.cpuList = new ContainerCraftingStatus.CraftingCpuList(orderedEntries);
    }

    @Override
    public CraftingStatusCpuMetadataList ae2additions$getCpuMetadata() {
        return this.ae2additions$cpuMetadata;
    }

    @Unique
    private Map<Integer, CraftingStatusCpuMetadata> ae2additions$createMetadataBySerial() {
        Map<Integer, CraftingStatusCpuMetadata> metadataBySerial = new HashMap<>();
        for (ICraftingCPU cpu : this.lastCpuSet) {
            if (!(cpu instanceof AdvCraftingCPU quantumCpu)) {
                continue;
            }

            ClusterAdvCraftingCPU cluster = quantumCpu.getParent();
            if (cluster == null) {
                continue;
            }

            Integer serial = this.cpuSerialMap.get(cpu);
            if (serial == null) {
                continue;
            }

            metadataBySerial.put(serial, new CraftingStatusCpuMetadata(serial, cluster.getGuiClusterId(), cluster.getRemainingCapacityCPU() == quantumCpu));
        }
        return metadataBySerial;
    }

}
