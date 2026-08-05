package com.formlesslab.ae2additions.mixin;

import ae2.client.Point;
import ae2.client.gui.widgets.CPUSelectionList;
import ae2.client.gui.widgets.Scrollbar;
import ae2.container.implementations.ContainerCraftingStatus;
import com.formlesslab.ae2additions.client.util.CraftingStatusCpuGrouping;
import com.formlesslab.ae2additions.client.util.CraftingStatusCpuMetadata;
import com.formlesslab.ae2additions.client.util.CraftingStatusCpuMetadataProvider;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.IntSupplier;

@Mixin(value = CPUSelectionList.class, remap = false)
public abstract class MixinCPUSelectionList {
    @Unique
    private static final int ae2additions$HEADER_HEIGHT = 31;
    @Unique
    private static final int ae2additions$LIST_CONTENT_X = 17;
    @Unique
    private static final int ae2additions$ROW_WIDTH = 67;
    @Unique
    private static final int ae2additions$ROW_HEIGHT = 22;
    @Unique
    private static final int ae2additions$ROW_SPACING = 1;
    @Unique
    private static final int ae2additions$QUANTUM_GROUP_COLOR = 0xFFB65CFF;

    @Shadow
    @Final
    private ContainerCraftingStatus container;

    @Shadow
    @Final
    private Scrollbar scrollbar;

    @Shadow
    @Final
    private IntSupplier visibleRowsSupplier;

    @Shadow
    @Final
    private ObjectArrayList<ContainerCraftingStatus.CraftingCpuListEntry> visibleCpus;

    @Shadow
    private Rectangle bounds;

    @Inject(method = "refreshView", at = @At("TAIL"))
    private void ae2additions$groupQuantumComputerCpus(CallbackInfo ci) {
        Map<Integer, CraftingStatusCpuMetadata> metadataBySerial = ae2additions$getMetadataBySerial();
        if (metadataBySerial.isEmpty() || this.visibleCpus.isEmpty()) {
            return;
        }

        Map<Integer, ContainerCraftingStatus.CraftingCpuListEntry> entriesBySerial = new HashMap<>();
        ObjectArrayList<Integer> serials = new ObjectArrayList<>(this.visibleCpus.size());
        for (ContainerCraftingStatus.CraftingCpuListEntry cpu : this.visibleCpus) {
            entriesBySerial.put(cpu.serial(), cpu);
            serials.add(cpu.serial());
        }

        List<Integer> orderedSerials = CraftingStatusCpuGrouping.orderSerials(serials, metadataBySerial);
        this.visibleCpus.clear();
        for (Integer serial : orderedSerials) {
            ContainerCraftingStatus.CraftingCpuListEntry cpu = entriesBySerial.get(serial);
            if (cpu != null) {
                this.visibleCpus.add(cpu);
            }
        }
    }

    @Inject(method = "drawBackgroundLayer", at = @At("TAIL"))
    private void ae2additions$drawQuantumComputerGroups(Rectangle screenBounds, Point mouse, CallbackInfo ci) {
        Map<Integer, CraftingStatusCpuMetadata> metadataBySerial = ae2additions$getMetadataBySerial();
        if (metadataBySerial.isEmpty() || this.visibleCpus.isEmpty()) {
            return;
        }

        int rows = Math.max(1, this.visibleRowsSupplier.getAsInt());
        int from = MathHelper.clamp(this.scrollbar.getCurrentScroll(), 0, this.visibleCpus.size());
        int to = MathHelper.clamp(from + rows, 0, this.visibleCpus.size());
        int x = screenBounds.x + this.bounds.x + ae2additions$LIST_CONTENT_X;
        int firstRowY = screenBounds.y + this.bounds.y + ae2additions$HEADER_HEIGHT;
        int rowStep = ae2additions$ROW_HEIGHT + ae2additions$ROW_SPACING;

        for (int absoluteIndex = from; absoluteIndex < to; absoluteIndex++) {
            ContainerCraftingStatus.CraftingCpuListEntry cpu = this.visibleCpus.get(absoluteIndex);
            CraftingStatusCpuMetadata metadata = metadataBySerial.get(cpu.serial());
            if (metadata == null || !metadata.quantum()) {
                continue;
            }

            int y = firstRowY + (absoluteIndex - from) * rowStep;
            boolean startsGroup = !ae2additions$isSameGroup(absoluteIndex - 1, metadata.clusterId(), metadataBySerial);
            boolean endsGroup = !ae2additions$isSameGroup(absoluteIndex + 1, metadata.clusterId(), metadataBySerial);

            Gui.drawRect(x - 1, y, x, y + rowStep, ae2additions$QUANTUM_GROUP_COLOR);
            Gui.drawRect(x + ae2additions$ROW_WIDTH, y, x + ae2additions$ROW_WIDTH + 1, y + rowStep, ae2additions$QUANTUM_GROUP_COLOR);
            if (startsGroup) {
                Gui.drawRect(x - 1, y - 1, x + ae2additions$ROW_WIDTH + 1, y, ae2additions$QUANTUM_GROUP_COLOR);
            }
            if (endsGroup) {
                Gui.drawRect(x - 1, y + ae2additions$ROW_HEIGHT, x + ae2additions$ROW_WIDTH + 1, y + ae2additions$ROW_HEIGHT + 1, ae2additions$QUANTUM_GROUP_COLOR);
            }
        }
    }

    @Unique
    private Map<Integer, CraftingStatusCpuMetadata> ae2additions$getMetadataBySerial() {
        if (this.container instanceof CraftingStatusCpuMetadataProvider provider) {
            return provider.ae2additions$getCpuMetadata().bySerial();
        }
        return Map.of();
    }

    @Unique
    private boolean ae2additions$isSameGroup(int index, int clusterId, Map<Integer, CraftingStatusCpuMetadata> metadataBySerial) {
        if (index < 0 || index >= this.visibleCpus.size()) {
            return false;
        }
        CraftingStatusCpuMetadata other = metadataBySerial.get(this.visibleCpus.get(index).serial());
        return other != null && other.clusterId() == clusterId;
    }
}
