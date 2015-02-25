
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class TestClass extends ServerManager {
	TestClass() {
//		ResultSet result = null;
		try (Connection connection = this.getDataBaseConnection();
//				PreparedStatement statement = connection.prepareStatement(
//						CABIN_WITH_LOW_LOG_STATUS_STATEMENT,
//						ResultSet.TYPE_SCROLL_INSENSITIVE,
//						ResultSet.CONCUR_READ_ONLY);) {
//			statement.setInt(1, tooLowLogStatusThreshold);
//			result = statement.executeQuery();
				) {
//			while (result.next()) {
//				
//			}
			System.out.println("Worked");
		} catch (SQLException e) {

			this.processSQLException(e);

		}
	}
}
