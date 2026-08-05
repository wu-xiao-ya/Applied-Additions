package com.formlesslab.ae2additions.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public final class WirelessHighlightHandler {
    public static final WirelessHighlightHandler INSTANCE = new WirelessHighlightHandler();
    private static final long DURATION_MS = 20_000L;

    private BlockPos target;
    private int dimension;
    private long expiresAt;

    private WirelessHighlightHandler() {
    }

    public void highlight(BlockPos pos) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.world == null || pos == null) {
            return;
        }
        this.target = pos;
        this.dimension = minecraft.world.provider.getDimension();
        this.expiresAt = System.currentTimeMillis() + DURATION_MS;
    }

    @SubscribeEvent
    public void renderWorldLast(RenderWorldLastEvent event) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.world == null || this.target == null) {
            return;
        }
        if (minecraft.world.provider.getDimension() != this.dimension || System.currentTimeMillis() > this.expiresAt) {
            this.target = null;
            return;
        }
        if (((System.currentTimeMillis() / 300L) & 1L) == 1L) {
            return;
        }

        double camX = minecraft.getRenderManager().viewerPosX;
        double camY = minecraft.getRenderManager().viewerPosY;
        double camZ = minecraft.getRenderManager().viewerPosZ;

        GlStateManager.pushMatrix();
        GlStateManager.translate(-camX, -camY, -camZ);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(2.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);

        RenderGlobal.drawSelectionBoundingBox(new AxisAlignedBB(this.target).grow(0.01D), 0.35F, 0.85F, 1.0F, 0.9F);

        GlStateManager.glLineWidth(1.0F);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(GL11.GL_LEQUAL);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}
