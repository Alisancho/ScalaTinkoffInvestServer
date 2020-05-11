package ru.mytelegrambot;

import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class InvestInfoBot extends TelegramLongPollingBot {
    private final String token;
    private final String name;
    private final Long chat_id;

    public InvestInfoBot(String token, String name, DefaultBotOptions defaultBotOptions, Long chat_id) {
        super(defaultBotOptions);
        this.token = token;
        this.name = name;
        this.chat_id = chat_id;
    }

    public InvestInfoBot(String token, String name, Long chat_id) {
        this.token = token;
        this.name = name;
        this.chat_id = chat_id;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.getMessage() != null && update.getMessage().hasText()) {
            try {
                execute(new SendMessage(chat_id, update.getMessage().getChatId().toString()));
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

    public void sendMess(String mess) {
    }


}
