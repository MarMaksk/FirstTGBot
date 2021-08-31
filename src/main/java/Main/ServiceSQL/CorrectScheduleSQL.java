package Main.ServiceSQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CorrectScheduleSQL {
    private final static String INSERT_CORRECT_SCHEDULE = "INSERT INTO tb_user_schedule_time(\n" +
            "\ttb_user_id, tb_pair_length, tb_time_start, tb_time_lunch, tb_lunch_after, tb_time_change, tb_time_change_fourth_pair)\n" +
            "\tVALUES (?, ?, ?, ?, ?, ?, ?);";
    private final static String UPDATE_CORRECT_SCHEDULE = "UPDATE tb_user_schedule_time" +
            " SET  tb_pair_length = ?, tb_time_start = ?, tb_time_lunch = ?, tb_lunch_after = ?, tb_time_change = ?, tb_time_change_fourth_pair = ? WHERE tb_user_id = ?";
    private final static String SELECT_CORRECT_SCHEDULE = "SELECT tb_pair_length, tb_time_start, tb_time_lunch, tb_lunch_after, tb_time_change, tb_time_change_fourth_pair\n" +
            "\tFROM tb_user_schedule_time WHERE tb_user_id = ?;";

    public static void InsertUpdateCorrectSchedule(Long userId, List<String> correct) {
        try (Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/telegram_bot",
                "postgres",
                "596228")) {
            con.setAutoCommit(false);
            boolean resultInput = true;
            PreparedStatement stmt = con.prepareStatement(INSERT_CORRECT_SCHEDULE);
            try {
                stmt.setLong(1, userId);
                for (int i = 0; i < correct.size(); i++)
                    stmt.setString(i + 2, correct.get(i));
                stmt.executeUpdate();
                con.commit();
            } catch (SQLException ex) {
                try {
                    con.rollback();
                    stmt = con.prepareStatement(UPDATE_CORRECT_SCHEDULE);
                    for (int i = 0; i < correct.size(); i++)
                        stmt.setString(i + 1, correct.get(i));
                    stmt.setLong(7, userId);
                    stmt.executeUpdate();
                    con.commit();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

//            if (!resultInput) {
//                try {
//                    stmt = con.prepareStatement(UPDATE_CORRECT_SCHEDULE);
//                    for (int i = 0; i < correct.size(); i++)
//                        stmt.setString(i + 1, correct.get(i));
//                    stmt.setLong(7, userId);
//                    stmt.executeUpdate();
//                    con.commit();
//                } catch (SQLException ex) {
//                    con.rollback();
//                }
    }


    public static List<String> SelectCorrectSchedule(Long userId) {
        List<String> correct = new ArrayList<>();
        try (Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/telegram_bot",
                "postgres",
                "596228")) {
            PreparedStatement stmt = con.prepareStatement(SELECT_CORRECT_SCHEDULE);
            try {
                stmt.setLong(1, userId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    try {
                        correct.add(0, rs.getString("tb_pair_length"));
                        correct.add(1, rs.getString("tb_time_start"));
                        correct.add(2, rs.getString("tb_time_lunch"));
                        correct.add(3, rs.getString("tb_lunch_after"));
                        correct.add(4, rs.getString("tb_time_change"));
                        correct.add(5, rs.getString("tb_time_change_fourth_pair"));
                    } catch (NullPointerException ex) {
                        correct.removeAll(Collections.singleton(null));
                        //correct.add("");
                        return correct;
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return correct;
    }

}
