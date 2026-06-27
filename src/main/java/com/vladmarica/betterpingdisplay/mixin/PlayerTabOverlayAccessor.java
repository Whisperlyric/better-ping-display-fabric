package com.vladmarica.betterpingdisplay.mixin;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(PlayerTabOverlay.class)
public interface PlayerTabOverlayAccessor {
  @Invoker("extractPingIcon")
  void invokeRenderLatencyIcon(GuiGraphicsExtractor graphics, int width, int x, int y, PlayerInfo playerInfo);
}
