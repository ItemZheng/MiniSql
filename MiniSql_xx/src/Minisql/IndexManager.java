package Minisql;

import Minisql.BufferManage.BufferOperator;
import Minisql.Structure.*;

public class IndexManager {
	public static void Create_Index(Table tb, Index inx)
	{
		try {
			String filename= inx.index_name+".index";
			BufferOperator fp = new BufferOperator(filename); //create the file
			fp.close();
			//build a B+ tree
		}catch (Exception e) {
			System.err.println(e.getMessage());
			System.err.println("Failure in index manager to create a index");
		}
		System.out.println("create index successfully in index manager");
		
	}
	public static void Drop_Index(String IndexName) {
		try {
			String filename= IndexName+".index";
			BufferManage.dropFile(filename);
			
		}catch (Exception e) {
			System.err.println(e.getMessage());
			System.err.println("Failure in index manager to drop a index");
		}
		System.out.println("drop index successfully in index manager");
		
	}
	public static void InsertKey(Index inx, byte[]value) {
		
	}
	
	public static void DeleteKey(Index inx, byte[] value) {
		
	}
	
	public static void Delete_Index(Table tb, Index inx)
	{
		try {
			IndexManager.Drop_Index(inx.index_name); //delete the .index file
			IndexManager.Create_Index(tb, inx); //create new .index file and b+ tree. but first make sure there is no record at this time...
		}catch (Exception e) {
			System.err.println(e.getMessage());
			System.err.println("Failure in index manager to delete index information");
		}
		System.out.println("delete index information successfully in index manager");
	}

}
