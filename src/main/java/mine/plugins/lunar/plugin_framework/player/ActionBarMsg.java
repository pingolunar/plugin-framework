package mine.plugins.lunar.plugin_framework.player;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;

public class ActionBarMsg {

    final TextComponent textComponent;

    public ActionBarMsg() {
        textComponent = new TextComponent();
    }

    /**
     * @param primaryColor Should be an actual color
     * @param secondaryColor Should be a text modifier
     */
    public ActionBarMsg appendMsg(String msg, ChatColor primaryColor, ChatColor secondaryColor) {
        appendMsg(String.valueOf(primaryColor)+secondaryColor+msg);
        return this;
    }

    public ActionBarMsg appendMsg(String msg, ChatColor color) {
        appendMsg(color+msg);
        return this;
    }

    public ActionBarMsg appendMsg(String msg) {
        textComponent.addExtra(msg);
        return this;
    }

    public ActionBarMsg appendObj(Object obj, ChatColor primaryColor, ChatColor secondaryColor) {
        appendMsg(String.valueOf(obj), primaryColor, secondaryColor);
        return this;
    }

    public ActionBarMsg appendObj(Object obj, ChatColor color) {
        appendMsg(String.valueOf(obj), color);
        return this;
    }

}
