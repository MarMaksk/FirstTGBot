package Main.service;

import Main.state.BotState;
import Main.table.CorrectScheduleSQL;
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
import java.util.*;

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
        String res = sentToUserDay(bot, userId, 0l);
    }

    private static String sentToUserDay(TelegramBot bot, Long userId, Long day) {
        String res = null;
        try {
            String dayOfWeek = getDaysOfAWeek(day);
            List<String> ls = new ArrayList<>();
            res = SelectTableFromSQL.getScheduleForDayOfWeek(userId, dayOfWeek, ls);
            bot.execute(new SendMessage(userId, "Расписание на " + (res.isEmpty() ? dayOfWeek : res)));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return res;
    }

    public static String getDaysOfAWeek(Long day) {
        LocalDate date = LocalDate.now().plusDays(day);
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        Locale localeRu = new Locale("ru", "RU");
        return dayOfWeek.getDisplayName(TextStyle.FULL, localeRu);
    }

    public static class Correct {
        private static Map<Long, Integer> correctStep = new HashMap<>();
        private static Map<Long, List<String>> scheduleTime = new HashMap();

        public static void buttonCorrect(Update update, TelegramBot bot, TelegramUser user, Long userId) {
            user.setUsersCurrentBotState(userId, BotState.BUTTON_CORRECT);

            String text = update.message().text();
            if (correctStep.get(userId) == null) {
                correctStep.put(userId, 6);
                scheduleTime.put(userId, new ArrayList<>());
            } else if (update.message().text().equals("/correct")) {
                correctStep.replace(userId, 6);
                scheduleTime.remove(userId);
                scheduleTime.put(userId, new ArrayList<>());
            }
            if (correctStep.get(userId) == 6) {
                bot.execute(new SendMessage(userId, "Настройки можно сменить в любой момент \n" +
                        "Сколько длятся пары с учётом перерыва во время пары (если он есть)?\n" +
                        "Ответ должен быть вида H:MM\n" +
                        "Пример: 1:35"));
                correctStep.replace(userId, 5);
            } else if (correctStep.get(userId) == 5) {
                if (!text.matches("\\d[:][0-6]\\d")) {
                    bot.execute(new SendMessage(userId, "Неправильный формат сообщения"));
                    return;
                }
                scheduleTime.get(userId).add(text);
                correctStep.replace(userId, 4);
                bot.execute(new SendMessage(userId, "Во сколько начинаются пары?\n" +
                        "Ответ должен быть вида H:MM\n" +
                        "Пример: 8:30"));
            } else if (correctStep.get(userId) == 4) {
                if (!text.matches("\\d[:][0-6]\\d")) {
                    bot.execute(new SendMessage(userId, "Неправильный формат сообщения"));
                    return;
                }
                scheduleTime.get(userId).add(text);
                correctStep.replace(userId, 3);
                bot.execute(new SendMessage(userId, "Сколько длится обед?\n" +
                        "Ответ должен быть в минутах\n" +
                        "Пример: 45"));
                return;
            } else if (correctStep.get(userId) == 3) {
                if (!text.matches("\\d\\d") && !text.matches("\\d")) {
                    bot.execute(new SendMessage(userId, "Неправильный формат сообщения"));
                    return;
                }
                scheduleTime.get(userId).add(text);
                correctStep.replace(userId, 2);
                bot.execute(new SendMessage(userId, "После какой пары обед?"));
                return;
            } else if (correctStep.get(userId) == 2) {
                if (!text.matches("\\d")) {
                    bot.execute(new SendMessage(userId, "Неправильный формат сообщения"));
                    return;
                }
                scheduleTime.get(userId).add(text);
                correctStep.replace(userId, 1);
                bot.execute(new SendMessage(userId, "Сколько длятся перемены?\n" +
                        "Ответ должен быть в минутах"));
            } else if (correctStep.get(userId) == 1) {
                if (!text.matches("\\d\\d") && !text.matches("\\d")) {
                    bot.execute(new SendMessage(userId, "Неправильный формат сообщения"));
                    return;
                }
                scheduleTime.get(userId).add(text);
                correctStep.replace(userId, 0);
                bot.execute(new SendMessage(userId, "Сколько длятся пермены после четвертой пары?"));
                return;
            } else if (correctStep.get(userId) == 0) {
                if (!text.matches("\\d\\d") && !text.matches("\\d")) {
                    bot.execute(new SendMessage(userId, "Неправильный формат сообщения"));
                    return;
                }
                scheduleTime.get(userId).add(text);
                correctStep.remove(userId);
                CorrectScheduleSQL.InsertUpdateCorrectSchedule(userId, scheduleTime.get(userId));
                //TODO WRITE TO SQL
                scheduleTime.get(userId).forEach(System.out::println);
                scheduleTime.remove(userId);
                user.setUsersCurrentBotState(userId, BotState.WAIT_STATUS);
                bot.execute(new SendMessage(userId, "Настройка завершена"));
            }

        }
    }
}
