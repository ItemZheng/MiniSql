# DatabaseSummerProject

####① Interpreter

######功能：
+ 检查用户输入的语法，如果正确，转化为参数，调用API；错误，则直接给出语法层次的错误信息。

	API 接口：
		`API(String [] arges)`

+ 支持的SQL语法：
		
	
		create table 表名 (
			列名 类型 ,
			列名 类型 ,
			列名 类型 ,
			primary key ( 列名 )
		);
		arguments: []
						
		[Doing]
		create index 索引名 on 表名 ( 列名 );		
		arguments: [1, index_name, table_name, column_name]

		
		[Done]
		drop index 索引名;
		arguments: [2, index_name]

		[Done]
		drop table 表名;
		arguments: [3, table_name]		

		
		select * from 表名 ;
		或：
		select * from 表名 where 条件 ;
		arguments: []	
		
		
		insert into 表名 values ( 值1 , 值2 , … , 值n );
		arguments: []	
		
		
		delete from 表名 ;
		或：
		delete from 表名 where 条件 ;
		arguments: []

+ 退出
		
		[Done]
		quit;
		arguments: [quit]
		

+ 执行 SQL 脚本
	
		execfile 文件名;
		arguments: [7, file_name];
