package Main.service;

import Main.state.BotState;
import Main.table.InsertTableToSQL;
import Main.table.TableOfOneDay;
import Main.table.UpdateTableToSQL;
import Main.user.TelegramUser;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

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
                couplesPerDay.put(idUserMessage, Integer.valueOf(update.message().text()));
                bot.execute(new SendMessage(idUserMessage, "Какой будет " + counter.get(idUserMessage) + " пара?" +
                        "\n Подсказка: можно приписать аудиторию"));
                return;
            } else if (couplesPerDay.get(idUserMessage) == counter.get(idUserMessage)) {
                TableOfOneDay.setOneDay(idUserMessage, update.message().text());
                if (user.getUsersCurrentBotState(idUserMessage) == BotState.DAY_RECEIVED)
                    InsertTableToSQL.createNewTable(idUserMessage, user, TableOfOneDay.getListOfOneDay(idUserMessage), null);
                else
                    UpdateTableToSQL.setDay(bot, update, user, TableOfOneDay.getListOfOneDay(idUserMessage));
//                if (user.getUsersCurrentBotState(idUserMessage) != BotState.CHANGE_SCHEDULE&&
//                user.getUsersCurrentBotState(idUserMessage)!=BotState.BUTTON_CHANGE) {
//                    user.setUsersCurrentBotState(idUserMessage, BotState.WAIT_CHANGE_DAY);
//                    ServiceForDay.selectionDay(idUserMessage, user, bot);
//                }
                //InsertTableToSQL.createNewTable(idUserMessage, user, TableOfOneDay.getListOfOneDay(idUserMessage), null);
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

    public static void preparationForReading(Update update, Long idUserMessage, TelegramUser user, TelegramBot bot) {
    }
}
