package Main.table;

import Main.service.ServiceForDay;
import Main.state.BotState;
import Main.user.TelegramUser;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import main.ServiceSQL.TablenameSQL;

public class Tablename {
    public static void addTimetableName(Update update, Long idMessage, TelegramUser user, TelegramBot bot) {
        if (user.getUsersCurrentBotState(idMessage) == BotState.BUTTON_ADD) {
            if (TablenameSQL.createNewTablename(idMessage, user, update.message().text(), bot)) {
                user.setUsersCurrentBotState(idMessage, BotState.WAIT_CHANGE_DAY);
                ServiceForDay.selectionDay(idMessage, user, bot);
            }
        }
    }
}
