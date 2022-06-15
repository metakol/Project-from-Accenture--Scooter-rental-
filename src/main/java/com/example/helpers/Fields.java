package com.example.helpers;

import javafx.scene.control.TextInputControl;

public class Fields {
    public static boolean fieldsAreEmpty(TextInputControl... fields) {
        for (TextInputControl field : fields) {
            if (field.getText().equals("")) return true;
        }
        return false;
    }

    public static void clear(TextInputControl... fields) {
        for (TextInputControl field : fields) {
            field.clear();
        }
    }

    public static boolean containsOnlyDigits(TextInputControl... fields) {
        for (TextInputControl field : fields) {
            for (char c : field.getText().toCharArray()) {
                if (!Character.isDigit(c)) return false;
            }
        }
        return true;
    }

}
