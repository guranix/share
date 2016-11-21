package dbService.dao;

import dbService.dataSets.LogsDataSet;
import dbService.executor.Executor;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by guran on 11/20/16.
 */
public class LogsDAO {

    private Executor executor;

    public LogsDAO(Connection connection) {
        this.executor = new Executor(connection);
    }

    public List<LogsDataSet> getAllLogs () throws SQLException, ParseException {
        SimpleDateFormat convertToDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<LogsDataSet> result = new ArrayList<>();
        return executor.execQuery("select date_time, user_name, custom_message from logs", resultSet -> {
            while (resultSet.next()) {
                result.add(new LogsDataSet (convertToDate.parse(resultSet.getString(1)), resultSet.getString(2), resultSet.getString(3)));
            }
            return result;
        });
    }

    public int getGroupedByUsernameRecordsCount (String username) throws SQLException, ParseException {
        return  executor.execQuery("SELECT COUNT(id) FROM logs WHERE user_name = '" + username + "' GROUP BY user_name", result -> {
            result.next();
            return result.getInt(1);
        });
    }

    public int getGroupedByHourRecordsCount (String day) throws SQLException, ParseException {
        return  executor.execQuery("SELECT COUNT(*) FROM `logs` WHERE HOUR(date_time) = " + day, result -> {
            result.next();
            return result.getInt(1);
        });
    }

    public void insertLog(String datetime, String username, String customMessage) throws SQLException {
//        System.out.println(datetime.toString());
        executor.execUpdate("insert into logs (date_time, user_name, custom_message) values ('" +
                datetime.toString() + "', '" +username+ "', '" + customMessage + "')");
    }

    public void createTable() throws SQLException {
        executor.execUpdate("create table if not exists logs (id bigint auto_increment, date_time datetime, " +
                "user_name varchar(256), custom_message varchar(256), primary key (id))");
    }

    public void dropTable() throws SQLException {
        executor.execUpdate("drop table logs");
    }
}