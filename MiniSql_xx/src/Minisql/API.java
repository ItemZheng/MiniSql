package Minisql;

import java.util.List;

import Minisql.Structure.*;

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
			if(CatalogManager.isExistTable(tbName)==false) {
				System.out.print("table already exists");
				return 0;}
			
			Table tb1= new Table();
			tb1.table_name=tbName;
			int colNum;
			colNum= Integer.parseInt(SQLargv.get(2).toString());
			tb1.attrNum = colNum;
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
			for(int j= SQLargv.size()-1 ; j>=3*colNum+3;j--)
			{
				int prikey_index = Integer.parseInt(SQLargv.get(j).toString());
				tb1.attributes.get(prikey_index).isPrimarykey=true;
			}
			
			tb1.blockNum=1;
			tb1.oneRecord_lengh=0;
			for(int k =0; k< colNum;k++)
			{
				tb1.oneRecord_lengh+= tb1.attributes.get(k).length;
			}
			tb1.maxRecordNumPerBlock= 4096/tb1.oneRecord_lengh;
		
			CatalogManager.Create_Table(tb1);
			RecordManager.Create_Table(tb1);
			
			
		}
		else if(SQLargv.get(0).equals("1"))// create index
		{
			
		}
		else if(SQLargv.get(0).equals("2"))//drop index
		{
			
		}
		else if( SQLargv.get(0).equals("3"))//drop table
		{
			
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
