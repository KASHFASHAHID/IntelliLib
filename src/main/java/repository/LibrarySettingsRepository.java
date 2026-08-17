package repository;

import config.DatabaseConnection;
import model.LibrarySettings;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class LibrarySettingsRepository {

    public LibrarySettings getSettings() {

        Map<String, String> values = new HashMap<>();

        String sql = """
                SELECT setting_name, setting_value
                FROM library_settings
                """;
try (
                Connection connection =
                        DatabaseConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet resultSet =
                        statement.executeQuery()
        ) {

            while (resultSet.next()) {

                values.put(
                        resultSet.getString("setting_name"),
                        resultSet.getString("setting_value")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return new LibrarySettings(

                values.get("exam_mode")
                        .equalsIgnoreCase("ON"),

                Integer.parseInt(
                        values.get("student_normal_days")
                ),

                Integer.parseInt(
                        values.get("student_exam_days")
                ),

                Integer.parseInt(
                        values.get("teacher_days")
                ),

                Integer.parseInt(
                        values.get("student_max_books")
                ),

                Integer.parseInt(
                        values.get("teacher_max_books")
                ),

                Integer.parseInt(
                        values.get("reservation_pickup_days")
                ),

                new BigDecimal(
                        values.get("fine_per_day")
                )
        );
    
        }
        public boolean updateSetting(
        String settingName,
        String settingValue
) {

    String sql = """
            UPDATE library_settings
            SET setting_value = ?
            WHERE setting_name = ?
            """;

    try (
            Connection connection =
                    DatabaseConnection.getConnection();

            PreparedStatement statement =
                    connection.prepareStatement(sql)
    ) {

        statement.setString(1, settingValue);
        statement.setString(2, settingName);

        return statement.executeUpdate() == 1;

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}
public boolean saveSettings(
        LibrarySettings settings
) {

    String sql = """
            UPDATE library_settings
            SET setting_value = ?
            WHERE setting_name = ?
            """;

    try (Connection connection =
                 DatabaseConnection.getConnection()) {

        connection.setAutoCommit(false);

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            saveValue(
                    statement,
                    "exam_mode",
                    settings.isExamMode() ? "ON" : "OFF"
            );

            saveValue(
                    statement,
                    "student_normal_days",
                    String.valueOf(
                            settings.getStudentNormalDays()
                    )
            );

            saveValue(
                    statement,
                    "student_exam_days",
                    String.valueOf(
                            settings.getStudentExamDays()
                    )
            );

            saveValue(
                    statement,
                    "teacher_days",
                    String.valueOf(
                            settings.getTeacherDays()
                    )
            );
            saveValue(
                    statement,
                    "student_max_books",
                    String.valueOf(
                            settings.getStudentMaxBooks()
                    )
            );

            saveValue(
                    statement,
                    "teacher_max_books",
                    String.valueOf(
                            settings.getTeacherMaxBooks()
                    )
            );

            saveValue(
                    statement,
                    "reservation_pickup_days",
                    String.valueOf(
                            settings.getReservationPickupDays()
                    )
            );

            saveValue(
                    statement,
                    "fine_per_day",
                    settings.getFinePerDay().toPlainString()
            );

            connection.commit();
            return true;

        } catch (Exception e) {

            connection.rollback();
            e.printStackTrace();
            return false;
        }

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

private void saveValue(
        PreparedStatement statement,
        String settingName,
        String settingValue
) throws Exception {

    statement.setString(1, settingValue);
    statement.setString(2, settingName);
    statement.executeUpdate();
}
}