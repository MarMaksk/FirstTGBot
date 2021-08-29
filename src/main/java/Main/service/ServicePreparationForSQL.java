package Main.service;

import Main.state.BotState;
import Main.table.AddTableToSQL;
import Main.table.TableOfOneDay;
import Main.table.Tablename;
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
        if (user.getUsersCurrentBotState(idUserMessage) == BotState.DAY_RECEIVED) {
            if (!couplesPerDay.containsKey(idUserMessage)) {
                counter.put(idUserMessage, 1);
                couplesPerDay.put(idUserMessage, Integer.valueOf(update.message().text()));
                bot.execute(new SendMessage(idUserMessage, "Какой будет " + counter.get(idUserMessage) + " пара?"));
                return;
            } else if (couplesPerDay.get(idUserMessage) == counter.get(idUserMessage)) {
                TableOfOneDay.setOneDay(idUserMessage, update.message().text());
                AddTableToSQL.createNewTable(idUserMessage, user, TableOfOneDay.getListOfOneDay(idUserMessage), Tablename.getTableName().get(idUserMessage));
                user.setUsersCurrentBotState(idUserMessage, BotState.WAIT_CHANGE_DAY);
                ServiceForDay.selectionDay(idUserMessage, user, bot);
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
