package Minisql;

import Minisql.Structure.*;

public class IndexManager {
	public static void Create_Index(Table tb, Index inx)
	{
	
		try {
			String filename= inx.index_name+".index";
			//build a B+ tree
		}catch (Exception e) {
			System.err.println(e.getMessage());
			System.err.println("Failure in index manager to create a index");
		}finally {
			//bufferManage.saveAllBlock();
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

}
