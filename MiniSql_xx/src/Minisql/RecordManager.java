package Minisql;
import java.beans.ExceptionListener;
import java.util.Vector;

import Minisql.BufferManage.*;
import Minisql.Structure.*;
/*this class is used for tablename.record*/
public class RecordManager {
	public static void Create_Table(Table tb)
	{
		try {
			String filename= tb.table_name+".record";
			BufferOperator fp = new BufferOperator(filename);
			fp.write(BufferManage.Int2byte(tb.RecordNum));
			fp.close();
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
			BufferManage.dropFile(filename);
			
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
				int intvalue1 = BufferManage.byte2Int(rec.columns.get(AttrIndex));
				
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
				float flvalue1 = BufferManage.ByteToFloat(rec.columns.get(AttrIndex));
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
				String value1 = BufferManage.byte2String(rec.columns.get(AttrIndex));
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
	
	/*check if there is at least one record meet the needs*/
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
		
		//open file
		BufferOperator fp = new BufferOperator(filename);
		//get the record
		int record_num = BufferManage.byte2Int(fp.read(4));
		record_num++;
		
		//get the first_block record num
		int first_block = (Global.BlockSize - 4) / tb.oneRecord_length;
		
		//get the offset
		int offset = 0;
		if(first_block >= record_num) {
			//at first block
			offset = 4 + (record_num - 1) * tb.oneRecord_length;
		}else {
			//get the block id
			int largest_records = Global.BlockSize / tb.oneRecord_length;
			int block_id = (record_num - first_block) / largest_records + 1;
			offset = block_id * Global.BlockSize + ((record_num - first_block) % largest_records - 1)
							* tb.oneRecord_length;
		}
		
		fp.move(0);
		fp.write(BufferManage.Int2byte(record_num));
		fp.move(offset);
		fp.write(rec.columns);
		fp.close();
		
		
		//insert the record line into the proper position in table_name.record
		
		System.out.println("insert success in file!");
		tb.RecordNum++;
		//???then  change the RecordNum in the table_name.record ????????????	
		
		//update all index's B+ tree 
		
		System.out.println("insert success for all indexes!");
	}
	public static void Delete_Table(Table tb) {
		
	}
	
	
		
}
	

