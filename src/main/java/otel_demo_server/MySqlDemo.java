package otel_demo_server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;


public class MySqlDemo {
    Connection connection;
    String host = "localhost";
    int port = 3306;
    String database = "demo";
    String user = "root";
    String password = "secret";

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(MySqlDemo.class);

    public MySqlDemo() {
        log.info("Starting MySQL demo");
    }

    void dropTable(String tableName) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("DROP TABLE " + tableName);
        stmt.close();
        log.info("Dropped " + tableName);
    }

    void deleteRowWithPreparedStatement(String tableName, int paramaterCount) throws SQLException {
        StringBuilder sb = new StringBuilder("DELETE FROM " + tableName + " WHERE id IN (");
        for (int x = 0; x < paramaterCount; x++) {
            sb.append("?");
            if (x < paramaterCount - 1) {
                sb.append(",");
            }
        }
        sb.append(")");
        String sql = sb.toString();
        log.info("SQL delete 2 ='" + sql + "'");
        PreparedStatement preparedStmt = connection.prepareStatement(sql);
        for (int x = 1; x <= paramaterCount; x++) {
            preparedStmt.setInt(x, x);
        }
        preparedStmt.execute();
        preparedStmt.close();
    }

    void deleteRow(String tableName, int fieldCount) throws SQLException {
        StringBuilder sb = new StringBuilder("DELETE FROM " + tableName + " WHERE id IN (");
        for (int x = 0; x < fieldCount; x++) {
            sb.append(x);
            if (x < fieldCount - 1) {
                sb.append(",");
            }
        }
        sb.append(")");
        String sql = sb.toString();
        log.info("SQL delete ='" + sql + "'");
        Statement stmt = connection.createStatement();
        stmt.executeUpdate(sql);
        stmt.close();
        // log.info("Deleted from " + tableName);
    }

    void insert(String tableName) throws SQLException {
        String query = " insert into " + tableName + " (first, last, id) values (?, ?, ?)";
        PreparedStatement preparedStmt = connection.prepareStatement(query);
        preparedStmt.setString(1, "abc");
        preparedStmt.setString(2, "xyz");
        preparedStmt.setInt(3, 1);
        preparedStmt.execute();
        preparedStmt.close();
    }

    void createTable(String tableName) throws SQLException {
        String sql = "CREATE TABLE " + tableName + " " + "(id INTEGER not NULL, " + " first VARCHAR(255), " + " last VARCHAR(255), " + " PRIMARY KEY ( id ))";
        Statement stmt = connection.createStatement();
        stmt.executeUpdate(sql);
        stmt.close();
        log.info("Created " + tableName);
    }

    void connectUsingMySqlDriver() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, user, password);
    }

    void connectUsingMariaDbDriver() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mariadb://" + host + ":" + port + "/" + database, user, password);
    }

    void run() throws SQLException, ClassNotFoundException {
        String table1 = "table1";
        String table2 = "table2";

        // use MySQL or Maria.  Needs to match the dependency in build.gradle
        connectUsingMariaDbDriver();
        // connectUsingMySqlDriver();

        try {
            dropTable(table1);
        } catch (SQLException e) {
            log.warn("drop exception: " + e.getMessage());
        }

        try {
            dropTable(table2);
        } catch (SQLException e) {
            log.warn("drop exception: " + e.getMessage());
        }

        try {
            createTable(table1);
            createTable(table2);

            for (int x = 1; x < 9; x++) {
                log.info("x=" + x);
                deleteRow(table1, x);
                deleteRowWithPreparedStatement(table2, x);
                Thread.sleep(2340);
            }
        } catch (SQLException e) {
            log.info("Exception: " + e.getMessage());
            throw e;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                connection.close();
                log.info("Closed connection");
            } catch (Exception e) {
                // ignore
            }
        }
    }
}
