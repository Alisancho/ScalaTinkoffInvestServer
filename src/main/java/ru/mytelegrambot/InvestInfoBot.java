package ru.mytelegrambot;

import akka.actor.ActorRef;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Objects;

public class InvestInfoBot extends TelegramLongPollingBot {
    private final String token;
    private final String name;
    private final Long chat_id;
    private final ActorRef acctorRef;

    public InvestInfoBot(@NotNull String token,
                         @NotNull String name,
                         @NotNull DefaultBotOptions defaultBotOptions,
                         @NotNull Long chat_id,
                         @NotNull ActorRef acctorRef) {
        super(defaultBotOptions);
        this.token = token;
        this.name = name;
        this.chat_id = chat_id;
        this.acctorRef = acctorRef;
    }

    public InvestInfoBot(@NotNull String token,
                         @NotNull String name,
                         @NotNull Long chat_id,
                         @NotNull ActorRef acctorRef) {
        this.token = token;
        this.name = name;
        this.chat_id = chat_id;
        this.acctorRef = acctorRef;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.getMessage() != null && update.getMessage().hasText() && Objects.equals(update.getMessage().getChatId(), this.chat_id)) {
            try {
                acctorRef.tell(update.getMessage().getText(), acctorRef);
                // execute(new SendMessage(chat_id, update.getMessage().getChatId().toString()));
            } catch (Throwable e) {

            }
        }
    }

    @Override
    public String getBotUsername() {
        return name;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    public void sendMessage(String mess) {
        try {
            execute(new SendMessage(chat_id, mess));
        } catch (Throwable ignored) {
            //log.error(ignored.getMessage());
        }
    }


}
