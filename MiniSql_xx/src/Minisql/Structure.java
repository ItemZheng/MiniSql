package Minisql;

import java.util.*;

public class Structure {
	public static  class Attribute {
		public int length;
		public String name;
		public int type;//0->integer ,256->float ,1~255->char()
		public Boolean isPrimarykey;
		public Boolean isUnique;
		public int offset;
		
		public Attribute() {
			isPrimarykey= false;
			isUnique= false;
			type=-1;
		}
		}	
	public static class Index{
		public String index_name; //index name;
		public String table_name;
		public String attribute_name;
		public int attr_index;// the index of its attribute
		public int attr_length;
	}
	public static class Table{
		public String table_name;
		public ArrayList<Attribute> attributes;
		public ArrayList<String> indexes; 
		public int oneRecord_length;//total length of one record, should be equal to sum(attributes[i].length)
		
		public ArrayList<String> primaryKeys;
		public int  blockNum;//the number of blocks tablename.record occupy
		public int maxRecordNumPerBlock;
		public int attrNum;
		public Table() {
			attributes=null;
			indexes= null;
			primaryKeys=null;
			table_name= null;
			blockNum =0;
			attrNum= 0;
			maxRecordNumPerBlock=0;
			oneRecord_length=0;
		}
		
	}
}

