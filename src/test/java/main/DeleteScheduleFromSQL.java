package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DeleteScheduleFromSQL {
    private static final String DELETE_SCHEDULE_FROM_USER_LIST = "DELETE FROM tb_users_tablename" +
            " WHERE tb_tablename = ?;";
    private static final String DELETE_SCHEDULE_FROM_TB_MONDAY = "DELETE FROM tb_monday" +
            " WHERE tb_name = ?;";
    private static final String DELETE_SCHEDULE_FROM_TB_TUESDAY = "DELETE FROM tb_tuesday" +
            " WHERE tb_name = ?;";
    private static final String DELETE_SCHEDULE_FROM_TB_WEDNESDAY = "DELETE FROM tb_wednesday" +
            " WHERE tb_name = ?;";
    private static final String DELETE_SCHEDULE_FROM_TB_THURSDAY = "DELETE FROM tb_thursday" +
            " WHERE tb_name = ?;";
    private static final String DELETE_SCHEDULE_FROM_TB_FRIDAY = "DELETE FROM tb_friday" +
            " WHERE tb_name = ?;";
    private static final String DELETE_SCHEDULE_FROM_TB_SATURDAY = "DELETE FROM tb_saturday" +
            " WHERE tb_name = ?;";
    private static final String DELETE_SCHEDULE_FROM_TB_SUNDAY = "DELETE FROM tb_sunday" +
            " WHERE tb_name = ?;";

    public static void removeSchedule(Long userId, String tablename) {
        try (Connection con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/telegram_bot",
                "postgres",
                "596228")) {
            PreparedStatement stmt = con.prepareStatement(DELETE_SCHEDULE_FROM_USER_LIST);
            stmt.setString(1, userId + tablename);
            stmt.executeUpdate();
            System.out.println(true);
            stmt = con.prepareStatement(DELETE_SCHEDULE_FROM_TB_MONDAY);
            stmt.setString(1, tablename);
            stmt.executeUpdate();
            System.out.println(true);
            stmt = con.prepareStatement(DELETE_SCHEDULE_FROM_TB_TUESDAY);
            stmt.setString(1, tablename);
            stmt.executeUpdate();
            System.out.println(true);
            stmt = con.prepareStatement(DELETE_SCHEDULE_FROM_TB_WEDNESDAY);
            stmt.setString(1, tablename);
            stmt.executeUpdate();
            System.out.println(true);
            stmt = con.prepareStatement(DELETE_SCHEDULE_FROM_TB_THURSDAY);
            stmt.setString(1, tablename);
            stmt.executeUpdate();
            System.out.println(true);
            stmt = con.prepareStatement(DELETE_SCHEDULE_FROM_TB_FRIDAY);
            stmt.setString(1, tablename);
            stmt.executeUpdate();
            System.out.println(true);
            stmt = con.prepareStatement(DELETE_SCHEDULE_FROM_TB_SATURDAY);
            stmt.setString(1, tablename);
            stmt.executeUpdate();
            System.out.println(true);
            stmt = con.prepareStatement(DELETE_SCHEDULE_FROM_TB_SUNDAY);
            stmt.setString(1, tablename);
            stmt.executeUpdate();
            System.out.println(true);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

}
