package dbService.dataSets;

import java.util.Date;

/**
 * Created by guran on 11/20/16.
 */
public class LogsDataSet {
    private Date dateTime;
    private String username;
    private String customMessage;

    public LogsDataSet(Date dateTime, String username, String customMessage) {
        this.dateTime = dateTime;
        this.username = username;
        this.customMessage = customMessage;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public String getUsername() {
        return username;
    }

    public String getCustomMessage() {
        return customMessage;
    }

    @Override
    public String toString() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateTime) + " " + username + " " + customMessage;
    }
}