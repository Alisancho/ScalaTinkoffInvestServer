package ru.mytelegrambot;

import akka.actor.ActorRef;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.invest.service.TelegramContainerMess;
import scala.Function2;
import scala.util.Either;
import scala.util.Left;
import scala.util.Right;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InvestInfoBot extends TelegramLongPollingBot {
    private final String token;
    private final String name;
    private final Long chat_id;
    private final ActorRef acctorRef;
    private ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();

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

        this.replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow keyboardFirstRow = new KeyboardRow();
        keyboardFirstRow.add(new KeyboardButton("ANALYTICS_START"));
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        keyboardSecondRow.add(new KeyboardButton("ANALYTICS_STOP"));
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        replyKeyboardMarkup.setKeyboard(keyboard);
        sendMessage("START_SERVER");
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
        SendMessage sendMessage =  new SendMessage(chat_id, mess);
        sendMessage.setReplyMarkup(this.replyKeyboardMarkup);
        try {
            execute(sendMessage);
        } catch (Throwable ignored) {
        }
    }
}
