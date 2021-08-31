package Main.table;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class CorrectScheduleSQL {
    private final static String INSERT_CORRECT_SCHEDULE = "INSERT INTO tb_user_schedule_time(\n" +
            "\ttb_user_id, tb_pair_length, tb_time_start, tb_time_lunch, tb_lunch_after, tb_time_change, tb_time_change_fourth_pair)\n" +
            "\tVALUES (?, ?, ?, ?, ?, ?, ?);";
    private final static String UPDATE_CORRECT_SCHEDULE = "UPDATE tb_user_schedule_time\\n\" +\n" +
            "            \"\\tSET  tb_pair_length = ?, tb_time_start = ?, tb_time_lunch = ?, tb_lunch_after = ?, tb_time_change = ?, tb_time_change_fourth_pair = ? WHERE tb_user_id = ?";
    private final static String SELECT_CORRECT_SCHEDULE = "SELECT tb_user_id, tb_pair_length, tb_time_start, tb_time_lunch, tb_lunch_after, tb_time_change, tb_time_change_fourth_pair\n" +
            "\tFROM tb_user_schedule_time;";

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
                con.rollback();
                resultInput = false;
                ex.printStackTrace();
                System.out.println("THAT'S NORMAL EXCEPTION");
            }
            if (!resultInput) {
                stmt = con.prepareStatement(UPDATE_CORRECT_SCHEDULE);
                for (int i = 0; i < correct.size(); i++)
                    stmt.setString(i+1, correct.get(i));
                stmt.setLong(7, userId);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void SelectCorrectSchedule() {

    }

}
