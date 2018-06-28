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
		//???? no need to use a list???
	
		/*write the following information to a file:
		tb.table_name
		*/
	}
	
	public static void Create_Index(Index inx)
	{
		indexList.add(inx);
		
		//???? no need to use a list???

		/*write the following information to a file:
		inx.index_name
		inx.table_name
		inx.attribute_name*/
	}
	
	public static Table getTable(String tableName) {
		//traverse the table list
		for (Table table : tableList) { 
			if (table.table_name.equals(tableName)) {
				return table;
			}
		}
		return null;
	}
	
	public static Index getIndex(String indexName) {
		//traverse the table list
		for (Index index : indexList) { 
			if (index.index_name.equals(indexName)) {
				return index;
			}
		}
		return null;
	}
	
	public static void dropIndex(String indexName) {
		Index inx = getIndex(indexName);//Already judge in API, it will not be NULL
		indexList.remove(inx);
		Table tb1= getTable(inx.table_name);
		if(tb1==null) System.out.println("error when drop index, because its table not exist!");
		else {
			tb1.indexes.remove(indexName);
		}
	}
	
	public static void dropTable(String tableName) {
		Table tb1= getTable(tableName); //Already judge in API, it will not be NULL
		// must delete the index from index list before delete the table from table list !!!
		for(int i=0;i<tb1.indexes.size();i++)
		{
			dropIndex(tb1.indexes.get(i));
		}
		
		tableList.remove(tb1);
		
		
	}
	
	/*
	public Table tableRead(String tableName);
	public void tableWrite(String tableName);
	*/	
}
