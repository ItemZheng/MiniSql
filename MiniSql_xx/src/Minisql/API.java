package Minisql;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import Minisql.Structure.*;

/*test case :
 * create table book(bno int, bname char(8) unique, price int, primary key(bno));
 * create index stunameidx on student ( sname );
 * 
 * */
public class API {
	public static int API_Module(List SQLargv)
	{
		System.out.println(SQLargv);
		if (SQLargv.get(0).equals("quit"))
		{
			//use buffermanage to save file.
			return 0;
		}
		else if( SQLargv.get(0).equals("0"))//create table
		{
			String tbName=SQLargv.get(1).toString();
			if(CatalogManager.getTable(tbName)!=null) {
				System.out.print("table already exists");
				return 1;
				}
			
			Table tb1= new Table();
			tb1.table_name=tbName;
			int colNum;// number of attributes.
			colNum= Integer.parseInt(SQLargv.get(2).toString());
			tb1.attrNum = colNum;
			tb1.attributes= new ArrayList<Attribute>();
			for(int i= 0;i< colNum;i++)
			{	
				tb1.attributes.add(new Attribute());
			}
			for (int i=0;i<colNum;i++)
			{
				tb1.attributes.get(i).name= SQLargv.get(3*i+3).toString();
				String argv1="";
				argv1= SQLargv.get(3*i+4).toString();// type information
				if(argv1.equals("int")){
					tb1.attributes.get(i).type=0;
					tb1.attributes.get(i).length=4;
				}
				else if (argv1.equals("float")) {
					tb1.attributes.get(i).type=256;
					tb1.attributes.get(i).length=4;
				}
				else if(argv1.subSequence(0, 4).equals("char")) {
					tb1.attributes.get(i).type=  Integer.parseInt(argv1.substring(4));
					tb1.attributes.get(i).length=tb1.attributes.get(i).type;
					//can be from 1~255
					}
				
				argv1=SQLargv.get(3*i+5).toString();
				if (argv1.equals("1")) tb1.attributes.get(i).isUnique=true;
				else   tb1.attributes.get(i).isUnique=false;		
				
				if(i>0) tb1.attributes.get(i).offset= tb1.attributes.get(i-1).offset+tb1.attributes.get(i-1).length;
				else if (i==0) tb1.attributes.get(0).offset =0;
			}
			int hasPrikey =0;
			if (SQLargv.size() > 3*colNum+3) hasPrikey=1;
			if(hasPrikey==1)
			{
				tb1.primaryKeys= new ArrayList<String>();
				   for(int j= SQLargv.size()-1 ; j>=3*colNum+3;j--)
				{
					int prikey_index = Integer.parseInt(SQLargv.get(j).toString());
					tb1.attributes.get(prikey_index).isPrimarykey=true;
					tb1.primaryKeys.add(tb1.attributes.get(prikey_index).name);
				}
			}


			tb1.oneRecord_length=0;
			tb1.RecordNum=0;
			for(int k =0; k< colNum;k++)
			{
				tb1.oneRecord_length+= tb1.attributes.get(k).length;
			}
		
		
			CatalogManager.Create_Table(tb1); //done
			RecordManager.Create_Table(tb1); //done
		
			//make the first primary key be an  index.
			if (hasPrikey==1)
			{
				tb1.indexes= new ArrayList<String>();
				int prikey_index = Integer.parseInt(SQLargv.get(3*colNum+3).toString());
				Index inx1= new Index();
				inx1.index_name=tb1.attributes.get(prikey_index).name+"-PrimaryKey";
				inx1.table_name=tb1.table_name;
				inx1.attribute_name=tb1.attributes.get(prikey_index).name;
				inx1.attr_length=tb1.attributes.get(prikey_index).length;
				inx1.attr_index=prikey_index;
				
				tb1.indexes.add(inx1.index_name);
				CatalogManager.Create_Index(inx1);
				IndexManager.Create_Index(tb1, inx1);

			}
			System.out.println("done");
			return 1;
			
		}
		else if(SQLargv.get(0).equals("1"))// create index
		{
			Index inx1 =new Index();
			inx1.index_name=SQLargv.get(1).toString();
			inx1.table_name=SQLargv.get(2).toString();
			inx1.attribute_name=SQLargv.get(3).toString();
			Table tb1=CatalogManager.getTable(inx1.table_name);
			if(tb1==null)
			{
				System.out.println("Cannot create index because of not existing the table!");
				return 1;
			}
			else {
				int i;
				for (i=0; i<tb1.attrNum;i++)
				{
					if(tb1.attributes.get(i).name.equals(inx1.attribute_name))
					{
						inx1.attr_index=i;
						inx1.attr_length=tb1.attributes.get(i).length;
						break;
					}
				}
				if(i== tb1.attrNum)
				{
					System.out.println("Cannot create index because of not existing the attribute!");
					return 1;
				}
				if(CatalogManager.getIndex(inx1.index_name)==null) { //if not exist
					tb1.indexes.add(inx1.index_name);
					IndexManager.Create_Index(tb1, inx1);
					CatalogManager.Create_Index(inx1);
					System.out.println("done");
					return 1;
					
				}
			}
		}
		else if(SQLargv.get(0).equals("2"))//drop index
		{
			String inxName=SQLargv.get(1).toString();
			Index inx1= CatalogManager.getIndex(inxName);
			if(inx1==null)
			{
				System.out.println("Cannot drop index because of not existing the index!");
				return 1;
			}else {
				
				IndexManager.Drop_Index(inxName);
				CatalogManager.dropIndex(inxName);
				
			}
			System.out.println("done");
			return 1;	
		}
		else if( SQLargv.get(0).equals("3"))//drop table
		{
			String tbName= SQLargv.get(1).toString();
			Table tb1= CatalogManager.getTable(tbName);
			if(tb1==null)
			{
				System.out.println("Cannot drop table because of not existing the table!");
				return 1;
			}else {	
				CatalogManager.dropTable(tbName);//at the same time delete  indexes.
				RecordManager.Drop_Table(tbName);
				
			}
			System.out.println("done");
			return 1;	
		}
		else if(SQLargv.get(0).equals("4")) //select
		{
			String tbName =SQLargv.get(1).toString();
			Table tb1= CatalogManager.getTable(tbName);
			if(tb1 ==null)
			{
				System.out.println("Cannot selecet from table because of not existing the table!");
				return 1;
			}
			int col_num = Integer.parseInt(SQLargv.get(2).toString());
			int condition_num = Integer.parseInt(SQLargv.get(3+col_num).toString());
			Vector< Condition> conditions=new Vector< Condition>();
			
			if(col_num==0 && condition_num ==0) //select* from table 
			{
				
			}
			else if (col_num==0&& condition_num >0){// select * from table where 
				for(int i=0; i< condition_num;i++)
				{   Condition  cd =new Condition();
					String attrName= SQLargv.get(4+col_num+ 4*i).toString();
	                String tempValue = SQLargv.get(6+col_num+ 4*i).toString();
					int isString = Integer.parseInt(SQLargv.get(7+col_num+ 4*i).toString());;
	                int typeflag =0;
					int j=0;
					for(j=0;j<tb1.attrNum;j++)
					{
						if(tb1.attributes.get(j).name == attrName)
						{
						
							if(isString==1)
							{
								if(tb1.attributes.get(j).type==0||tb1.attributes.get(j).type==256)
								{
									typeflag=1;
									break;
								}
							}
							else// integer or float
							{
								if(tb1.attributes.get(j).type>=1 && tb1.attributes.get(j).type<= 255)
								{
									typeflag=1;
									break;
								}
								else if(tb1.attributes.get(j).type==0)//integer
								{
									if(tempValue.contains(".")) {
										typeflag=1;
										break;
									}
								}
							}
							break;
						}
					}
					
					if(typeflag==1)
					{	
						System.out.println("Cannot selecet from table because of wrong type!");
					return 1;
						
					}
					if(j>=tb1.attrNum)
					{
						System.out.println("Cannot selecet from table because of not existing the attribute!");
						return 1;
					}
					
					String CdOp =SQLargv.get(5+col_num+ 4*i).toString();
					switch (CdOp) {
					case "<=":
						cd.op= Comparison.Le;
						break;
					case "<":
						cd.op= Comparison.Lt;
						break;
					case ">=":
						cd.op= Comparison.Ge;
						break;
					case ">":
						cd.op= Comparison.Gt;
						break;
					case "!=":
						cd.op= Comparison.Ne;
						break;
					case "==":
						cd.op= Comparison.Eq;
						break;
					}
					conditions.add(cd);
				}
				
				//already save all conditions to the vector  conditions.
				// more work about  indexManager and RecordManager and CatalogManager need to be done.
				
			}
			else { //select col1,col2,... from  table where...
	

			}
			
		}
		else if(SQLargv.get(0).equals("5"))//insert
		{
			String tbName= SQLargv.get(1).toString();
			Table tb1= CatalogManager.getTable(tbName);
			if(tb1 == null)
			{
				System.out.println("Cannot insert values because of not existing the table!");
				return 1;
			}
			else {
				int valuesNum= Integer.parseInt(SQLargv.get(2).toString());
				if(valuesNum!= tb1.attrNum)
				{
					System.out.println("Cannot insert values because of wrong value number!");
					return 1;
				}
				else {
					for (int i= 0;i<tb1.attrNum;i++)
					{
						if(SQLargv.get(i*2+4).toString().equals("1"))// the value is a string 
						{
							if(tb1.attributes.get(i).type ==0 || tb1.attributes.get(i).type ==256 )// integer or float
							{
								System.out.println("Cannot insert values because of wrong type!");
								return 1;
							}
							//check the length
							if(SQLargv.get(i*2+4).toString().length() > tb1.attributes.get(i).length)
							{
								System.out.println("Cannot insert values because of wrong length!");
								return 1;
							}
						}
						else {// the value is not a string 
							if(tb1.attributes.get(i).type >=1 && tb1.attributes.get(i).type <=255 )// char(*)
							{
								System.out.println("Cannot insert values because of wrong type!");
								return 1;
							}
							if(tb1.attributes.get(i).type ==0)//integer
							{
								if(SQLargv.get(i*2+4).toString().contains(".")) {
									System.out.println("Cannot insert values because of wrong type!");
									return 1;
								}
							}
						}
						// when the type is correct.
						
						// judge whether the unique value is unique..
						if(tb1.attributes.get(i).isUnique)
						{
							Vector< Condition> unique_conditions=new Vector< Condition>();
							Condition cd = new Condition();
							cd.op =Comparison.Eq; //judge whether exist old value is equal to the new value.
							cd.value=SQLargv.get(i*2+3).toString();
							cd.AttrIndex =i;
							
							unique_conditions.add(cd);
							if(RecordManager.exist(tb1, unique_conditions)) {
								System.out.println("Cannot insert values because of breaking the unique principle!");
								return 1;
							}
						}
					}
					
					//judge whether the primary key is unique. 
					Vector< Condition> primary_conditions=new Vector< Condition>();
					for (int i= 0;i<tb1.attrNum;i++)
					{
						if(tb1.attributes.get(i).isPrimarykey)
						{
							Condition cd = new Condition();
							cd.op =Comparison.Eq;//just one of the new primary keys' value is not exist will be okay.
							cd.value=SQLargv.get(i*2+3).toString();
							cd.AttrIndex =i;
							
							primary_conditions.add(cd);
						}
					}
					if(RecordManager.exist(tb1, primary_conditions)== true) { // all new primary values is equal to the old primary key values
						System.out.println("Cannot insert values because of breaking the primary principle!");
						return 1;
					}
					
					//now all is correct
					Record rec1= new Record();
					rec1.Length=tb1.oneRecord_length;
					for (int i= 0;i<tb1.attrNum;i++)
					{	
						String tempValue= SQLargv.get(i*2+3).toString();
						byte[] tempBytes;
						if(tb1.attributes.get(i).type==0) //integer
						{
							int value = Integer.parseInt(tempValue);
							tempBytes = BufferManage.Int2byte(value);
						}else if(tb1.attributes.get(i).type==256) //float
						{
							float value = Float.parseFloat(tempValue);
							tempBytes = BufferManage.Float2Byte(value);
						}
						else { //char(x)
							tempBytes = BufferManage.String2byte(tempValue, tb1.attributes.get(i).length);
						}
						rec1.columns.add(tempBytes);
					}
					RecordManager.Insert_Value(tb1,rec1);
					System.out.println("done");
					return 1;	
					
				}
				
			}
		}
		else if(SQLargv.get(0).equals("6"))//delete 
		{
			String tbName= SQLargv.get(1).toString();
			Table tb1=CatalogManager.getTable(tbName);
			
			if(SQLargv.get(2).equals("0")) //delete all :clear all records and indexes
			{
				RecordManager.Delete_Table(tb1);
			}else { //have conditions
				Vector< Condition> conditions=new Vector< Condition>();
				
			}
		}
		return 1;
	}

}
