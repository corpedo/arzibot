package com.arzival;

import com.github.prominence.openweathermap.api.OpenWeatherMapClient;
import com.github.prominence.openweathermap.api.enums.UnitSystem;
import com.github.prominence.openweathermap.api.model.Coordinate;
import com.github.prominence.openweathermap.api.model.weather.Weather;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

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
        Message msg = update.getMessage();
        User user = msg.getFrom();
        long id = user.getId();

        System.out.println(update);

        if (update.getMessage().isCommand()) {
           if (update.getMessage().getText().startsWith("/weather")) {
                requestLocation(id);
           }
        } else if(update.getMessage().isUserMessage()) {
            if (update.getMessage().hasLocation()) {
                sendWeather(id, update.getMessage().getLocation());
            }

            String response = GeminiHttpClient.ask(msg.getText());

            sendText(id, response);
        }
    }

    private void requestLocation(Long who) {
        KeyboardRow row = new KeyboardRow();
        row.add(KeyboardButton.builder().text("Use current location").requestLocation(true).build());

        ReplyKeyboardMarkup replyKeyboardMarkup = ReplyKeyboardMarkup.builder().oneTimeKeyboard(true).selective(false).keyboardRow(row).build();

        SendMessage message = SendMessage.builder().chatId(who.toString()).replyMarkup(replyKeyboardMarkup).build();

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendWeather(Long who, Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        OpenWeatherMapClient openWeatherClient = new OpenWeatherMapClient("f7ef0976d52faa01bdb06ee90745fa8f");

        Weather weather = openWeatherClient
                .currentWeather()
                .single()
                .byCoordinate(Coordinate.of(latitude, longitude))
                .unitSystem(UnitSystem.METRIC)
                .retrieve()
                .asJava();

        System.out.println(weather);

       /* String msg = weather.getLocation().getName() + "\n" + "Max temp: " + weather.getTemperature().getMaxTemperature() + " Min temp: " + weather.getTemperature().getMinTemperature() + "\n" +
                "Rain: " + weather.getRain().getUnit();

        System.out.println(msg);*/

        SendMessage message = SendMessage.builder().chatId(who.toString()).text(weather.toString()).build();

        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendText(Long who, String what) {
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString())
                .text(what)
                .protectContent(true)
                .build();

        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}