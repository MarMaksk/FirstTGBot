package Main.service;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.request.SendMessage;

public class ServiceForStatus implements Service {

    public static void buttonEndInDayChange(Update update, TelegramBot bot) {
        ReplyKeyboardRemove rkr = new ReplyKeyboardRemove();
        bot.execute(new SendMessage(update.message().chat().id(), "Расписание добавлено. Вот оно:").replyMarkup(rkr));
        //TODO вывод расписания
    }
}
