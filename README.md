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

##### Record FILE #####

+ Record 存储格式
	
		按字节存储
		如student( id int, name char(3), grade float);
		每一条在文件中记录，如果两个记录：
			2332, zyh, 99.0
			2602, xcl, 99.0
		则在文件中：
			前四个字节（int）表示2332，后三个字节（char[3]）表示zyh，然后四个字节（float）表示99.0，共11个字节，
			接下来11个字节表示第二条记录
  
+ 缓冲区操作

		块大小 4KB = 4048字节 ，缓冲区按块读取或写入文件。
		每一页设置（待决定）
		缓冲区大小  512KB， 也就是可以读取128块。
		读取记录的时候，每次按块的大小读取，普通查找的时候按顺序将所有的块一块块读入缓冲区，直到找到做所
		需要的记录。注意记录缓冲区中每一块的位置和是否被修改，程序结束的时候要把块协会文件。
		
##### Catelog File #####
	
+ 存储格式(文本格式即可)
		
		begin
		relation_name
		attribute_num
		attribute_name    type   isUnique   index_num   index_name1
		....
		end

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
