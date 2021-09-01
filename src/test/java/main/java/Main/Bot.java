package Main;

import com.pengrad.telegrambot.TelegramBot;


import java.util.logging.Logger;

public class Bot {
    private static Logger logger = Logger.getLogger(Bot.class.getName());

    public static void main(String[] args) {
        TelegramBot bot = new TelegramBot("1872562052:AAFaSs47WRM3S1w7-u9Y7J52ahzdA2bYPhQ");
        Runner run = new Runner(bot);
        run.run();
        logger.info("Я включился");
    }


}