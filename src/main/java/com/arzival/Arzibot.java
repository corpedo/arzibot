package com.arzival;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Arzibot extends TelegramLongPollingBot {

    @Override
    public String getBotUsername() {
        return "Arzibot";
    }

    @Override
    public String getBotToken() {
        return System.getenv("ARZIBOT_TOKEN");
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update);

        Message msg = update.getMessage();
        User user = msg.getFrom();
        long id = user.getId();

        sendText(id, msg.getText());
    }

    public void sendText(Long who, String what){
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString())
                .text(what).build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void copyMessage(Long who, Integer msgId){
        CopyMessage cm = CopyMessage.builder()
                .fromChatId(who.toString())  //We copy from the user
                .chatId(who.toString())      //And send it back to him
                .messageId(msgId)            //Specifying what message
                .build();
        try {
            this.execute(cm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}