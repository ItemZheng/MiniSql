package Minisql;

import Minisql.BufferManage.BufferOperator;
import Minisql.Structure.*;

import java.util.ArrayList;

import Minisql.BplusTree.*;
public class IndexManager {
	public static void Create_Index(Table tb, Index inx)
	{
		try {
			String filename= inx.index_name+".index";
			//build a B+ tree
			if(tb.RecordNum==0)
			{
				Tree newTree = new Tree(inx.attr_type);
				newTree.saveToFile(filename);
				return;
			}
			else {
				
				Tree newTree = new Tree(inx.attr_type);
				String Recordfilename = tb.table_name+".record";
				BufferOperator fp1 = new BufferOperator(Recordfilename);
				for(int i=0;i< tb.RecordNum; i++)
				{
					int offset = fp1.getOffset();
					byte[] RecByteLine =  fp1.read(tb.oneRecord_length);
					byte[] value = TreeNode.getSubByte(RecByteLine, tb.attributes.get(inx.attr_index).offset, inx.attr_length);
					newTree.InsertTree(value, offset);
				}
				newTree.saveToFile(filename);
				fp1.close();
				
				
			}
		}catch (Exception e) {
			System.err.println(e.getMessage());
			System.err.println("Failure in index manager to create a index");
		}
		System.out.println("create index successfully in index manager");
		
	}
	public static void Drop_Index(String IndexName) {
		try {
			String filename= IndexName+".index";
			BufferManage.dropFile(filename);
			
		}catch (Exception e) {
			System.err.println(e.getMessage());
			System.err.println("Failure in index manager to drop a index");
		}
		System.out.println("drop index successfully in index manager");
		
	}
	public static void InsertKey(Index inx, byte[]value,int offset) {
		String filename = inx.index_name + ".index";
		Tree tree = new Tree(filename);

		tree.InsertTree(value, offset);

		tree.saveToFile(filename);

	}
	
	public static void DeleteKey(Index inx, byte[] value, int offset) {
		String filename = inx.index_name + ".index";
		Tree tree = new Tree(filename);
		tree.Delete(value, offset);
		tree.saveToFile(filename);
	}
	
	public static void Delete_Index(Table tb, Index inx)
	{
		try {
			IndexManager.Drop_Index(inx.index_name); //delete the .index file
			Tree tree1 = new Tree(inx.attr_type);
			tree1.saveToFile(inx.index_name+".index");
			
		}catch (Exception e) {
			System.err.println(e.getMessage());
			System.err.println("Failure in index manager to delete index information");
		}
		System.out.println("delete index information successfully in index manager");
	}
	
	public static ArrayList<Integer> SearchWithIndex(Index inx, byte [] Upper, byte[] Lower) {
		String filename = inx.index_name + ".index";
		BufferOperator fp = new BufferOperator(filename);
		ArrayList<Integer> offsets = new ArrayList<Integer>();
		if(Upper == null) {
			int RootOffset = BufferManage.byte2Int(fp.read(4));
			if(RootOffset == -1) {
				System.out.println("The table is empty");
				return null;
			}
			int type = BufferManage.byte2Int(fp.read(4));
			int oneNodeSize = BplusTree.TreeNode.GetOneNodeSize(type);
			
			int current_offset = RootOffset;
			int type_length = type;
			if(type == 0 || type == 256) {
				type_length = 4;
			}
			while(true) {
				//read the 
				fp.move(current_offset);
				int isLeaf = BufferManage.byte2Int(fp.read(4));
				if(isLeaf == 1) {
					break;
				}
				else {
					//skip parent id
					fp.read(4);
					int valueNum = BufferManage.byte2Int(fp.read(4));
					
					int postion = 0;
					for(postion = 0; postion < valueNum; postion++) {
						byte[] key_value = fp.read(type_length);
						if(BplusTree.Tree.compare(Lower, key_value, type) == 2) {
							break;
						}
					}
					
					//get the position
					fp.move(current_offset + 4 + 4 + 4 + 3 * type_length + 3 * 4 + 4 );
					
					//get the next position
					fp.seek(postion * 4);
					current_offset = BufferManage.byte2Int(fp.read(4));
				}
			}
			
			//get the leaf
			
			while(true) {
				fp.move(current_offset);
				fp.seek(4 + 4);
				int valueNum = BufferManage.byte2Int(fp.read(4));
				for(int i = 0; i < valueNum; i++) {
					//current position
					fp.move(current_offset+12+i*type_length);
					
					byte[] keyValue = fp.read(type_length);
					if(BplusTree.Tree.compare(Lower, keyValue, type) < 2) {
						fp.move(current_offset + 12 + 3 * type_length + i * 4);
						int offset = BufferManage.byte2Int(fp.read(4));
						offsets.add(offset);
					}
				}
				fp.move(current_offset + 12 + 3 * type_length + 3 * 4);
				current_offset = BufferManage.byte2Int(fp.read(4));
				if(current_offset == -1) {
					break;
				}
			}
		}
		else if (Lower == null){
			int RootOffset = BufferManage.byte2Int(fp.read(4));
			if(RootOffset == -1) {
				System.out.println("The table is empty");
				return null;
			}
			int type = BufferManage.byte2Int(fp.read(4));
			int oneNodeSize = BplusTree.TreeNode.GetOneNodeSize(type);
			
			int current_offset = RootOffset;
			int type_length = type;
			if(type == 0 || type == 256) {
				type_length = 4;
			}
			while(true) {
				//read the 
				fp.move(current_offset);
				int isLeaf = BufferManage.byte2Int(fp.read(4));
				if(isLeaf == 1) {
					break;
				}
				else {
					//skip parent id
					fp.read(4);
					int postion = 0;
					//get the position
					fp.move(current_offset + 4 + 4 + 4 + 3 * type_length + 3 * 4 + 4 );
					
					//get the next position
					fp.seek(postion * 4);
					current_offset = BufferManage.byte2Int(fp.read(4));
				}
			}
			
			//get the leaf
			boolean end = false;
			while(true) {
				fp.move(current_offset);
				fp.seek(4 + 4);
				int valueNum = BufferManage.byte2Int(fp.read(4));
				for(int i = 0; i < valueNum; i++) {
					//current position
					fp.move(current_offset+12+i*type_length);
					
					byte[] keyValue = fp.read(type_length);
					if(BplusTree.Tree.compare(keyValue, Upper, type) < 2) {
						fp.move(current_offset + 12 + 3 * type_length + i * 4);
						int offset = BufferManage.byte2Int(fp.read(4));
						offsets.add(offset);
					}
					if(BplusTree.Tree.compare(Upper, keyValue, type) == 1) {
						end = true;
					}
				}
				fp.move(current_offset + 12 + 3 * type_length + 3 * 4);
				current_offset = BufferManage.byte2Int(fp.read(4));
				if(current_offset == -1) {
					break;
				}
				if(end) {
					break;
				}
			}
		}
		fp.close();
		return offsets;
	}
	
	
	
	public static ArrayList<Integer> SearchWithIndex(Index inx, byte[] value){
		String filename = inx.index_name + ".index";
		BufferOperator fp = new BufferOperator(filename);
		
		int RootOffset = BufferManage.byte2Int(fp.read(4));
		if(RootOffset == -1) {
			System.out.println("The table is empty");
			return null;
		}
		int type = BufferManage.byte2Int(fp.read(4));
		int oneNodeSize = BplusTree.TreeNode.GetOneNodeSize(type);
		
		int current_offset = RootOffset;
		int type_length = type;
		if(type == 0 || type == 256) {
			type_length = 4;
		}
		while(true) {
			//read the 
			fp.move(current_offset);
			int isLeaf = BufferManage.byte2Int(fp.read(4));
			if(isLeaf == 1) {
				break;
			}
			else {
				//skip parent id
				fp.read(4);
				int valueNum = BufferManage.byte2Int(fp.read(4));
				
				int postion = 0;
				for(postion = 0; postion < valueNum; postion++) {
					byte[] key_value = fp.read(type_length);
					if(BplusTree.Tree.compare(value, key_value, type) == 2) {
						break;
					}
				}
				
				//get the position
				fp.move(current_offset + 4 + 4 + 4 + 3 * type_length + 3 * 4 + 4 );
				
				//get the next position
				fp.seek(postion * 4);
				current_offset = BufferManage.byte2Int(fp.read(4));
			}
		}
		
		//get the leaf
		ArrayList<Integer> offsets = new ArrayList<Integer>();
		boolean end = false;
		while(true) {
			fp.move(current_offset);
			fp.seek(4 + 4);
			int valueNum = BufferManage.byte2Int(fp.read(4));
			for(int i = 0; i < valueNum; i++) {
				//current position
				fp.move(current_offset+12+i*type_length);
				
				byte[] keyValue = fp.read(type_length);
				if(BplusTree.Tree.compare(value, keyValue, type) == 1) {
					fp.move(current_offset + 12 + 3 * type_length + i * 4);
					int offset = BufferManage.byte2Int(fp.read(4));
					offsets.add(offset);
				}
				if(BplusTree.Tree.compare(keyValue, value, type) == 2) {
					end = true;
				}
			}
			if(end) {
				break;
			}
			fp.move(current_offset + 12 + 3 * type_length + 3 * 4);
			current_offset = BufferManage.byte2Int(fp.read(4));
			if(current_offset == -1) {
				break;
			}
		}
		fp.close();
		return offsets;
	}

}
