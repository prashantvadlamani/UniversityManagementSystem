package com.dms.grade;

import com.dms.grade.interfaces.IPostGrades;
import connection.interfaces.ISqlConnection;
import connection.SqlConnectionImpl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostGradesDAO implements IPostGrades {
    private final ISqlConnection connectionManager;
    public PostGradesDAO() {
        connectionManager = new SqlConnectionImpl();
    }

    public List<String> getCourseNames(String userName) throws SQLException {
        List<String> courseNames = new ArrayList<>();
        try {
            String sql = "select courseName from Course\n" +
                    "inner join Faculty on Faculty.facultyId = Course.facultyId\n" +
                    "inner join User on User.userId = Faculty.userId\n" +
                    "where User.userName='" + userName + "' ";
            connectionManager.executeRead(sql);
            ResultSet result = connectionManager.executeResult(sql);
            while (result.next()) {
                courseNames.add(result.getString(1));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        } finally{
            connectionManager.closeConnection();
        }
        return courseNames;
    }

    public Map<Integer, List<Integer>> getStudentDetails(String userName, String courseName) throws SQLException {
        Map<Integer, List<Integer>> studentDetails = new HashMap<Integer,List<Integer>>();
        try {
            String sql = "select CourseRegistrationDetails.studentId, CourseRegistrationDetails.courseId , grade from CourseRegistrationDetails \n" +
                    "                    inner join Course on CourseRegistrationDetails.courseId = Course.courseId\n" +
                    "                    inner join Faculty on Faculty.facultyId = Course.facultyId\n" +
                    "                    inner join User on User.userId = Faculty.userId\n" +
                    "                    where User.userName='" + userName + "' and Course.courseName='" + courseName + "'";

            connectionManager.executeRead(sql);
            ResultSet result = connectionManager.executeResult(sql);
            while (result.next()) {
                int studentId = result.getInt("studentId");
                int courseId = result.getInt("courseId");
                int grade = result.getInt("grade");
                List<Integer> courseGrades = new ArrayList<>();
                courseGrades.add(courseId);
                courseGrades.add(grade);
                studentDetails.put(studentId,courseGrades);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        } finally{
            connectionManager.closeConnection();
        }
        return studentDetails;
    }

    public boolean  postGrades(int marks, int studentID, int courseId) throws SQLException {
        boolean gradeUpdated = false;
        try {
            String sql = "UPDATE CourseRegistrationDetails SET CourseRegistrationDetails.grade='"+marks+"'" +
                    " WHERE CourseRegistrationDetails.studentId='"+studentID+"' and CourseRegistrationDetails.courseId='"+courseId+"' ";
            connectionManager.executeWriteForAutoGeneratedKey(sql);
            gradeUpdated = true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally{
            connectionManager.closeConnectionWithoutResultset();
        }
        return gradeUpdated;
        }
    }




