package Main.service;

import Main.user.TelegramUser;
import Main.state.BotState;
import Main.table.AddTableToSQL;
import Main.table.TableOfOneDay;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

import java.util.HashMap;
import java.util.Map;

public class ServicePreparationForSQL {
    private static Map<Long, Integer> couplesPerDay = new HashMap<>();

    public static void preparationForWriting(Update update, Long idUserMessage, TelegramUser user, TelegramBot bot) {
        if (user.getUsersCurrentBotState(idUserMessage) == BotState.DAY_RECEIVED) {
            if (!couplesPerDay.containsKey(idUserMessage)) {
                couplesPerDay.put(idUserMessage, Integer.valueOf(update.message().text()));
                bot.execute(new SendMessage(idUserMessage, "Какой будет " + update.message().text() + " пара?"));
                return;
            } else if (couplesPerDay.get(idUserMessage) == 1) {
                TableOfOneDay.setOneDay(idUserMessage, update.message().text());
                AddTableToSQL.createNewTable(idUserMessage, user, TableOfOneDay.getListOfOneDay(idUserMessage), TableOfOneDay.getTableName().get(idUserMessage));
                user.setUsersCurrentBotState(idUserMessage, BotState.WAIT_CHANGE_DAY);
                ServiceForDay.selectionDay(update, idUserMessage, user, bot);
                couplesPerDay.remove(idUserMessage);
                return;
            } else {
                couplesPerDay.put(idUserMessage, couplesPerDay.get(idUserMessage) - 1);
                bot.execute(new SendMessage(idUserMessage, "Какой будет " + couplesPerDay.get(update.message().from().id()) + " пара?"));
                TableOfOneDay.setOneDay(idUserMessage, update.message().text());
                return;
            }
        }
    }

    public static void preparationForReading(Update update, Long idUserMessage, TelegramUser user, TelegramBot bot) {
    }
}
