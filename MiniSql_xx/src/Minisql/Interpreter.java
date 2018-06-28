package Minisql;
import java.awt.desktop.PrintFilesEvent;
import java.io.*;
import java.util.*;

public class Interpreter {
	
	public static void start() {
		// TODO Auto-generated method stub
		//initial start minisql
		InitMiniSql();
		//handle the command input
		HandleCommand();
	}
	
	public static void InitMiniSql(){
		//print welcome
		System.out.println("Welcome to use Minisql!");
	}
	
	public static void HandleCommand() {
		while(true)
		{
			//means input a 
			System.out.print("Minisql->");
			//read the ; and execute
			String command = "";
			try {
				//read a line
				BufferedReader s = new BufferedReader(new InputStreamReader(System.in));
				String str = s.readLine();
				
				//add the string to command until there is a ;
				command = command + str;
				while(!str.contains(";"))
				{
					//tip, input not end
					System.out.print("       ->");
					//read a new line
					str = s.readLine();
					command = command + " " + str;
				}
				//System.out.println(command);
			}catch (Exception e) {
				System.out.println(e.toString());
			}
			
			//check syntax
			List argv = CheckSyntax(command);
			if(argv == null)
			{
				//invalid command
				continue;
			}
			else if( argv.get(0).equals("7"))
			{
				//execute file
				String filename = argv.get(1).toString();
				//System.out.println(filename);
				List commands = GetCommandInFile(filename);
				if(commands != null)
				{
					System.out.println("Get " + commands.size() +" commands!\n");
					int i = 0, count = 0;
					boolean quit = false;
					for(i = 0; i < commands.size(); i++)
					{
						String cmd = commands.get(i).toString();
						System.out.println("Execute command : " + cmd.substring(0, cmd.length()-1) + " ...");
						//check the command!
						argv = CheckSyntax(cmd);
						if(argv != null)
						{
							count = count + 1;
							int res= API.API_Module(argv);
							if (res==0) break;
							else if (res==1) {
								System.out.println("Execute successfully!");
								continue;
							}
							else 
								{
									System.out.println("Error API return arguments!");
									break;
								}
						}
						System.out.println();
					}
					System.out.println("Finish execute commands! There are " + commands.size() + " commands totally and " + count + 
										" commands successfully! ");
				}
			}
			else 
			{
				int res= API.API_Module(argv);
				if(res==0) break;
				else if (res==1) continue;
				else 
					{
						System.out.println("Error API return arguments!");
						break;
					}
			}
		}
		
		System.out.println("Bye~");
	}
	
	public static List GetCommandInFile(String filename)
	{
		String content = "";
        try {
                String encoding="UTF-8";
                File file = new File(filename);
                InputStreamReader read = new InputStreamReader(new FileInputStream(file),encoding); //encode type
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while((lineTxt = bufferedReader.readLine()) != null){
                    content = content + " " + lineTxt;
                }
                read.close();
        }catch (Exception e) {
        	System.out.println("Error in read file!");
            return null;
        }
        
        //not have any command
        if(content.equals("")) {
        	System.out.println("This file does not has any command !");
        	return null;
        }
        
        //get all commands and end with ;
        List commands = new ArrayList();
        //there is an ;
        while(content.indexOf(";") != -1)
        {
        	//spilt commands
        	String newCommand = content.substring(0, content.indexOf(";") + 1);
        	content = content.substring(content.indexOf(";") + 1);
        	
        	int i = 0;
    		while(newCommand.charAt(i) == ' ' || newCommand.charAt(i) == '\t' || newCommand.charAt(i) == '\n') i++;
    		newCommand = newCommand.substring(i);
        	commands.add(newCommand);
        }
        
        if(commands.isEmpty()){
        	System.out.println("This file does not has any valid command !");
        	return null;
        }
		return commands;
	}
	
	public static List CheckSyntax(String command)
	{
		//Check if end with ;
		int index = command.indexOf(';');
		for(index++ ; index < command.length();index++)
		{
			//there is some thing after ;
			if(command.charAt(index) != '\t' && command.charAt(index) != ' ')
			{
				System.out.println("Syntax Error! Please end with a \" ; \"!");
				return null;
			}
		}
		
		//get first space
		int i = 0;
		while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
		command = command.substring(i);
		
		//get first argv
		i = 0;
		while(command.charAt(i) != ' ' && command.charAt(i) != '\t' && command.charAt(i) != '\n' && command.charAt(i) != ';') i++;
		String op = command.substring(0, i).toLowerCase();
		
		op = op.toLowerCase();
		command = command.substring(i);
		
		if(op.equals("create"))
		{
			//judge second argv
			//skip space
			i = 0;
			while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
			command = command.substring(i);
			//get second argv
			i = 0;
			while(command.charAt(i) != ' ' && command.charAt(i) != '\t' && command.charAt(i) != '\n' && command.charAt(i) != ';') i++;
			String arv1 = command.substring(0, i).toLowerCase();
			command = command.substring(i);
			
			//create table
			if(arv1.equals("table"))
			{
				return CheckCreateTable(command);
			}
			//create index
			else if(arv1.equals("index"))
			{
				return CheckCreateIndex(command);
			}
			//else syntax error
			else
			{
				System.out.println("Syntax Error! Expected table or index!");
			}
		}
		else if(op.equals("drop"))
		{
			//judge second argv
			//skip space
			i = 0;
			while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
			command = command.substring(i);
			//get second argv
			i = 0;
			while(command.charAt(i) != ' ' && command.charAt(i) != '\t' && command.charAt(i) != '\n' && command.charAt(i) != ';') i++;
			String arv1 = command.substring(0, i).toLowerCase();
			command = command.substring(i);
			
			//drop table
			if(arv1.equals("table"))
			{
				return CheckDropTable(command);
			}
			//drop index
			else if(arv1.equals("index"))
			{
				return CheckDropIndex(command);
			}
			//else syntax error
			else
			{
				System.out.println("Syntax Error! Expected table or index!");
			}
		}
		else if(op.equals("select"))
		{
			return CheckSelect(command);
		}
		else if(op.equals("insert"))
		{
			return CheckInsert(command);
		}
		else if(op.equals("delete"))
		{
			return CheckDelete(command);
		}
		else if(op.equals("quit"))
		{
			List argv = new ArrayList();
			argv.add(op);
			
			//skip all '\t' and ' ' and '\n' 
			i = 0;
			while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
			command = command.substring(i);
			
			if(command.charAt(0) != ';')
			{
				System.out.println("Syntax Error! After quit expect nothing!");
				return null;
			}
			return argv;
		}
		else if(op.equals("execfile"))
		{
			return CheckExecuteFile(command);
		}
		else 
		{
			System.out.println("Syntax Error! Please check first argument again!");
		}
		return null;
	}
	
	//return LIST and first argv is 0
	/*
	 * 	argument: [0, table_name, colomn_number, 
	 * 				col1_name, col1_type, col1_is_unique,
	 * 				col2_name, col2_type, col2_is_unique,....
	 * 				primary_key1, primary_key2,.....]
	 * */
	public static List CheckCreateTable(String command)
	{
		List argv = new ArrayList();
		//add operation code 0
		argv.add("0");
		
		//First : table name
		//skip all space first
		int i = 0;
		while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
		command = command.substring(i);
		//get the table name
		i = 0;
		while(command.charAt(i) != ' ' && command.charAt(i) != '\t' && command.charAt(i) != '\n' && command.charAt(i) != ';' && command.charAt(i) != '(')  i++;
		String table_name = command.substring(0, i);
		command = command.substring(i);
		//check the table name
		if(!IsValidName(table_name))
		{
			return null;
		}
		//the name is OK, add it
		argv.add(table_name);
		
		//Next: (
		//skip all space
		i = 0;
		while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
		command = command.substring(i);
		//check
		if(command.charAt(0) != '(') {
			System.out.println("Syntax Error! Expect \"(\"!");
			return null;
		}
		//OK
		command = command.substring(1);
		
		//Next get the column info
		List col = new ArrayList();
		List primary_Key = new ArrayList();
		int col_num = -1;		//number of columns
		boolean appear_primary = false;		//the primary key is allowed to appear only once
		while(true)
		//means there are more cols
		{
			//Next: new argument
			i = 0;
			while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
			command = command.substring(i);
			i = 0;
			while(command.charAt(i) != ' ' && command.charAt(i) != '\t' && command.charAt(i) != '\n' && command.charAt(i) != ',' && command.charAt(i) != ')'
												&& command.charAt(0) != ';') i++;
			String cmd1 = command.substring(0, i);
			command = command.substring(i);
			/*
			 * cmd1: two possible
			 * (1)	a new col
			 * (2)  to describe the primary key
			 */
			
			if(cmd1.toLowerCase().equals("primary"))
			{
				//check key
				//skip all spaces
				i = 0;
				while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
				command = command.substring(i);
				i = 0;
				while(command.charAt(i) != ' ' && command.charAt(i) != '\t' && command.charAt(i) != '\n' && command.charAt(i) != ','
						 && command.charAt(i) != '(' && command.charAt(i) != ')' && command.charAt(i) != ';') i++;
				String argu = command.substring(0, i);
				command = command.substring(i);
				
				if(!argu.toLowerCase().equals("key")) {
					System.out.println("Syntax Error! \"Key\" is expected after primary!");
					return null;
				}
				//primary key is OK
				//primary only appear once
				if(appear_primary){
					System.out.println("Syntax Error! Primary key should be defined only once!");
					return null;
				}
				else{
					appear_primary = true;
				}
				
				//skip all space
				i = 0;
				while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
				command = command.substring(i);
				
				//check the '('
				if(!(command.charAt(0) == '('))
				{
					System.out.println("Syntax Error! Expected \"(\" behind primary key!");
					return null;
				}
				command = command.substring(1);
				
				while(true)
				{
					//skip all space
					i = 0;
					while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
					command = command.substring(i);
					//get the primary key column name
					i = 0;
					while(command.charAt(i) != ' ' && command.charAt(i) != '\t' && command.charAt(i) != '\n' && command.charAt(0) != ';'
							&& command.charAt(i) != ',' && command.charAt(i) != '(' && command.charAt(i) != ')') i++;
					String pri_col = command.substring(0, i);
					command = command.substring(i);
					
					//travel col to check if this col name is appeared
					boolean appeared = false;
					for(i = 0; i < col.size(); i = i + 3)
					{
						String col_n = col.get(i).toString();
						if(col_n.equals(pri_col))
						{
							appeared = true;
							break;
						}
					}
					if(!appeared){
						System.out.println("Syntax Error!" +" Attribute "+  pri_col + " does not exist !");
						return null;
					}
					//check if it has been defined as primary key
					for(i = 0; i < primary_Key.size(); i++)
					{
						if(primary_Key.get(i).equals(pri_col))
						{
							System.out.println("Syntax Error!" + pri_col + " has been defined twice in primary key!");
							return null;
						}
					}
					
					//exist 
					primary_Key.add(pri_col);
					
					//check the end
					//skip all space
					i = 0;
					while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
					command = command.substring(i);
					if(command.charAt(0) == ')')
					{
						command = command.substring(1);
						break;
					}
					else if(command.charAt(0) == ',')
					{
						command = command.substring(1);
						continue;
					}
					else{
						System.out.println("Syntax Error! Near the " + pri_col + " !");
						return null;
					}
				}
				//skip all space
				i = 0;
				while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
				command = command.substring(i);
				if(command.charAt(0) == ')')
				{
					command = command.substring(1);
					break;
				}
				else if(command.charAt(0) == ',')
				{
					command = command.substring(1);
					continue;
				}
				else{
					System.out.println("Syntax Error! Expected \",\" or \")\" after primary key definition!");
					return null;
				}
			}
			//new column 
			else
			{
				col_num++;
				String col_name, type, isUnique = "0";
				col_name = cmd1;
				//check the col_name
				if(!IsValidName(col_name))
				{
					return null;
				}
				//check if it has been declared
				for(i = 0; i < col.size(); i = i + 3)
				{
					if(col.get(i).toString().equals(col_name))
					{
						System.out.println("Syntax Error! Redeclared attribute " + col_name + "!");
						return null;
					}
				}
				//OK, add it
				col.add(col_name);
				
				//Next: type
				//possible type: int  char(n)   float
				//get type
				i = 0;
				while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
				command = command.substring(i);
				i = 0;
				while(command.charAt(i) != ' ' && command.charAt(i) != '\t' && command.charAt(i) != '\n' && command.charAt(0) != ';'
								&& command.charAt(i) != ',' && command.charAt(i) != '(' && command.charAt(i) != ')') i++;
				type = command.substring(0, i).toLowerCase();
				command = command.substring(i);
				//check the type
				if(type.equals("int")){
					//integer, OK
					type = "int";
				}
				else if(type.equals("float")){
					//float, OK
					type = "float";
				}
				else{
					//check "char"
					if(type.indexOf("char") == -1)
					{
						System.out.println("Syntax Error! " + type + " is not an valid type! ");
						return null;
					}
					
					//check if the first 4 letter equals "char"
					if(!type.substring(0,4).equals("char"))
					{
						System.out.println("Syntax Error! " + type + " is not an valid type! ");
						return null;
					}
					
					type = "char";
					
					//skip all space
					i = 0;
					while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
					command = command.substring(i);
					
					//means char(n)
					if(command.charAt(0) == '(')
					{
						//spilt (
						command = command.substring(1);
						
						//skip all space
						i = 0;
						while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
						command = command.substring(i);
						
						//follow should be numbers
						i = 0;
						while(command.charAt(i) <= '9' && command.charAt(i) >= '0')		i++;
						String n = command.substring(0, i);
						command = command.substring(i);
						// n is the number of chars
						//check n
						if(n.equals(""))
						{
							System.out.println("Syntax Error! Type char(n) and n is expected to be a interger!");
							return null;
						}
						
						//skip all space and check the )
						i = 0;
						while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
						command = command.substring(i);
						if(command.charAt(0) != ')')
						{
							System.out.println("Syntax Error! Expected \"(\" of type char(n) !");
							return null;
						}
						//OK 
						command = command.substring(1);
						type = type + n;   //"char n"
					}
					else
					{
						type = type + "0";  //"char 0"
					}
				}
				//type is OK, add it to col
				col.add(type);
				
				//Next: check if is unique
				//get next argument
				//skip all space
				i = 0;
				while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
				command = command.substring(i);
				//check if end
				if(command.charAt(0) == ',')
				{
					//this definition end
					command = command.substring(1);
					//not unique,add it
					isUnique = "0";
					col.add(isUnique);
					continue;
				}
				else if(command.charAt(0) == ')')
				{
					//create end
					command = command.substring(1);
					break;
				}
				else if(command.charAt(0) == ';')
				{
					System.out.println("Syntax Error! Expected \")\" before \";\"!");
					return null;
				}
				//not ","
				i = 0;
				while(command.charAt(i) != ' ' && command.charAt(i) != '\t' && command.charAt(i) != '\n' && command.charAt(0) != ';'
						&& command.charAt(i) != ',' && command.charAt(i) != '(' && command.charAt(i) != ')') i++;
				String argu = command.substring(0, i);
				command = command.substring(i);
				if(argu.toLowerCase().equals("unique"))
				{
					isUnique = "1";
					col.add(isUnique);
				}
				else
				{
					col.add(isUnique);  //is unique = 0
					
					//check if the primary key
					if(argu.toLowerCase().equals("primary"))
					{
						//then must check key
						//skip all spaces
						i = 0;
						while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
						command = command.substring(i);
						i = 0;
						while(command.charAt(i) != ' ' && command.charAt(i) != '\t' && command.charAt(i) != '\n' && command.charAt(i) != ','
								 && command.charAt(i) != '(' && command.charAt(i) != ')' && command.charAt(i) != ';') i++;
						argu = command.substring(0, i);
						command = command.substring(i);
						
						if(!argu.toLowerCase().equals("key")) {
							System.out.println("Syntax Error! \"Key\" is expected after primary!");
							return null;
						}
						//primary key is OK
						//primary only appear once
						if(appear_primary){
							System.out.println("Syntax Error! Primary key should appear only once!");
							return null;
						}
						else{
							appear_primary = true;
						}
						primary_Key.add(col_name);
					}
					else
					{
						System.out.println("Syntax Error! " + argu + " can not be recognized! ");
						return null;
					}
				}
				
				//skip all spaces
				i = 0;
				while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
				command = command.substring(i);
				if(command.charAt(0) == ',')
				{
					//this definition end
					command = command.substring(1);
					continue;
				}
				else if(command.charAt(0) == ')')
				{
					//create end
					command = command.substring(1);
					break;
				}
				else if(command.charAt(0) == ';')
				{
					System.out.println("Syntax Error! Expected \")\" before \";\"!");
					return null;
				}
				else {
					System.out.println("Syntax Error! Expected \",\" afert defination a column!");
					return null;
				}
			}
			
		}
		//check if end with ;
		//skip all spaces
		i = 0;
		while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
		command = command.substring(i);
		if(!(command.charAt(0) == ';')){
			System.out.println("Syntax Error! Expect only \";\" behind the \")\" !");
			return null;
		}
		//merge all information to the argv
		
		argv.add(""+(col_num + 1));
		for(i = 0; i < col.size(); i++)
		{
			argv.add(col.get(i));
		}
		for(i = 0; i < primary_Key.size(); i++)
		{
			argv.add(primary_Key.get(i));
		}
		return argv;
	}
	
	//return LIST and first argv is 1
	/*
	 * 	arguments:
	 * 			[ 1, index_name, table_name, column_name]	
	 * */
	public static List CheckCreateIndex(String command)
	{
		//add the op code 1
		List argv = new ArrayList();
		argv.add("1");
		
		//skip all spaces and get next argument
		int i = 0;
		while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
		command = command.substring(i);
		i = 0;
		while(command.charAt(i) != ' ' && command.charAt(i) != '\t' && command.charAt(i) != '\n' && command.charAt(i) != ';') i++;
		String index_name = command.substring(0, i);
		command = command.substring(i);
		
		//check the index name
		if(!IsValidName(index_name))
		{
			return null;
		}
		//index name is OK, add it
		argv.add(index_name);
		
		//Next: on
		i = 0;
		while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
		command = command.substring(i);
		i = 0;
		while(command.charAt(i) != ' ' && command.charAt(i) != '\t' && command.charAt(i) != '\n' && command.charAt(i) != ';') i++;
		String on = command.substring(0, i);
		command = command.substring(i);
		if(!on.equals("on"))
		{
			System.out.println("Syntax Error! Expect \"on\"!");
			return null;
		}
		// on is OK
		
		//Next: table name
		i = 0;
		while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
		command = command.substring(i);
		i = 0;
		while(command.charAt(i) != ' ' && command.charAt(i) != '\t' && command.charAt(i) != '\n' && command.charAt(i) != ';' && command.charAt(i) != '(') i++;
		String table_name = command.substring(0, i);
		command = command.substring(i);
		//check the table name
		if(!IsValidName(table_name))
		{
			return null;
		}
		//table name is OK, add it
		argv.add(table_name);
		
		//Next: (
		//skip all space
		i = 0;
		while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
		command = command.substring(i);			//command's length always larger than 1 because of the ";"
		if(!(command.charAt(0) == '('))
		{
			System.out.println("Syntax Error! Expect \"(\"!");
			return null;
		}
		//( is OK
		command = command.substring(1);
		
		//Next: column name
		i = 0;
		while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
		command = command.substring(i);
		i = 0;
		while(command.charAt(i) != ' ' && command.charAt(i) != '\t' && command.charAt(i) != '\n' && command.charAt(i) != ';' && command.charAt(i) != ')') i++;
		String column_name = command.substring(0, i);
		command = command.substring(i);
		//check the column name
		if(!IsValidName(column_name))
		{
			return null;
		}
		//column name is OK, add it
		argv.add(column_name);
		
		//Next: )
		//skip all space
		i = 0;
		while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
		command = command.substring(i);			//command's length always larger than 1 because of the ";"
		if(!(command.charAt(0) == ')'))
		{
			System.out.println("Syntax Error! Expect \")\"!");
			return null;
		}
		// ( is OK, skip it
		command = command.substring(1);
		
		//Next: ;
		i = 0;
		while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
		command = command.substring(i);			//command's length always larger than 1 because of the ";
		if((command.charAt(0) == ';'))	//the command is OK, valid
		{
			return argv;
		}
		System.out.println("Syntax Error! After \")\" expect nothing!");
		return null;
	}
	
	//return LIST and first argv is 2
	//argument format [ 2, index_name ]
	public static List CheckDropIndex(String command)
	{
		List argv = new ArrayList();
		//push the op code as first argument
		argv.add("2");
		
		//then get the index name
		//skip all spaces
		int i = 0;
		while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
		command = command.substring(i);
		
		//then get the index name
		i = 0;
		while(command.charAt(i) != ' ' && command.charAt(i) != '\t' && command.charAt(i) != '\n' && command.charAt(i) != ';') i++;
		String index_name = command.substring(0, i);
		command = command.substring(i);
		
		//index name is valid
		if(IsValidName(index_name))
		{
			//check if followed by ;
			for(i = 0; i < command.indexOf(';'); i++)
			{
				if(command.charAt(i) != ' ' && command.charAt(i) != '\t' && command.charAt(i) != '\n')
				{
					System.out.println("Syntax Error! After index name expect nothing!");
					return null;
				}
			}
			//add the argument index_name
			argv.add(index_name);
			return argv;
		}
		return null;
	}
	
	//return LIST and first argv is 3
	//argument format [ 3, table_name]
	public static List CheckDropTable(String command)
	{
		List argv = new ArrayList();
		//push the op code as first argument
		argv.add("3");
		
		//then get the table name
		//skip all spaces
		int i = 0;
		while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
		command = command.substring(i);
		
		//then get the table name
		i = 0;
		while(command.charAt(i) != ' ' && command.charAt(i) != '\t' && command.charAt(i) != '\n' && command.charAt(i) != ';')  i++;
		String table_name = command.substring(0, i);
		command = command.substring(i);
		
		//check if index_name = null;
		if(table_name.equals(""))
		{
			System.out.println("Syntax Error! Table name can not be empty !");
			return null;
		}
		
		//index name is valid
		if(IsValidName(table_name))
		{
			//check if followed by ;
			for(i = 0; i < command.indexOf(';'); i++)
			{
				if(command.charAt(i) != ' ' && command.charAt(i) != '\t' && command.charAt(i) != '\n')
				{
					System.out.println("Syntax Error! After table name expect nothing!");
					return null;
				}
			}
			//add the argument index_name
			argv.add(table_name);
			return argv;
		}
		return null;
	}
	
	//return LIST and first argv is 4
	public static List CheckSelect(String command)
	{
		//add op code 4
		List argv = new ArrayList();
		argv.add("4");
		//skip all spaces
		int i = 0;
		while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
		command = command.substring(i);
		
		//get the column name
		int column_num = 0;
		List col_names = new ArrayList();
		if(command.charAt(0) == '*') {
			column_num = 0;
			command = command.substring(1);
		}else {
			while(true) {
				//skip all spaces
				i = 0;
				while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
				command = command.substring(i);
				
				//list all column name
				i = 0;
				column_num++;
				while(command.charAt(i) != ' ' && command.charAt(i) != '\t' && command.charAt(i) != '\n' && command.charAt(i) != ';' && command.charAt(i) != ',') i++;
				String col_name = command.substring(0, i);
				command = command.substring(i);
				
				//check the col_name
				if(!IsValidName(col_name)) {
					return null;
				}
				col_names.add(col_name);
				
				//skip all spaces
				i = 0;
				while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
				command = command.substring(i);
				if(command.charAt(0) != ',') {
					//end 
					break;
				}else {
					// == ','
					command = command.substring(1);
				}
			}
		}
		
		//column number is OK
		//check: from
		//skip all spaces
		i = 0;
		while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
		command = command.substring(i);
		i = 0;
		while(command.charAt(i) != ' ' && command.charAt(i) != '\t' && command.charAt(i) != '\n' && command.charAt(i) != ';') i++;
		String from = command.substring(0, i);
		command = command.substring(i);
		if(!from.toLowerCase().equals("from")){
			System.out.println("Syntax Error! Expected from!");
			return null;
		}
		
		//get the table
		//skip all spaces
		i = 0;
		while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
		command = command.substring(i);
		i = 0;
		while(command.charAt(i) != ' ' && command.charAt(i) != '\t' && command.charAt(i) != '\n' && command.charAt(i) != ';') i++;
		String table_name = command.substring(0, i);
		command = command.substring(i);
		if(!IsValidName(table_name)){
			return null;
		}
		
		List conditions = CheckConditions(command);
		if(conditions == null) {
			return null;
		}
		
		argv.add(table_name);
		argv.add(column_num + "");
		for(i = 0; i < column_num; i++) {
			argv.add(col_names.get(i));
		}
		for(i = 0; i < conditions.size(); i++) {
			argv.add(conditions.get(i));
		}
		return argv;
	}
	
	//return LIST and first argv is 5
	public static List CheckInsert(String command)
	{
		List argv = new ArrayList();
		//push the op code as first argument
		argv.add("5");
		
		//check  "into"
		int i = 0;
		//skip all spaces
		while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
		command = command.substring(i);
		//get the first argv
		i = 0;
		while(command.charAt(i) != ' ' && command.charAt(i) != '\t' && command.charAt(i) != '\n' && command.charAt(i) != ';') i++;
		String arv1 = command.substring(0, i).toLowerCase();
		command = command.substring(i);
		if(arv1.equals("into"))
		{	
			i = 0;
			while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
			command = command.substring(i);
			//then get the table name
			i = 0;
			while(command.charAt(i) != ' ' && command.charAt(i) != '\t' && command.charAt(i) != '\n' && command.charAt(i) != ';')  i++;
			String table_name = command.substring(0, i);
			command = command.substring(i);
			
			//index name is valid
			if(IsValidName(table_name))
			{
				//check if followed by "values";
				i=0;
				//skip all spaces
				while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
				command = command.substring(i);
				//get the second argv
				i = 0;
				while(command.charAt(i) != ' ' && command.charAt(i) != '\t' && command.charAt(i) != '\n' && command.charAt(i) != ';' &&command.charAt(i) != '(' ) i++;
				String arv2 = command.substring(0, i).toLowerCase();
				command = command.substring(i);
				if(arv2.equals("values"))
				{	
					i=0;
					//skip all spaces
					while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
					command = command.substring(i);
					if(!(command.charAt(0) == '(')) {
						System.out.println("Syntax Error! Missing '(' !");
        				return null;
					}else {
						command = command.substring(1);
					}
					//begin values
					int values_num=0;
					List Valueargv = new ArrayList();
					while(true) {
						i=0;
						//skip all spaces
						while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
						command = command.substring(i);
						
						String value, isString;
						if(command.charAt(0) == '\'') {
							isString = "1";
							command = command.substring(1);
							
							int index = command.indexOf("\'");
							if(index == -1) {
								System.out.println("Syntax Error! Missing the right \' in a string !");
								return null;
							}else {
								//OK
								value = command.substring(0, index);
								command = command.substring(index+1);
							}
						}else {
							isString = "0";//number
							i = 0;
							while(command.charAt(i) != ' ' && command.charAt(i) != '\t' && command.charAt(i) != '\n' && command.charAt(i) != ';'  
									&& command.charAt(i) != ')'&& command.charAt(i) != ','	)	i++;
							value = command.substring(0, i);
							command = command.substring(i);
							//check if value is a number
							if(!IsNumric(value)) {
								return null;
							}
						}
						Valueargv.add(value);
						Valueargv.add(isString);
						values_num++;
						//add ok
						i=0;
						//skip all spaces
						while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
						command = command.substring(i);
						
						if(command.charAt(0) == ',') {
							command = command.substring(1);
							continue;
						}else if(command.charAt(0) == ')') {
							command = command.substring(1);
							break;
						}else {
							System.out.println("Syntax Error! Unrecognized symbol near " + value + " !");
							return null;
						}
					}
					//add ok
					i=0;
					//skip all spaces
					while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
					command = command.substring(i);
					if(command.charAt(0) != ';') {
						System.out.println("Syntax Error! Expect nothing behind \')\' !");
						return null;
					}
					
					//add the argument 
					argv.add(table_name);
					argv.add(String.valueOf(values_num));
					argv.addAll(Valueargv);
					return argv;
				}
				else {
					System.out.println("Syntax Error! Missing 'values' after the table name!");
					return null;			
				}
			}
		}
		else {
			System.out.println("Syntax Error! Missing 'into' !");
			return null;
		}
		return null;
		
	}
	
	//return LIST and first argv is 6
	//similar to select
	public static List CheckDelete(String command)
	{
		List argv = new ArrayList();
		//push the op code as first argument
		argv.add("6");

		//check: from
		//skip all spaces
		int i = 0;
		while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
		command = command.substring(i);
		i = 0;
		while(command.charAt(i) != ' ' && command.charAt(i) != '\t' && command.charAt(i) != '\n' && command.charAt(i) != ';') i++;
		String from = command.substring(0, i);
		command = command.substring(i);
		if(!from.toLowerCase().equals("from")){
			System.out.println("Syntax Error! Expected from!");
			return null;
		}
				
		//get the table
		//skip all spaces
		i = 0;
		while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
		command = command.substring(i);
		i = 0;
		while(command.charAt(i) != ' ' && command.charAt(i) != '\t' && command.charAt(i) != '\n' && command.charAt(i) != ';') i++;
		String table_name = command.substring(0, i);
		command = command.substring(i);
		if(!IsValidName(table_name)){
			return null;
		}
		//table name is OK
		argv.add(table_name);
		
		//get the where
		i = 0;
		while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
		command = command.substring(i);
		
		
		List conditions = CheckConditions(command);
		if(conditions == null) {
			return null;
		}
		for(i = 0; i < conditions.size(); i++) {
			argv.add(conditions.get(i));
		}
		return argv;
	}
	
	//return LIST and first argv is 7
	//argument format [7, filename]
	public static List CheckExecuteFile(String command)
	{
		
		//add the op code 7
		List argv = new ArrayList();
		argv.add("7");
				
		//skip all spaces and get next argument
		int i = 0;
		while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
		command = command.substring(i);
		i = 0;
		while(command.charAt(i) != ' ' && command.charAt(i) != '\t' && command.charAt(i) != '\n' && command.charAt(i) != ';') i++;
		String file_name = command.substring(0, i);
		command = command.substring(i);
		
		//only check if is null
		if(file_name.equals(null))
		{
			System.out.println("Syntax Error! Expect file name!");
			return null;
		}
		
		//Check file name if OK
		File file = new File(file_name);
		if(!file.exists())
		{
			System.out.println("File do not exist!");
			return null;
		}
		if(!file.isFile())
		{
			System.out.println("Not a file !");
			return null;
		}
		//this file is OK
		//add it 
		argv.add(file_name);
		
		//Next: Check ;
		i = 0;
		while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
		command = command.substring(i);			//command's length always larger than 1 because of the ";
		if((command.charAt(0) == ';'))	//the command is OK, valid
		{
			return argv;
		}
		System.out.println("Syntax Error! After \")\" expect nothing!");
		return null;
	}
	
	public static boolean IsValidName(String name)
	{
		//almost all key word
		
		if(name.equals(""))
		{
			System.out.println("Syntax Error! Table name or index name or cloumn name can not be empty!");
			return false;
		}
			
		String [] KeyWord = {
				"char", "varchar", "int", "smallint", "numeric", "real", "double", "precision", "float",
				"primary", "key", "not", "null", "foreign", "references", "create", "table", "insert", "into",
				"values", "delete", "from", "update", "set", "where", "drop", "unique", "index", "on", "alter", "add",
				"distinct", "all", "and", "or", "natural", "join", "as", "by", "exist", "is", "group", "having"
		};
		
		//check the name is valid !
		int i;
		for(i = 0; i < KeyWord.length; i++)
		{
			//can not be key word of SQL
			if(name.toLowerCase().equals(KeyWord[i]))
			{
				System.out.println("Syntax Error! Table name or index name or cloumn name can not be " + name + "!");
				return false;
			}
		}
		
		//check if is valid
		//our SQL's name must consist of number, letter or '_', and must start with '_' or letter
		
		//check first char
		char firstCh = name.charAt(0);
		if(!((firstCh >= 'a' && firstCh <= 'z') || (firstCh >= 'A' && firstCh <= 'Z') || firstCh == '_'))
		{
			System.out.println("Syntax Error!" + name + " is an invalid table name or index name or cloumn name!");
			return false;
		}
		
		//check if is number or letter or '_'
		for(i = 0; i < name.length(); i++)
		{
			char ch = name.charAt(i);
			if(ch >= 'a' && ch <= 'z')
			{
				continue;
			}
			else if(ch >= 'A' && ch <= 'Z')
			{
				continue;
			}
			else if(ch == '_')
			{
				continue;
			}
			else 
			{
				//some char is not OK
				System.out.println("Syntax Error!" + name + " is an invalid table name or index name or cloumn name!");
				return false;
			}
		}
		return true;
	}

	public static List CheckConditions(String command)
	{
		List cons = new ArrayList();
		//skip all space and check it is no condition
		int i = 0;
		while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
		command = command.substring(i);
		if(command.charAt(0) == ';') {
			cons.add("0");
			//no any condition
			return cons;
		}
		
		i = 0;
		while(command.charAt(i) != ' ' && command.charAt(i) != '\t' && command.charAt(i) != '\n' && command.charAt(i) != ';') i++;
		String where = command.substring(0, i);
		command = command.substring(i);
		if(!where.toLowerCase().equals("where")){
			System.out.println("Syntax Error! Expected where!");
			return null;
		}
		
		int con_num = 0;
		while(true)
		{
			con_num ++;
			//skip all spaces and get next argument
			i = 0;
			while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
			command = command.substring(i);
			i = 0;
			while(command.charAt(i) != ' ' && command.charAt(i) != '\t' && command.charAt(i) != '\n' && command.charAt(i) != ';'
					&& command.charAt(i) != '<' && command.charAt(i) != '=' && command.charAt(i) != '>')	i++;
			String col_name = command.substring(0, i);
			command = command.substring(i);
			//check the col_name
			if(!IsValidName(col_name))
			{
				return null;
			}
			//OK, and add it
			cons.add(col_name);
			
			//get the operation
			//skip spaces
			i = 0;
			while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
			command = command.substring(i);
			i = 0;
			while(command.charAt(i) == '=' || command.charAt(i) == '>' || command.charAt(i) == '<') i++;
			String op = command.substring(0, i);
			command = command.substring(i);
			//check op
			if(!(op.equals("<") || op.equals("=") || op.equals(">") || op.equals(">=") || op.equals("<=") || op.equals("<>"))) {
				System.out.println("Syntax Error! Can't find valid operation near " + col_name + "!");
				return null;
			}
			cons.add(op);
			
			//check the value
			String value = "", type = "";
			//skip spaces
			i = 0;
			while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
			command = command.substring(i);
			if(command.charAt(0) == '\'') {
				//a string
				command = command.substring(1);
				int index = command.indexOf("\'");
				if(index == -1)
				{
					//can not find another '
					System.out.println("Syntax Error! Missing the right \' in a string !");
    				return null;
				}
				//find \'
				value = command.substring(0, index);
				command = command.substring(index+1);
				cons.add(value);
				type = "1"; //is string
				cons.add(type);
			}else {
				//should be number
				type = "0";
				i = 0;
				while(command.charAt(i) != ' ' && command.charAt(i) != '\t' && command.charAt(i) != '\n' && command.charAt(i) != ';')	i++;
				value = command.substring(0, i);
				command = command.substring(i);
				//check if value is a number
				if(!IsNumric(value)) {
					return null;
				}
				//is number
				cons.add(value);
				cons.add(type);
			}
			//skip all space
			i = 0;
			while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
			command = command.substring(i);
			//check if is and or ;
			if(command.charAt(0) == ';') {
				break;
			}else if(command.toLowerCase().indexOf("and") != -1){
				if(command.substring(0, 3).toLowerCase().equals("and")) {
					//another condition
					command = command.substring(3);
					continue;
				}else {
					System.out.println("Syntax Error! Unrecognized symbol near " + value + " !");
    				return null;
				}
			}else {
				System.out.println("Syntax Error! Unrecognized symbol near " + value + " !");
				return null;
			}
		}
		List conditions = new ArrayList();
		conditions.add(con_num+"");
		for(i = 0; i < cons.size(); i++) {
			conditions.add(cons.get(i));
		}
		return conditions;
	}
	
	public static boolean IsNumric(String value){
		//check if all number or '.'
		try {
			Float.parseFloat(value);
		}catch(Exception e) {
			System.out.println(value + " is not a number!");
			return false;
		}
		return true;
	}
}
