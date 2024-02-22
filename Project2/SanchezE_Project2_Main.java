import java.util.Scanner;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.IOException;

public class SanchezE_Project2_Main {
    public static void main(String[] args) throws IOException{
        if(args.length < 3){
            System.err.println("Invalid Number of Arguments, 3 Required: <inputFile> <outputFile> <debugFile>");
            System.exit(1);
        }

        File inFile = new File(args[0]);
        File outFile1 = new File(args[1]);
        File debugFile = new File(args[2]);

        try(FileWriter debugWriter = new FileWriter(debugFile, true);
            FileWriter outWriter = new FileWriter(outFile1, true);){
            RadixSort radixSort = new RadixSort(inFile, debugWriter);

        }


    }    
}
class listNode{
    int data;
    listNode next;

    listNode(int data){
        this.data = data;
        this.next = null;
    }
}

class LLQueue{
    listNode head;
    listNode tail;

    LLQueue(){
        this.head = new listNode(-9999);
        this.tail = head;
    }

    static void insertQ(LLQueue[][] hashTable, int currentTable, int index, listNode newNode){
        hashTable[currentTable][index].tail.next = newNode;
        hashTable[currentTable][index].tail = newNode;

    }

    listNode deleteQ(){
        listNode t = head;
        if (t.next != null) {
            if (tail == t.next) {
                tail = head;
            }
            listNode tmp = t.next;
            t.next = tmp.next;
            tmp.next = null;
            return tmp;
        }
        return null;
    }

    Boolean isEmpty(){
        return tail == head;
    }

    void printQueue(){

    }
}

class RadixSort{
    private int tableSize = 10;
    LLQueue[][] hashTable; //A specific 2-D array designed to hold LLQueues().
    int data;
    int currentTable;
    int previousTable;
    int maxLength;
    int offSet;
    int digitPosition;
    int currentDigit;

    RadixSort(File inFile, FileWriter debugWriter){
        this.hashTable = new LLQueue[2][tableSize];//does not create dummy nodes
        for(int i = 0; i < hashTable.length; i++){
            for(int j = 0; j < hashTable[i].length; j++){
                this.hashTable[i][j] = new LLQueue();
            }
        }
        
        preProcessing(inFile, debugWriter);
    }

    void preProcessing(File inFile, FileWriter debugWriter){
        int negativeNum = 0;
        int positiveNum = 0;
        
        try (Scanner scanner = new Scanner(new FileReader(inFile))){
            debugWriter.write("** Entering preProcessing()\n");
            while (scanner.hasNextLine()) { //file contains another line?
                //initially read entire line
                String line = scanner.nextLine();

                //new scanner to scan the entire String line
                try (Scanner lineScanner = new Scanner(line)) {
                    //read each number in the line
                    while (lineScanner.hasNextInt()) {
                        int data = lineScanner.nextInt(); 

                        if (data < negativeNum){
                            negativeNum = data;
                        } else if (data > positiveNum) {
                            positiveNum = data;
                        }
                    }
                } //inner lineScanner close automatically, IOException not required for scanner String
            }
            if (negativeNum < 0) {
                offSet = Math.abs(negativeNum);
            } else {
                offSet = 0;
            }

            positiveNum = positiveNum + offSet;
            maxLength = getLength(positiveNum);
            debugWriter.write("In preProcessin():\nnegativeNum = " + negativeNum + "\npositiveNum = " + positiveNum + "\nmaxLength = " + maxLength + "\n");
            debugWriter.write("Leaving preProcessing()\n");
        } catch (IOException e) { //outter scanner closed
            e.printStackTrace();
        }
    }

    void RSort(File inFile, FileWriter outWriter, FileWriter debugWriter){
        digitPosition = 0;
        currentTable = 0;

        try (Scanner scanner = new Scanner(new FileReader(inFile))) {
            debugWriter.write("*** Entering RSort()\n");
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                try(Scanner lineScanner = new Scanner(line)){
                    while (lineScanner.hasNextInt()) {
                        int data = lineScanner.nextInt();
                        data += offSet; //add negative number if it exists
                        
                        listNode newNode = new listNode(data);
                        int hashIndex = getIndex(data,digitPosition);

                        LLQueue.insertQ(hashTable, currentTable, hashIndex, newNode);
                    }
                    debugWriter.write("In RSort: After inserting all data from inFile into hashTable[0]\n");
                } //close inner lineScanner
            }
        } catch (IOException e){ //close outter scanner
            e.printStackTrace();
        }
    }

    int getLength(int positiveNum){
        return String.valueOf(positiveNum).length();
    }

    int getIndex(int data, int position){ //hash function
        //divide data by 10^postion and mod by 10
        return (data / (int)Math.pow(10, position)) % 10;
    }


}
