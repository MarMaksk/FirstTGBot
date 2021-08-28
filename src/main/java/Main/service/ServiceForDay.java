package Main.service;

import Main.state.BotState;
import Main.state.DayState;
import Main.user.TelegramUser;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.request.SendMessage;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ServiceForDay implements Service {

    private static Map<Long, List<String>> daysButton = new LinkedHashMap<>();

    public static void selectionDay(Update update, Long idMessage, TelegramUser user, TelegramBot bot) {
        if (user.getUsersCurrentBotState(idMessage) == BotState.WAIT_CHANGE_DAY) {
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup("Завершить")
               .resizeKeyboard(false).selective(true).oneTimeKeyboard(true);
            daysButton.get(idMessage).forEach(el -> replyKeyboardMarkup.addRow(el));

            bot.execute(new SendMessage(idMessage, "Какой день недели будем заполнять?").replyMarkup(replyKeyboardMarkup));
        }
    }

    public static void changeDay(Update update, String text, BotState userStatus, TelegramUser user, TelegramBot bot) {
        if (update.message() != null && user.getUsersCurrentBotState(update.message().from().id()) == BotState.WAIT_CHANGE_DAY) {
            Long userID = update.message().from().id();
            if (text.equals("Понедельник")) {
                user.setUsersCurrentDayState(userID, DayState.MONDAY);
                daysButton.get(userID).remove(0);
            }
            if (text.equals("Вторник")) {
                user.setUsersCurrentDayState(userID, DayState.TUESDAY);
                daysButton.get(userID).remove(1);
            }
            if (text.equals("Среда")) {
                user.setUsersCurrentDayState(userID, DayState.WEDNESDAY);
                daysButton.get(userID).remove(2);
            }
            if (text.equals("Четверг")) {
                user.setUsersCurrentDayState(userID, DayState.THURSDAY);
                daysButton.get(userID).remove(3);
            }
            if (text.equals("Пятница")) {
                user.setUsersCurrentDayState(userID, DayState.FRIDAY);
                daysButton.get(userID).remove(4);
            }
            if (text.equals("Суббота")) {
                user.setUsersCurrentDayState(userID, DayState.SATURDAY);
                daysButton.get(userID).remove(5);
            }
            if (text.equals("Воскресенье")) {
                user.setUsersCurrentDayState(userID, DayState.SUNDAY);
                daysButton.get(userID).remove(6);
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

    public static void fillMapDaysButton(Long idUser) {
        List<String> listOfDays = new LinkedList<>();
        listOfDays.add(0, "Понедельник");
        listOfDays.add(1, "Вторник");
        listOfDays.add(2, "Среда");
        listOfDays.add(3, "Четверг");
        listOfDays.add(4, "Пятница");
        listOfDays.add(5, "Суббота");
        listOfDays.add(6, "Воскресенье");
        daysButton.put(idUser, listOfDays);
    }

    public static Map<Long, List<String>> getDaysButton() {
        return daysButton;
    }

    public static void setDaysButton(Map<Long, List<String>> daysButton) {
        ServiceForDay.daysButton = daysButton;
    }
}
