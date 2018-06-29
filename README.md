# DatabaseSummerProject

###  ① Interpreter ###

#### 功能： ####
+ 检查用户输入的语法，如果正确，转化为参数，调用API；错误，则直接给出语法层次的错误信息。

	API 接口：
		`API(String [] arges)`

+ 支持的SQL语法：
		
		[Done]
		create table 表名 (
			列名 类型 ,
			列名 类型 ,
			列名 类型 ,
			primary key ( 列名 )
		);
		arguments: [0, table_name, colomn_number, 
					col1_name, col1_type, col1_is_unique,
					col2_name, col2_type, col2_is_unique,
					....
					primary_key1, primary_key2,.....	
					]
		for example: 	create table book( 
							bno int, 
							bname char(8) unique, 
							price int, 
							primary key(bno)
						);
		and its arguments:	[0, book, 3,   bno, int, 0,  bname, char8, 1,  price, int, 0,  0]		

						
		[Done]
		create index 索引名 on 表名 ( 列名 );		
		arguments: [1, index_name, table_name, column_name]

		
		[Done]
		drop index 索引名;
		arguments: [2, index_name]

		[Done]
		drop table 表名;
		arguments: [3, table_name]		

		[Done]
		select * from 表名 ;
		arguments: [4, table_name,0,0]
		e.g.  select * from abc
			[4, abc, 0, 0]
	
		或：
		select * from 表名 where 条件 ;
		arguments: [4, table_name, col_num, col1, col2, col3.....
					condition_num, left1, op1, right1, right_isString1....]
					if col_num == 0 means all
		e.g. 	select * from student where sage > 20 and sgender = 'F';
				[ 4, student, 0,  2, sage, >, 20, 0, sgender, =, F, 1]		
		
		[Done]
		insert into 表名 values ( 值1 , 值2 , … , 值n );
		arguments: [5, table_name, values_num,
					value1, isString, .....]	
		e.g	insert inTO student vALues ('12345678','wy',22,'M');

		[5, student, 4, 12345678, 1, wy, 1, 22, 0, M, 1]
		
		
		[Done]
		delete from 表名 ;
		arguments: [6, table_name, 0]
		e.g.  delete from sasda;
		     [6, sasda, 0]
		
		或：
		delete from 表名 where 条件 ;
		arguments: [6, table_name, condition_num, left1, op1, right1, right_isString1....]
					if col_num == 0 means all
		e.g. 	delete from student where sage > 20 and sgender = 'F';
				[ 6, student, 2, sage, >, 20, 0, sgender, =, F, 1]	
		

+ 退出
		
		[Done]
		quit;
		arguments: [quit]
		

+ 执行 SQL 脚本
		
		[Done]
		execfile 文件名;
		arguments: [7, file_name];

###   ② DB File ###

##### Buffer #####

  
+ 缓冲区操作

		块大小 4KB = 4048字节 ，缓冲区按块读取或写入文件。
		缓冲区大小  512KB， 也就是可以最大128块。

+ 缓冲区结构
		
		Bufferblock:
			String 	filename
			int 	Offset			//offset % 4kb should be zero
			bool  	isLock
			Byte    values[4*1024]	//4KB
			int		Usage 			//to record use time recently
			byte[]  getValue(int begin, int length);
			int 	getInt(int pos);   //get four byte
			char[]  getChar(int pos, int length);
			float 	getFloat();
			void    setByte(int pos, Byte [] value);
			void    setInt(int pos, int value);
			void    setFloat(int pos, float value);
			void    setChar(int pos, char[] value);
		
		Buffer:
			Bufferblock		blocks[128];
			int 	getReplaceBlockID();
			int 	bufferRead(String fileName, int OfferSet);
			
			void    bufferWrite();

+ 接口
	
		变量
			Global.BlockSize
			Global.BlcokNum
		函数
			Class BufferManage;
				BufferManage.Init();
				void saveAllBlock();   //write buffer to file
				void dropFile(String filename);  //delete file both file system and buffer
				byte[] Int2byte(int number);
				int byte2Int(byte[] sourse);
				byte[] String2byte(String source, int length);
				String byte2String(byte[] source);
				byte[] Float2Byte(float x);
				float ByteToFloat(byte[] source);
				
			Class BufferOperator;
				BufferOperator(String filename);
				BufferOperator(String filename, int pos);
				public void seek(int offset);    //move offset from current position
				public void move(int offset);	 //move offser from head of file
				void write(ArrayList <byte[]> values);
				void write(byte[] values);
				byte[] read(int length);
				void close();
+ Example
		
		BufferManage.Init();	/* At the begin of program*/
		
		BufferOperator fp = new BufferOperator("table.record");
		
		int record_num = 2;
		//first write the record number
		fp.write(BufferManage.Int2byte(record_num));
		
		int i;
		for(i = 0; i < record_num; i++) {
			ArrayList<byte[]> records = new ArrayList<byte[]>();
			int id = i;
			String name = "zyh";
			float grade = (float)99.0;
			
			records.add(BufferManage.Int2byte(id));
			records.add(BufferManage.String2byte(name, 3));
			records.add(BufferManage.Float2Byte(grade));
			
			fp.write(records);
		}
		fp.close();
		/*byte []recordnum = fp.read(4);
		System.out.println(BufferManage.byte2Int(recordnum));
		//System.out.println(recordnum);
		fp.close();*/
		BufferManage.saveAllBlock();

###use###
+ create table
 
		检查table.catelog里tablename存在否，存在报错，不存在就在这里加入此表信息。检查primary key，如果存在就调用建立索引。
		如果有多个Primary key 则只为第一个建立索引 
+ drop table 表名
		检查tablename存在否table.catelog.  drop所有该表的index。删除tablename.record 
		同时也要删除缓冲区的.更新table.catelog。
+ create index

		检查表名列名 ，检查index名字是否在index.catelog存在 。存在报错，
		不存在就在index.catelog写入一个新的索引。根据这个索引和这个表新建一个B+树 .index文件。
+ drop index

		检查index名字是否在index.catelog存在 。不存在报错，存在就在index.catelog删除索引和相应的 .index 的b+树文件。

+ select*/attributes from 表名  (where)

		在table.catelog里判断是不是有这个表。
		有的话就判断有没有where，没有的话就读出tablename.record，然后根据要求的属性输出。
		有多个条件的话就判断列名是否存在，再判断某个条件的属性是不是索引，在index.catelog里找。
		如果是索引，先处理它，去对应的indexname.index里读出符合条件的B+树，若还有条件就再
		去tablename.record里面找，返回的就是相应的offset（存到一个数组） 
		再写个函数根据offset数组去tablename.record里找再输出。。
		没有索引就直接读tablename.record

+ insert into 表名 values(value1,value2,value3...)

		检查表名.检查value类型、个数。再检查 Unique，primary（如果primary有多列也要考虑）
		给缓冲区tablename.record的参数，让缓冲区来写，这条记录直接放在这个.record文件文件的最后
		所有建过索引的列名  更新对应的B+： 从indexname.index读出、建立B+树结构，再插入新的结点，
		然后再写入indexname.index
+ delete from 表名 ;

		判断table.catelog中表名是否存在。清空（null）缓冲区的tablename.record
 
+ delete from 表名 where 条件 ;

		判断表名、条件的列名存在否。
		先select对应的操作 返回对应每条记录的offset数组。。
		按照从大到小的顺序删除记录（删除的同时从这个表的记录的最后一条提上来补充空位。。）

			
##### DB Files #####


+ File Structure

		table.catelog		//save all information of table and its attributes
		index.catelog 		//save all indexs of all the tables
		tablename.record	//save all records of the table records
		indexname.index		//save all information of B+ tree
	
+ 存储格式
		
		table.catelog
			table1_name
			attribute_name1    type1   isUnique1  isPrimary1
			...
			-1
		
		index.catelog
			index_name	tablename	attributename 
			-1
			...
		
		tablename.record
			record_num
			value1 value2 value3 ...
		
		indexname.index
			value1 value2 value3 offset1 offset2  offset3
			....
			
+ Index 结构
		
		Index:
			string 	index_name
			string 	table_name
			string 	attribute_name


+ Attribute 结构
		
		Attribute:
			int				length
			String 			name
			int 			type //0->int,256->float,1~255->char()
			bool			isPrimaryKey
			bool 			isUnique
		
+ Table 结构
		
		Table:
			int					table_length
			String 				table_name
			vector<Attribute>	attributes
			vector<Index>		indexes
			bool				isExistIndex(string indexName);
			Table 	`			tableRead(string tableName);
			void  				tableWrite(string tableName);
			bool				isExistTable(string tableName);
+ Leaf 结构
			
		Leaf:	
			value
			order
			offset


+ Node 结构
		
		Node:
			value1 
			value2
			value3
			NodeId 1``
			NodeId 2
			NodeId 3
			NodeId 4
			Leaf      //if it is inner node, it should be NULL

+ B+ Tree结构
		
		BplusTree:
			string 	indexname;
			vector<Node> nodes;		//all nodes, first node should be rrot
			vector<Leaf> leaves;
			void ReadFrom();	// read to buffer,then read buffer to rebuliud a tree
			void InsertNode(value);
			void DeleteNode(value);
			void WriteTo();
			
			

##### Index File #####

+ 用途
	
		管理B+数索引
+ 存储格式
		
		字节
		每个节点一共需要  4个偏移量（即在这个后面多少字节， 相当于指针），3个数据（根据数据类型确定）
		可以实现在索引中飞快跳跃，实现对位置的查找
		最后的叶子保存的是每条记录的在record文件中的偏移量，最后可以直接定位到这条记录。

+ 操作
		
		搜索，插入，删除



