package com.vladmarica.betterpingdisplay.hud;

import com.vladmarica.betterpingdisplay.BetterPingDisplayMod;
import com.vladmarica.betterpingdisplay.Config;
import com.vladmarica.betterpingdisplay.mixin.PlayerTabOverlayAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;

public final class CustomPlayerListHud {
  private static final int PING_TEXT_RENDER_OFFSET = -13;
  private static final int PING_BARS_WIDTH = 11;
  private static final Config config = BetterPingDisplayMod.instance().getConfig();

  public static void renderPingDisplay(
      Minecraft client, PlayerTabOverlay hud, GuiGraphicsExtractor graphics, int width, int x, int y, PlayerInfo player) {
    String pingString;
    if (player.getLatency() == 0) {
      // Use the configured placeholder (prefixed with the formatting color code) for null/0ms pings.
      pingString = config.getNullPingPlaceholderColor() + config.getNullPingPlaceholder();
    } else {
      pingString = String.format(config.getTextFormatString(), player.getLatency());
    }

    int pingStringWidth = client.font.width(pingString);
    int pingTextColor = config.shouldAutoColorPingText()
        ? PingColors.getColor(player.getLatency()) : config.getTextColor().getRGB();
    int textX = width + x - pingStringWidth + PING_TEXT_RENDER_OFFSET;

    if (!config.shouldRenderPingBars()) {
      textX += PING_BARS_WIDTH;
    }

    // Draw the ping text for the given player
    graphics.text(client.font, pingString, textX, y, pingTextColor, true);

    if (config.shouldRenderPingBars()) {
      ((PlayerTabOverlayAccessor) hud).invokeRenderLatencyIcon(graphics, width, x, y, player);
    }
  }
}
