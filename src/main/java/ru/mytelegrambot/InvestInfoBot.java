package ru.mytelegrambot;

import akka.actor.ActorRef;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

//@Slf4j
public class InvestInfoBot extends TelegramLongPollingBot {
    private final String token;
    private final String name;
    private final Long chat_id;
    private final ActorRef acctorRef;

    public InvestInfoBot(String token, String name, DefaultBotOptions defaultBotOptions, Long chat_id, ActorRef acctorRef) {
        super(defaultBotOptions);
        this.token = token;
        this.name = name;
        this.chat_id = chat_id;
        this.acctorRef = acctorRef;
    }

    public InvestInfoBot(String token, String name, Long chat_id, ActorRef acctorRef) {
        this.token = token;
        this.name = name;
        this.chat_id = chat_id;
        this.acctorRef = acctorRef;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.getMessage() != null && update.getMessage().hasText()) {
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
