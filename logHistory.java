package helper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;


public class logHistory {

    private static final String loginAttempts = "login_history.txt";


    public static void auditLogin(String userName, Boolean successBool) throws IOException {
        try {

            BufferedWriter loghistory = new BufferedWriter(new FileWriter(loginAttempts, true));
            loghistory.append(ZonedDateTime.now(ZoneOffset.UTC).toString() + " UTC-LOGIN ATTEMPT-USERNAME: " + userName +
                    " LOGIN SUCCESSFUL: " + successBool.toString() + "\n");
            loghistory.flush();
            loghistory.close();
        }
        catch (IOException error) {
            error.printStackTrace();
        }

    }
}
