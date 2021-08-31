package Main;

import Main.service.ServiceForButton;
import Main.service.ServiceForDay;
import Main.service.ServiceForStatus;
import Main.service.ServicePreparationForSQL;
import Main.state.BotState;
import Main.state.MessageType;
import Main.table.SelectTableFromSQL;
import Main.table.Tablename;
import Main.table.TablenameSQL;
import Main.table.UpdateTableToSQL;
import Main.user.TelegramUser;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

import java.sql.SQLException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

public class Runner {
    private static TelegramBot bot;
    private static TelegramUser user = new TelegramUser();
    private static Logger logger = Logger.getLogger(Bot.class.getName());

    public Runner(TelegramBot bot) {
        this.bot = bot;
    }

    public void run() {
        bot.setUpdatesListener(new UpdatesListener() {
            @Override
            public int process(List<Update> lst) {
                lst.forEach(update -> {
                            new Thread(() -> {

                                BotState stateUserCallbackQuery = null;
                                BotState stateUserMessage = null;
                                Long idUserCallbackQuery = null;
                                Long idUserMessage = null;
                                if (update.callbackQuery() != null) {
                                    stateUserCallbackQuery = user.getUsersCurrentBotState(update.callbackQuery().from().id());
                                    idUserCallbackQuery = update.callbackQuery().from().id();
                                }
                                if (update.message() != null) {
                                    stateUserMessage = user.getUsersCurrentBotState(update.message().from().id());
                                    idUserMessage = update.message().from().id();
                                }
                                switch (messageType(update)) {
                                    case START:
                                        invokeChatMember(update);
                                        return;
                                    case NOW:
                                        buttonNow(idUserMessage);
                                        return;
                                    case NEXT:
                                        return;
                                    case TODAY:
                                        ServiceForButton.buttonToday(bot, idUserMessage);
                                        return;
                                    case TOMORROW:
                                        ServiceForButton.buttonTomorrow(bot, idUserMessage);
                                        return;
                                    case AWEEK:
                                        ServiceForButton.buttonAWeek(idUserMessage, bot);
                                        return;
                                    case ADD:
                                        ServiceForDay.fillMapDaysButton(idUserMessage);
                                        ServiceForButton.buttonAdd(update, user, bot);
                                        return;
                                    case CHANGE:
                                        ServiceForDay.fillMapDaysButton(idUserMessage);
                                        ServiceForButton.buttonChange(bot, user, idUserMessage);
                                        return;
                                    case CHANGE_TABLE:
                                        ServicePreparationForSQL.preparationDayForWriting(update, idUserMessage, user, bot);
                                        UpdateTableToSQL.preparationDayForUpdate(bot, user, update.message().text().toLowerCase(), idUserMessage);
                                        return;
                                    case CHOICE:
                                        ServiceForButton.buttonChoice(update, user, bot);
                                        return;
                                    case CHOICE_TABLENAME:
                                        TablenameSQL.setActualTablename(bot, idUserMessage, update.message().text(), user);
                                        return;
                                    case MESSAGE:
                                        Tablename.addTimetableName(update, idUserMessage, user, bot);
                                        ServicePreparationForSQL.preparationDayForWriting(update, update.message().from().id(), user, bot);
                                        return;
                                    case CORRECT:
                                        ServiceForButton.Correct.buttonCorrect(update, bot, user, idUserMessage);
                                        return;
                                    case CHANGE_DAY:
                                        ServiceForDay.changeDay(update, update.message().text(), user, bot);
                                        return;
                                    case CHAT_MEMBER:
                                        invokeChatMember(update);
                                        return;
                                    case CALLBACK_QUERY:
                                        return;
                                    case END:
                                        ServiceForStatus.buttonEndInDayChange(update, bot);
                                        return;
                                    default:
                                        throw new IllegalStateException("Unexpected value: " + messageType(update));
                                }
                            }).start();
                        }
                );
                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            }


        });
    }

    //TODO Удалять ли клавиатуру везде? Сделать ли отдельный класс для хранения ключа бота? (что я имел ввиду?)
    // TODO А если я напишу на убранный понедельник понедельник? Сколько может быть пар? Напоминания
    public static void buttonNow(Long userId) {
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
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("H:mm")
                .withLocale(Locale.getDefault())
                .withZone(ZoneId.systemDefault());
        LocalTime timeNow = LocalTime.now();
        LocalTime timeStart = LocalTime.parse("8:00", formatter).minusMinutes(1);
        LocalTime timePair = LocalTime.parse("1:30", formatter);
        LocalTime timeChange = LocalTime.parse("0:" + "15", formatter);
        LocalTime firstPair = timeStart.plusHours(timePair.getHour()).plusMinutes(timePair.getMinute());
        LocalTime secondPair = firstPair.plusHours(timePair.getHour()).plusMinutes(timePair.getMinute()).plusMinutes(timeChange.getMinute());
        LocalTime thirdPair = secondPair.plusHours(timePair.getHour()).plusMinutes(timePair.getMinute()).plusMinutes(timeChange.getMinute());
        LocalTime fourthPair = thirdPair.plusHours(timePair.getHour()).plusMinutes(timePair.getMinute()).plusMinutes(timeChange.getMinute());
        LocalTime timeChangeAfterFourthPair = LocalTime.parse("0:" + "10", formatter);
        LocalTime fifthPair = fourthPair.plusHours(timePair.getHour()).plusMinutes(timePair.getMinute()).plusMinutes(timeChangeAfterFourthPair.getMinute());
        LocalTime sixthPair = fifthPair.plusHours(timePair.getHour()).plusMinutes(timePair.getMinute()).plusMinutes(timeChangeAfterFourthPair.getMinute());
        LocalTime seventhPair = sixthPair.plusHours(timePair.getHour()).plusMinutes(timePair.getMinute()).plusMinutes(timeChangeAfterFourthPair.getMinute());
        LocalTime eigthPair = seventhPair.plusHours(timePair.getHour()).plusMinutes(timePair.getMinute()).plusMinutes(timeChangeAfterFourthPair.getMinute());
        LocalTime ninthPair = eigthPair.plusHours(timePair.getHour()).plusMinutes(timePair.getMinute()).plusMinutes(timeChangeAfterFourthPair.getMinute());
        String pairNone = "На это время нет пар";
        int size = schedule.size();
        String messege = null;
        if (timeNow.isAfter(timeStart) &&
                timeNow.isBefore(firstPair)) {
            messege = schedule.get(0);
        } else if (timeNow.isAfter(firstPair) &&
                timeNow.isBefore(secondPair) &&
                1 < size) {
            messege = schedule.get(1);
        } else if (timeNow.isAfter(secondPair) &&
                timeNow.isBefore(thirdPair) &&
                2 < size) {
            messege = schedule.get(2);
        } else if (timeNow.isAfter(thirdPair) &&
                timeNow.isBefore(fourthPair) &&
                3 < size) {
            messege = schedule.get(3);
        } else if (timeNow.isAfter(fourthPair) &&
                timeNow.isBefore(fifthPair) &&
                4 < size) {
            messege = schedule.get(4);
        } else if (timeNow.isAfter(fifthPair) &&
                timeNow.isBefore(sixthPair) &&
                5 < size) {
            messege = schedule.get(5);
        } else if (timeNow.isAfter(sixthPair) &&
                timeNow.isBefore(seventhPair) &&
                6 < size) {
            messege = schedule.get(6);
        } else if (timeNow.isAfter(seventhPair) &&
                timeNow.isBefore(eigthPair) &&
                7 < size) {
            messege = schedule.get(7);
        } else if (timeNow.isAfter(eigthPair) &&
                timeNow.isBefore(ninthPair) &&
                8 < size) {
            messege = schedule.get(8);
        } else {
            messege = pairNone;
        }
        bot.execute(new SendMessage(userId, messege));
        return;
    }

    private void invokeChatMember(Update update) {
        bot.execute(new SendMessage(update.message().chat().id(), "Когда-то когда человечество от  ̶g̶̶̶a̶̶̶r̶̶̶b̶̶̶a̶̶̶g̶̶̶e̶̶̶ ̶̶̶c̶̶̶o̶̶̶l̶̶̶l̶̶̶e̶̶̶c̶̶̶t̶̶̶o̶̶̶r̶̶̶ ̶̶̶б̶̶̶ы̶̶̶л̶̶̶о̶̶̶ ̶̶̶с̶̶̶п̶̶̶а̶̶̶с̶̶̶е̶̶̶н̶̶̶о̶̶̶ ̶̶̶п̶̶̶е̶̶̶р̶̶̶е̶̶̶о̶̶̶п̶̶̶р̶̶̶е̶̶̶д̶̶̶е̶̶̶л̶̶̶ё̶̶̶н̶̶̶н̶̶̶ы̶̶̶м̶̶̶ ̶̶̶м̶̶̶е̶̶̶т̶̶̶о̶̶̶д̶̶̶о̶̶̶м̶̶̶ ̶̶̶f̶̶̶i̶̶̶n̶̶̶a̶̶̶l̶̶̶i̶̶̶z̶̶̶e̶̶ " +
                "потопа было спасено Ноем, в мире была несогласованность. " +
                "Ни у кого не было распорядка. Этот бот был создан чтобы не повторять подобное. " +
                "Как минимум чтобы это не повторилось у создателя бота"));
    }

    private MessageType messageType(Update update) {
        String text = "";
        BotState userStatus = update.message() == null ?
                null : user.getUsersCurrentBotState(update.message().from().id());
        if (update.message() != null)
            text = update.message().text();
        if (update.message() != null && text.equals("/start")) return MessageType.START;
        if (update.message() != null && text.equals("/now")) return MessageType.NOW;
        if (update.message() != null && text.equals("/next")) return MessageType.NEXT;
        if (update.message() != null && text.equals("/today")) return MessageType.TODAY;
        if (update.message() != null && text.equals("/tomorrow")) return MessageType.TOMORROW;
        if (update.message() != null && text.equals("/aweek")) return MessageType.AWEEK;
        if (update.message() != null && text.equals("/add")) return MessageType.ADD;
        if (update.message() != null && text.equals("/change")) return MessageType.CHANGE;
        if (update.message() != null && text.equals("/choice")) return MessageType.CHOICE;
        if (update.message() != null && text.equals("/correct")) return MessageType.CORRECT;
        if (update.message() != null && userStatus == BotState.BUTTON_CHANGE) return MessageType.CHANGE_TABLE;
        if (update.message() != null && userStatus == BotState.CHANGE_SCHEDULE) return MessageType.CHANGE_TABLE;
        if (update.message() != null && userStatus == BotState.BUTTON_CHOICE) return MessageType.CHOICE_TABLENAME;
        if (update.message() != null && userStatus == BotState.SET_ACTUAL_TABLENAME) return MessageType.CHOICE;
        if (update.message() != null && userStatus == BotState.WAIT_CHANGE_DAY) return MessageType.CHANGE_DAY;
        if (update.message() != null && userStatus == BotState.END) return MessageType.END;
        if (update.message() != null && userStatus == BotState.DAY_RECEIVED) return MessageType.MESSAGE;
        if (update.message() != null && userStatus == BotState.BUTTON_CORRECT) return MessageType.CORRECT;
        if (update.myChatMember() != null) return MessageType.CHAT_MEMBER;
        if (update.callbackQuery() != null) return MessageType.CALLBACK_QUERY;
        return MessageType.UNSUPPORTED;
    }

}