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
	public static void Drop_Table(String tableName)
	{
		try {
			String filename= tableName+".table";
			//Buffer Manager to create a block...
		}catch (Exception e) {
			System.err.println(e.getMessage());
			System.err.println("Failure in record manager to drop a table");
		}
		System.out.println("drop table successfully in record manager");
	}
}
