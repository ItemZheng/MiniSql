import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.print.attribute.standard.PrinterLocation;
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
			String [] argv = CheckSyntax(command);
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
	 
	public static String[] CheckSyntax(String command)
	{
		String [] argv = null;
		
		
		return null;
	}
}
