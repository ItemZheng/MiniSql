package Minisql;

import java.util.Vector;

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
	}
	public static class Table{
		public String table_name;
		public Vector<Attribute> attributes;
		public Vector<Index> indexes;
		public int oneRecord_lengh;//total length of one record, should be equal to sum(attributes[i].length)
		
		public Vector<String> primaryKeys;
		public int  blockNum;//the number of blocks tablename.record occupy
		public int maxRecordNumPerBlock;
		public int attrNum;
		public Table() {
			table_name= null;
			blockNum =0;
			attrNum= 0;
			maxRecordNumPerBlock=0;
			oneRecord_lengh=0;
		}
		
	}
}

