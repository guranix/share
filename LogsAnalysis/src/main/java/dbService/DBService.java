package dbService;

import dbService.dao.LogsDAO;
import dbService.dataSets.LogsDataSet;
import org.h2.jdbcx.JdbcDataSource;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * Created by guran on 11/20/16.
 */
public class DBService {

    private final Connection connection;

    public DBService() {this.connection = getH2Connection();}
//    public DBService() { this.connection = getMysqlConnection();}

    public int getGroupedByUsernameRecordsCount (String username) throws DBException, SQLException, ParseException {
        try {
            return new LogsDAO(connection).getGroupedByUsernameRecordsCount(username);
        } catch (SQLException e) {
            throw new DBException(e);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new LogsDAO(connection).getGroupedByUsernameRecordsCount(username);
    }

    public int getGroupedByHourRecordsCount (String hour) throws DBException, SQLException, ParseException {
        try {
            return new LogsDAO(connection).getGroupedByHourRecordsCount(hour);
        } catch (SQLException e) {
            throw new DBException(e);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new LogsDAO(connection).getGroupedByUsernameRecordsCount(hour);
    }

    public List<LogsDataSet> getAll () throws DBException, SQLException, ParseException {
        try {
            return new LogsDAO(connection).getAllLogs();
        } catch (SQLException e) {
            throw new DBException(e);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new LogsDAO(connection).getAllLogs();
    }

    public void addLog(String dateTime, String username, String customMessage) throws DBException {
        try {
            connection.setAutoCommit(false);
            LogsDAO dao = new LogsDAO(connection);
            dao.createTable();
            dao.insertLog(dateTime, username, customMessage);
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ignore) {
            }
            throw new DBException(e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException ignore) {
            }
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void cleanUp() throws DBException {
        LogsDAO dao = new LogsDAO(connection);
        try {
            dao.dropTable();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void printConnectInfo() {
        try {
            System.out.println("DB name: " + connection.getMetaData().getDatabaseProductName());
            System.out.println("DB version: " + connection.getMetaData().getDatabaseProductVersion());
            System.out.println("Driver: " + connection.getMetaData().getDriverName());
            System.out.println("Autocommit: " + connection.getAutoCommit());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public static Connection getMysqlConnection() {
        try {
            DriverManager.registerDriver((Driver) Class.forName("com.mysql.jdbc.Driver").newInstance());

            StringBuilder url = new StringBuilder();

            url.
                    append("jdbc:mysql://").            //db type
                    append("localhost:").               //host name
                    append("3306/").                    //port
                    append("logs_app?").                //db name
                    append("user=root&").               //login
                    append("password=mpLgr55i");       //password

            System.out.println("URL: " + url + "\n");

            return DriverManager.getConnection(url.toString());
        } catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("UnusedDeclaration")
    public static Connection getH2Connection() {
        try {
            String url = "jdbc:h2:./h2db";
            String name = "guran";
            String pass = "guran";

            JdbcDataSource ds = new JdbcDataSource();
            ds.setURL(url);
            ds.setUser(name);
            ds.setPassword(pass);

            return DriverManager.getConnection(url, name, pass);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
