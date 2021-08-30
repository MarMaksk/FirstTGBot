package Main.table;

import Main.user.TelegramUser;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SelectTableFromSQL {

    private static final String SELECT_TABLE_MONDAY = "SELECT tb_one, tb_two, tb_three, tb_four, tb_five, tb_six, tb_seven, tb_eight, tb_nine\n" +
            "\tFROM tb_monday WHERE tb_user_id = ? AND tb_name = ?";
    private static final String SELECT_TABLE_TUESDAY = "SELECT tb_one, tb_two, tb_three, tb_four, tb_five, tb_six, tb_seven, tb_eight, tb_nine\n" +
            "\tFROM tb_tuesday WHERE tb_user_id = ? AND tb_name = ?";
    private static final String SELECT_TABLE_WEDNESDAY = "SELECT tb_one, tb_two, tb_three, tb_four, tb_five, tb_six, tb_seven, tb_eight, tb_nine\n" +
            "\tFROM tb_wednesday WHERE tb_user_id = ? AND tb_name = ?";
    private static final String SELECT_TABLE_THURSDAY = "SELECT tb_one, tb_two, tb_three, tb_four, tb_five, tb_six, tb_seven, tb_eight, tb_nine\n" +
            "\tFROM tb_thursday WHERE tb_user_id = ? AND tb_name = ?";
    private static final String SELECT_TABLE_FRIDAY = "SELECT tb_one, tb_two, tb_three, tb_four, tb_five, tb_six, tb_seven, tb_eight, tb_nine\n" +
            "\tFROM tb_friday WHERE tb_user_id = ? AND tb_name = ?";
    private static final String SELECT_TABLE_SATURDAY = "SELECT tb_one, tb_two, tb_three, tb_four, tb_five, tb_six, tb_seven, tb_eight, tb_nine\n" +
            "\tFROM tb_saturday WHERE tb_user_id = ? AND tb_name = ?";
    private static final String SELECT_TABLE_SUNDAY = "SELECT tb_one, tb_two, tb_three, tb_four, tb_five, tb_six, tb_seven, tb_eight, tb_nine\n" +
            "\tFROM tb_sunday WHERE tb_user_id = ? AND tb_name = ?";

    public static List<String> getTableOfOneDay(TelegramBot bot, Long idUserMessage, String day, TelegramUser user) {
        List<String> dayList = new ArrayList<>();
        try (Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/telegram_bot",
                "postgres",
                "596228")) {
            getDayFromTable(idUserMessage, dayList, con);
        } catch (SQLException throwables) {         //TODO сделать работу с днями
            throwables.printStackTrace();
        }
        return dayList;
    }

    private static void getDayFromTable(Long idUserMessage, List<String> dayList, Connection con) throws SQLException {
        PreparedStatement stmt = con.prepareStatement(SELECT_TABLE_MONDAY);
        getOneTable(idUserMessage, stmt);
    }


    public static void showAllTable(TelegramBot bot, Long idUserMessage) {
        try (Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/telegram_bot",
                "postgres",
                "596228")) {
            PreparedStatement stmt0 = con.prepareStatement(SELECT_TABLE_MONDAY);
            PreparedStatement stmt1 = con.prepareStatement(SELECT_TABLE_TUESDAY);
            PreparedStatement stmt2 = con.prepareStatement(SELECT_TABLE_WEDNESDAY);
            PreparedStatement stmt3 = con.prepareStatement(SELECT_TABLE_THURSDAY);
            PreparedStatement stmt4 = con.prepareStatement(SELECT_TABLE_FRIDAY);
            PreparedStatement stmt5 = con.prepareStatement(SELECT_TABLE_SATURDAY);
            PreparedStatement stmt6 = con.prepareStatement(SELECT_TABLE_SUNDAY);
            List<String> ls = new LinkedList<>();
            weekday(idUserMessage, stmt0, ls, "Понедельник", "Расписание на понедельник отсутсвует");
            weekday(idUserMessage, stmt1, ls, "Вторник", "Расписание на вторник отсутсвует");
            weekday(idUserMessage, stmt2, ls, "Среда", "Расписание на среду отсутсвует");
            weekday(idUserMessage, stmt3, ls, "Четверг", "Расписание на четверг отсутсвует");
            weekday(idUserMessage, stmt4, ls, "Пятница", "Расписание на пятницу отсутсвует");
            weekend(idUserMessage, stmt5, ls, "Суббота");
            weekend(idUserMessage, stmt6, ls, "Воскресенье");
            String weekSchedule = "";
            for (String list : ls) {
                weekSchedule += list + "\n";
            }
            bot.execute(new SendMessage(idUserMessage, weekSchedule));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private static void weekend(Long idUserMessage, PreparedStatement stmt5, List<String> ls, String day) throws SQLException {
        List<String> ls0 = getOneTable(idUserMessage, stmt5);
        ls0.removeAll(Collections.singleton(null));
        if (!ls0.isEmpty()) {
            ls.add(day);
            ls0.forEach(el -> ls.add(el));
        }
    }

    private static void weekday(Long idUserMessage, PreparedStatement stmt0, List<String> ls, String day, String dayIsEmpty) throws SQLException {
        List<String> ls0 = getOneTable(idUserMessage, stmt0);
        ls0.removeAll(Collections.singleton(null));
        if (!ls0.isEmpty()) {
            ls.add(day);
            ls0.forEach(el -> ls.add(el));
        } else {
            ls.add(dayIsEmpty);
        }
    }

    private static List<String> getOneTable(Long idUserMessage, PreparedStatement stmt) throws SQLException {
        List<String> dayList = new ArrayList<>();
        stmt.setLong(1, idUserMessage);
        stmt.setString(2, TablenameSQL.getActualTablename(idUserMessage));
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            dayList.add(0, rs.getString("tb_one"));
            dayList.add(1, rs.getString("tb_two"));
            dayList.add(2, rs.getString("tb_three"));
            dayList.add(3, rs.getString("tb_four"));
            dayList.add(4, rs.getString("tb_five"));
            dayList.add(5, rs.getString("tb_six"));
            dayList.add(6, rs.getString("tb_seven"));
            dayList.add(7, rs.getString("tb_eight"));
            dayList.add(8, rs.getString("tb_nine"));
        }
        dayList.removeAll(Collections.singleton("empty"));
        return dayList;
    }
}
