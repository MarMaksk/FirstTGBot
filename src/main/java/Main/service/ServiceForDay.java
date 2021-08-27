package Main.service;

import Main.user.TelegramUser;
import Main.state.BotState;
import Main.state.DayState;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.request.SendMessage;

public class ServiceForDay implements Service {
    public static void selectionDay(Update update, Long idMessage, TelegramUser user, TelegramBot bot) {
        if (user.getUsersCurrentBotState(idMessage) == BotState.WAIT_CHANGE_DAY) {
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup("Понедельник", "Вторник")
                    .addRow("Среда", "Четверг").addRow("Пятница", "Суббота").addRow("Воскресенье", "Завершить")
                    .resizeKeyboard(false).selective(true).oneTimeKeyboard(true);
            bot.execute(new SendMessage(idMessage, "Какой день недели будем заполнять?").replyMarkup(replyKeyboardMarkup));
        }
    }

    public static void changeDay(Update update, String text, BotState userStatus, TelegramUser user, TelegramBot bot) {
        if (update.message() != null && user.getUsersCurrentBotState(update.message().from().id()) == BotState.WAIT_CHANGE_DAY) {
            Long userID = update.message().from().id();
            if (text.equals("Понедельник")) {
                user.setUsersCurrentDayState(userID, DayState.MONDAY);
            }
            if (text.equals("Вторник")) {
                user.setUsersCurrentDayState(userID, DayState.TUESDAY);
            }
            if (text.equals("Среда")) {
                user.setUsersCurrentDayState(userID, DayState.WEDNESDAY);
            }
            if (text.equals("Четверг")) {
                user.setUsersCurrentDayState(userID, DayState.THURSDAY);
            }
            if (text.equals("Пятница")) {
                user.setUsersCurrentDayState(userID, DayState.FRIDAY);
            }
            if (text.equals("Суббота")) {
                user.setUsersCurrentDayState(userID, DayState.SATURDAY);
            }
            if (text.equals("Воскресенье")) {
                user.setUsersCurrentDayState(userID, DayState.SUNDAY);
            }
            if (text.equals("Завершить")) {
                user.setUsersCurrentDayState(userID, DayState.WAIT_STATUS);
                user.setUsersCurrentBotState(userID, BotState.END);
                ServiceForStatus.endStatus(update, bot);
                return;
            }
            user.setUsersCurrentBotState(userID, BotState.DAY_RECEIVED);
            ReplyKeyboardRemove rkr = new ReplyKeyboardRemove();
            bot.execute(new SendMessage(userID, "Сколько будет пар?").replyMarkup(rkr));
        }
        return;
    }
}
