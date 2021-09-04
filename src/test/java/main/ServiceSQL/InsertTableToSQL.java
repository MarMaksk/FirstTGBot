package main.ServiceSQL;


import Main.state.DayState;
import Main.user.TelegramUser;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class InsertTableToSQL extends OperationSQL {
    private static final String INSERT_MONDAY = "INSERT INTO tb_monday(\n" +
            "\ttb_user_id, tb_name, tb_one, tb_two, tb_three, tb_four, tb_five, tb_six, tb_seven, tb_eight, tb_nine, tb_public)\n" +
            "\tVALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    private static final String INSERT_TUESDAY = "INSERT INTO tb_tuesday(\n" +
            "\ttb_user_id, tb_name, tb_one, tb_two, tb_three, tb_four, tb_five, tb_six, tb_seven, tb_eight, tb_nine, tb_public)\n" +
            "\tVALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    private static final String INSERT_WEDNESDAY = "INSERT INTO tb_wednesday(\n" +
            "\ttb_user_id, tb_name, tb_one, tb_two, tb_three, tb_four, tb_five, tb_six, tb_seven, tb_eight, tb_nine, tb_public)\n" +
            "\tVALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    private static final String INSERT_THURSDAY = "INSERT INTO tb_thursday(\n" +
            "\ttb_user_id, tb_name, tb_one, tb_two, tb_three, tb_four, tb_five, tb_six, tb_seven, tb_eight, tb_nine, tb_public)\n" +
            "\tVALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    private static final String INSERT_FRIDAY = "INSERT INTO tb_friday(\n" +
            "\ttb_user_id, tb_name, tb_one, tb_two, tb_three, tb_four, tb_five, tb_six, tb_seven, tb_eight, tb_nine, tb_public)\n" +
            "\tVALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    private static final String INSERT_SATURDAY = "INSERT INTO tb_saturday(\n" +
            "\ttb_user_id, tb_name, tb_one, tb_two, tb_three, tb_four, tb_five, tb_six, tb_seven, tb_eight, tb_nine, tb_public)\n" +
            "\tVALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    private static final String INSERT_SUNDAY = "INSERT INTO tb_sunday(\n" +
            "\ttb_user_id, tb_name, tb_one, tb_two, tb_three, tb_four, tb_five, tb_six, tb_seven, tb_eight, tb_nine, tb_public)\n" +
            "\tVALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";


    //DriverManager.getConnection("jdbc:postgresql://localhost:5432/telegram_bot",
//        "postgres",
//        "596228")
// TODO: 26.08.2021
    public static void createNewTable(Long idUserMessage, TelegramUser user, List<String> oneDay, PreparedStatement stmt) {
        try (Connection con = DriverManager.getConnection(urlSQL, loginSQL, passwordSQL)) {
            if (stmt == null)
                stmt = getPreparedStatement(idUserMessage, user, con);
            con.setAutoCommit(false);
            try {
                stmt.setLong(1, idUserMessage);
                stmt.setString(2, TablenameSQL.getActualTablename(idUserMessage));
                for (int i = oneDay.size() + 2; i > 2; i--)
                    stmt.setString(i, oneDay.get(i - 3));
                for (int i = oneDay.size() + 3; i < 12; i++)
                    stmt.setString(i, "empty");
                stmt.setBoolean(12, false);
                stmt.executeUpdate();
                con.commit();
                System.out.println("Sucsess");
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    @Nullable
    private static PreparedStatement getPreparedStatement(Long idUserMessage, TelegramUser user, Connection con) throws SQLException {
        PreparedStatement stmt = null;
        DayState userStatus = user.getUsersCurrentDayState(idUserMessage);
        if (userStatus == DayState.MONDAY)
            stmt = con.prepareStatement(INSERT_MONDAY);
        if (userStatus == DayState.TUESDAY)
            stmt = con.prepareStatement(INSERT_TUESDAY);
        if (userStatus == DayState.WEDNESDAY)
            stmt = con.prepareStatement(INSERT_WEDNESDAY);
        if (userStatus == DayState.THURSDAY)
            stmt = con.prepareStatement(INSERT_THURSDAY);
        if (userStatus == DayState.FRIDAY)
            stmt = con.prepareStatement(INSERT_FRIDAY);
        if (userStatus == DayState.SATURDAY)
            stmt = con.prepareStatement(INSERT_SATURDAY);
        if (userStatus == DayState.SUNDAY)
            stmt = con.prepareStatement(INSERT_SUNDAY);
        return stmt;
    }

}
