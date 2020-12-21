package main.util;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


/**
 * <h1>Common</h1>
 * Common class is used for methods that show up in many different controllers throughout the project.
 *
 * @author Lee Rhodes
 */
public class Common {
    /**
     * exitWindow() closes the window from the passed-in parameter's scene.
     * If this window is the starting/main window, it will end the program as well.
     *
     * @param btn the button that the method uses to find which window to close
     */
    public static void exitWindow(Button btn) {
        Stage stage = (Stage) btn.getScene().getWindow();
        stage.close();
    }

    /**
     * handleException() investigates a given exception by printing helpful information that the developer
     * can use to try and debug the error.
     *
     * @param e the exception that the method uses to print debugging information
     */
    public static void handleException(Exception e) {
        System.out.println(
                "Error from method "
                + e.getStackTrace()[0].getMethodName()
                + " in "
                + e.getClass()
                + " at line "
                + e.getStackTrace()[0].getLineNumber()
                + "."
        );
        System.out.println("Trace from getCause()");
        e.getCause().printStackTrace();
        System.out.println("Trace from exception:");
        e.printStackTrace();
    }

    /**
     * setCharLimit() adds a listener to a TextField that will limit how many characters the user can enter.
     * The max length passed in should be equal to the maximum length allowed by the mySQL field.
     * <p>
     * LAMBDA EXPRESSION REASONING: A lambda is used here in order to add a listener to the TextField that can
     * make changes with the field using the old value depending on on the new value. The new value is checked
     * to see if changes are necessary, and if so, the old value can be used to replace the text to make the field
     * valid again.
     *
     * @param field the TextField that the listener will be applied to
     * @param maxLen the maximum length that the field will allow
     */
    public static void setCharLimit(TextField field, int maxLen) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && newValue.length() > maxLen) {
                field.setText(oldValue);
                field.positionCaret(oldValue.length());
            }
        });
    }

    /**
     * setCharLimit() adds a listener to a TextArea that will limit how many characters the user can enter.
     * The max length passed in should be equal to the maximum length allowed by the mySQL field.
     *
     * @param field the TextArea that the listener will be applied to
     * @param maxLen the maximum length that the field will allow
     */
    public static void setCharLimit(TextArea field, int maxLen) {
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && newValue.length() > maxLen) {
                field.setText(oldValue);
                field.positionCaret(oldValue.length());
            }
        });
    }

    /**
     * scaleButton() makes sure the button that is displayed to the user will be wide enough to fit the text inside of
     * it. This is important since English and French text could be vastly different in size.
     *
     * @param button the button to widen or shorten
     * @param text the text to set inside the button (English or French)
     */
    public static void scaleButton(Button button, String text) {
        button.setText(text);
        button.getParent().layout();
        button.getParent().applyCss();
    }

}
