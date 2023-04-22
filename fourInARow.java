package application;
import java.util.Scanner;
import application.PVP;
import javafx.scene.Scene;
import application.AI;

public class fourInARow {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner input = new Scanner(System.in);
		int user; 
		System.out.print("Enter 1 to play against computer or 2 to play against another player:");
		user = input.nextInt();
		if (user == 1) {
			AI newObj = new AI();
			newObj.main(args);
		}else {
			PVP obj2 = new PVP();
			obj2.main(args);
		}
	}
	
}
