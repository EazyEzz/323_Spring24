import java.util.Scanner;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class SanchezE_Project0A_Main {

    public static void main(String[] args){
        if (args.length < 3){
            System.err.println("Invalid Number of Arguments, 3 required <inputFile> <outputFile> <debugFile>");
            System.exit(1);
        }

        File inFile = new File(args[0]);
        File outFile = new File(args[1]);
        File debugFile = new File(args[2]);

        int largest = -9999;
        
        //try-catch automatically closes Scanner and FileWriter
        try (
            Scanner scanner = new Scanner(new FileReader(inFile));
            FileWriter writer = new FileWriter(outFile);
            FileWriter debugWriter = new FileWriter(debugFile)){

                //start debugging
                debugWriter.write("Starting File Read-In:\n");

                while (scanner.hasNextInt()){
                    int data = scanner.nextInt();
                    writer.write(data + "\n");

                    debugWriter.write("Input number read: " + data + "\n");

                    if (data > largest) {
                        largest = data;
                        debugWriter.write("Largest updated to: " + largest + "\n");
                    }
                }
                writer.write("The largest integer in the input file is: " + largest);

                //debugger end
                debugWriter.write("Completed File Processing, Largest found: " + largest + "\n");

            } catch (IOException e) {
                e.printStackTrace();
            }
    }
}
