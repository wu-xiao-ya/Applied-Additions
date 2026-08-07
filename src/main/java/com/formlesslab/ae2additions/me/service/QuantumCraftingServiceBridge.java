package com.formlesslab.ae2additions.me.service;

import ae2.api.networking.IGrid;
import ae2.api.networking.IGridNode;
import com.formlesslab.ae2additions.me.cluster.ClusterAdvCraftingCPU;
import com.formlesslab.ae2additions.tile.TileAdvCraftingBlock;

import java.util.HashSet;
import java.util.Set;

public final class QuantumCraftingServiceBridge {
    private QuantumCraftingServiceBridge() {
    }

    public static Set<ClusterAdvCraftingCPU> collectClusters(IGrid grid) {
        Set<ClusterAdvCraftingCPU> clusters = new HashSet<>();
        if (grid == null) {
            return clusters;
        }

        for (TileAdvCraftingBlock tile : grid.getMachines(TileAdvCraftingBlock.class)) {
            ClusterAdvCraftingCPU cluster = tile.getCluster();
            if (cluster != null && !cluster.isDestroyed()) {
                clusters.add(cluster);
            }
        }
        return clusters;
    }

    public static boolean ownsQuantumCpuNode(IGridNode node) {
        return node != null && node.getOwner() instanceof TileAdvCraftingBlock;
    }
}
