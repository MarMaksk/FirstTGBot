package main;

import Main.state.BotState;
import Main.state.MessageType;
import Main.user.TelegramUser;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import main.ServiceSQL.TablenameSQL;
import main.service.ServiceForButton;
import main.service.ServiceForDay;
import main.service.ServiceForStatus;
import main.service.ServicePreparationForSQL;
import main.table.Tablename;

import java.util.List;
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
                                if (update.message().text()==null){
                                    bot.execute(new SendMessage(update.message().from().id(),"Бот читает только символы, буквы и цифры"));
                                    return;
                                }
                                Long idUserMessage = null;
                                if (update.message() != null) {
                                    idUserMessage = update.message().from().id();
                                }
                                switch (messageType(update)) {
                                    case START:
                                        invokeChatMember(update);
                                        return;
                                    case NOW:
                                        ServiceForButton.buttonNow(bot, idUserMessage);
                                        return;
                                    case NEXT:
                                        ServiceForButton.buttonNext(bot, idUserMessage);
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
                                        ServicePreparationForSQL.preparationDayForUpdate(bot, user, update.message().text().toLowerCase(), idUserMessage);
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
                                    case DELETE:
                                        ServiceForButton.buttonDelete(bot, update, user, idUserMessage);
                                        ServiceForButton.buttonChoice(update, user, bot);
                                        return;
                                    case DELETE_TWO:
                                        ServiceForButton.buttonDelete(bot, update, user, idUserMessage);
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
                                        ServiceForStatus.botStateEnd(update, bot);
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


    private void invokeChatMember(Update update) {
        bot.execute(new SendMessage(update.message().chat().id(), "Добро пожаловать" +
                "\nДля начала работы требуется добавить расписание через меню" +
                "\nПосле добавления будут работать все пункты меню кроме пара сейчас и следующая" +
                "\nДля них требуется настроить расписание(Последняя кнопка меню)" +
                "\nМожно добавлять несколько расписаний" +
                "\nРасписание можно в любой момент сменить или удалить"));
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
        if (update.message() != null && text.equals("/delete")) return MessageType.DELETE;
        if (update.message() != null && userStatus == BotState.BUTTON_ADD) return MessageType.MESSAGE;
        if (update.message() != null && userStatus == BotState.BUTTON_DELETE) return MessageType.DELETE_TWO;
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