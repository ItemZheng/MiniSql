package Minisql;

import java.util.*;
public class Structure {
	public static  class Attribute {
		public String name;
		public int type;//0->integer ,256->float ,1~255->char()
		public int length;
		public Boolean isPrimarykey;
		public Boolean isUnique;
		public int offset;
		public Attribute() {
			isPrimarykey= false;
			isUnique= false;
			type=-1;
			offset=0;
		}
		}	
	public static class Index{
		public String index_name; //index name;
		public String table_name;
		public String attribute_name;
		public int attr_index;// the index of its attribute
		public int attr_length;
		public int attr_type;
	}
	public static class Table{
		public String table_name;
		public int RecordNum;
		public int attrNum;
		public int oneRecord_length;//total length of one record, should be equal to sum(attributes[i].length)
		public ArrayList<Attribute> attributes;
		public ArrayList<String> indexes; //name of indexes		
		public ArrayList<String> primaryKeys; //name of primary keys
		
		public Table() {
			attributes=new ArrayList<Attribute>();
			indexes= new ArrayList<String>();
			primaryKeys= new ArrayList<String>();
			table_name= "";
			attrNum= 0;
			oneRecord_length=0;
			RecordNum=0;
		}
	}
	
	public static class Record{
		public int Length;
		public ArrayList<byte[]> columns;
		public Record() {
			columns = new ArrayList<byte[]>();
		}
		/*Record selectedRecord(Table tb, selectedAttribute selections)
		{
			Record returnRecord =new Record();
			for(int i=0; i<tb.attrNum;i++) {
				for(int j=0;j<selections.AttrIndexes.size();j++) {
					if(i== selections.AttrIndexes.get(j)){
						returnRecord.columns.add(this.columns.get(i));
						break;
					}
				}
			}
			return returnRecord;
		}
		*/
		
	}
	
	public static class OneRow
	{
		public Vector<String> colunms;
		public OneRow() {
			
			colunms =new Vector<String>();
		}
		
	}
	public static class Data{
		public Vector <OneRow> rows;
		public Data() {
			rows= new Vector<OneRow>();
		}
	}
	
	public static class selectedAttribute{
		Vector<Integer> AttrIndexes;
		public selectedAttribute() {
			AttrIndexes= new Vector<Integer>();
		}
	}
	
	public enum Comparison{
		Eq, //equal 
		Ne, //not equal 
		Ge, //great than or equal
		Gt, //great than
		Le, //less than or equal
		Lt //less than
		
	}
	public static class Condition{
		public  int  AttrIndex;
		public  Comparison op;
		public String value;
	}
}

