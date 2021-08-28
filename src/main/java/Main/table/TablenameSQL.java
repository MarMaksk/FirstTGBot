package Main.table;

import Main.config.Config;
import Main.user.TelegramUser;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class TablenameSQL extends OperationSQL {
    private static final String INSERT_TABLENAME = "INSERT INTO tb_users_tablename(\n" +
            "\ttb_user_id, tb_tablename)\n" +
            "\tVALUES (?, ?);";
    private static final String INSERT_ACTUAL_TABLENAME = "INSERT INTO tb_users_actual_table_name(\n" +
            "\ttb_user_id, tb_tablename)\n" +
            "\tVALUES (?, ?);";
    private static final String UPDATE_ACTUAL_TABLENAME = "UPDATE tb_users_actual_table_name\n" +
            "\tSET tb_user_id=?, tb_tablename=?\n" +
            "\tWHERE <condition>;";

    //TODO refactoring - make one method

    //DriverManager.getConnection("jdbc:postgresql://localhost:5432/telegram_bot",
//        "postgres",
//        "596228")
// TODO: 26.08.2021
    public static void setActualTablename(Long idUserMessage, TelegramUser user, List<String> oneDay, String tableName) {
        try (Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/telegram_bot",
                "postgres",
                "596228")) {
            PreparedStatement stmt = con.prepareStatement(UPDATE_ACTUAL_TABLENAME);
            stmt.setLong(1, idUserMessage);
            stmt.setString(2, tableName);
            stmt.executeUpdate();
            System.out.println("Sucsess");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static boolean createNewTableName(Long idUserMessage, TelegramUser user, String tableName, TelegramBot bot) {
        try (Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/telegram_bot",
                "postgres",
                "596228")) {
            PreparedStatement stmt = con.prepareStatement(INSERT_TABLENAME);
            stmt.setLong(1, idUserMessage);
            stmt.setString(2, idUserMessage + tableName);
            stmt.executeUpdate();
            System.out.println("Sucsess");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            bot.execute(new SendMessage(idUserMessage, "Расписание с таким названием уже существует"));
            return false;
        }
        return true;
    }
}
