package main.service;

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
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class ServiceForDay implements Service {

    private static Map<Long, Set<String>> daysButton = new LinkedHashMap<>();

    public static void selectionDay(Long idMessage, @NotNull TelegramUser user, TelegramBot bot) {
        if (user.getUsersCurrentBotState(idMessage) == BotState.WAIT_CHANGE_DAY
                || user.getUsersCurrentBotState(idMessage) == BotState.BUTTON_CHANGE) {
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup("")
                    .resizeKeyboard(false).selective(true).oneTimeKeyboard(true);
            daysButton.get(idMessage).forEach(replyKeyboardMarkup::addRow);
            replyKeyboardMarkup.addRow("Завершить");
            bot.execute(new SendMessage(idMessage, "С каким днём будем работать?").replyMarkup(replyKeyboardMarkup));
        }
    }

    public static void changeDay(@NotNull Update update, String text, TelegramUser user, TelegramBot bot) {
        Long userID = update.message().from().id();
        if (update.message() != null && user.getUsersCurrentBotState(userID) == BotState.WAIT_CHANGE_DAY) {
            for (String list : daysButton.get(userID)) {
                if (list.equals(text) && text.equals("Понедельник")) {
                    user.setUsersCurrentDayState(userID, DayState.MONDAY);
                    user.setUsersCurrentBotState(userID, BotState.DAY_RECEIVED);
                    daysButton.get(userID).remove(text);
                    break;
                }
                if (list.equals(text) && text.equals("Вторник")) {
                    user.setUsersCurrentDayState(userID, DayState.TUESDAY);
                    user.setUsersCurrentBotState(userID, BotState.DAY_RECEIVED);
                    daysButton.get(userID).remove(text);
                    break;
                }
                if (list.equals(text) && text.equals("Среда")) {
                    user.setUsersCurrentDayState(userID, DayState.WEDNESDAY);
                    user.setUsersCurrentBotState(userID, BotState.DAY_RECEIVED);
                    daysButton.get(userID).remove(text);
                    break;
                }
                if (list.equals(text) && text.equals("Четверг")) {
                    user.setUsersCurrentDayState(userID, DayState.THURSDAY);
                    user.setUsersCurrentBotState(userID, BotState.DAY_RECEIVED);
                    daysButton.get(userID).remove(text);
                    break;
                }
                if (list.equals(text) && text.equals("Пятница")) {
                    user.setUsersCurrentDayState(userID, DayState.FRIDAY);
                    user.setUsersCurrentBotState(userID, BotState.DAY_RECEIVED);
                    daysButton.get(userID).remove(text);
                    break;
                }
                if (list.equals(text) && text.equals("Суббота")) {
                    user.setUsersCurrentDayState(userID, DayState.SATURDAY);
                    user.setUsersCurrentBotState(userID, BotState.DAY_RECEIVED);
                    daysButton.get(userID).remove(text);
                    break;
                }
                if (list.equals(text) && text.equals("Воскресенье")) {
                    user.setUsersCurrentDayState(userID, DayState.SUNDAY);
                    user.setUsersCurrentBotState(userID, BotState.DAY_RECEIVED);
                    daysButton.get(userID).remove(text);
                    break;
                }
            }
            if (text.equals("Завершить")) {
                user.setUsersCurrentDayState(userID, DayState.WAIT_STATUS);
                user.setUsersCurrentBotState(userID, BotState.WAIT_STATUS);
                daysButton.remove(userID);
                ServiceForStatus.botStateEnd(update, bot);
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
        Set<String> listOfDays = new LinkedHashSet<>();
        listOfDays.add("Понедельник");
        listOfDays.add("Вторник");
        listOfDays.add("Среда");
        listOfDays.add("Четверг");
        listOfDays.add("Пятница");
        listOfDays.add("Суббота");
        listOfDays.add("Воскресенье");
        daysButton.put(idUser, listOfDays);
    }

    public static Map<Long, Set<String>> getDaysButton() {
        return daysButton;
    }

    public static void setDaysButton(Map<Long, Set<String>> daysButton) {
        ServiceForDay.daysButton = daysButton;
    }
}
