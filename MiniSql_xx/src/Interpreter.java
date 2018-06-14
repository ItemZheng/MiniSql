import java.io.*;
import java.util.*;

public class Interpreter {
	
	public static void main(String[] args) {
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
			else if( argv.get(0).equals("quit"))
			{
				//quit
				break;
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
							if(argv.get(0).toString().equals("quit"))
							{
								quit = true;
							}
							//api interface
							//Api(argv);
							System.out.println("Execute successfully!");
							//if success
							
							if(quit)
							{
								break;
							}
							
						}
						System.out.println();
					}
					System.out.println("Finish execute commands! There are " + commands.size() + " commands totally and " + count + 
										" commands successfully! ");
					if(quit)
					{
						//quit
						break;
					}
				}
			}
			else {
				//api interface
				//Api(argv);
				System.out.println(argv);
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
	public static List CheckCreateTable(String command)
	{
		List argv = new ArrayList();
		
		return null;
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
		return null;
	}
	
	//return LIST and first argv is 5
	public static List CheckInsert(String command)
	{
		return null;
	}
	
	//return LIST and first argv is 6
	public static List CheckDelete(String command)
	{
		return null;
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
}
