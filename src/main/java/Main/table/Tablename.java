package Main.table;

import Main.service.ServiceForDay;
import Main.state.BotState;
import Main.user.TelegramUser;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import java.util.HashMap;
import java.util.Map;

public class Tablename {
    private static Map<Long, String> tableName = new HashMap<>();

    public static void addTimetableName(Update update, Long idMessage, TelegramUser user, TelegramBot bot) {
        if (user.getUsersCurrentBotState(idMessage) == BotState.BUTTON_ADD) {
            if (TablenameSQL.createNewTablename(idMessage, user, update.message().text(), bot)) {
                getTableName().put(idMessage, update.message().text());
                user.setUsersCurrentBotState(idMessage, BotState.WAIT_CHANGE_DAY);
                ServiceForDay.selectionDay(idMessage, user, bot);
            }
        }
    }


    public static Map<Long, String> getTableName() {
        return tableName;
    }

    public static void setTableName(Map<Long, String> tableName) {
        tableName = tableName;
    }
}
