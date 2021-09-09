package main.service;

import Main.state.BotState;
import Main.state.DayState;
import Main.state.ExtremHelpEnum;
import Main.user.TelegramUser;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.request.SendMessage;
import main.ServiceSQL.InsertTableToSQL;
import main.ServiceSQL.UpdateTableToSQL;
import main.table.TableOfOneDay;

import java.util.HashMap;
import java.util.Map;

public class ServicePreparationForSQL implements Service {
    private static Map<Long, Integer> couplesPerDay = new HashMap<>();
    private static Map<Long, Integer> counter = new HashMap<>();

    public static void preparationDayForWriting(Update update, Long idUserMessage, TelegramUser user, TelegramBot bot) {
        if (user.getUsersCurrentBotState(idUserMessage) == BotState.DAY_RECEIVED ||
                user.getUsersCurrentBotState(idUserMessage) == BotState.CHANGE_SCHEDULE) {
            if (!couplesPerDay.containsKey(idUserMessage)) {
                counter.put(idUserMessage, 1);
                try {
                    couplesPerDay.put(idUserMessage, Integer.valueOf(update.message().text()));
                } catch (NumberFormatException ex){
                    bot.execute(new SendMessage(idUserMessage, "Ожидается число"));
                    return;
                }
                bot.execute(new SendMessage(idUserMessage, "Какой будет " + counter.get(idUserMessage) + " пара?" +
                        "\n Подсказка: можно приписать аудиторию"));
                return;
            } else if (couplesPerDay.get(idUserMessage) == counter.get(idUserMessage)) {
                TableOfOneDay.setOneDay(idUserMessage, update.message().text());
                if (user.getUsersCurrentBotState(idUserMessage) == BotState.DAY_RECEIVED &&
                        user.getUsersCurrentExtremeState(idUserMessage) != ExtremHelpEnum.EXTREME_PARAM_ONE)
                    InsertTableToSQL.createNewTable(idUserMessage, user, TableOfOneDay.getListOfOneDay(idUserMessage), null);
                else
                    UpdateTableToSQL.updateDay(bot, update, user, TableOfOneDay.getListOfOneDay(idUserMessage));
                user.setUsersCurrentBotState(idUserMessage, BotState.WAIT_CHANGE_DAY);
                ServiceForDay.selectionDay(idUserMessage, user, bot);
                TableOfOneDay.removeUserList(idUserMessage);
                couplesPerDay.remove(idUserMessage);
                counter.remove(idUserMessage);
                return;
            } else {
                counter.put(idUserMessage, counter.get(idUserMessage) + 1);
                bot.execute(new SendMessage(idUserMessage, "Какой будет " + counter.get(idUserMessage) + " пара?"));
                TableOfOneDay.setOneDay(idUserMessage, update.message().text());
                return;
            }
        }
    }


    public static void preparationDayForUpdate(TelegramBot bot, TelegramUser user, String text, Long userId) {
        if (user.getUsersCurrentBotState(userId) == BotState.BUTTON_CHANGE) {
            if (text.equals("понедельник"))
                user.setUsersCurrentDayState(userId, DayState.MONDAY);
            if (text.equals("вторник"))
                user.setUsersCurrentDayState(userId, DayState.TUESDAY);
            if (text.equals("среда"))
                user.setUsersCurrentDayState(userId, DayState.WEDNESDAY);
            if (text.equals("четверг"))
                user.setUsersCurrentDayState(userId, DayState.THURSDAY);
            if (text.equals("пятница"))
                user.setUsersCurrentDayState(userId, DayState.FRIDAY);
            if (text.equals("суббота"))
                user.setUsersCurrentDayState(userId, DayState.SATURDAY);
            if (text.equals("воскресенье"))
                user.setUsersCurrentDayState(userId, DayState.SUNDAY);

            user.setUsersCurrentBotState(userId, BotState.CHANGE_SCHEDULE);
            ReplyKeyboardRemove rkr = new ReplyKeyboardRemove();
            bot.execute(new SendMessage(userId, "Сколько будет пар?").replyMarkup(rkr));
        }
    }
}
