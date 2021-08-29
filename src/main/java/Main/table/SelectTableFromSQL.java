package Main.table;

import Main.user.TelegramUser;
import com.pengrad.telegrambot.TelegramBot;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SelectTableFromSQL {

    private static final String SELECT_TABLE = "SELECT tb_one, tb_two, tb_three, tb_four, tb_five, tb_six, tb_seven, tb_eight, tb_nine\n" +
            "\tFROM tb_monday WHERE tb_user_id = ? AND tb_name = ?";

    public static List<String> getTableOfOneDay(TelegramBot bot, Long idUserMessage, String day, TelegramUser user) {
        List<String> dayList = new ArrayList<>();
        try (Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/telegram_bot",
                "postgres",
                "596228")) {
            PreparedStatement stmt = con.prepareStatement(SELECT_TABLE);
            //stmt.setString(1, "tb_monday");
            String tablename = TablenameSQL.getActualTablename(idUserMessage);
            stmt.setLong(1, idUserMessage);
            stmt.setString(2, TablenameSQL.getActualTablename(idUserMessage));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                dayList.add(rs.getString("tb_one"));
                dayList.add(rs.getString("tb_two"));
                dayList.add(rs.getString("tb_three"));
                dayList.add(rs.getString("tb_four"));
                dayList.add(rs.getString("tb_five"));
                dayList.add(rs.getString("tb_six"));
                dayList.add(rs.getString("tb_seven"));
                dayList.add(rs.getString("tb_eight"));
                dayList.add(rs.getString("tb_nine"));
            }
            dayList.removeAll(Collections.singleton("empty"));
            dayList.forEach(System.out::println);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return dayList;
    }
}
