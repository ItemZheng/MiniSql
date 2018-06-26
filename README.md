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
		
		
		[Done]
		delete from 表名 ;
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
			void 	bufferRead(String fileName, int OfferSet);
			void    bufferWrite();
			
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
			...
		
		tablename.record
			value1 value2 value3 ...
		
		indexname.index
			value1 value2 value3 offset1 offset2  offset3
			....
			
+ Index 结构
		
		Index:
			string 	name
			string 	table_name
			string 	attribute_name


+ Attribute 结构
		
		Attribute:
			int				length
			String 			name
			int 			type	
			bool			isPrimaryKey
			bool 			isUnique
		
+ Table 结构
		
		Table:
			int					table_length
			String 				table_name
			vector<Attribute>	attributes
			vector<Index>		
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



