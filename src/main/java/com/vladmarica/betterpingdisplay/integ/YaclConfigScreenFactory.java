package com.vladmarica.betterpingdisplay.integ;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.vladmarica.betterpingdisplay.BetterPingDisplayMod;
import com.vladmarica.betterpingdisplay.Config;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.ColorControllerBuilder;
import dev.isxander.yacl3.api.controller.CyclingListControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import static com.vladmarica.betterpingdisplay.BetterPingDisplayMod.LOGGER;
import static net.minecraft.network.chat.Component.translatable;

public class YaclConfigScreenFactory implements ConfigScreenFactory<Screen> {

    @Override
    public Screen create(Screen parent) {
        BetterPingDisplayMod mod = BetterPingDisplayMod.instance();
        Config config = mod.getConfig();

        Option<Color> pingTextColorOption =  Option.<Color>createBuilder()
                .name(translatable("betterpingdisplay.settings.pingTextColor"))
                .description(OptionDescription.of(translatable("betterpingdisplay.settings.pingTextColor.description")))
                .binding(config.getTextColor(), config::getTextColor, config::setTextColor)
                .controller(o -> ColorControllerBuilder.create(o).allowAlpha(false))
                .available(!config.shouldAutoColorPingText())
                .build();

        Option<Boolean> autoColorPingTextOption = Option.<Boolean>createBuilder()
                .name(translatable("betterpingdisplay.settings.autoColorPingText"))
                .description(OptionDescription.of(translatable("betterpingdisplay.settings.autoColorPingText.description")))
                .binding(
                        config.shouldAutoColorPingText(),
                        config::shouldAutoColorPingText,
                        config::setShouldAutoColorPingText)
                .controller(o -> BooleanControllerBuilder.create(o).coloured(true))
                .addListener((option, event) -> pingTextColorOption.setAvailable(!option.pendingValue()))
                .build();

        Option<String> textFormatOption = Option.<String>createBuilder()
                .name(translatable("betterpingdisplay.settings.pingTextFormatString"))
                .description(OptionDescription.of(translatable("betterpingdisplay.settings.pingTextFormatString.description")))
                .binding(
                        config.getTextFormatString(),
                        config::getTextFormatString,
                        config::setTextFormatString)
                .controller(StringControllerBuilder::create)
                .build();

        Option<Boolean> renderPingBarsOption = Option.<Boolean>createBuilder()
                .name(translatable("betterpingdisplay.settings.renderPingBars"))
                .description(OptionDescription.of(translatable("betterpingdisplay.settings.renderPingBars.description")))
                .binding(
                        config.shouldRenderPingBars(),
                        config::shouldRenderPingBars,
                        config::setShouldRenderPingBars)
                .controller(o -> BooleanControllerBuilder.create(o).coloured(true))
                .build();

        Option<String> nullPingPlaceholderOption = Option.<String>createBuilder()
                .name(translatable("betterpingdisplay.settings.nullPingPlaceholder"))
                .description(OptionDescription.of(translatable("betterpingdisplay.settings.nullPingPlaceholder.description")))
                .binding(Config.DEFAULT_NULL_PING_PLACEHOLDER, config::getNullPingPlaceholder, config::setNullPingPlaceholder)
                .controller(StringControllerBuilder::create)
                .build();

        Option<ChatFormatting> nullPingPlaceholderColorOption = Option.<ChatFormatting>createBuilder()
                .name(translatable("betterpingdisplay.settings.nullPingPlaceholderColor"))
                .description(OptionDescription.of(translatable("betterpingdisplay.settings.nullPingPlaceholderColor.description")))
                .binding(Config.DEFAULT_NULL_PING_PLACEHOLDER_COLOR, config::getNullPingPlaceholderColor, config::setNullPingPlaceholderColor)
                .controller(opt -> CyclingListControllerBuilder.create(opt)
                        .values(Arrays.stream(ChatFormatting.values())
                                .filter(ChatFormatting::isColor)
                                .toList())
                        .formatValue(v -> Component.translatable("betterpingdisplay.formatting." + v.getName().toLowerCase(Locale.ROOT))))
                .build();

        return YetAnotherConfigLib.createBuilder()
                .title(translatable("betterpingdisplay.settings.title"))
                .category(ConfigCategory.createBuilder()
                        .name(translatable("betterpingdisplay.settings.title"))
                        .option(autoColorPingTextOption)
                        .option(pingTextColorOption)
                        .option(textFormatOption)
                        .option(renderPingBarsOption)
                        .option(nullPingPlaceholderOption)
                        .option(nullPingPlaceholderColorOption)
                        .build())
                .save(() -> {
                    try {
                        config.writeToFile(mod.getConfigFilePath().toFile());
                    } catch (IOException ex) {
                        LOGGER.warn("Failed to write config file", ex);
                    }
                })
                .build()
                .generateScreen(parent);
    }
}
