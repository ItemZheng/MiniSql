package Minisql;

import java.util.ArrayList;
import java.util.Arrays;
import Minisql.*;
import Minisql.BufferManage.BufferOperator;;

public class BplusTree {
	//Our MiniSql use B+ Tree with n = 4
		//Every node has at least 1 value and most 3 value
		//Every node has at least 2 pointer and most 4 pointer
		
		/*
		 * 	.index
		 * 	first 4 byte, save the root's offset
		 * 	when search jump to root and begin search
		 * 
		 * */
		
		static public class TreeNode{
			//the position ID: for write and read
			int positionId;
			int type;
			
			boolean isLeaf;
			TreeNode ParentNode;
			ArrayList<byte[]> values;
			
			// if is a leaf the offsets saves the offset of record
			// if is not a leaf it saves the Node Id 
			
			// leaf
			ArrayList<Integer> offsets;
			TreeNode		NextLeaf;
			//node
			ArrayList<TreeNode> NextNodes;
			
			public TreeNode(int type) {
				// TODO Auto-generated constructor stub
				//Default initial
				positionId = 0;
				isLeaf = false;
				NextLeaf = null;
				ParentNode = null;
				this.type = type;
				
				//initial the
				values = new ArrayList<byte[]>();
				NextNodes = new ArrayList<TreeNode>();	
				offsets = new ArrayList<Integer>();
			}
		
			public ArrayList<byte []> NodeWriteInfo() {
				// Not need to save the positionId
				// first write the isLeaf
				ArrayList<byte[]> nodes = new ArrayList<byte[]>();
				
				//isLeaf
				if(isLeaf) {
					nodes.add(BufferManage.Int2byte(1));
				}else {
					nodes.add(BufferManage.Int2byte(0));
				}
				
				//ParentId
				if(ParentNode != null) {
					nodes.add(BufferManage.Int2byte(ParentNode.positionId));
				}else {
					nodes.add(BufferManage.Int2byte(-1));
				}
				
				
				//3 Values
				nodes.add(BufferManage.Int2byte(values.size()));
				for(int i = 0; i < 3; i++) {
					if(i >= values.size()) {
						nodes.add(BufferManage.String2byte("", getLength(type)));
					}else{
						nodes.add(values.get(i));
					}
				}
				
				// offset 
				for(int i = 0; i < 3; i++) {
					if(i >= offsets.size()) {
						nodes.add(BufferManage.Int2byte(-1));
					}else{
						nodes.add(BufferManage.Int2byte(offsets.get(i)));
					}
				}
				
				//NextLeafOffset
				if(NextLeaf != null) {
					nodes.add(BufferManage.Int2byte(GetOffsetOfBlock(NextLeaf.positionId, type )));
				}else {
					nodes.add(BufferManage.Int2byte(-1));
				}
				
				
				//last next node
				for(int i = 0; i < 4; i++) {
					if(i >= NextNodes.size()) {
						nodes.add(BufferManage.Int2byte(-1));
					}else{
						nodes.add(BufferManage.Int2byte(GetOffsetOfBlock(NextNodes.get(i).positionId, type)));
					}
				}
				
				
				return nodes;
			}
		
			public static int GetOneNodeSize(int type) {
				int length = 0;
				
				//is leaf and parent node
				length = length + 4 + 4;
				
				//value num
				//and three value
				length = length + 4 + getLength(type) * 3;
				
				//offsets
				length = length + 3 * 4;
				
				//Next Leaf
				length = length + 4;
				//4 next Nodes
				length = length + 4 * 4;
				
				return length;
			}
			
			public static int GetOffsetOfBlock(int id, int type) {
				id++;
				int oneSize = GetOneNodeSize(type);
				int offset = 0;
				int LargeNodes = Global.BlockSize / oneSize;
				int BlockNum = id / LargeNodes;
				
				offset = BlockNum * Global.BlockSize + (id % LargeNodes) * oneSize;
				return offset;
			}
			
			private static int getLength(int type) {
				if((type == 0 || type == 256)) {
					return 4;
				}
				return type;
			}
		
			public static int getId(int offset, int type) {
				int oneSize = GetOneNodeSize(type);
				int id = -1;
				int LargeNodes = Global.BlockSize / oneSize;
				
				id = LargeNodes * (offset / Global.BlockSize) + (offset % Global.BlockSize) / oneSize;
				id--;
				return id;
			}
		
			public static byte[] getSubByte(byte[] sourse, int begin, int length) {
				//System.out.println(begin);
				byte []res = new byte[length];
				for(int i = 0; i < length; i++) {
					res[i] = sourse[begin+i];
				}
				return res;
			}
		}	
		
		static public class Tree{
			//prepare information
			private int type;
			private TreeNode Root;
			
			// all nodes
			ArrayList<TreeNode> nodes;
			
			//to generate the tree, we must read it from file or create a index
			public Tree(String filename) {
				//read tree from file
				BufferOperator fp = new BufferOperator(filename);
				int rootID = TreeNode.getId(BufferManage.byte2Int(fp.read(4)), type);
				type = BufferManage.byte2Int(fp.read(4));
				nodes = new ArrayList<TreeNode>();
				int NodeSize = BufferManage.byte2Int(fp.read(4));
				fp.move(TreeNode.GetOneNodeSize(type));
				
				ArrayList<Integer> ParentIds = new ArrayList<Integer>();
				ArrayList<Integer> NextLeafs = new ArrayList<Integer>();
				ArrayList<Integer[]> NextNodes = new ArrayList<Integer[]>();
				//System.out.println(NodeSize);
				for(int i = 0; i < NodeSize; i++) {
					TreeNode newNode = new TreeNode(type);
					
					byte[] node = fp.read(TreeNode.GetOneNodeSize(type));
					
					//isLeaf Ok
					int isLeaf = BufferManage.byte2Int(TreeNode.getSubByte(node, 0, 4));
					if(isLeaf == 1) {
						newNode.isLeaf = true;
					}else {
						newNode.isLeaf = false;
					}
					
					//parentID
					int ParentID = BufferManage.byte2Int(TreeNode.getSubByte(node, 4, 8));
					ParentIds.add(ParentID);
					
					int valueNum = BufferManage.byte2Int(TreeNode.getSubByte(node ,8, 12));
					
					//value ok
					int length = 12;
					for(int j = 0; j < valueNum; j++) {
						byte []value = TreeNode.getSubByte(node, length, TreeNode.getLength(type));
						newNode.values.add(value);
						length = length + TreeNode.getLength(type);
					}
					
					length = 12 + 3 * TreeNode.getLength(type);
					//offset
					if(newNode.isLeaf) {
						//offset
						for(int j = 0; j < valueNum; j++) {
							byte []value = TreeNode.getSubByte(node, length + j * 4, 4);
							int offset = BufferManage.byte2Int(value);
							newNode.offsets.add(offset);
						}
					}
					
					length = length + 3 * 4;
					
					//Next LeafId
					int nextLeaf = BufferManage.byte2Int(TreeNode.getSubByte(node, length, 4));
					NextLeafs.add(nextLeaf);
					length = length + 4;
						
					Integer[] nextIds =  new Integer[4];
					//only read the nextNodeIds
					for(int j = 0; j < 4; j++) {
						byte []value = TreeNode.getSubByte(node, length + j * 4, 4);
						int nextId = BufferManage.byte2Int(value);
						nextIds[j] = nextId;
					}
					NextNodes.add(nextIds);
					
					nodes.add(newNode);
				}
				
				//System.out.println(nodes.size());
				UpdateAllPositionId();
				Root = nodes.get(rootID); 
				for(int i = 0; i < NodeSize; i++) {
					//parent id
					int parentID = ParentIds.get(i);
					if(parentID == -1) {
						nodes.get(i).ParentNode = null;
					}else {
						nodes.get(i).ParentNode = nodes.get(parentID);
					}
					
					//Next leaf
					int nextLeafID = NextLeafs.get(i);
					if(nextLeafID == -1) {
						nodes.get(i).NextLeaf = null;
					}
					else {
						nodes.get(i).NextLeaf = nodes.get(TreeNode.getId(nextLeafID, type));
					}
					
					//Next nodes
					//System.out.println(i);
					Integer [] nextIDS = NextNodes.get(i);
					for(int j = 0; j < 4; j++) {
						if(nextIDS[j] == -1) {
							nodes.get(i).NextNodes.add(null);
						}
						else {
							nodes.get(i).NextNodes.add(nodes.get(TreeNode.getId(nextIDS[j], type)));
						}
					}
				}
				fp.close();
				
			}
			
			public Tree(int type) {
				// TODO Auto-generated constructor stub
				// initial 
				Root = null;
				nodes = new ArrayList<TreeNode>();
				this.type = type;
			}
			
			//insert one record and its
			public void InsertTree(byte[] value, int offset) {
				TreeNode Leaf = FindPosition(value);
				// if it is an empty tree
				if(Leaf == null) {
					//set the value of the newNode
					TreeNode newNode = new TreeNode(type);
					newNode.isLeaf = true;
					newNode.NextLeaf = null;
					newNode.offsets.add(offset);
					newNode.ParentNode = null;
					newNode.values.add(value);
					
					//change the value of the Tree
					Root = newNode;
					nodes.add(newNode);				
				}
				else{
					InsertToLeaf(value, offset, Leaf);
				}
				//System.out.println(nodes.size());
			}
			
			private void InsertToLeaf(byte[] value, int offset, TreeNode leaf) {
				int count = leaf.values.size();
				//get the position
				int position = 0;
				for(position = 0; position < leaf.values.size(); position++) {
					byte[] c_value = leaf.values.get(position);
					if(compare(value, c_value) == 0) {
						break;
					}
				}
				// already get the position
				if(count < 3) {
					//insert is OK
					leaf.values.add(position, value);
					leaf.offsets.add(position, offset);
				}else
				{
					//need to spilt the Node
					//add the new value into the node
					leaf.values.add(position, value);
					leaf.offsets.add(position, offset);
					
					//spilt it
					TreeNode newLeaf = new TreeNode(type);
					newLeaf.isLeaf = true;
					newLeaf.NextLeaf = null;
					leaf.NextLeaf = newLeaf; //link them
					
					newLeaf.ParentNode = leaf.ParentNode; //same parent
					newLeaf.offsets.add(leaf.offsets.get(2));
					newLeaf.offsets.add(leaf.offsets.get(3));
					leaf.offsets.remove(3);
					leaf.offsets.remove(2);
					
					newLeaf.values.add(leaf.values.get(2));
					newLeaf.values.add(leaf.values.get(3));
					leaf.values.remove(3);
					leaf.values.remove(2);
					nodes.add(newLeaf);
					//then insert the newNode to Parent
					InsertToNode(newLeaf.values.get(0), leaf, newLeaf, newLeaf.ParentNode);
				}
			}
			
			// insert insert node to TreeNode
			// need to update the son's parent
			private void InsertToNode(byte[] value, TreeNode firstNext, TreeNode secondNext, TreeNode node) {
				if(node == null) {
					//need a new Node
					//this must be Root
					TreeNode root = new TreeNode(type);
					root.isLeaf = false;
					root.values.add(value);
					root.NextNodes.add(firstNext);
					root.NextNodes.add(secondNext);
					nodes.add(root);
					//update Root
					Root = root;
					
					//update son's parent ID
					firstNext.ParentNode = root;
					secondNext.ParentNode = root;
				}else {
					//get the position
					int position = 0;
					for(position = 0; position < node.values.size(); position++) {
						byte[] c_value = node.values.get(position);
						if(compare(value, c_value) == 0) {
							break;
						}
					}
					//add first
					node.values.add(position, value);
					node.NextNodes.add(position+1, secondNext);
					
					//judge if it should been spilt
					if(node.values.size() > 3) {
						//split it
						TreeNode newNode = new TreeNode(type);
						newNode.isLeaf = false;
						newNode.values.add(node.values.get(3));
						byte [] insertValue = node.values.get(2).clone();
						node.values.remove(3);
						node.values.remove(2);
						
						newNode.NextNodes.add(node.NextNodes.get(3));
						newNode.NextNodes.add(node.NextNodes.get(4));
						node.NextNodes.remove(4);
						node.NextNodes.remove(3);
						
						newNode.ParentNode = node.ParentNode;
						
						//update son's Parent
						newNode.NextNodes.get(0).ParentNode = newNode;
						newNode.NextNodes.get(1).ParentNode = newNode;
						nodes.add(newNode);
						InsertToNode(insertValue, node, newNode, node.ParentNode);
					}
				}
			}
				
			// return
			// 0: less than
			// 1: equal
			// 2: large
			private int compare(byte[] value, byte[] c_value) {
				//int 
				try {
					if(type == 0) {
						int v = BufferManage.byte2Int(c_value);
						int newValue = BufferManage.byte2Int(value);
						if(newValue < v) {
							return 0;
						}else if(newValue == v) {
							return 1;
						}else if(newValue > v) {
							return 2;
						}
					}
					//float
					else if(type == 256) {
						float v = BufferManage.ByteToFloat(c_value);
						float newValue = BufferManage.ByteToFloat(value);
						if(newValue < v) {
							return 0;
						}else if(newValue == v) {
							return 1;
						}else if(newValue > v) {
							return 2;
						}
					}
					//string
					else if((type >= 1) && (type <= 255)) {
						String v = BufferManage.byte2String(c_value);
						String newValue = BufferManage.byte2String(value);
						if(newValue.compareTo(v) < 0) {
							return 0;
						}else if(newValue.equals(newValue)) {
							return 1;
						}else if(newValue.compareTo(v) > 0) {
							return 2;
						}
					}
					
					throw new Exception("INDEX_ERR: COMPARE_ERR");
				}catch (Exception e) {
					// TODO: handle exception
					System.out.println(e.toString());
					//BufferManage.saveAllBlock();
					System.exit(0);
				}
				return -1;

			}
			
			private TreeNode FindPosition(byte[] value) {
				if(Root == null) {
					return null;
				}
				//get the root
				TreeNode c_Node = Root;
				//compare the size
				while(true) {
					//this is leaf
					if(c_Node.isLeaf) {
						return c_Node;
					}
					//Judge Next Node Id
					int i;
					for(i = 0; i < c_Node.values.size(); i++) {
						byte[] c_value = c_Node.values.get(i);
						if(compare(value, c_value) == 0) {
							break;
						}
					}
					c_Node = c_Node.NextNodes.get(i);
				}
				
			}
			
			private void UpdateAllPositionId() {
				for(int i = 0; i < nodes.size(); i++) {
					//from 1
					//because the position 0 is to record the basic information of Tree
					nodes.get(i).positionId = i;
				}
			}
			
			public int Find(byte[] value) {
				
				int offset = -1;
				TreeNode leaf = FindPosition(value);
				
				/*while(leaf != null) {
					for(int i = 0; i < leaf.offsets.size(); i++) {
						System.out.println(leaf.offsets.get(i));
					}
					leaf = leaf.NextLeaf;
				}*/
				if(leaf != null) {
					for(int i = 0; i < leaf.values.size(); i++) {
						if(Arrays.equals(value, leaf.values.get(i))) {
							offset = leaf.offsets.get(i);
							break;
						}
					}
				}
				
				return offset;
			}
			
			public void Delete(byte[] value, int offset) {
				try {
					//find the tree node
					TreeNode leaf = FindPosition(value);
					if(leaf == null) {
						throw new Exception("B_PLUS_TREE_ERR: DELETE_ON_EMPTY_TREE");
					};
					deleteValueLeaf(value, offset, leaf);
				}
				catch (Exception e) {
					// TODO: handle exception
					System.out.println(e.toString());
					//BufferManage.saveAllBlock();
					System.exit(0);
				}
				//System.out.println(nodes.size());
			}
			
			private void deleteValueLeaf(byte[] value, int offset, TreeNode leaf) {
				//find the node
				//get the node position, delete it
				for(int i = 0; i < leaf.values.size(); i++) {
					if(Arrays.equals(value, leaf.values.get(i)) && (leaf.offsets.get(i) == offset)) {
						leaf.values.remove(i);
						leaf.offsets.remove(i);
						break;
					}
				}
				
				//Judge if this leaf is OK
				// the valid number is 2 - 3
				if(leaf.values.size() < 2 ) {
					//first get the left brother the Node
					TreeNode parent = leaf.ParentNode;
					//Root
					if((parent == null) && (leaf.values.size() == 0)) {
						nodes.remove(0);
						Root = null;
					}
					if(parent == null) {
						return;
					}
					//Not root
					//Get the Brother
					int position = 0;
					for(position = 0; position < parent.NextNodes.size(); position++) {
						if(parent.NextNodes.get(position) == leaf) {
							break;
						}
					}
					
					//find the position
					TreeNode brother;
					if(position != 0) {
						brother = parent.NextNodes.get(position-1);
						if(brother.values.size() > 2) {
							byte[] borrow_value = brother.values.get(2).clone();
							int	borrow_offset = brother.offsets.get(2);
							brother.values.remove(2);
							brother.offsets.remove(2);
							leaf.values.add(0, borrow_value);
							leaf.offsets.add(0, borrow_offset);
							
							//change the index
							parent.values.set(position-1, borrow_value);
							//OK
							return;
						}
					}
					else {
						brother = parent.NextNodes.get(position+1);
						if(brother.values.size() > 2) {
							byte[] borrow_value = brother.values.get(0).clone();
							int borrow_offset = brother.offsets.get(0);
							brother.values.remove(0);
							brother.offsets.remove(0);
							leaf.values.add(borrow_value);
							leaf.offsets.add(borrow_offset);
							//change the index
							parent.values.set(position, brother.values.get(0));
							//OK
							return;
						}
					}
					
					//Brother Can not borrow
					if(position != 0) {
						brother = parent.NextNodes.get(position-1);
						MergeTwoLeafNode(brother, leaf);
					}else {
						brother = parent.NextNodes.get(position+1);
						MergeTwoLeafNode(leaf, brother);
					}
				}
			}
		
			private void MergeTwoLeafNode(TreeNode leaf1, TreeNode leaf2) {
				//add all values
				for(int i = 0; i < leaf2.values.size(); i++) {
					leaf1.values.add(leaf2.values.get(i));
					leaf1.offsets.add(leaf2.offsets.get(i));
				}
				leaf1.NextLeaf = leaf2.NextLeaf;
				
				//update parent
				int position = 0;
				TreeNode parent = leaf1.ParentNode;
				for(position = 0; position < parent.NextNodes.size(); position++) {
					if(parent.NextNodes.get(position) == leaf1) {
						break;
					}
				}
				
				parent.values.remove(position);
				parent.NextNodes.remove(position+1);
				nodes.remove(leaf2);
				
				if(parent.values.size() < 1) {
					AdjustNoneLeafNode(parent);
				}
			}
			
			private void AdjustNoneLeafNode(TreeNode T) {
				if(T.values.size() > 0) {
					return; //OK
				}
				TreeNode parent = T.ParentNode;
				if(parent == null) {
					Root = T.NextNodes.get(0);
					Root.ParentNode = null;
					//Root
					nodes.remove(T);
					return;
				}
				
				//parent is not null
				//get the position
				int position = 0;
				for(position = 0; position < parent.NextNodes.size(); position++) {
					if(parent.NextNodes.get(position) == T) {
						break;
					}
				}
				
				//has get the position
				TreeNode brother;
				if(position != 0) {
					brother = parent.NextNodes.get(position-1);
					if(brother.values.size() > 1) {
						//first add the parent value to T
						T.values.add(0, parent.values.get(position));
						
						//parentOK
						int brosize = brother.values.size();
						parent.values.set(position, brother.values.get(brosize-1));
						T.NextNodes.add(0, brother.NextNodes.get(brosize));
						
						//remove the value
						brother.values.remove(brosize-1);
						brother.NextNodes.remove(brosize);
						return;
					}
				}else {
					//position = 0
					brother = parent.NextNodes.get(position+1);
					if(brother.values.size() > 1) {
						//first add the parent value to T
						T.values.add(parent.values.get(0));
						
						//parentOK
						parent.values.set(0, brother.values.get(0));
						T.NextNodes.add(brother.NextNodes.get(0));
						
						//remove the value
						brother.values.remove(0);
						brother.NextNodes.remove(0);
						return;
					}
				}
				
				//Brother can not borrow
				if(position != 0) {
					brother = parent.NextNodes.get(position-1);
					brother.values.add(parent.values.get(position-1));
					brother.NextNodes.add(T.NextNodes.get(0));
					T.NextNodes.get(0).ParentNode = brother;
					
					parent.values.remove(position-1);
					parent.NextNodes.remove(position);
					
					nodes.remove(T);
				}else {
					//position = 0
					brother = parent.NextNodes.get(position+1);
					
					//brother OK
					brother.values.add(0, parent.values.get(0));
					brother.NextNodes.add(0, T.NextNodes.get(0));
					T.NextNodes.get(0).ParentNode = brother;
					
					parent.values.remove(0);
					parent.NextNodes.remove(0);
					
					nodes.remove(T);
				}
				
				AdjustNoneLeafNode(parent);
			}
		
			public void saveToFile(String filename) {
				UpdateAllPositionId();
				int oneNodeSize = TreeNode.GetOneNodeSize(type);
				
				//first write the Basic information of Tree
				BufferOperator fp = new BufferOperator(filename);
				fp.write(BufferManage.Int2byte(TreeNode.GetOffsetOfBlock(Root.positionId, type)));
				fp.write(BufferManage.Int2byte(type));
				fp.write(BufferManage.Int2byte(nodes.size()));
				fp.write(BufferManage.String2byte("", oneNodeSize-12));
				
				for(int i = 0; i < nodes.size(); i++) {
					fp.write(nodes.get(i).NodeWriteInfo());
				}
				fp.close();
			}

		}
}
