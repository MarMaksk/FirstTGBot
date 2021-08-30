package Main.user;

import Main.state.BotState;
import Main.state.DayState;
import Main.state.ExtremHelpEnum;

import java.util.HashMap;
import java.util.Map;

public class TelegramUser {
    private static Map<Long, BotState> usersBotStates = new HashMap<>();
    private static Map<Long, ExtremHelpEnum> usersExtStates = new HashMap<>();
    private static Map<Long, DayState> usersDayStates = new HashMap<>();
    private static Map<Long, UserProfileData> usersProfileData = new HashMap<>();


    public void setUsersCurrentBotState(Long userId, BotState botState) {
        usersBotStates.put(userId, botState);
    }

    public ExtremHelpEnum getUsersCurrentExtremeState(Long userId) {
        ExtremHelpEnum exh = usersExtStates.get(userId);
        if (exh == null) {
            exh = ExtremHelpEnum.WAIT_PARAM;
        }
        return exh;
    }

    public void setUsersCurrentExtremeState(Long userId, ExtremHelpEnum exh) {
        usersExtStates.put(userId, exh);
    }

    public BotState getUsersCurrentBotState(Long userId) {
        BotState botState = usersBotStates.get(userId);
        if (botState == null) {
            botState = BotState.WAIT_STATUS;
        }
        return botState;
    }

    public static DayState getUsersCurrentDayState(Long userId) {
        DayState dayState = usersDayStates.get(userId);
        if (dayState == null) {
            dayState = DayState.WAIT_STATUS;
        }
        return dayState;
    }

    public static void setUsersCurrentDayState(Long userId, DayState dayState) {
        usersDayStates.put(userId, dayState);
    }


    public UserProfileData getUserProfileData(Long userId) {
        UserProfileData userProfileData = usersProfileData.get(userId);
        if (userProfileData == null) {
            userProfileData = new UserProfileData();
        }
        return userProfileData;
    }

    public void saveUserProfileData(Long userId, UserProfileData userProfileData) {
        usersProfileData.put(userId, userProfileData);
    }
}
