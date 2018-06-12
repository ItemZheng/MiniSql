# DatabaseSummerProject

####① Interpreter

######功能：
+ 检查用户输入的语法，如果正确，转化为参数，调用API；错误，则直接给出语法层次的错误信息。

	API 接口：
		`API(int op, string [] arges)`

+ 支持的语法：
	
		create table 表名 (
			列名 类型 ,
			列名 类型 ,
			列名 类型 ,
			primary key ( 列名 )
		);				
		
		drop table 表名 ;
		
		
		create index 索引名 on 表名 ( 列名 );

		
		drop index stunameidx;

		
		select * from 表名 ;
		或：
		select * from 表名 where 条件 ;

		
		insert into 表名 values ( 值1 , 值2 , … , 值n );
		
		
		delete from 表名 ;
		或：
		delete from 表名 where 条件 ;
	
