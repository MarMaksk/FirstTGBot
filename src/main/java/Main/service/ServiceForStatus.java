package Main.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

public class ServiceForStatus implements Service {

    public static void endStatus(Update update, TelegramBot bot) {
        bot.execute(new SendMessage(update.message().chat().id(), "Расписание добавлено. Вот оно:"));
    }
}
