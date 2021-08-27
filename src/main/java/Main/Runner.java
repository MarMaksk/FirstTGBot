package Main;

import Main.service.ServiceForButton;
import Main.service.ServiceForStatus;
import Main.service.ServicePreparationForSQL;
import Main.service.ServiceForDay;
import Main.table.TableOfOneDay;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import Main.state.BotState;
import Main.state.MessageType;
import Main.user.TelegramUser;

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
                                        return;
                                    case NEXT:
                                        return;
                                    case TODAY:
                                        return;
                                    case TOMORROW:
                                        return;
                                    case AWEEK:

                                        return;
                                    case ADD:
                                        // TODO: 27.08.2021  
                                        ServiceForButton.buttonAdd(update, user, bot);
                                        return;
                                    case CHANGE:
                                        return;
                                    case CHOICE:
                                        return;
                                    case MESSAGE:
                                        // TODO: 27.08.2021  
                                        TableOfOneDay.addTimetableName(update, idUserMessage, user, bot);
                                        ServicePreparationForSQL.preparationForWriting(update, update.message().from().id(), user, bot);
                                        return;
                                    case CHANGE_DAY:
                                        ServiceForDay.changeDay(update, update.message().text(), user.getUsersCurrentBotState(idUserMessage), user, bot);
                                        return;
                                    case CHAT_MEMBER:
                                        invokeChatMember(update);
                                        return;
                                    case CALLBACK_QUERY:
                                        return;
                                    case END:
                                        ServiceForStatus.endStatus(update, bot);
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

    public void buttonChoice(Update update, List<String> listTableName) {
        //TODO полученние названий из SQL
        KeyboardButton[] keyboardButtons = new KeyboardButton[listTableName.size()];
        for (int i = 0; i < keyboardButtons.length; i++) {
            keyboardButtons[i] = new KeyboardButton(listTableName.get(i));
        }
        ReplyKeyboardMarkup replyKeyboardMarkup = null;
        for (KeyboardButton kb : keyboardButtons) {
            replyKeyboardMarkup.addRow(kb);
        }
        replyKeyboardMarkup.resizeKeyboard(false).selective(true).oneTimeKeyboard(true);
        bot.execute(new SendMessage(update.message().chat().id(), "Выбери расписание из доступных").replyMarkup(replyKeyboardMarkup));

        user.setUsersCurrentBotState(update.message().chat().id(), BotState.BUTTON_CHOICE);
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
        if (update.message() != null && userStatus == BotState.WAIT_CHANGE_DAY) return MessageType.CHANGE_DAY;
        if (update.message() != null && userStatus == BotState.END) return MessageType.END;
        if (update.message() != null && text.equals("/start")) return MessageType.START;
        if (update.message() != null && text.equals("/now")) return MessageType.NOW;
        if (update.message() != null && text.equals("/next")) return MessageType.NEXT;
        if (update.message() != null && text.equals("/today")) return MessageType.TODAY;
        if (update.message() != null && text.equals("/tomorrow")) return MessageType.TOMORROW;
        if (update.message() != null && text.equals("/aweek")) return MessageType.AWEEK;
        if (update.message() != null && text.equals("/add")) return MessageType.ADD;
        if (update.message() != null && text.equals("/change")) return MessageType.CHANGE;
        if (update.message() != null && text.equals("/choice")) return MessageType.CHOICE;
        if (update.message() != null) return MessageType.MESSAGE;
        if (update.myChatMember() != null) return MessageType.CHAT_MEMBER;
        if (update.callbackQuery() != null) return MessageType.CALLBACK_QUERY;
        return MessageType.UNSUPPORTED;
    }

}
