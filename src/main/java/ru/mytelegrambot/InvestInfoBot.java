package ru.mytelegrambot;

import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class InvestInfoBot extends TelegramLongPollingBot {
    private final String token;
    private final String name;


    public InvestInfoBot(String token, String name, DefaultBotOptions defaultBotOptions) {
        super(defaultBotOptions);
        this.token = token;
        this.name = name;

    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.getMessage() != null && update.getMessage().hasText()) {
            Long chat_id = update.getMessage().getChatId();
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
