package dbService.executor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

/**
 * Created by guran on 11/20/16.
 */
public interface ResultHandler<T> {
    T handle(ResultSet resultSet) throws SQLException, ParseException;
}
