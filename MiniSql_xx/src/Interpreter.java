import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.print.attribute.standard.PrinterLocation;

import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;
import java.util.ArrayList;

import sun.plugin2.message.GetAppletMessage;
import sun.print.resources.serviceui;
import sun.security.util.Length;

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
			String oldCommand = "";
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
			else {
				//api interface
				//Api(argv);
				
			}
			oldCommand = command;
		}
		
		System.out.println("Bye~");
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
		while((command.charAt(i) <= 'z' && command.charAt(i) >= 'a') || (command.charAt(i) <= 'Z' && command.charAt(i) >= 'A')) i++;
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
			while((command.charAt(i) <= 'z' && command.charAt(i) >= 'a') || (command.charAt(i) <= 'Z' && command.charAt(i) >= 'A')) i++;
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
			while((command.charAt(i) <= 'z' && command.charAt(i) >= 'a') || (command.charAt(i) <= 'Z' && command.charAt(i) >= 'A')) i++;
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
	public static List CheckCreateIndex(String command)
	{
		
		return null;
	}
	
	//return LIST and first argv is 2
	public static List CheckDropIndex(String command)
	{
		
		return null;
	}
	
	//return LIST and first argv is 3
	public static List CheckDropTable(String command)
	{
		
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
	public static List CheckExecuteFile(String command)
	{
		
		return null;
	}
	
	public static boolean IsValidName(String name)
	{
		//almost all key word
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
				System.out.println("Table name or index name or cloumn name can not be " + name + "!");
				return false;
			}
		}
		
		//check if is valid
		//our SQL's name must consist of number, letter or '_', and must start with '_' or letter
		
		//check first char
		char firstCh = name.charAt(0);
		if(!((firstCh >= 'a' && firstCh <= 'z') || (firstCh >= 'A' && firstCh <= 'Z') || firstCh == '_'))
		{
			System.out.println("Invalid table name or index name or cloumn name!");
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
				System.out.println("Invalid table name or index name or cloumn name!");
				return false;
			}
		}
		
		return true;
	}
}
