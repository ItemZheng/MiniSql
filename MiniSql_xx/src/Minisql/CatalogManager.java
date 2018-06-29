package Minisql;
import java.util.LinkedList;
import Minisql.Structure.*;
/*this class is used for table_catelog and index_catelog*/

public class CatalogManager {
	protected static LinkedList<Index> indexList= new LinkedList<Index>();
	protected static LinkedList<Table> tableList= new LinkedList<Table>();	
	public static void UpdateIndexCatalog() {
		
	}
	
	public static void UpdateTableCatalog() {
		
	}
	
	public static void ReadIndexCatalog() {
		
	}
	public static void ReadTableCatalog() {
		
	}
	
	
	public static void Create_Table(Table tb)
	{
		tableList.add(tb);
	}
	
	public static void Create_Index(Index inx)
	{
		indexList.add(inx);

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
		//FILE 
		//table.INDEXeS
		//index LIST
		
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
			dropIndex(tb1.indexes.get(i));//list /table.indexes
			IndexManager.Drop_Index(tb1.indexes.get(i)); //file
		}
		
		tableList.remove(tb1);
		
		
	}

}
