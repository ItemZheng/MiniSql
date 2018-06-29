package Minisql;
public class MiniSQL {
	public static void main(String[] args) {
		    //init!!!!catelog read 
			BufferManage.Init();
			Interpreter.start();
			BufferManage.saveAllBlock();
			//end save!!!!catelog update
	} 
}
