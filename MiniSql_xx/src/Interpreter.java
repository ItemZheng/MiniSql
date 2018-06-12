import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.print.attribute.standard.PrinterLocation;
import java.util.List;
import java.util.ArrayList;
import sun.print.resources.serviceui;

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
			else
			{
				//api interface
				//Api(argv);
			}
		}
	}
	 
	public static List CheckSyntax(String command)
	{
		//get first space
		int i = 0;
		while(command.charAt(i) == ' ' || command.charAt(i) == '\t' || command.charAt(i) == '\n') i++;
		command = command.substring(i);
		
		//get first argv
		i = 0;
		while(command.charAt(i) <= 'z' && command.charAt(i) >= 'a') i++;
		String op = command.substring(0, i);
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
			while(command.charAt(i) <= 'z' && command.charAt(i) >= 'a') i++;
			String arv1 = command.substring(0, i);
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
				System.out.println(arv1 + " is not equel to table or index!");
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
			while(command.charAt(i) <= 'z' && command.charAt(i) >= 'a') i++;
			String arv1 = command.substring(0, i);
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
				System.out.println(arv1 + " is not equel to table or index!");
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
		else 
		{
			System.out.println("Syntax Error! Please check first word again!");
		}
		return null;
	}
	
	public static List CheckCreateTable(String command)
	{
		List argv = new ArrayList();
		
		return null;
	}
	
	public static List CheckCreateIndex(String command)
	{
		
		return null;
	}
	
	public static List CheckDropIndex(String command)
	{
		
		return null;
	}
	
	public static List CheckDropTable(String command)
	{
		
		return null;
	}
	
	public static List CheckSelect(String command)
	{
		return null;
	}
	
	public static List CheckInsert(String command)
	{
		return null;
	}
	
	public static List CheckDelete(String command)
	{
		return null;
	}
	
}
