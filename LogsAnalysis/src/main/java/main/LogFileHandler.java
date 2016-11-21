package main;

import dbService.DBException;
import dbService.DBService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by guran on 11/20/16.
 */
public class LogFileHandler implements Runnable {

    private File file;
    private DBService dbService;
    private ArgsHandler argsHandler;

    public LogFileHandler(File file, DBService dbService, ArgsHandler argsHandler) {
        this.file = file;
        this.dbService = dbService;
        this.argsHandler = argsHandler;
    }

    private boolean after(Date date1, Date date2) {
        return date1.getTime() >= date2.getTime();
    }
    private boolean before(Date date1, Date date2) {
        return date1.getTime() <= date2.getTime();
    }

    @Override
    public void run() {

        String argUsername = argsHandler.getUsername();
        Date argAfter = argsHandler.getAfter();
        Date argBefore = argsHandler.getBefore();
        String customMessageRegex = argsHandler.getCustomMessageRegex();

//        System.out.println("Thread " + Thread.currentThread().getName());

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;

            DateFormat convertDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            while ((line = reader.readLine()) != null) {

                String[] strings = line.split("(\\h)");

                String username = strings[2];
                String customMessage = strings[3];
                String fullSQLDatetime = strings[0] + " " + strings[1];

                if ((argUsername==null || username.equals(argUsername))
                        && (customMessageRegex==null || customMessage.matches(customMessageRegex))){

                    java.util.Date dateTime = convertDate.parse(fullSQLDatetime);

                    if (argAfter != null && argBefore != null) {
                        if (after(dateTime, argAfter) && before(dateTime, argBefore)) {

                            dbService.addLog(convertDate.format(dateTime), username, customMessage);
                        }
                    }
                    if (argAfter == null && argBefore != null) {
                        if (before(dateTime, argBefore)) {
                            dbService.addLog(convertDate.format(dateTime), username, customMessage);
                        }
                    }
                    if ((argAfter != null && argBefore == null)) {
                        if (after(dateTime, argAfter)) {
                            dbService.addLog(convertDate.format(dateTime), username, customMessage);
                        }
                    }
                    if (argAfter == null && argBefore == null) {
                        dbService.addLog(convertDate.format(dateTime), username, customMessage);
                    }
                }
            }
        } catch (IOException | ParseException | DBException e) {
            e.printStackTrace();
        }

    }
}