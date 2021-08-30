package Main.service;

import Main.state.BotState;
import Main.table.SelectTableFromSQL;
import Main.table.TablenameSQL;
import Main.user.TelegramUser;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class ServiceForButton implements Service {
    public static void buttonAdd(Update update, TelegramUser user, TelegramBot bot) {
        bot.execute(new SendMessage(update.message().chat().id(), "Как оно будет называться?"));
        user.setUsersCurrentBotState(update.message().chat().id(), BotState.BUTTON_ADD);
    }

    public static void buttonAWeek(Long userId, TelegramBot bot) {
        String weekSchedule = SelectTableFromSQL.getScheduleAWeek(bot, userId);
        bot.execute(new SendMessage(userId, weekSchedule));
    }

    public static void buttonChange(TelegramBot bot, TelegramUser user, Long userId) {
        user.setUsersCurrentBotState(userId, BotState.BUTTON_CHANGE);
        ServiceForDay.selectionDay(userId, user, bot);
    }

    public static void buttonChoice(Update update, TelegramUser user, TelegramBot bot) {
        //TODO полученние названий из SQL
        if (user.getUsersCurrentBotState(update.message().chat().id()) != BotState.BUTTON_CHOICE) {
            List<String> listTablename = TablenameSQL.getExistingTablename(update.message().chat().id());
            KeyboardButton[] keyboardButtons = new KeyboardButton[listTablename.size()];
            for (int i = 0; i < keyboardButtons.length; i++) {
                keyboardButtons[i] = new KeyboardButton(listTablename.get(i));
            }
            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup("");
            for (KeyboardButton kb : keyboardButtons) {
                replyKeyboardMarkup.addRow(kb);
            }
            replyKeyboardMarkup.resizeKeyboard(false).selective(true).oneTimeKeyboard(true);
            bot.execute(new SendMessage(update.message().chat().id(), "Выбери расписание из доступных").replyMarkup(replyKeyboardMarkup));
            user.setUsersCurrentBotState(update.message().chat().id(), BotState.BUTTON_CHOICE);
        }
        return;
    }

    public static void buttonTomorrow(TelegramBot bot, Long userId) {
        sentToUserDay(bot, userId, 1l);
    }

    public static void buttonToday(TelegramBot bot, Long userId) {
        sentToUserDay(bot, userId, 0l);
    }

    private static void sentToUserDay(TelegramBot bot, Long userId, Long day) {
        try {
            String dayOfWeek = getDaysOfAWeek(day);
            String res = SelectTableFromSQL.getScheduleForDayOfWeek(userId, dayOfWeek);
            bot.execute(new SendMessage(userId, "Расписание на " + (res.isEmpty() ? dayOfWeek : res)));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private static String getDaysOfAWeek(Long day) {
        LocalDate date = LocalDate.now().plusDays(day);
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        Locale localeRu = new Locale("ru", "RU");
        return dayOfWeek.getDisplayName(TextStyle.FULL, localeRu);
    }
}
