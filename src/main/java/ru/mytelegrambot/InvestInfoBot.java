package ru.mytelegrambot;

import akka.actor.ActorRef;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.invest.service.TelegramContainerMess;
import scala.Function2;
import scala.util.Either;
import scala.util.Left;
import scala.util.Right;

import java.util.Objects;

public class InvestInfoBot extends TelegramLongPollingBot {
    private final String token;
    private final String name;
    private final Long chat_id;
    private final ActorRef acctorRef;

    Function2<String, String, Either<Throwable, String>> fun = (chat_id, mess) -> {
        try {
            execute(new SendMessage(chat_id, mess));
            return Right.apply("");
        } catch (TelegramApiException e) {
            return Left.apply(e);
        }
    };

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
                acctorRef.tell(TelegramContainerMess.apply(update.getMessage().getText(), this), acctorRef);
            } catch (Throwable ignored) {

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
        }
    }
}
