package com.dms.authentication;

import com.dms.authentication.interfaces.ISignUpDAO;
import com.dms.user.interfaces.IUserModel;

import connection.SqlConnectionImpl;
import connection.interfaces.ISqlConnection;
import org.apache.commons.lang3.StringUtils;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SignUpDAO implements ISignUpDAO {
    private static final String STUDENT_ROLE = "student";
    private static final String FACULTY_ROLE = "faculty";
    private static final String DEFAULT_STATUS = "pending";
    private static final String USER_NAME_COLUMN = "username";
    private final ISqlConnection connectionManager;

    public SignUpDAO() {
        connectionManager = new SqlConnectionImpl();
    }

    public Boolean isUserValid(IUserModel user) throws SQLException {
        boolean isUserExist = false;
        try {
            String userCheckNameQuery = "SELECT username FROM User where userName='" + user.getUsername() + "'";
            connectionManager.executeRead(userCheckNameQuery);
            ResultSet result = connectionManager.executeResult(userCheckNameQuery);
            while (result.next()) {
                String existingUserName = result.getString(USER_NAME_COLUMN);
                if (StringUtils.equals(result.getString(1), existingUserName)) {
                    isUserExist = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connectionManager.closeConnection();
        }
        return isUserExist;
    }

    public Boolean insertUser(IUserModel user) throws SQLException {
            try {
                user.setStatus(DEFAULT_STATUS);
                String insertQuery = "INSERT INTO User(userName, password, email, firstName, lastName, dob, securityAnswer, role, status) VALUES('" +
                        user.getUsername() + "','" + user.getPassword() + "','" + user.getEmail() + "','" + user.getFirstName() + "','" + user.getLastName() + "','" +
                        user.getDob() + "','" + user.getSecurityAnswer() + "','" + user.getRole() + "','" + user.getStatus() + "')";
                connectionManager.executeWriteForAutoGeneratedKey(insertQuery);
                ResultSet result = connectionManager.executeResultForAutoGeneratedKey(insertQuery);
                if (result.next()) {
                    result.getInt(1);
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                connectionManager.closeConnection();
            }
        return false;
    }

    public Boolean insertInfoInSeparateTableBasedOnUserRole(IUserModel user) throws SQLException {
        try {
            String fetchUserRole = "SELECT role,userId from User WHERE username='" + user.getUsername() + "'";
            connectionManager.executeRead(fetchUserRole);
            ResultSet resultSet = connectionManager.executeResult(fetchUserRole);
            while (resultSet.next()) {
                if (StringUtils.equals(resultSet.getString(1), STUDENT_ROLE)) {
                    int userId = resultSet.getInt(2);
                    String insertStudent = "INSERT INTO Student(userName, userId, email) VALUES ('" + user.getUsername() + "'," + userId + ",'" + user.getEmail() + "')";
                    connectionManager.executeWrite(insertStudent);
                } else if (StringUtils.equals(resultSet.getString(1), FACULTY_ROLE)) {
                    int userId = resultSet.getInt(2);
                    String insertFaculty = "INSERT INTO Faculty(userName, userId, email) VALUES ('" + user.getUsername() + "'," + userId + ",'" + user.getEmail() + "')";
                    connectionManager.executeWrite(insertFaculty);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connectionManager.closeConnection();
        }
        return true;
    }
}


