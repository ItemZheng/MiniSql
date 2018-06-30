package Minisql;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

import Minisql.BufferManage.*;
import Minisql.Structure.*;
import javafx.geometry.Pos;

/*this class is used for tablename.record*/
public class RecordManager {
	public static void Create_Table(Table tb)
	{
		try {
			String filename= tb.table_name+".record";
			BufferOperator fp = new BufferOperator(filename); //create the file
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
	
	public static Boolean Compare (Table tb, Record rec, Vector<Condition> conditions)// traverse in physical order...??? with index???
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
		return true;
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
		
		int record_num = tb.RecordNum;
		record_num++;
		
		//get the first_block record num
		int first_block = (Global.BlockSize) / tb.oneRecord_length;
		
		//get the offset
		int offset = 0;
		if(first_block >= record_num) {
			//at first block
			offset = (record_num - 1) * tb.oneRecord_length;
		}else {
			//get the block id
			int largest_records = Global.BlockSize / tb.oneRecord_length;
			int block_id = (record_num - first_block) / largest_records + 1;
			offset = block_id * Global.BlockSize + ((record_num - first_block) % largest_records - 1)
							* tb.oneRecord_length;
		}
		
		fp.move(0);
		fp.move(offset);
		fp.write(rec.columns);//insert the record line into the proper position in table_name.record
		fp.close();
		
		
		System.out.println("insert success in record file!");
		
	}
	
	public static void Delete_Table(Table tb) {
		try {
			String filename= tb.table_name+".record";
			BufferManage.dropFile(filename);
			RecordManager.Create_Table(tb);
			
		}catch (Exception e) {
			System.err.println(e.getMessage());
			System.err.println("Failure in record manager to delete a table");
		}
		System.out.println("delete table successfully in record manager");
	}
	
	public static void Delete(Table tb ,Vector<Condition>conditions){
		
		String filename= tb.table_name+".record";
		
	/*	for(int i=0;i< all record???)
		{
			byte[] RecByteLine =   read one Line record From BufferManager
	
			record rec=SplitRecord(tb, RecByteLine);
					
					
			if(Compare(tb, rec, conditions)==true)
			{
				BufferManage.deleteRecord
				count++;
				tb.RecordNum--;
				for(int j=0;j<tb.indexes.size();j++)
				{
					Index inx1= CatalogManager.getIndex(tb.indexes.get(j));
					IndexManager.DeleteKey(inx1, rec.columns.get(inx1.attr_index));
				}
			}
		}
	*/			
	ArrayList< Integer> offsets = new ArrayList<Integer>();
		offsets =  SelectBeforeDelete (tb,conditions);
		Collections.reverse(offsets);
		for(int i=0;i<offsets.size();i++)
		{
			int maxRecordPerBlock = Global.BlockSize / tb.oneRecord_length;
			int BlockNum = (tb.RecordNum - 1) / maxRecordPerBlock;
			int maxOff;
			if((tb.RecordNum-1) % maxRecordPerBlock == 0) {
				//full
				maxOff = BlockNum * Global.BlockSize;
			}else {
				maxOff = BlockNum * Global.BlockSize + ((tb.RecordNum-1) % maxRecordPerBlock) * tb.oneRecord_length;
			}
			
			BufferOperator fp = new BufferOperator(filename, maxOff);
			byte[] lastRecord = fp.read(tb.oneRecord_length);
			fp.move(offsets.get(i));
			fp.write(lastRecord);
			fp.close();
			tb.RecordNum--;
		}
			
		System.out.println("delete "+offsets.size()+ " records successfully in record manager");
	
	}
	public static ArrayList< Integer>  SelectBeforeDelete (Table tb, Vector<Condition> conditions) {
		String filename= tb.table_name+".record";
		// if has index
		int flag =0;
		int k;
		/*for(k=0;k<tb.indexes.size();k++)
		{
			String inxName=tb.indexes.get(k);
			Index inx1= CatalogManager.getIndex(inxName);
			for(int j=0;j<conditions.size();j++)
			{
				if(inx1.attr_index ==conditions.get(j).AttrIndex ) {
					flag=1;
					break;
				}	
			}
			if(flag==1)
				break;
		}
		//find one index
		if(flag ==1) {
			String inxName=tb.indexes.get(k);
			Index inx1= CatalogManager.getIndex(inxName);
			//B+search  and get offsets?????????????????????/
		  // offsets : ArrayList<Integer>???????????????????????????????????????
		
		}else {*/
			ArrayList< Integer> offsets = new ArrayList<Integer>();
			BufferOperator fp = new BufferOperator(filename);
			for(int i=0;i< tb.RecordNum; i++)
			{
				int TempOffset = fp.getOffset();
				byte[] RecByteLine = fp.read(tb.oneRecord_length);
							
				Record rec=SplitRecord(tb, RecByteLine);
				if(Compare(tb, rec, conditions)==true)
				{
					offsets.add(TempOffset);
				}		
			}
			fp.close();
			return  offsets;
		//}
		
	}
	
	
	
	//select all record from the table 
	public static void Select (Table tb) {
		
		String filename=tb.table_name+".record";    
		Data datas=new Data();
		BufferOperator fp = new BufferOperator(filename);
		OneRow attrName =new OneRow();
		for(int j=0;j<tb.attrNum;j++) {
			attrName.colunms.addElement(tb.attributes.get(j).name);
		}
		
		for(int i=0;i< tb.RecordNum; i++)
		{
			byte[] RecByteLine =  fp.read(tb.oneRecord_length);
			OneRow row1=SplitRecord2OneRow(tb, RecByteLine);
			datas.rows.add(row1);
		}	
		fp.close();
		showAttrName(attrName);
		showDatas(datas);
	
		
	}
	
	//select the records which meets the conditions from the table
	public static void Select (Table tb, Vector<Condition> conditions) {
		String filename= tb.table_name+".record";
		Data datas=new Data();
		OneRow attrName =new OneRow();
		for(int j=0;j<tb.attrNum;j++) {
			attrName.colunms.addElement(tb.attributes.get(j).name);
		}
		
		// if has index
		int flag =0;
		int k;
		/*for(k=0;k<tb.indexes.size();k++)
		{
			String inxName=tb.indexes.get(k);
			Index inx1= CatalogManager.getIndex(inxName);
			for(int j=0;j<conditions.size();j++)
			{
				if(inx1.attr_index ==conditions.get(j).AttrIndex ) {
					flag=1;
					break;
				}	
			}
			if(flag==1)
				break;
		}
		//find one index
		if(flag ==1) {
			String inxName=tb.indexes.get(k);
			Index inx1= CatalogManager.getIndex(inxName);
			//B+search  and get offsets?????????????????????/
		  // offsets : ArrayList<Integer>???????????????????????????????????????
		
		}else {*/
			BufferOperator fp = new BufferOperator(filename);
			for(int i=0;i< tb.RecordNum; i++)
			{
				byte[] RecByteLine = fp.read(tb.oneRecord_length);
						
				Record rec=SplitRecord(tb, RecByteLine);
				if(Compare(tb, rec, conditions)==true)
				{
					OneRow row1=SplitRecord2OneRow(tb, RecByteLine);
					datas.rows.add(row1);
				}		
			}
			fp.close();
		//}
		showAttrName(attrName);
		showDatas(datas);
	}
	
	
	public static void Select(Table tb, Vector<Condition> conditions,selectedAttribute selections) {
		String filename= tb.table_name+".record";
		Data datas=new Data();
		
		OneRow attrName =new OneRow();
		for(int j=0;j<selections.AttrIndexes.size();j++) {
			attrName.colunms.addElement(tb.attributes.get(selections.AttrIndexes.get(j)).name);
		}
		
	/*	// if has index
		int flag =0;
		int k;
		for(k=0;k<tb.indexes.size();k++)
		{
			String inxName=tb.indexes.get(k);
			Index inx1= CatalogManager.getIndex(inxName);
			for(int j=0;j<conditions.size();j++)
			{
				if(inx1.attr_index ==conditions.get(j).AttrIndex ) {
					flag=1;
					break;
				}	
			}
			if(flag==1)
				break;
		}
		//find one index
		if(flag ==1) {
			String inxName=tb.indexes.get(k);
			Index inx1= CatalogManager.getIndex(inxName);
			//B+search  and get offsets?????????????????????/
		  // offsets : ArrayList<Integer>???????????????????????????????????????
			// get oneRow 
			// get selectedRow
		
		}else {*/
			BufferOperator fp = new BufferOperator(filename);
			for(int i=0;i< tb.RecordNum; i++)
			{
				byte[] RecByteLine = fp.read(tb.oneRecord_length);
						
				Record rec=SplitRecord(tb, RecByteLine);
				if(Compare(tb, rec, conditions)==true)
				{
					OneRow row1=SplitRecord2OneRow(tb, RecByteLine);
					OneRow selectedRow = new OneRow();
					for(int j=0; j< selections.AttrIndexes.size();j++)
					{
						int tempAttrIndex= selections.AttrIndexes.get(j);
						selectedRow.colunms.add(row1.colunms.get(tempAttrIndex));
					}
					datas.rows.add(selectedRow);
				}		
			}
			fp.close();
		//}
			
		showAttrName(attrName);
		showDatas(datas);
	}
	
	
	
	public static void Select(Table tb,selectedAttribute selections) {
		String filename= tb.table_name+".record"; 
		Data datas=new Data();
		BufferOperator fp = new BufferOperator(filename);
		OneRow attrName =new OneRow();
		for(int j=0;j<selections.AttrIndexes.size();j++) {
			attrName.colunms.addElement(tb.attributes.get(selections.AttrIndexes.get(j)).name);
		}
		
		
		for(int i=0;i< tb.RecordNum; i++)
		{
			byte[] RecByteLine =  fp.read(tb.oneRecord_length);
					
			OneRow row1=SplitRecord2OneRow(tb, RecByteLine);
			OneRow selectedRow = new OneRow();
			for(int j=0; j< selections.AttrIndexes.size();j++)
			{
				int tempAttrIndex= selections.AttrIndexes.get(j);
				selectedRow.colunms.add(row1.colunms.get(tempAttrIndex));
			}
			datas.rows.add(selectedRow);
		}
		
		showAttrName(attrName);
		showDatas(datas);
		
		fp.close();
	}
	
	
	public static OneRow SplitRecord2OneRow(Table tb, byte[] RecByteLine)
	{
		
		OneRow row1= new OneRow();
		int startPos=0;
		for(int i=0;i< tb.attrNum;i++)
		{
			byte[] tempByte = new byte[tb.attributes.get(i).length];
			for(int j=0;j<tempByte.length;j++)
			{
				tempByte[j]= RecByteLine[j+startPos];
			}
			
			startPos += tempByte.length;
			
			if(tb.attributes.get(i).type== 0) //integer
			{
				int tempvalue = BufferManage.byte2Int(tempByte);
				row1.colunms.addElement(Integer.toString(tempvalue));
			}else if(tb.attributes.get(i).type== 256) //float
			{
				float tempvalue = BufferManage.ByteToFloat(tempByte);
				row1.colunms.addElement(Float.toString(tempvalue));
			}
			else// char(X) 
			{
				String tempvalue= BufferManage.byte2String(tempByte);
				row1.colunms.addElement( tempvalue);
			}
		}
		return row1;
	}
	
	public static Record SplitRecord(Table tb, byte[] RecByteLine) {
		Record rec1 =new Record();
		int startPos=0;
		for(int i=0;i< tb.attrNum;i++)
		{
			byte[] tempByte = new byte[tb.attributes.get(i).length];
			for(int j=0;j<tempByte.length;j++)
			{
				tempByte[j]= RecByteLine[j+startPos];
			}
			startPos += tempByte.length;
			rec1.columns.add(tempByte);
		}
		return rec1;
	}
	
	
	
	/*show the data added into the datas */
	public static void showDatas(Data datas){
		if(datas.rows.size()==0)
		{
			System.out.println("The query result is empty");
			return;
		}
		else {
			for (int i=0; i<datas.rows.size(); i++)
			{
				System.out.print("[");
				int j;
				for(j=0;j<datas.rows.get(i).colunms.size()-1;j++)
				{
					System.out.print(datas.rows.get(i).colunms.get(j)+",");
				}
				System.out.print(datas.rows.get(i).colunms.get(j));
				System.out.print("]\n");
			}
		}
	}
	
	public static void showAttrName(OneRow row1) {
		if(row1.colunms.size()== 0)
		{
			System.out.println("no selected attributes");
			return;
		}
		else {
			System.out.print("[");
			int i;
			for (i=0; i<row1.colunms.size()-1;i++)
			{
				System.out.print(row1.colunms.get(i)+",");
			}
			System.out.print(row1.colunms.get(i));
			System.out.print("]\n");
		
		}
	}
}
	

