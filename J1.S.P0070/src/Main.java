
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class Main {

    private final Scanner in;
    private final ResourceBundle vietnameseBundle;
    private final ResourceBundle englishBundle;

    private static final String ACCOUNT_NUMBER_VALID = "^\\d{10}$";
    private static final char[] CHARACTERS = "1AaBbCc2DdEeFf3GgHhIiJjKkLl4MmNnOo5PpQqRrSsTt6UuVvUuWw8XxYyZz9".toCharArray();
    private static final int CAPTCHA_LENGTH = 6;

    public Main() {
        this.in = new Scanner(System.in);
        this.vietnameseBundle = ResourceBundle.getBundle("Language/Language_vi", new Locale("vi"));
        this.englishBundle = ResourceBundle.getBundle("Language/Language_en", Locale.ENGLISH);
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.display();
    }

    private void display() {
        boolean running = true;
        while (running) {
            System.out.println("1. Vietnamese");
            System.out.println("2. English");
            System.out.println("3. Exit");
            System.out.print("Please choose one option: ");
            int choice = checkInputIntLimit(1, 3, englishBundle);
            switch (choice) {
                case 1:
                    login(vietnameseBundle);
                    break;
                case 2:
                    login(englishBundle);
                    break;
                case 3:
                    running = false;
                    in.close();
                    break;
                default:
                    System.err.println("Invalid choice. Please choose again.");
                    break;
            }
        }
        System.out.println("Exiting program...");
    }

    private void login(ResourceBundle language) {
        displayMessage(language, "enterAccountNumber");
        int accountNumber = checkInputAccount(language);
        displayMessage(language, "enterPassword");
        String password = checkInputPassword(language);

        boolean captchaValid = false;
        while (!captchaValid) {
            String captchaGenerated = generateCaptchaText();
            captchaValid = checkInputCaptcha(captchaGenerated, language);
            if (captchaValid) {
                displayMessage(language, "loginSuccess");
                System.out.println();
            } else {
                displayMessage(language, "errCaptchaIncorrect");
                System.out.println();
            }
        }
    }

    private int checkInputIntLimit(int min, int max, ResourceBundle language) {
        while (true) {
            try {
                int result = Integer.parseInt(in.nextLine().trim());
                if (result >= min && result <= max) {
                    return result;
                }
            } catch (NumberFormatException ignored) {
            }
            displayMessage(language, "errorCheckInputIntLimit");
            System.out.println();
        }
    }

    private String checkInputString(ResourceBundle language) {
        while (true) {
            String result = in.nextLine().trim();
            if (!result.isEmpty()) {
                return result;
            }
            displayMessage(language, "errCheckInputIntLimit");
            System.out.println();
        }
    }

    private int checkInputAccount(ResourceBundle language) {
        while (true) {
            String result = in.nextLine().trim();
            if (result.matches(ACCOUNT_NUMBER_VALID)) {
                return Integer.parseInt(result);
            }
            displayMessage(language, "errCheckInputAccount");
            System.out.println();
        }
    }

    private String checkInputPassword(ResourceBundle language) {
        while (true) {
            String password = checkInputString(language);
            if (isValidPassword(password, language)) {
                return password;
            }
        }
    }

    private boolean isValidPassword(String password, ResourceBundle language) {
        int length = password.length();
        if (length < 8 || length > 31) {
            displayMessage(language, "errCheckLengthPassword");
            System.out.println();
            return false;
        }

        boolean hasDigit = false;
        boolean hasLetter = false;
        for (char ch : password.toCharArray()) {
            if (Character.isDigit(ch)) {
                hasDigit = true;
            }
            if (Character.isLetter(ch)) {
                hasLetter = true;
            }
            if (hasDigit && hasLetter) {
                break;
            }
        }

        if (!hasDigit || !hasLetter) {
            displayMessage(language, "errCheckAlphanumericPassword");
            System.out.println();
            return false;
        }
        return true;
    }

    private boolean checkInputCaptcha(String captchaGenerated, ResourceBundle language) {
        System.out.println(captchaGenerated);
        displayMessage(language, "enterCaptcha");
        String captchaInput = checkInputString(language);
        return captchaGenerated.equals(captchaInput);
    }

    private String generateCaptchaText() {
        StringBuilder captcha = new StringBuilder(CAPTCHA_LENGTH);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            captcha.append(CHARACTERS[random.nextInt(CHARACTERS.length)]);
        }
        return captcha.toString();
    }

    private void displayMessage(ResourceBundle bundle, String key) {
        String value = bundle.getString(key);
        System.out.print(value);
    }
}
