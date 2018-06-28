package Minisql;

import java.util.ArrayList;
import java.util.List;
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
			String tbName="";
			tbName=SQLargv.get(1).toString();
			if(CatalogManager.isExistTable(tbName)==true) {
				System.out.print("table already exists");
				return 1;}
			
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

			tb1.blockNum=1;
			tb1.oneRecord_length=0;
			for(int k =0; k< colNum;k++)
			{
				tb1.oneRecord_length+= tb1.attributes.get(k).length;
			}
			tb1.maxRecordNumPerBlock= 4096/tb1.oneRecord_length;
		
			CatalogManager.Create_Table(tb1);
			RecordManager.Create_Table(tb1);
			//CatalogManager.UpdateTable()
			
			//make the first primary key be an  index.
			if (hasPrikey==1)
			{
				tb1.indexes= new ArrayList<String>();
				int prikey_index = Integer.parseInt(SQLargv.get(3*colNum+3).toString());
				Index inx1= new Index();
				inx1.index_name=tb1.attributes.get(prikey_index).name+"-PrimaryKey";
				inx1.table_name=tb1.table_name;
				inx1.attribute_name=inx1.index_name;
				inx1.attr_length=tb1.attributes.get(prikey_index).length;
				inx1.attr_index=prikey_index;
				
				tb1.indexes.add(inx1.index_name);
				CatalogManager.Create_Index(inx1);
				IndexManager.Create_Index(tb1, inx1);
				//CatalogManager.UpdateIndex()
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
			Table tb1;
			tb1= CatalogManager.getTable(inx1.table_name);
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
			}
			IndexManager.Create_Index(tb1, inx1);
			CatalogManager.Create_Index(inx1);
			System.out.println("done");
			return 1;
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
				//BufferManager.dropIndex()?????
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
				//BufferManager.dropTable()????
			}
			System.out.println("done");
			return 1;	
		}
		else if(SQLargv.get(0).equals("4")) //select
		{
			
		}
		else if(SQLargv.get(0).equals("5"))//insert
		{
			
		}
		else if(SQLargv.get(0).equals("6"))//delete
		{
			
		}
		return 1;
	}

}
