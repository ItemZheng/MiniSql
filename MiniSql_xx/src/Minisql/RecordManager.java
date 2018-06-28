package Minisql;
import Minisql.Structure.*;
/*this class is used for tablename.record*/
public class RecordManager {
	public static void Create_Table(Table tb)
	{
		try {
			String filename= tb.table_name+".table";
			//Buffer Manager to create a block...
		}catch (Exception e) {
			System.err.println(e.getMessage());
			System.err.println("Failure in record manager to create a table");
		}
		System.out.println("create table successfully in record manager");
	}
}
