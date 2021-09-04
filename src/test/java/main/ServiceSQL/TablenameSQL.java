package main.ServiceSQL;

import Main.state.BotState;
import Main.user.TelegramUser;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.request.SendMessage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TablenameSQL extends OperationSQL {
    private static final String INSERT_TABLENAME = "INSERT INTO tb_users_tablename(\n" +
            "\ttb_user_id, tb_tablename)\n" +
            "\tVALUES (?, ?);";
    private static final String SELECT_TABLENAME = "SELECT tb_tablename FROM tb_users_tablename " +
            "WHERE tb_user_id = ?";
    private static final String INSERT_ACTUAL_TABLENAME = "INSERT INTO tb_users_actual_table_name(\n" +
            "\ttb_user_id, tb_tablename)\n" +
            "\tVALUES (?, ?);";
    private static final String UPDATE_ACTUAL_TABLENAME = "UPDATE tb_users_actual_table_name\n" +
            "\tSET tb_user_id=?, tb_tablename=?\n";
    private static final String SELECT_ACTUAL_TABLENAME = "SELECT tb_tablename\n" +
            "\tFROM tb_users_actual_table_name WHERE tb_user_id=?";

    public static String getActualTablename(Long userId) {
        String tablename = null;
        try (Connection con = DriverManager.getConnection(urlSQL, loginSQL, passwordSQL)) {
            PreparedStatement stmt = con.prepareStatement(SELECT_ACTUAL_TABLENAME);
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next())
                tablename = rs.getString("tb_tablename").replace(userId + "", "");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return tablename;
    }
    //TODO refactoring - make one method

    //DriverManager.getConnection("jdbc:postgresql://localhost:5432/telegram_bot",
//        "postgres",
//        "596228")
// TODO: 26.08.2021
    public static void setActualTablename(TelegramBot bot, Long idUserMessage, String tableName, TelegramUser user) {
        if (user.getUsersCurrentBotState(idUserMessage) == BotState.BUTTON_CHOICE) {
            try (Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/telegram_bot",
                    "postgres",
                    "596228")) {
                List<String> listTablename = getExistingTablename(idUserMessage);
                if (!listTablename.contains(tableName)) {
                    bot.execute(new SendMessage(idUserMessage, "Это расписание не из списка"));
                    return;
                }
                boolean resultUpdate = true;
                PreparedStatement stmt = con.prepareStatement(INSERT_ACTUAL_TABLENAME);
                ;
                con.setAutoCommit(false);
                try {
                    writeTablenameToTable(idUserMessage, tableName, con, stmt);
                } catch (SQLException ex) {
                    con.rollback();
                    resultUpdate = false;
                }
                if (!resultUpdate) {
                    stmt = con.prepareStatement(UPDATE_ACTUAL_TABLENAME);
                    try {
                        writeTablenameToTable(idUserMessage, tableName, con, stmt);
                    } catch (SQLException ex) {
                        con.rollback();
                    }
                }
                ReplyKeyboardRemove rkr = new ReplyKeyboardRemove();
                bot.execute(new SendMessage(idUserMessage, "Расписание выбрано").replyMarkup(rkr));
                user.setUsersCurrentBotState(idUserMessage, BotState.WAIT_STATUS);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    public static boolean createNewTablename(Long idUserMessage, TelegramUser user, String tableName, TelegramBot bot) {
        try (Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/telegram_bot",
                "postgres",
                "596228")) {
            PreparedStatement stmt = con.prepareStatement(INSERT_TABLENAME);

            con.setAutoCommit(false);
            try {
                writeTablenameToTable(idUserMessage, tableName, con, stmt);
                user.setUsersCurrentBotState(idUserMessage, BotState.BUTTON_CHOICE);
                setActualTablename(bot, idUserMessage, tableName, user);
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            bot.execute(new SendMessage(idUserMessage, "Расписание с таким названием уже существует"));
            return false;
        }
        return true;
    }

    public static List<String> getExistingTablename(Long userId) {
        List<String> listTablename = new ArrayList<>();
        try (Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/telegram_bot",
                "postgres",
                "596228")) {
            PreparedStatement stmt = con.prepareStatement(SELECT_TABLENAME);
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                listTablename.add(rs.getString("tb_tablename").replace(userId.toString(), ""));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return listTablename;
    }

    private static void writeTablenameToTable(Long idUserMessage, String tableName, Connection con, PreparedStatement stmt) throws SQLException {
        stmt.setLong(1, idUserMessage);
        stmt.setString(2, idUserMessage + tableName);
        stmt.executeUpdate();
        con.commit();
        System.out.println("Sucsess");
    }
}
