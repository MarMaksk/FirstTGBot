package Main.service;

import Main.state.BotState;
import Main.state.DayState;
import Main.user.TelegramUser;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.request.SendMessage;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ServiceForDay implements Service {

    private static Map<Long, List<String>> daysButton = new LinkedHashMap<>();

    public static void selectionDay(Long idMessage, @NotNull TelegramUser user, TelegramBot bot) {
        if (user.getUsersCurrentBotState(idMessage) == BotState.WAIT_CHANGE_DAY) {
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup("")
                    .resizeKeyboard(false).selective(true).oneTimeKeyboard(true);
            daysButton.get(idMessage).forEach(el -> replyKeyboardMarkup.addRow(el));
            replyKeyboardMarkup.addRow("Завершить");
            bot.execute(new SendMessage(idMessage, "Какой день недели будем заполнять?").replyMarkup(replyKeyboardMarkup));
        }
    }

    public static void changeDay(@NotNull Update update, String text, TelegramUser user, TelegramBot bot) {
        Long userID = update.message().from().id();
        if (update.message() != null && user.getUsersCurrentBotState(userID) == BotState.WAIT_CHANGE_DAY) {
            //         if (daysButton.entrySet().contains(text)){}
            if (daysButton.get(userID).contains(text)) {
                user.setUsersCurrentDayState(userID, DayState.MONDAY);
                user.setUsersCurrentBotState(userID, BotState.DAY_RECEIVED);
                daysButton.get(userID).remove(0);
            }
            if (daysButton.entrySet().contains(text)) {
                user.setUsersCurrentDayState(userID, DayState.TUESDAY);
                user.setUsersCurrentBotState(userID, BotState.DAY_RECEIVED);
                daysButton.get(userID).remove(1);
            }
            if (daysButton.entrySet().contains(text)) {
                user.setUsersCurrentDayState(userID, DayState.WEDNESDAY);
                user.setUsersCurrentBotState(userID, BotState.DAY_RECEIVED);
                daysButton.get(userID).remove(2);
            }
            if (daysButton.entrySet().contains(text)) {
                user.setUsersCurrentDayState(userID, DayState.THURSDAY);
                user.setUsersCurrentBotState(userID, BotState.DAY_RECEIVED);
                daysButton.get(userID).remove(3);
            }
            if (daysButton.entrySet().contains(text)) {
                user.setUsersCurrentDayState(userID, DayState.FRIDAY);
                user.setUsersCurrentBotState(userID, BotState.DAY_RECEIVED);
                daysButton.get(userID).remove(4);
            }
            if (daysButton.entrySet().contains(text)) {
                user.setUsersCurrentDayState(userID, DayState.SATURDAY);
                user.setUsersCurrentBotState(userID, BotState.DAY_RECEIVED);
                daysButton.get(userID).remove(5);
            }
            if (daysButton.entrySet().contains(text)) {
                user.setUsersCurrentDayState(userID, DayState.SUNDAY);
                user.setUsersCurrentBotState(userID, BotState.DAY_RECEIVED);
                daysButton.get(userID).remove(6);
            }
            if (text.equals("Завершить")) {
                user.setUsersCurrentDayState(userID, DayState.WAIT_STATUS);
                user.setUsersCurrentBotState(userID, BotState.WAIT_STATUS);
                daysButton.remove(userID);
                ServiceForStatus.buttonEndInDayChange(update, bot);
                return;
            }
            if (user.getUsersCurrentBotState(userID) == BotState.WAIT_CHANGE_DAY) {
                bot.execute(new SendMessage(userID, "Похоже этот день уже заполнен"));
                return;
            }
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
