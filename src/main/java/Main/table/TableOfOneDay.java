package Main.table;

import Main.user.TelegramUser;
import Main.service.ServiceForDay;
import Main.state.BotState;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import java.util.*;

public class TableOfOneDay {
    private static Map<Long, String> tableName = new HashMap<>();
    private static Map<Long, List<String>> oneDay = new HashMap<>();

    public static List<String> getListOfOneDay(Long idMessage) {
        return oneDay.get(idMessage);
    }

    public static Map<Long, List<String>> getOneDay() {
        return oneDay;
    }

    public static void setOneDay(Long idMessage, String task) {
        List<String> list = new LinkedList<>();
        if (oneDay.containsKey(idMessage))
            list = getOneDay().get(idMessage);
        list.add(task);
        oneDay.put(idMessage, list);
    }

    public static void addTimetableName(Update update, Long idMessage, TelegramUser user, TelegramBot bot) {
        if (user.getUsersCurrentBotState(idMessage) == BotState.BUTTON_ADD) {
            if (AddTableToSQL.createNewTableName(idMessage, user, update.message().text(), bot)) {
                getTableName().put(idMessage, update.message().text());
                user.setUsersCurrentBotState(idMessage, BotState.WAIT_CHANGE_DAY);
                ServiceForDay.selectionDay(update, idMessage, user, bot);
            }
        }
    }

    public static Map<Long, String> getTableName() {
        return tableName;
    }

    public static void setTableName(Map<Long, String> tableName) {
        TableOfOneDay.tableName = tableName;
    }
}
