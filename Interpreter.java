// Author name: Punthmuny Srun
// Student ID: u3241596
// Date: 30 Apr, 2023

import java.util.*;
import java.io.*;

public class Interpreter
{ 
    boolean condWhile = false; 
    boolean cond = true; 
    boolean working = true; 
    double sVal; 
    ArrayList<String> loopList = new ArrayList<>();  
    String temp;
    FileReader fr;
    Scanner inFile; 
    Hashtable<String, Double> variable = new Hashtable<>();  
    public void readFile(String fname) {
        try
        {   
            System.out.println("Reading code file of "+fname);
            int lcnt = 0; 
            int tcnt = 0;
            fr = new FileReader(fname.toLowerCase()); 
            inFile = new Scanner(fr);
            while (inFile.hasNextLine())
            {
                tcnt++;
                temp = inFile.nextLine();

                if (!temp.equals(""))
                {
                    if (!(temp.charAt(0)== '/')) lcnt++;
                }
            }
            System.out.println("Lines Without Comment Read:                        "+lcnt);
            System.out.println("Total Lines Read:                                  "+tcnt);
            System.out.println("Running code now ------------------------------------\n");
            inFile.close(); 
        }
        catch (IOException e)
        {
            System.out.println("File not found"); 
        }
    }

    public void executeFile(String fname) {
        try
        {
            readFile(fname); 
            fr = new FileReader(fname); 
            inFile = new Scanner(fr);
            int lcnt = 0;
            while (inFile.hasNextLine())
            {
                lcnt++;
                temp = inFile.nextLine(); 
                if (!temp.equals("")) 
                { 
                    if (!cond) // executing the if condition; 
                    {
                        if (temp.equals("endif")) cond = true; 
                        continue;
                    }
                    processLine(temp);

                    if (temp.contains("\t") || temp.equals("endwhile") || temp.contains("while")) 
                    { 
                        loopList.add(temp.trim());

                        if (temp.equals("endwhile")) // executing the while condition
                        { 
                            while (condWhile) 
                            {
                                for (String stmt : loopList)
                                {
                                    //System.out.println(stmt);
                                    processLine(stmt);
                                    if (!condWhile) break; 
                                }
                            }
                            if (!condWhile) 
                            {loopList.clear(); continue;}

                        }
                    }
                    if (!working) 
                    {
                        System.out.print(lcnt+")\n");
                        break; 
                    }
                }
            }
            inFile.close();
        }catch (IOException e)
        {
            return; // because this method will always run another method
            // that reads the same file name, it does not need to do another
            // System.out.print("..."); 
        }
    }

    public void processLine(String line) 
    {
        String[] token = line.split(" ");
        //System.out.println(token[0]);
        switch (token[0].toUpperCase())
        {       
            case "//":
                break; 
            case "PRINTLN":
                token = line.split("'");
                print(token);
                break;
            case "SET":
                set(token);
                break;
            case "ADD":
                add(token);
                break;
            case "PRINT":
                token = line.split("'"); 
                print(token);
                break;
            case "SUBTRACT":
                subtract(token);
                break;
            case "MULTIPLY":
                multiply(token);
                break;
            case "DIVIDE":
                divide(token);
                break;
            case "IF":
                stmtIf(token);
                break;
            case "WHILE":
                stmtWhile(token);
                break;
            case "\n":
                break;
            case "END":
                //System.exit(0);
                break;
            case "ENDWHILE":
                break;
            case "ENDIF":
                break;

        }
    }

    public void parseErr(String[] token)
    {
        // This parser FOLLOWS the rules mentioned within the assignment;
        if (token[0].toUpperCase().equals("SET"))
        {
            if (isNumeric(token[1])) syntaxError(token[1]); // if token[1] is a number;
            else if (!token[2].toUpperCase().equals("TO")) syntaxError(token[2]); // if token[2] is not "to";
            else if (!isNumeric(token[3])) // if token[3] is not a variable; 
            {
                if (variable.get(token[3])==null) varError(token[3]);
            } 
        }
        else if (token[0].toUpperCase().equals("ADD"))
        {
            if (!isNumeric(token[1])) 
            {if (variable.get(token[1])==null) varError(token[1]);} // if token[1] is not a declared variable; 
            else if (!token[2].toUpperCase().equals("TO")) syntaxError(token[2]); 
            else if (isNumeric(token[3])) // if token[3] is not a variable; 
            {
                working = false;
                System.out.printf("Error! %s should be an variable, not an integer. (Line ", token[3]);
            } 
            else if (!isNumeric(token[3]) && variable.get(token[3])==null) varError(token[3]); // if the variable is not declared; 
        }
        else if (token[0].toUpperCase().equals("SUBTRACT"))
        {
            if (!isNumeric(token[1])) 
            {if (variable.get(token[1])==null) varError(token[1]);} 
            else if (!token[2].toUpperCase().equals("FROM")) syntaxError(token[2]); // if token[2] is not "FROM"; 
            else if (isNumeric(token[3])) 
            {
                working = false;
                System.out.printf("Error! %s should be an variable, not an integer. (Line ", token[3]);
            }
            else if (!isNumeric(token[3]) && variable.get(token[3])==null) varError(token[3]); 
        }
        else if (token[0].toUpperCase().equals("MULTIPLY"))
        { 
            if (isNumeric(token[1])) // if token[1] is not a variable; 
            {working = false; 
                System.out.printf("Error! %s should be an variable, not an integer.(Line ", token[3]);}
            else if (!isNumeric(token[1]))
            {if (variable.get(token[1])==null) varError(token[1]);}  
            else if (!token[2].toUpperCase().equals("BY")) syntaxError(token[2]); 
            else if (!isNumeric(token[3]) && variable.get(token[3])==null) varError(token[3]); 
        }
        else if (token[0].toUpperCase().equals("DIVIDE"))
        {
            // same logic as "MULTIPLY" command; 
            if (isNumeric(token[1])) 
            {working = false; 
                System.out.printf("Error! %s should be an variable, not an integer.(Line ", token[3]);}
            else if (!isNumeric(token[1]))
            {if (variable.get(token[1])==null) varError(token[1]);}
            else if (!token[2].toUpperCase().equals("INTO")) syntaxError(token[2]); 
            else if (!isNumeric(token[3]) && variable.get(token[3])==null) varError(token[3]);
        }
    }

    public void parseWaI(String[] token)
    {
        if (token.length==5)
        {
            if (token[0].toUpperCase().equals("IF"))
            {
                if (!isNumeric(token[1]))  
                {
                    if (variable.get(token[1])==null) varError(token[1]);
                }
                // if the operator is none other than these 3; 
                else if (!token[2].equals(">") || !token[2].equals("<") || !token[2].equals("==")) System.out.println("Unknown Operator: "+token[2]);
                else if (!isNumeric(token[3]))
                {
                    if (variable.get(token[3])==null) varError(token[3]); 
                }
                else if (!token[4].toUpperCase().equals("THEN")) syntaxError(token[4]);   
            }
            else if (token[0].toUpperCase().equals("WHILE"))
            {
                // Same logic as "IF" command. 
                if (!isNumeric(token[1]))
                {
                    if (variable.get(token[1])==null) varError(token[1]);
                }
                else if (!token[2].equals(">") || !token[2].equals("<") || !token[2].equals("==")) System.out.println("Unknown Operator: "+token[2]);
                else if (!isNumeric(token[3]))
                {
                    if (variable.get(token[3])==null) varError(token[3]); 
                }
                else if (!token[4].toUpperCase().equals("DO")) syntaxError(token[4]);   
            }
           
        }
        else 
        {
            working = false;
            System.out.print("Syntax Error AT Line (");
             
        }
    }

    public void print(String[] token)
    {
        if (token[0].trim().equals("println"))
        {
            System.out.println(token[1]);
        }
        else if (token[0].trim().equals("print"))
        {
            System.out.print(token[1]);
        }
        else //normally the tokenizer splits by the symbol "'";
        // thus, if the tokenizer does not see the symbol, it does not split
        // and returns the token[] array with the size of 1, which is token[0]. 
        {
            String[] split = token[0].split(" ");
            for (String key : variable.keySet())
            {
                if (split[1].equals(key))
                    System.out.printf("%.0f\n",variable.get(split[1]));
                //else if (split[0].equals("print")) System.out.printf("%.0f",sVal);
            }
            if (split[1].equals("cls")) 
            {
                if (split[0].equals("print")) System.out.print("\u000c");
                else if (split[0].equals("println")) System.out.println("\u000c");
            }
            else if (isNumeric(split[1]))
            {
                if (split[0].equals("print")) System.out.print(split[1]);
                else if (split[0].equals("println")) System.out.println(split[1]);
            }
            else if (!isNumeric(split[1]))
            {
                if (variable.get(split[1])==null) {if (working) varError(split[1]);};
            }
        }
    }

    public void set(String[] token)
    {
        parseErr(token);
        if (!isNumeric(token[3])) variable.put(token[1], variable.get(token[3]));
        else variable.put(token[1], Double.parseDouble(token[3]));
        //System.out.println(variable.get(token[1]));
    }

    public void stmtWhile(String[] token)
    {
        parseWaI(token); 
        int retv = 0;
        String val1 = ""; 
        String val3= "";
        if (working)
        {
            if (!isNumeric(token[1])) val1 = Double.toString(variable.get(token[1])); // if token[1] is not a number;
            else val1 = token[1]; // if token[1] is a number; 
            if (!isNumeric(token[3])) val3 = Double.toString(variable.get(token[3]));
            else val3 = token[3]; 
            // comparison between the two values: val1 and val3; 
            retv = Double.compare(Double.parseDouble(val1), Double.parseDouble(val3)); 
            if (retv==-1) // if val1 < val3
            {
                if (token[2].equals("<")) condWhile = true; 
                else condWhile=false;
            }
            else if (retv==0) // if val1==val3;
            {
                if (token[2].equals("==")) condWhile = true;
                else condWhile=false;
            }
            else if (retv==1) //if val1>val3; 
            { 
                if (token[2].equals(">")) condWhile=true;
                else condWhile=false;
            }
        }
    }
    public void stmtIf(String[] token)
    {
        parseWaI(token); 
        int retv = 0;
        String val1 = ""; 
        String val3= "";

        if (working)
        {
            if (!isNumeric(token[1])) val1 = Double.toString(variable.get(token[1]));
            else val1 = token[1]; 
            if (!isNumeric(token[3])) val3 = Double.toString(variable.get(token[3]));
            else val3 = token[3]; 

            retv = Double.compare(Double.parseDouble(val1), Double.parseDouble(val3));

            if (retv==-1)
            {
                if (token[2].equals("<")) cond = true; 
                else cond = false;
            }
            else if (retv==0)
            {
                if (token[2].equals("==")) cond = true;
                else cond = false;
            }
            else if (retv==1)
            {
                if (token[2].equals(">")) cond=true;
                else cond = false;
            }
        }

    }

    public void divide(String[] token)
    {
        parseErr(token);
        for (String key : variable.keySet()) // return all the set variables 
        {
            if (token[1].equals(key)) // find the right variable; 
            {
                if (isNumeric(token[3])) sVal = variable.get(token[1])/Double.parseDouble(token[3]); 
                else sVal = variable.get(token[1])/variable.get(token[3]);
                variable.replace(token[1], sVal); // replace the value of the variable; 
            }
        }
    }

    public void multiply(String[] token)
    {
        parseErr(token);
        for (String key : variable.keySet()) // return all the set variables; 
        {
            if (token[1].equals(key)) // find the right variable; 
            {
                if (isNumeric(token[3])) sVal = variable.get(token[1])*Double.parseDouble(token[3]); 
                else sVal = variable.get(token[1])*variable.get(token[3]);
                variable.replace(token[1], sVal); //replace the value of the variable; 
            }
        }
    }

    public void add(String[] token)
    {
        parseErr(token); 
        for (String key : variable.keySet()) // return all the set variables; 
        {
            if (token[3].equals(key)) // find the right variable; 
            {
                if (isNumeric(token[1])) sVal = variable.get(token[3])+Double.parseDouble(token[1]); 
                else sVal = variable.get(token[3])+variable.get(token[1]);
                variable.replace(token[3], sVal); 
            }
        }
    }

    public void subtract(String[] token)
    {
        parseErr(token);
        for (String key : variable.keySet()) // return all the set variables; 
        {
            if (token[3].equals(key)) // find the right variable; 
            {
                if (isNumeric(token[1])) sVal = variable.get(token[3])-Double.parseDouble(token[1]);
                else sVal = variable.get(token[3])-variable.get(token[1]);
                variable.replace(token[3], sVal); 
            }
        }
    }

    public boolean isNumeric(String str) // to test if the string is a number or not; 
    {
        // Reference (1); 
        try 
        {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) 
        {return false;}
    }

    public void varError(String errVal) // error raised when the variable is not declared; 
    {
        System.out.print("Error! "+"'"+errVal+"'"+" is not a declared variable (Line ");
        working = false; 
    }

    public void syntaxError(String errVal) // error raised when there is a syntax error; 
    {
        System.out.printf("Syntax Error '%s' At (Line ", errVal);
        working = false; 
    }
}