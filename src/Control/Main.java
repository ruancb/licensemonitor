package Control;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;


/**
 * Main class. Instances a GUI. 
 * @author Ruan Costa Borges
 * @since 1.0
 * @version 1.2
 *
 */
public class Main {

	private static  Processa run;

	/**
	 * Starts the processing creating the GUI.
	 * @param args
	 * Default parameter of Java void main
	 * @throws IOException 
	 * Returns a error with read/write access.
	 * @throws ParseException 
	 * Returns a error with date conversion.
	 * @throws SQLException 
	 * Returns a error with database, for instance a SQL statement error.
	 */
	public static void main(String[] args) throws IOException, ParseException, SQLException {
		run = new Processa();
		
	}

}
