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
		
		[Doing]
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
