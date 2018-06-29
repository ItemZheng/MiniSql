package Minisql;
import java.util.Vector;


import Minisql.Structure.*;
/*this class is used for tablename.record*/
public class RecordManager {
	public static void Create_Table(Table tb)
	{
		try {
			String filename= tb.table_name+".record";
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
			String filename= tableName+".record";
			//Buffer Manager to create a block...
		}catch (Exception e) {
			System.err.println(e.getMessage());
			System.err.println("Failure in record manager to drop a table");
		}
		System.out.println("drop table successfully in record manager");
	}
	
	public static Boolean Compare (Table tb, Record rec, Vector<Condition> conditions)
	{
		for(int i=0 ; i< conditions.size();i++)
		{
			int  AttrIndex=  conditions.get(i).AttrIndex;
			String value2= conditions.get(i).value;
			switch(tb.attributes.get(AttrIndex).type){
			case 0://integer
				int intvalue2 = Integer.parseInt(value2);
				int intvalue1 =0;
				for(int j=0;j<4;j++){
					intvalue1  +=(rec.columns.get(AttrIndex)[j] & 0xFF)<<(8*(3-j));
				}
				switch(conditions.get(i).op){
				case Lt:
					if (intvalue1 >= intvalue2) return false;break;
				case Le:
					if (intvalue1 >intvalue2 ) return false;break;
				case Gt:
					if(intvalue1 <= intvalue2) return false;break;
				case Ge:
					if( intvalue1<  intvalue2 ) return false;break;
				case Eq:
					if(intvalue1!= intvalue2) return false;break;
				case Ne:
					if(intvalue1== intvalue2) return false;break;
				}
				break;
			case 256: //float
				float flvalue1 = 0;
				int l;
				l = rec.columns.get(AttrIndex)[0];
				l &= 0xff; 
				l |= ((long) rec.columns.get(AttrIndex)[1] << 8); 
				l &= 0xffff; 
				l |= ((long) rec.columns.get(AttrIndex)[2] << 16); 
				l &= 0xffffff; 
				l |= ((long) rec.columns.get(AttrIndex)[3] << 24); 
				flvalue1= Float.intBitsToFloat(l);
				float flvalue2 = Float.valueOf(value2).floatValue();
				switch (conditions.get(i).op) {
				case Lt:
					if (flvalue1 >= flvalue2) return false;break;
				case Le:
					if (flvalue1> flvalue2 )return false;break;
				case Gt:
					if(flvalue1<= flvalue2) return false;break;
				case Ge:
					if(flvalue1<  flvalue2 ) return false;break;
				case Eq:
					if(flvalue1 !=flvalue2) return false;break;
				case Ne:
					if(flvalue1 == flvalue2) return false;break;
				}
				break;
			default://char 1-255
				String value1 =new String(); //wrong transfer!!
				//String value1 =new String(rec.columns.get(AttrIndex),"ISO-8859-1");
				switch (conditions.get(i).op) {
				case Lt:
					if (value1.compareTo(value2) >= 0) return false;break;
				case Le:
					if (value1.compareTo(value2) > 0)return false;break;
				case Gt:
					if (value1.compareTo(value2) <= 0)return false;break;
				case Ge:
					if (value1.compareTo(value2) < 0) return false;break;
				case Eq:
					if (value1.compareTo(value2) != 0) return false;break;
				case Ne:
					if (value1.compareTo(value2) == 0)  return false;break;
				}
				break;
			}
		
		}
		return false;
	}
	
	/*check if there is at least one condition meet the needs*/
	public static Boolean exist(Table tb,Vector<Condition>conditions)
	{	String filename= tb.table_name+".record";
		
		///????
		for(int i=0; i<conditions.size();i++)
		{
			
		}
		return false;
	}
	
	/*complete the whole insert operation including the index*/
	public static void Insert_Value(Table tb, Record rec){
		String filename= tb.table_name+".record";
		
		///????
		////insert the record line into the proper position in table_name.record
		System.out.println("insert success in file!");
		tb.RecordNum++;
		//???then  change the RecordNum in the table_name.record ????????????	
		
		//update all index's B+ tree 
		
		System.out.println("insert success for all indexes!");
	}
	public static void Delete_Table(Table tb) {
		
	}
	
	
		
}
	

