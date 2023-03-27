package home.serg.chatik.util;

import home.serg.chatik.exception.ValidationException;

public class HTMLFilter {
    public static String filter(String s) {
        if (s == null || s.length() == 0 || s.isBlank()) throw new ValidationException("String is empty");
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '<') {
                out.append("&lt;");
            } else if (c == '>') {
                out.append("&gt;");
            } else if (c == '&') {
                out.append("&amp;");
            } else {
                out.append(c);
            }
        }
        return out.toString();
    }
}
