package Main;

import Main.service.ServiceForButton;
import Main.service.ServiceForDay;
import Main.service.ServiceForStatus;
import Main.service.ServicePreparationForSQL;
import Main.state.BotState;
import Main.state.MessageType;
import Main.table.Tablename;
import Main.table.TablenameSQL;
import Main.table.UpdateTableToSQL;
import Main.user.TelegramUser;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
                                        buttonNow();
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
    public static void buttonNow() {
        SimpleDateFormat sdf = new SimpleDateFormat("H");

        //   Date date = sdf.parse("2");
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("HH:mm")
                .withLocale(Locale.getDefault())
                .withZone(ZoneId.systemDefault());

        Instant now = Instant.now();
        LocalDateTime ld = LocalDateTime.now();
        LocalTime l = LocalTime.now();
        LocalTime lt = LocalTime.parse("22:00"); //Сюда вставить время начало из таблицы

        if (l.isAfter(lt)) {
            System.out.println(true);
        }
        lt = lt.plusHours(1).plusMinutes(30);
        System.out.println(lt);
        System.out.println(l);
        String formatted = formatter.format(ld);
        System.out.println(formatted);
//            String fileName = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME);
//            String fileNam1e = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
//            System.out.println(date);

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
        if (update.message() != null && userStatus == BotState.BUTTON_CHANGE) return MessageType.CHANGE_TABLE;
        if (update.message() != null && userStatus == BotState.CHANGE_SCHEDULE) return MessageType.CHANGE_TABLE;
        if (update.message() != null && userStatus == BotState.BUTTON_CHOICE) return MessageType.CHOICE_TABLENAME;
        if (update.message() != null && userStatus == BotState.SET_ACTUAL_TABLENAME) return MessageType.CHOICE;
        if (update.message() != null && userStatus == BotState.WAIT_CHANGE_DAY) return MessageType.CHANGE_DAY;
        if (update.message() != null && userStatus == BotState.END) return MessageType.END;
        if (update.message() != null || userStatus == BotState.DAY_RECEIVED) return MessageType.MESSAGE;
        if (update.myChatMember() != null) return MessageType.CHAT_MEMBER;
        if (update.callbackQuery() != null) return MessageType.CALLBACK_QUERY;
        return MessageType.UNSUPPORTED;
    }

}