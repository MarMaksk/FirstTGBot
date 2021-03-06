package main.ServiceSQL;

import Main.state.DayState;
import Main.state.ExtremHelpEnum;
import Main.user.TelegramUser;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class UpdateTableToSQL extends OperationSQL {
    private static final String UPDATE_MONDAY = "UPDATE tb_monday\n" +
            "\tSET tb_one=?, tb_two=?, tb_three=?, tb_four=?, tb_five=?, tb_six=?, tb_seven=?, tb_eight=?, tb_nine=?, tb_public=? WHERE tb_user_id = ? AND tb_name = ?";
    private static final String UPDATE_TUESDAY = "UPDATE tb_tuesday " +
            "SET tb_one=?, tb_two=?, tb_three=?, tb_four=?, tb_five=?, tb_six=?, tb_seven=?, tb_eight=?, tb_nine=?, tb_public=? WHERE tb_user_id=? AND tb_name =?";
    private static final String UPDATE_WEDNESDAY = "UPDATE tb_wednesday\\n\" +\n" +
            "            \"\\tSET tb_user_id=?, tb_one=?, tb_two=?, tb_three=?, tb_four=?, tb_five=?, tb_six=?, tb_seven=?, tb_eight=?, tb_nine=?, tb_public=? WHERE tb_name=?";
    private static final String UPDATE_THURSDAY = "UPDATE tb_thursday\\n\" +\n" +
            "            \"\\tSET tb_user_id=?, tb_one=?, tb_two=?, tb_three=?, tb_four=?, tb_five=?, tb_six=?, tb_seven=?, tb_eight=?, tb_nine=?, tb_public=? WHERE tb_name = ?";
    private static final String UPDATE_FRIDAY = "UPDATE tb_friday\\n\" +\n" +
            "            \"\\tSET tb_user_id=?, tb_one=?, tb_two=?, tb_three=?, tb_four=?, tb_five=?, tb_six=?, tb_seven=?, tb_eight=?, tb_nine=?, tb_public=? WHERE tb_name = ?";
    private static final String UPDATE_SATURDAY = "UPDATE tb_saturday\\n\" +\n" +
            "            \"\\tSET tb_user_id=?, tb_one=?, tb_two=?, tb_three=?, tb_four=?, tb_five=?, tb_six=?, tb_seven=?, tb_eight=?, tb_nine=?, tb_public=? WHERE tb_name = ?";
    private static final String UPDATE_SUNDAY = "UPDATE tb_sunday\\n\" +\n" +
            "            \"\\tSET tb_user_id=?, tb_one=?, tb_two=?, tb_three=?, tb_four=?, tb_five=?, tb_six=?, tb_seven=?, tb_eight=?, tb_nine=?, tb_public=? WHERE tb_name = ?";

    public static void updateDay(TelegramBot bot, Update update, TelegramUser user, List<String> oneDay) {
        try (Connection con = DriverManager.getConnection(urlSQL, loginSQL, passwordSQL)) {
            con.setAutoCommit(false);
            boolean resultUpdate = true;
            Long userId = update.message().from().id();
            PreparedStatement stmt = getPreparedStatement(user, userId, con);
            try {
                updateSchedule(userId, oneDay, con, stmt);
            } catch (SQLException ex) {
                con.rollback();
                resultUpdate = false;
                ex.printStackTrace();
                System.out.println("THAT'S NORMAL EXCEPTION");
            }
            if (!resultUpdate) {
                InsertTableToSQL.createNewTable(userId, user, oneDay, null);
            }
            bot.execute(new SendMessage(userId, "???????????????????? ????????????????"));
            user.setUsersCurrentExtremeState(userId, ExtremHelpEnum.EXTREME_PARAM_ONE);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private static void updateSchedule(Long idUserMessage, List<String> oneDay, Connection con, PreparedStatement stmt) throws SQLException {
        for (int i = oneDay.size(); i > 0; i--)
            stmt.setString(i, oneDay.get(i - 1));
        for (int i = oneDay.size() + 1; i < 10; i++)
            stmt.setString(i, "empty");
        stmt.setBoolean(10, false);
        stmt.setLong(11, idUserMessage);
        stmt.setString(12, TablenameSQL.getActualTablename(idUserMessage));
        stmt.executeUpdate();
        con.commit();
        System.out.println("Sucsess");
    }

    private static PreparedStatement getPreparedStatement(TelegramUser user, Long userId, Connection con) throws SQLException {
        PreparedStatement stmt = null;
        DayState userStatus = user.getUsersCurrentDayState(userId);
        if (userStatus == DayState.MONDAY)
            stmt = con.prepareStatement(UPDATE_MONDAY);
        if (userStatus == DayState.TUESDAY)
            stmt = con.prepareStatement(UPDATE_TUESDAY);
        if (userStatus == DayState.WEDNESDAY)
            stmt = con.prepareStatement(UPDATE_WEDNESDAY);
        if (userStatus == DayState.THURSDAY)
            stmt = con.prepareStatement(UPDATE_THURSDAY);
        if (userStatus == DayState.FRIDAY)
            stmt = con.prepareStatement(UPDATE_FRIDAY);
        if (userStatus == DayState.SATURDAY)
            stmt = con.prepareStatement(UPDATE_SATURDAY);
        if (userStatus == DayState.SUNDAY)
            stmt = con.prepareStatement(UPDATE_SUNDAY);
        return stmt;

    }

}
