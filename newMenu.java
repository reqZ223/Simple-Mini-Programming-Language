import java.util.*;
import java.text.*;
import java.io.*;
import java.lang.Math;

public class newMenu
{
    String fileName;
    Scanner in = null;
    public void runIt()
    {
        while (true) {
            in = new Scanner(System.in);
            System.out.println("");
            System.out.println("----------------------------");
            System.out.println("Simple mini-interpreter");
            System.out.println("To quit, type q (case-insensitive)");
            System.out.println("---------------------------\n");
            System.out.println("Please input your file name:");
            fileName = in.nextLine();
            if (fileName.equalsIgnoreCase("q")) {
                System.out.println("Exiting");
                System.out.println("-----------------");
                break; 
            }
            new Interpreter().executeFile("textFile/"+fileName);
        }
    }
}
