package com.samxel.log2discord;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;

import java.io.OutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DiscordConsoleAppender extends AbstractAppender {
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("HH:mm:ss");

    private static final Queue<String> messageQueue = new ConcurrentLinkedQueue<>();
    private static final Timer timer = new Timer("Log2Discord-Flusher", true);

    static {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                flushQueue();
            }
        }, 1000, 1000);
    }

    protected DiscordConsoleAppender(
            String name,
            Filter filter,
            Layout<? extends Serializable> layout
    ) {
        super(name, filter, layout, false, Property.EMPTY_ARRAY);
    }

    @Override
    public void append(LogEvent event) {
        switch (event.getLevel().name()) {
            case "DEBUG":
            case "TRACE":
                return;
        }

        String webhook = Log2Discord.CONFIG.webhookUrl;
        if (webhook == null || webhook.isEmpty()) return;

        String timestamp = DATE_FORMAT.format(new Date(event.getTimeMillis()));
        String thread = event.getThreadName();
        String level = event.getLevel().toString();
        String logger = event.getLoggerName();
        String msg = event.getMessage().getFormattedMessage();

        String formatted = String.format(
                "> [%s] [%s/%s] (%s) **%s**",
                timestamp,
                thread,
                level,
                logger,
                msg
        );

        messageQueue.add(formatted);
    }

    private static void flushQueue() {
        String webhook = Log2Discord.CONFIG == null ? null : Log2Discord.CONFIG.webhookUrl;
        if (webhook == null || webhook.isEmpty()) return;
        if (messageQueue.isEmpty()) return;

        StringBuilder batch = new StringBuilder();
        while (!messageQueue.isEmpty()) {
            String line = messageQueue.poll();
            if (batch.length() + line.length() + 1 > 1900) {
                sendToDiscord(webhook, batch.toString());
                batch.setLength(0);
            }
            if (!batch.isEmpty()) {
                batch.append("\n");
            }
            batch.append(line);
        }

        if (!batch.isEmpty()) {
            sendToDiscord(webhook, batch.toString());
        }
    }

    private static void sendToDiscord(String webhook, String content) {
        try {
            String safe = content
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "");

            URL url = new URL(webhook);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = "{\"content\":\"" + safe + "\"}";
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            conn.getInputStream().close();
            conn.disconnect();
        } catch (Exception e) {
            Log2Discord.LOGGER.error("Failed to send batch to Discord", e);
        }
    }
}