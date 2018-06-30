package Minisql;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;

class Global {
	public static final int BlockSize = 4 * 1024;	//4KB
	public static final int BlockNum  = 128;			//128 blocks
	public static final int INF	 = 10000000;
	public static final int ERR  = -1;
	public static final int UNUSED = -1;
};

class BufferBlock{
	// public variables
	public String filename;
	public int offset;
	public byte []value = new byte[Global.BlockSize];
	public int usetime;
	public boolean isLock;
	public boolean isChange;
	
	//constructor
	public BufferBlock() {
		filename = null;
		offset   = -1;
		isLock 	 = false;
		isChange = false;
		usetime  = Global.UNUSED;
		//initial the value
		int i;
		for(i = 0; i < Global.BlockSize; i++) {
			value[i] = 0;
		}
	}
	
	//functions
	void WriteFile() {
		//write to file
		//is filename is null
		try {
			if(filename == null) {
				throw new Exception("BUFFER_ERR: WRITE_FILE_WITH_NAME_NULL");
			}
			//Then write to file
			if(isChange) {
				RandomAccessFile file = new RandomAccessFile(filename, "rw");
				file.seek(offset * Global.BlockSize);
				file.write(value);
				file.close();
			}	
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.toString());
			BufferManage.saveAllBlock();
			System.exit(0);
		}		
	}
	void ReadFile(String filename, int offset) {
		//judge if is changed
		if(isChange) {
			//save changes
			WriteFile();
		}
		//initial the block
		this.filename = filename;
		this.offset = offset;
		isChange = false;
		usetime = 1;
		
		try {
			//read file
			File  file = new File(filename);
			if(!file.exists()) {
				//create file
				file.createNewFile();
			}
			
			//exist this file
			RandomAccessFile reader = new RandomAccessFile(filename, "r");
			reader.seek(offset * Global.BlockSize);
			reader.read(value);
			reader.close();
			
		}catch (Exception e) {
			// TODO: handle exception
			System.out.print(e.toString());
			BufferManage.saveAllBlock();
			System.exit(0);
		}
	}
	
	void Writebyte(int offset, byte[] value){
		usetime++;
		isChange = true;
		
		int i;
		for(i = 0; i < value.length; i++) {
			this.value[i+offset] = value[i];
		} 
	}
	
	byte[] Readbyte(int offset, int length) {
		usetime++;
		
		byte[] readDat = new byte[length];
		int i;
		for(i = 0; i < length; i++) {
			readDat[i] = value[i+offset];
		}
		return readDat;
	}
	
	void ClearBlock() {
		filename = null;
		offset   = -1;
		isLock 	 = false;
		isChange = false;
		usetime  = -1;
		//initial the value
		int i;
		for(i = 0; i < Global.BlockSize; i++) {
			value[i] = 0;
		}
	}
	
};


class BufferManage {
	//private manage block
	private static BufferBlock [] blocks = new BufferBlock[Global.BlockNum];
	
	//sun class
	public static class BufferOperator{
		String filename;
		private int pos;		//pos of current file position
		private int block_id;
		
		//construct: prove all date is right
		//no arguments		
		//open with the filename and pos = 0
		BufferOperator(String filename){
			this.filename = filename;
			pos = 0;
			// Find the block ID
			block_id = getBlockId(filename, pos / Global.BlockSize);
			blocks[block_id].isLock = true;
		}
		
		//open with pos
		BufferOperator(String filename, int pos){
			this.filename = filename;
			this.pos = pos;
			//Find the block ID
			block_id = getBlockId(filename, pos / Global.BlockSize);
			blocks[block_id].isLock = true;
		}
		
		/*
		 * 	Provided Function :
		 *	FileOperator fp = new FileOperator(filename);
		 *	fp.readString();
		 *	fp.Write
		 */
		public void seek(int offset) {
			pos = pos + offset;
		}
		
		public void move(int offset) {
			pos = offset;
		}
		
		public int getOffset() {
			return pos;
		}
		
		public void close() {
			//release this class
			try {
				if((filename == null) || (block_id == -1)) {
					throw new Exception("BUFFER_ERR: CLOSE_FILE_WITH_NAME_NULL");
				}else {
					blocks[block_id].isLock = false;
					filename = null;
					pos = -1;
					block_id = -1;
				}
			}catch (Exception e) {
				// TODO: handle exception
				System.out.println(e.toString());
				saveAllBlock();
				System.exit(0);
			}
		}
		
		public void write(ArrayList <byte[]> values) {
			//write from pos
			//ArrayList <byte[]> value = new ArrayList<byte[]>();
			//calculate the length of values
			int i, length = 0;
			for(i = 0; i < values.size(); i++) {
				length = length + values.get(i).length;
			}
			
			//judge if need to change to next to block
			//unlock this block
			blocks[block_id].isLock = false;
			if(pos % Global.BlockSize + length > Global.BlockSize) {
				//update the pos
				pos = (pos / Global.BlockSize + 1) * Global.BlockSize;
			}
			if(pos / Global.BlockSize != blocks[block_id].offset) {
				block_id = getBlockId(filename, pos / Global.BlockSize); 
			}
			blocks[block_id].isLock = true;
			
			for(i = 0; i < values.size(); i++) {
				blocks[block_id].Writebyte(pos % Global.BlockSize, values.get(i));
				//update pos
				pos = pos + values.get(i).length;
			}
		}
		
		public void write(byte[] values) {
			int i, length = values.length;
			
			//judge if need to change to next to block
			//unlock this block
			blocks[block_id].isLock = false;
			if(pos % Global.BlockSize + length > Global.BlockSize) {
				//update the pos
				pos = (pos / Global.BlockSize + 1) * Global.BlockSize;
			}
			if(pos / Global.BlockSize != blocks[block_id].offset) {
				block_id = getBlockId(filename, pos / Global.BlockSize); 
			}
			blocks[block_id].isLock = true;
			
			blocks[block_id].Writebyte(pos % Global.BlockSize, values);
			//update pos
			pos = pos + length;
		}
		
		//read length byte from pos
		public byte[] read(int length) {	
			//judge if need to change to next to block
			//unlock this block
			blocks[block_id].isLock = false;
			if(pos % Global.BlockSize + length > Global.BlockSize) {
				//update the pos
				pos = (pos / Global.BlockSize + 1) * Global.BlockSize;
			}
			if(pos / Global.BlockSize != blocks[block_id].offset) {
				block_id = getBlockId(filename, pos / Global.BlockSize); 
			}
			blocks[block_id].isLock = true;
			
			byte[] data = blocks[block_id].Readbyte(pos % Global.BlockSize, length);
			pos = pos + length;
			return data;
		}
	}
	
	public static void Init(){
		// just initial the buffer
		int i;
		for(i = 0; i < Global.BlockNum; i++) {
			blocks[i] = new BufferBlock();
		}
	}
	
	//functions, (offset is the pos / 4K) 
	private static int getBlockId(String name, int offset) {
		try {
			int i, mintimes = Global.INF, minID = -1;
			for(i = 0; i < Global.BlockNum; i++) {
				//judge the block if is in block
				if(name.equals((blocks[i].filename)) && (blocks[i].offset == offset)) {
					if(blocks[i].isLock) {
						throw new Exception("BUFFER_ERROR: Can not open a file twice!");
					}
					//not lock
					return i;
				}
			}
			
			for(i = 0; i < Global.BlockNum; i++) {
				//find the unused
				if((blocks[i].usetime == Global.UNUSED) && (!blocks[i].isLock)) {
					blocks[i].ReadFile(name, offset);
					return i;
				}
			}
			//means all blocks are used
			//find the min-used
			for(i = 0; i < Global.BlockNum; i++) {
				if(!blocks[i].isLock && (mintimes > blocks[i].usetime)) {
					minID = i;
					mintimes = blocks[i].usetime;
				}
			}
			
			if(minID == -1) {
				throw new Exception("BUFFER_ERROR: All blocks have been locked!");
			}
			blocks[minID].ReadFile(name, offset);
			return minID;
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.toString());
			//save all blcoks to file 
			saveAllBlock();
			System.exit(0);
		}
		return Global.ERR;
	}

	public static void saveAllBlock() {
		//save all block
		int i;
		for(i = 0; i < Global.BlockNum; i++) {
			if(blocks[i].isChange) {
				//write buffer to file
				blocks[i].WriteFile();
			}
		}
	}
	
	public static void dropFile(String filename) {
		//Delete file
		//Clear this file in buffer
		File file = new File(filename);
		if(file.exists()) {
			file.delete();
		}
		//Clear buffer
		try {
			int i;
			for(i = 0; i < Global.BlockNum; i++) {
				if(filename.equals(blocks[i].filename)) {
					if(blocks[i].isLock) {
						throw new Exception("BUFFER_ERROR: delete a block when it is locked!");
					}
					blocks[i].ClearBlock();
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.toString());
			saveAllBlock();
			System.exit(0);
		}
	}
	
	public static byte[] Int2byte(int number) {
		byte [] target = new byte[4];
		target[0] = (byte) ((number >> 24) & 0xFF);
		target[1] = (byte) ((number >> 16) & 0xFF);
		target[2] = (byte) ((number >>  8) & 0xFF);
		target[3] = (byte) (number & 0xFF);
		return target;
	}

	public static int byte2Int(byte[] sourse) {
		int target = (int)(((sourse[0] & 0xFF) << 24) | ((sourse[1] & 0xFF)<<16) | ((sourse[2] & 0xFF)<<8) | (sourse[3] & 0xFF));
		return target;
	}
	
	public static byte[] String2byte(String source, int length) {
		byte [] str = new byte[length];
		int i;
		for(i = 0; i < length; i++) {
			if(i < source.length()) {
				str[i] = (byte) source.charAt(i);
			}
			else {
				str[i] = 0;
			}
		}
		return str;
	}
	
	public static String byte2String(byte[] source) {
		String str = new String(source);
		return str;
	}
	
	public static byte[] Float2Byte(float x) {
		int fbit = Float.floatToIntBits(x);
		return Int2byte(fbit);
	}
	
	public static float ByteToFloat(byte[] source) {
		int v = byte2Int(source);
		return Float.intBitsToFloat(v);
	}
}
