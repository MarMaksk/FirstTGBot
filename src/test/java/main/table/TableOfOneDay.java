package main.table;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TableOfOneDay {
    private static Map<Long, List<String>> oneDay = new HashMap<>();

    public static void setOneDay(Long idMessage, String task) {
        List<String> list = new LinkedList<>();
        if (oneDay.containsKey(idMessage))
            list = getOneDay().get(idMessage);
        list.add(task);
        oneDay.put(idMessage, list);
    }

    public static void removeUserList(Long userId){
        oneDay.remove(userId);
    }

    public static List<String> getListOfOneDay(Long idMessage) {
        return oneDay.get(idMessage);
    }

    public static Map<Long, List<String>> getOneDay() {
        return oneDay;
    }

}
