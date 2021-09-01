package Main.service;

import Main.state.BotState;
import Main.user.TelegramUser;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import main.CorrectScheduleSQL;
import main.SelectTableFromSQL;
import main.TablenameSQL;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

public class ServiceForButton implements Service {
    public static void buttonAdd(Update update, TelegramUser user, TelegramBot bot) {
        bot.execute(new SendMessage(update.message().chat().id(), "Как оно будет называться?"));
        user.setUsersCurrentBotState(update.message().chat().id(), BotState.BUTTON_ADD);
    }

    public static String buttonAWeek(Long userId, TelegramBot bot) {
        String weekSchedule = SelectTableFromSQL.getScheduleAWeek(userId);
        bot.execute(new SendMessage(userId, weekSchedule));
        return weekSchedule;
    }

    public static void buttonChange(TelegramBot bot, TelegramUser user, Long userId) {
        user.setUsersCurrentBotState(userId, BotState.BUTTON_CHANGE);
        ServiceForDay.selectionDay(userId, user, bot);
    }

    public static void buttonChoice(Update update, TelegramUser user, TelegramBot bot) {
        if (user.getUsersCurrentBotState(update.message().chat().id()) != BotState.BUTTON_CHOICE ||
                user.getUsersCurrentBotState(update.message().chat().id()) == BotState.BUTTON_DELETE) {
            List<String> listTablename = TablenameSQL.getExistingTablename(update.message().chat().id());
            if (listTablename.isEmpty()) {
                bot.execute(new SendMessage(update.message().chat().id(), "Для начала нужно добавить расписание"));
                return;
            }
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
            if (user.getUsersCurrentBotState(update.message().chat().id()) != BotState.BUTTON_DELETE)
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

    public static void buttonNow(TelegramBot bot, Long userId) {
        List<String> schedule = new ArrayList<>();
        try {
            schedule = SelectTableFromSQL.getListOfSchedule(userId);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        schedule.remove(0);
        if (schedule.isEmpty()) {
            System.out.println("На это время нет пар");
            return;
        }
        List<String> correct = CorrectScheduleSQL.SelectCorrectSchedule(userId);
        if (correct.isEmpty()) {
            bot.execute(new SendMessage(userId, "Для начала следует скорректировать расписание"));
            return;
        }
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("H:mm")
                .withLocale(Locale.getDefault())
                .withZone(ZoneId.systemDefault());
        LocalTime timeNow = LocalTime.now();
        LocalTime timePair = LocalTime.parse(correct.get(0), formatter);
        LocalTime timeStart = LocalTime.parse(correct.get(1), formatter);
        LocalTime timeChange = LocalTime.parse("0:" + correct.get(4), formatter);
        LocalTime firstPair = timeStart.plusHours(timePair.getHour()).plusMinutes(timePair.getMinute());
        LocalTime secondPair = firstPair.plusHours(timePair.getHour()).plusMinutes(timePair.getMinute()).plusMinutes(timeChange.getMinute());
        LocalTime thirdPair;
        LocalTime fourthPair;
        if (Integer.valueOf(correct.get(3)) == 2) {
            thirdPair = secondPair.plusHours(timePair.getHour()).plusMinutes(timePair.getMinute()).plusMinutes(timeChange.getMinute()).plusMinutes(Integer.valueOf(correct.get(2)));
            fourthPair = thirdPair.plusHours(timePair.getHour()).plusMinutes(timePair.getMinute()).plusMinutes(timeChange.getMinute()).minusMinutes(timeChange.getMinute());
        } else {
            thirdPair = secondPair.plusHours(timePair.getHour()).plusMinutes(timePair.getMinute()).plusMinutes(timeChange.getMinute());
            fourthPair = thirdPair.plusHours(timePair.getHour()).plusMinutes(timePair.getMinute()).plusMinutes(timeChange.getMinute()).plusMinutes(Integer.valueOf(correct.get(2))).minusMinutes(timeChange.getMinute());
        }
        LocalTime timeChangeAfterFourthPair = LocalTime.parse("0:" + correct.get(5), formatter);
        LocalTime fifthPair = fourthPair.plusHours(timePair.getHour()).plusMinutes(timePair.getMinute()).plusMinutes(timeChangeAfterFourthPair.getMinute());
        LocalTime sixthPair = fifthPair.plusHours(timePair.getHour()).plusMinutes(timePair.getMinute()).plusMinutes(timeChangeAfterFourthPair.getMinute());
        LocalTime seventhPair = sixthPair.plusHours(timePair.getHour()).plusMinutes(timePair.getMinute()).plusMinutes(timeChangeAfterFourthPair.getMinute());
        LocalTime eigthPair = seventhPair.plusHours(timePair.getHour()).plusMinutes(timePair.getMinute()).plusMinutes(timeChangeAfterFourthPair.getMinute());
        LocalTime ninthPair = eigthPair.plusHours(timePair.getHour()).plusMinutes(timePair.getMinute()).plusMinutes(timeChangeAfterFourthPair.getMinute());
        //pos нужен чтобы можно было переиспользовать код для нахождения следующей пары
        int pos = 0;
        String messege = getPair(schedule, timeNow, timeStart, firstPair, secondPair, thirdPair, fourthPair, fifthPair, sixthPair, seventhPair, eigthPair, ninthPair, pos);
        bot.execute(new SendMessage(userId, messege));
        return;
    }

    public static void buttonNext(TelegramBot bot, Long userId) {
        List<String> schedule = new ArrayList<>();
        try {
            schedule = SelectTableFromSQL.getListOfSchedule(userId);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        schedule.remove(0);
        if (schedule.isEmpty()) {
            System.out.println("На это время нет пар");
            return;
        }
        List<String> correct = CorrectScheduleSQL.SelectCorrectSchedule(userId);
        if (correct.isEmpty()) {
            bot.execute(new SendMessage(userId, "Для начала следует скорректировать расписание"));
            return;
        }
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("H:mm")
                .withLocale(Locale.getDefault())
                .withZone(ZoneId.systemDefault());
        LocalTime timeNow = LocalTime.now();
        LocalTime timePair = LocalTime.parse(correct.get(0), formatter);
        LocalTime timeStart = LocalTime.parse(correct.get(1), formatter);
        LocalTime timeChange = LocalTime.parse("0:" + correct.get(4), formatter);
        LocalTime firstPair = timeStart.plusHours(timePair.getHour()).plusMinutes(timePair.getMinute());
        LocalTime secondPair = firstPair.plusHours(timePair.getHour()).plusMinutes(timePair.getMinute()).plusMinutes(timeChange.getMinute());
        LocalTime thirdPair;
        LocalTime fourthPair;
        if (Integer.valueOf(correct.get(3)) == 2) {
            thirdPair = secondPair.plusHours(timePair.getHour()).plusMinutes(timePair.getMinute()).plusMinutes(timeChange.getMinute()).plusMinutes(Integer.valueOf(correct.get(2)));
            fourthPair = thirdPair.plusHours(timePair.getHour()).plusMinutes(timePair.getMinute()).plusMinutes(timeChange.getMinute()).minusMinutes(timeChange.getMinute());
        } else {
            thirdPair = secondPair.plusHours(timePair.getHour()).plusMinutes(timePair.getMinute()).plusMinutes(timeChange.getMinute());
            fourthPair = thirdPair.plusHours(timePair.getHour()).plusMinutes(timePair.getMinute()).plusMinutes(timeChange.getMinute()).plusMinutes(Integer.valueOf(correct.get(2))).minusMinutes(timeChange.getMinute());
        }
        LocalTime timeChangeAfterFourthPair = LocalTime.parse("0:" + correct.get(5), formatter);
        LocalTime fifthPair = fourthPair.plusHours(timePair.getHour()).plusMinutes(timePair.getMinute()).plusMinutes(timeChangeAfterFourthPair.getMinute());
        LocalTime sixthPair = fifthPair.plusHours(timePair.getHour()).plusMinutes(timePair.getMinute()).plusMinutes(timeChangeAfterFourthPair.getMinute());
        LocalTime seventhPair = sixthPair.plusHours(timePair.getHour()).plusMinutes(timePair.getMinute()).plusMinutes(timeChangeAfterFourthPair.getMinute());
        LocalTime eigthPair = seventhPair.plusHours(timePair.getHour()).plusMinutes(timePair.getMinute()).plusMinutes(timeChangeAfterFourthPair.getMinute());
        LocalTime ninthPair = eigthPair.plusHours(timePair.getHour()).plusMinutes(timePair.getMinute()).plusMinutes(timeChangeAfterFourthPair.getMinute());
        //pos нужен чтобы можно было переиспользовать код для нахождения следующей пары
        int pos = 1;
        String messege = getPair(schedule, timeNow, timeStart, firstPair, secondPair, thirdPair, fourthPair, fifthPair, sixthPair, seventhPair, eigthPair, ninthPair, pos);
        bot.execute(new SendMessage(userId, messege));
        return;
    }

    private static String getPair(List<String> schedule, LocalTime timeNow, LocalTime timeStart, LocalTime firstPair, LocalTime secondPair, LocalTime thirdPair, LocalTime fourthPair, LocalTime fifthPair, LocalTime sixthPair, LocalTime seventhPair, LocalTime eigthPair, LocalTime ninthPair, int pos) {
        String messege;
        String pairNone = "На это время нет пар";
        int size = schedule.size();
        if (timeNow.isAfter(timeStart) &&
                timeNow.isBefore(firstPair) &&
                pos != size) {
            messege = schedule.get(pos + 0);
        } else if (timeNow.isAfter(firstPair) &&
                timeNow.isBefore(secondPair) &&
                pos + 1 < size) {
            messege = schedule.get(pos + 1);
        } else if (timeNow.isAfter(secondPair) &&
                timeNow.isBefore(thirdPair) &&
                pos + 2 < size) {
            messege = schedule.get(pos + 2);
        } else if (timeNow.isAfter(thirdPair) &&
                timeNow.isBefore(fourthPair) &&
                pos + 3 < size) {
            messege = schedule.get(pos + 3);
        } else if (timeNow.isAfter(fourthPair) &&
                timeNow.isBefore(fifthPair) &&
                pos + 4 < size) {
            messege = schedule.get(pos + 4);
        } else if (timeNow.isAfter(fifthPair) &&
                timeNow.isBefore(sixthPair) &&
                pos + 5 < size) {
            messege = schedule.get(pos + 5);
        } else if (timeNow.isAfter(sixthPair) &&
                timeNow.isBefore(seventhPair) &&
                pos + 6 < size) {
            messege = schedule.get(pos + 6);
        } else if (timeNow.isAfter(seventhPair) &&
                timeNow.isBefore(eigthPair) &&
                pos + 7 < size) {
            messege = schedule.get(7);
        } else if (timeNow.isAfter(eigthPair) &&
                timeNow.isBefore(ninthPair) &&
                pos + 8 < size) {
            messege = schedule.get(pos + 8);
        } else {
            messege = pairNone;
        }
        return messege;
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
                scheduleTime.get(userId).forEach(System.out::println);
                scheduleTime.remove(userId);
                user.setUsersCurrentBotState(userId, BotState.WAIT_STATUS);
                bot.execute(new SendMessage(userId, "Настройка завершена"));
            }
        }
    }
}
