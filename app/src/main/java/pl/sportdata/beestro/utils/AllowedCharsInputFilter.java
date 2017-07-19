package pl.sportdata.beestro.utils;

import android.text.InputFilter;
import android.text.Spanned;

public class AllowedCharsInputFilter implements InputFilter {

    private char[] allowedChars;

    public AllowedCharsInputFilter(char[] allowedChars) {
        this.allowedChars = allowedChars;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        for (int i = start; i < end; i++) {
            char character = source.charAt(i);
            for (char allowedCharacter : allowedChars) {
                if (allowedCharacter == character) {
                    return null;
                }
            }
        }
        return "";
    }
}
