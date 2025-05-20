package org.example;

import java.awt.*;
import java.util.Map;

public class JulyUtils {
    public static final Map<String, String> TYPE_JP_TO_EN = Map.of(
            "漢字", "kanji",
            "文法", "grammar",
            "語彙", "vocabulary"
    );

    public static final Map<String, String> TYPE_EN_TO_JP = Map.of(
            "kanji", "漢字",
            "grammar", "文法",
            "vocabulary", "語彙"
    );
    public static final Font FONT_VI = new Font("Segoe UI", Font.PLAIN, 16);
    public static final Font FONT_JP = new Font("Meiryo", Font.PLAIN, 16);


}
