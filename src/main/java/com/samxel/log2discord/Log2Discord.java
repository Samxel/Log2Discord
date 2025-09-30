package com.samxel.log2discord;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.slf4j.LoggerFactory;

public class Log2Discord implements DedicatedServerModInitializer {
    public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger("log2discord");
    public static Log2DiscordConfig CONFIG;

    @Override
    public void onInitializeServer() {
        CONFIG = Log2DiscordConfig.load();

        Logger rootLogger = (Logger) LogManager.getRootLogger();
        DiscordConsoleAppender appender =
                new DiscordConsoleAppender("DiscordConsoleAppender", null, null);
        appender.start();
        rootLogger.addAppender(appender);
        LOGGER.info("Console capture enabled");

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("log2discord")
                    .requires(src -> src.hasPermissionLevel(4))
                    .then(CommandManager.literal("reload")
                            .executes(ctx -> {
                                CONFIG = Log2DiscordConfig.load();
                                ctx.getSource().sendFeedback(
                                        () -> net.minecraft.text.Text.literal("Log2Discord config reloaded"),
                                        true
                                );
                                return 1;
                            }))
                    .then(CommandManager.literal("setWebhook")
                            .then(CommandManager.argument("url", StringArgumentType.string())
                                    .executes(ctx -> {
                                        CONFIG.webhookUrl = StringArgumentType.getString(ctx, "url");
                                        CONFIG.save();
                                        ctx.getSource().sendFeedback(
                                                () -> net.minecraft.text.Text.literal("Webhook updated"),
                                                true
                                        );
                                        return 1;
                                    }))));
        });

        LOGGER.info("Log2Discord mod initialized");
    }
}