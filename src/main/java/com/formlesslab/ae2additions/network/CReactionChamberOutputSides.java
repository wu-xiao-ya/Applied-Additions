package com.formlesslab.ae2additions.network;

import ae2.container.GuiIds;
import ae2.core.gui.locator.GuiHostLocator;
import ae2.core.gui.locator.GuiHostLocators;
import ae2.core.network.serverbound.SwitchGuisPacket;
import com.formlesslab.ae2additions.container.ContainerReactionChamber;
import com.formlesslab.ae2additions.network.base.ModServerboundPacket;
import net.minecraft.entity.player.EntityPlayerMP;

public class CReactionChamberOutputSides extends ModServerboundPacket {
    @Override
    public void handleServer(EntityPlayerMP player) {
        if (!(player.openContainer instanceof ContainerReactionChamber container)) {
            return;
        }

        GuiHostLocator locator = container.getLocator();
        if (locator == null) {
            locator = GuiHostLocators.forTile(container.getHost());
            container.setLocator(locator);
        }

        SwitchGuisPacket.openSubGui(player, locator, GuiIds.GuiKey.OUTPUT_SIDES, container);
    }
}
