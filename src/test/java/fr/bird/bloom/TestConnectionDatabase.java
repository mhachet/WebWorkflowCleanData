/**
 * src.test
 * TestConnectionDatabase
 */
package fr.bird.bloom;

import org.junit.Ignore;
import org.junit.Test;
import fr.bird.bloom.model.ConnectionDatabase;
import fr.bird.bloom.model.DatabaseTreatment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static org.junit.Assert.fail;

/**
 * src.test
 * 
 * TestConnectionDatabase.java
 * TestConnectionDatabase
 */
@Ignore
public class TestConnectionDatabase {


    /**
     * Test method for {@link fr.bird.bloom.model.ConnectionDatabase#newConnection(java.lang.String, java.lang.String)}.
     * Test database connection
     */
    @Test
    public void testNewConnection() {
	
	
    	Statement statement = null;
		try {
			statement = ConnectionDatabase.getInstance().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DatabaseTreatment newConnection = new DatabaseTreatment(statement);
		String choiceStatement = "", sql = "";
		choiceStatement = "execute";
		sql = "SHOW COLUMNS FROM Workflow.IsoCode;";
		ArrayList<String> messages = newConnection.executeSQLcommand(choiceStatement, sql);
		if(messages.contains("Connection error")){
			fail("Connection to database failed");
		}
    }

}
