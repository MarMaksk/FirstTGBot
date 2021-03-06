package main.ServiceSQL;

import main.service.ServiceForButton;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SelectTableFromSQL extends OperationSQL {

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

    public static List<String> getTableOfOneDay(Long idUserMessage) {
        List<String> dayList = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(urlSQL, loginSQL, passwordSQL)) {
            getDayFromTable(idUserMessage, con);
        } catch (SQLException throwables) {         //TODO ?????????????? ???????????? ?? ??????????
            throwables.printStackTrace();
        }
        return dayList;
    }

    private static void getDayFromTable(Long idUserMessage, Connection con) throws SQLException {
        PreparedStatement stmt = con.prepareStatement(SELECT_TABLE_MONDAY);
        getOneTable(idUserMessage, stmt);
    }


    public static String getScheduleAWeek(Long idUserMessage) {
        String weekSchedule = "";
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
            ReceivingDays.weekday(idUserMessage, stmt0, ls, "??????????????????????", "???????????????????? ???? ?????????????????????? ????????????????????");
            ReceivingDays.weekday(idUserMessage, stmt1, ls, "??????????????", "???????????????????? ???? ?????????????? ????????????????????");
            ReceivingDays.weekday(idUserMessage, stmt2, ls, "??????????", "???????????????????? ???? ?????????? ????????????????????");
            ReceivingDays.weekday(idUserMessage, stmt3, ls, "??????????????", "???????????????????? ???? ?????????????? ????????????????????");
            ReceivingDays.weekday(idUserMessage, stmt4, ls, "??????????????", "???????????????????? ???? ?????????????? ????????????????????");
            ReceivingDays.weekend(idUserMessage, stmt5, ls, "??????????????");
            ReceivingDays.weekend(idUserMessage, stmt6, ls, "??????????????????????");
            for (String list : ls) {
                weekSchedule += list + "\n";
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return weekSchedule;
    }

    private static class ReceivingDays {
        public static List<String> weekend(Long idUserMessage, PreparedStatement stmt, List<String> ls, String day) throws SQLException {
            List<String> ls0 = getOneTable(idUserMessage, stmt);
            ls0.removeAll(Collections.singleton(null));
            if (!ls0.isEmpty()) {
                ls.add(day);
                ls0.forEach(ls::add);
            }
            return ls0;
        }

        public static List<String> weekday(Long idUserMessage, PreparedStatement stmt, List<String> ls, String day, String dayIsEmpty) throws SQLException {
            List<String> ls0 = getOneTable(idUserMessage, stmt);
            ls0.removeAll(Collections.singleton(null));
            if (!ls0.isEmpty()) {
                ls.add(day);
                ls0.forEach(ls::add);
            } else {
                ls.add(dayIsEmpty);
            }
            return ls0;
        }
    }

    public static List<String> getOneTable(Long idUserMessage, PreparedStatement stmt) throws SQLException {
        List<String> dayList = new ArrayList<>();
        try (Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/telegram_bot",
                "postgres",
                "596228")) {
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
        }
        return dayList;
    }

    public static List<String> getListOfSchedule(Long userId) throws SQLException {
        List<String> ls = new ArrayList<>();
        String dayOfWeek = ServiceForButton.getDaysOfAWeek(0l);
        getScheduleForDayOfWeek(userId, dayOfWeek, ls);
        return ls;
    }


    public static String getScheduleForDayOfWeek(Long userId, String dayOfWeek, List<String> ls) throws SQLException {
        PreparedStatement stmt = null;
        String weekSchedule = "";
        try (Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/telegram_bot",
                "postgres",
                "596228")) {
            if (dayOfWeek.equals("??????????????????????"))
                stmt = con.prepareStatement(SELECT_TABLE_MONDAY);
            if (dayOfWeek.equals("??????????????"))
                stmt = con.prepareStatement(SELECT_TABLE_TUESDAY);
            if (dayOfWeek.equals("??????????"))
                stmt = con.prepareStatement(SELECT_TABLE_WEDNESDAY);
            if (dayOfWeek.equals("??????????????"))
                stmt = con.prepareStatement(SELECT_TABLE_THURSDAY);
            if (dayOfWeek.equals("??????????????"))
                stmt = con.prepareStatement(SELECT_TABLE_FRIDAY);
            if (dayOfWeek.equals("??????????????"))
                stmt = con.prepareStatement(SELECT_TABLE_SATURDAY);
            if (dayOfWeek.equals("??????????????????????"))
                stmt = con.prepareStatement(SELECT_TABLE_SUNDAY);
            if (dayOfWeek.equals("??????????????") || dayOfWeek.equals("??????????????????????"))
                ReceivingDays.weekend(userId, stmt, ls, dayOfWeek);
            else
                ReceivingDays.weekday(userId, stmt, ls, dayOfWeek, dayOfWeek + " ????????????????");
            for (String list : ls) {
                weekSchedule += list + "\n";
            }
        }
        return weekSchedule;
    }

}
