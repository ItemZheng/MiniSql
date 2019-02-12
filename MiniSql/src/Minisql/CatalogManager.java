package Minisql;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import Minisql.Structure.*;


/*this class is used for table_catelog and index_catelog*/


public class CatalogManager {
	protected static LinkedList<Index> indexList= new LinkedList<Index>();
	protected static LinkedList<Table> tableList= new LinkedList<Table>();
	
	
	public static void ReadCatalog() {
		String filename= "table.catalog";
		File file = new File(filename);
		if (!file.exists()) {
			try {
				file.createNewFile();
				BufferedWriter writer = new BufferedWriter(new FileWriter(file));
				String str1 = "-1\n-1\n";
				writer.write(str1);
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		BufferedReader br;
		try {
			br= new BufferedReader(new FileReader(filename));
			Table table;
			Attribute attr;
			
			String data= br.readLine();
			while(!data.equals("-1")) {
				table =new Table();
				table.table_name=data;
				data= br.readLine();
				table.RecordNum=Integer.parseInt(data);
				data= br.readLine();
				table.attrNum=Integer.parseInt(data);
				data= br.readLine();
				table.oneRecord_length=Integer.parseInt(data);
				table.attributes= new ArrayList<Attribute>();
				int offsetlength=0;
				data= br.readLine();
				while(!data.equals("-1")) {
					attr= new Attribute();
					attr.name= data;
					attr.offset= offsetlength;
					data= br.readLine();
					attr.type= Integer.parseInt(data);
					data= br.readLine();
					attr.length= Integer.parseInt(data);
					data= br.readLine();
					attr.isPrimarykey= data.toString().equals("true")?true:false;
					data= br.readLine();
					attr.isUnique= data.toString().equals("true")?true:false;
					table.attributes.add(attr);
					if(attr.isPrimarykey)
						table.primaryKeys.add(attr.name);
					data= br.readLine();
					offsetlength+= attr.length;
				}
				tableList.add(table);
				data= br.readLine();
			}
			br.close();
		}catch (IOException e) {
			e.printStackTrace();
		}
		
		
		 filename= "index.catalog";
		 file = new File(filename);
		 if (!file.exists()) {
				try {
					file.createNewFile();
					BufferedWriter writer = new BufferedWriter(new FileWriter(file));
					String str1 = "-1\n";
					writer.write(str1);
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		 try {
				br= new BufferedReader(new FileReader(filename));			
				String data= br.readLine();
				Index inx= new Index();
				while(!data.equals("-1")) {
					inx =new Index();
					inx.index_name=data;
					data= br.readLine();
					inx.table_name=data;
					data= br.readLine();
					inx.attribute_name=data;
					data= br.readLine();
					inx.attr_index=Integer.parseInt(data);
					data= br.readLine();
					inx.attr_length=Integer.parseInt(data);
					data= br.readLine();
					inx.attr_type=Integer.parseInt(data);
	
					indexList.add(inx);
					
					Table tb= getTable(inx.table_name);
					if(tb==null){
						System.out.println("Error in ReadCatalog, exsiting index lacking its table\n");
					}else{
						tb.indexes.add(inx.index_name);
					}
					data= br.readLine();
				}
				br.close();
			}catch (IOException e) {
				e.printStackTrace();
			}
	}

	
	public static void UpdateCatalog() {
		String filename= "table.catalog";
		File file;
		String str="";
		
		file= new File(filename);
		if(!file.exists())
		{
			try {
				file.createNewFile();
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		if(!tableList.isEmpty())
		{
			try {
				BufferedWriter writer= new BufferedWriter(new FileWriter(file));
				for(Table table: tableList) {
					
					str= table.table_name+"\n";
					str+= table.RecordNum+"\n"+Integer.toString(table.attrNum)+"\n"+Integer.toString(table.oneRecord_length)+"\n";
					if(!table.attributes.isEmpty())
					{
						for(Attribute attribute: table.attributes) {
							str+= attribute.name+"\n";
							str+= Integer.toString(attribute.type)+"\n";
							str+= Integer.toString(attribute.length)+"\n";
							str+= attribute.isPrimarykey+"\n";
							str+= attribute.isUnique+"\n";		
						}	
					}
					str+= "-1\n";
					writer.write(str);
				}
				writer.write("-1\n");
				writer.close();
				
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			
			
			File myfile = new File(filename);
			if(myfile.exists()) {
			   myfile.delete();
				}
			if (!myfile.exists()) {
				try {
					file.createNewFile();
					BufferedWriter writer = new BufferedWriter(new FileWriter(file));
					String str1 = "-1\n-1\n";
					writer.write(str1);
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		
		filename="index.catalog";
		str="";
		file = new File(filename);
		if(!file.exists())
		{
			try {
				file.createNewFile();
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		if(!indexList.isEmpty())
		{	
			try {
				BufferedWriter writer= new BufferedWriter(new FileWriter(file));
				for(Index index: indexList) {
					str = index.index_name+"\n" +index.table_name+"\n"+ index.attribute_name +"\n";
					str+= Integer.toString(index.attr_index)+"\n";
					str+= Integer.toString(index.attr_length)+"\n";
					str+= Integer.toString(index.attr_type)+"\n";	
					writer.write(str);
				}
				writer.write("-1");
				writer.close();
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			
			File myfile = new File(filename);
			if(myfile.exists()) {
			   myfile.delete();
				}
			if (!myfile.exists()) {
				try {
					file.createNewFile();
					BufferedWriter writer = new BufferedWriter(new FileWriter(file));
					String str1 = "-1\n";
					writer.write(str1);
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			
		}
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
		//*drop index FILE is done in  index Manager
		
		//1. remove from index list 
		//2 .remove from table.indexes
		
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
		tableList.remove(tb1);
		
	}


}
