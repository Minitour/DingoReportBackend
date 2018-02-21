package mobi.newsound.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator {



    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static Pattern pattern = Pattern.compile(EMAIL_PATTERN);;
    private static Matcher matcher;

    private EmailValidator() {
    }

    /**
     * Validate hex with regular expression
     *
     * @param hex
     *            hex for validation
     * @return true valid hex, false invalid hex
     */
    public static boolean validate(final String hex) {
        return pattern.matcher(hex).matches();
    }
}