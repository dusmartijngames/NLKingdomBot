package nl.dusmartijngames.bot.NLKingdom.objects;

import java.util.Arrays;
import java.util.List;

public interface ICommand {
    void handle(CommandContext event);

    String getName();

    String getHelp(CommandContext event);

    default List<String> getAliases() {
        return Arrays.asList();// use Arrays.asList if you are on java 8
    }

    String getCat();
}
