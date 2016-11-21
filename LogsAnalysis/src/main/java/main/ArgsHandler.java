package main;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Created by guran on 11/20/16.
 */
public class ArgsHandler {

    private String[] args;
    private String username = null;
    private Date after = null;
    private Date before = null;
    private String customMessageRegex = null;
    private String groupByUsername = null;
    private String groupByHour = null;
    private int numberOfThreads = 1;
    private String outputFilePath = null;
    private Properties prop;

    public ArgsHandler(String[] args) throws IOException {

        this.args = args;

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader.getResourceAsStream("config.properties");
        this.prop = new Properties();
        try {
            prop.load(stream);
        } catch (IOException e) {
            System.out.println("Can't load config.properties file.");
        }

    }

    public Properties getProp() {
        return prop;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAfter(Date after) {
        this.after = after;
    }

    public void setBefore(Date before) {
        this.before = before;
    }

    public void setCustomMessageRegex(String customMessageRegex) {
        this.customMessageRegex = customMessageRegex;
    }

    public void setGroupByUsername(String groupByUsername) {
        this.groupByUsername = groupByUsername;
    }

    public void setGroupByHour(String groupByHour) {
        this.groupByHour = groupByHour;
    }

    public void setNumberOfThreads(int numberOfThreads) {
        this.numberOfThreads = numberOfThreads;
    }

    public void setOutputFilePath(String outputFilePath) {
        this.outputFilePath = outputFilePath;
    }

    public String getUsername() {
        return username;
    }

    public Date getAfter() {
        return after;
    }

    public Date getBefore() {
        return before;
    }

    public String getCustomMessageRegex() {
        return customMessageRegex;
    }

    public String getGroupByUsername() {
        return groupByUsername;
    }

    public String getGroupByHour() {
        return groupByHour;
    }

    public int getNumberOfThreads() {
        return numberOfThreads;
    }

    public String getOutputFilePath() {
        return outputFilePath;
    }

    public boolean processArgs() throws ParseException {

        String firstDateArg = null;
        String firstTimeArg = null;
        String secondDateArg = null;
        String secondTimeArg = null;

        for (int i = 0; i < args.length; i++) {
            args[i] = args[i].trim();

            if (args[i].trim().matches(prop.getProperty("usernameRegex")) && getUsername() == null) {
                setUsername(args[i]);
            } else if (args[i].matches(prop.getProperty("dateRegex")) && firstDateArg == null) {
                firstDateArg = args[i];
            } else if (args[i].matches(prop.getProperty("dateRegex")) && firstDateArg != null && secondDateArg == null) {
                secondDateArg = args[i];
            } else if (args[i].matches(prop.getProperty("timeRegex")) && firstTimeArg == null && secondDateArg == null) {
                firstTimeArg = args[i];
            } else if ((args[i].matches(prop.getProperty("timeRegex"))) &&
                    ((firstTimeArg != null && secondTimeArg == null) || (firstDateArg != null && secondDateArg != null && secondTimeArg == null))) {
                secondTimeArg = args[i];
            } else if (args[i].matches(prop.getProperty("numberOfTrheadsRegex")) && getNumberOfThreads() == 1) {
                setNumberOfThreads(Integer.parseInt(args[i]));
            } else if (args[i].startsWith("^")) {
                setCustomMessageRegex(args[i]);
            } else if (args[i].endsWith(prop.getProperty("logFileExtention")) || (!args[i].startsWith("^") && args[i].contains(File.separator))) {
                setOutputFilePath(args[i]);
            } else if (args[i].startsWith("-u=")) {
                setGroupByUsername(args[i].replace("-u=", ""));
            } else if (args[i].startsWith("-h=")) {
                setGroupByHour(args[i].replace("-h=", ""));
            } else {
                System.out.println("Your argument :" + args[i] + " is wrong !");
                return false;
            }
        }


        SimpleDateFormat convertToDate = new SimpleDateFormat(prop.getProperty("dateFormat"));
        Date firstDate = null;
        Date secondDate = null;

        try {
            if (firstDateArg != null && firstTimeArg != null) {
                firstDate = convertToDate.parse(firstDateArg + " " + firstTimeArg);
            } else if (firstDateArg != null && firstTimeArg == null) {
                firstDate = convertToDate.parse(firstDateArg + " 00:00:00");
            }

            if (secondDateArg != null && secondTimeArg != null) {
                secondDate = convertToDate.parse(secondDateArg + " " + secondTimeArg);
            } else if (secondDateArg != null && secondTimeArg == null) {
                secondDate = convertToDate.parse(secondDateArg + " 00:00:00");
            }
        } catch (ParseException e) {
            System.out.println("Wrong date or time input.");
            return false;
        }


        if (firstDate!=null && secondDate!=null) {
            if (firstDate.compareTo(secondDate) == -1) {
                setAfter(firstDate);
                setBefore(secondDate);
            } else {
                setBefore(firstDate);
                setAfter(secondDate);
            }
        } else {
            setAfter(firstDate);
        }

        if (getUsername() == null && getAfter() == null && getBefore() == null && getCustomMessageRegex() == null) {
            System.out.println("At least one filter parameter (username,  time period or pattern for custom message) should be specified.");
            System.out.println("Examples: JohnSmith 2014-12-25 23:57:00 2016-01-30 14:00:12 ^[1-9]\\d*$");
            return false;
        } else if (getGroupByUsername() == null && getGroupByHour() == null) {
            System.out.println("At least one grouping parameter (username or hour unit) should be specified.");
            System.out.println("Examples: -u=JohnSmith -h=09");
            return false;
        }

        if (getOutputFilePath()  == null) {
            setOutputFilePath(File.separator + prop.getProperty("defaultFileName") + ".txt");
        } else if (new File(getOutputFilePath()).isDirectory()) {
            setOutputFilePath(getOutputFilePath() + File.separator + prop.getProperty("defaultFileName") + ".txt");
        }

        return true;
    }

}
