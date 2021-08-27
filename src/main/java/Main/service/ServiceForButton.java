package Main.service;

import Main.state.BotState;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import com.pengrad.telegrambot.request.SendMessage;
import Main.user.TelegramUser;

public class ServiceForButton implements Service{
    public static void buttonAdd(Update update, TelegramUser user, TelegramBot bot) {
        bot.execute(new SendMessage(update.message().chat().id(), "Как оно будет называться?"));
        user.setUsersCurrentBotState(update.message().chat().id(), BotState.WAIT_TIMETABLE_NAME);
    }

    public void buttonAWeek(Update update, TelegramUser user, TelegramBot bot) {
        bot.execute(new SendMessage(update.message().chat().id(), ""));
        user.setUsersCurrentBotState(update.message().chat().id(), BotState.BUTTON_AWEEK);
    }
}
