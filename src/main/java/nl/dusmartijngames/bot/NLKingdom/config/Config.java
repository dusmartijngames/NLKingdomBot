package nl.dusmartijngames.bot.NLKingdom.config;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {

    private static final Dotenv dotenv = Dotenv.load();

    public static String get(String token) {
        return dotenv.get(token);
    }
    public static long getLong(String token) {
        return Long.parseLong(dotenv.get(token));
    }

}
