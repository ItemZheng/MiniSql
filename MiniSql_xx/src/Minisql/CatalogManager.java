package Minisql;
import java.util.LinkedList;

import Minisql.Structure.*;
/*this class is used for table_catelog and index_catelog*/

public class CatalogManager {
	protected static LinkedList<Index> indexList= new LinkedList<Index>();
	protected static LinkedList<Table> tableList= new LinkedList<Table>();
	public static  Boolean isExistTable(String tableName)
	{
		return false;
	}
	
	public static  Boolean isExistIndex(String indexName)
	{
		return false;
	}
	
	public static void Create_Table(Table tb)
	{
		tableList.add(tb);
	}
	
	/*
	public Table tableRead(String tableName);
	public void tableWrite(String tableName);
	*/	
}
