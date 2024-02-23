import java.util.Scanner;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
            radixSort.RSort(inFile, outWriter, debugWriter);
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

    static String printNode(listNode node){
        String s = "";
        if (node!= null){
            if (node.next != null){
                s = "(" + node.data + "," + node.next.data  + ")";
            } else{
                s = "(" + node.data + ", null)";
            }
        } else{
            s = "(null, null)";
        }
        return s;
    }
}

class LLQueue{
    listNode head;
    listNode tail;

    LLQueue(){
        this.head = new listNode(-9999);
        this.tail = head;
    }

    void insertQ(LLQueue[][] hashTable, int currentTable, int index, listNode newNode){
        hashTable[currentTable][index].tail.next = newNode;
        hashTable[currentTable][index].tail = newNode;

    }

    listNode deleteQ(){
        listNode t = head;
        if (t.next != null) {
            if (tail == t.next){
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

    String printQueue(LLQueue[][] hashTable, int currentTable, int index){
        StringBuilder sb;
        if (!(hashTable[currentTable][index].isEmpty())){
            sb = new StringBuilder("Table[" + currentTable + "][" + index + "]: ");
            listNode currNode = hashTable[currentTable][index].head; //skips dummy node
            while (currNode != null){
                sb.append(listNode.printNode(currNode)).append(" -> ");
                currNode = currNode.next;
            }
            sb.append("NULL\n");
            return sb.toString();
        }
        return null;
    }
}

class RadixSort{
    private int tableSize = 10;
    LLQueue[][] hashTable; //A specific 2-D array designed to hold LLQueues().
    int data;
    int maxLength = 0;
    int offSet = 0;
    int currentDigit = 0;

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
            while (scanner.hasNextLine()){ //file contains another line?
                //initially read entire line
                String line = scanner.nextLine();

                //new scanner to scan the entire String line
                try (Scanner lineScanner = new Scanner(line)){
                    //read each number in the line
                    while (lineScanner.hasNextInt()) {
                        int data = lineScanner.nextInt(); 

                        if (data < negativeNum){
                            negativeNum = data;
                        } else if (data > positiveNum){
                            positiveNum = data;
                        }
                    }
                } //inner lineScanner close automatically, IOException not required for scanner String
            }
            if (negativeNum < 0){
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
        int digitPosition = 1;
        int currentTable = 0;
        int previousTable;
        int hashIndex;

        try (Scanner scanner = new Scanner(new FileReader(inFile))) {
            debugWriter.write("*** Entering RSort()\n");
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                try(Scanner lineScanner = new Scanner(line)){
                    while (lineScanner.hasNextInt()) {
                        int data = lineScanner.nextInt();
                        data += offSet; //add negative number if it exists
                        
                        listNode newNode = new listNode(data);
                        hashIndex = getIndex(data,digitPosition); //hash function

                        hashTable[currentTable][hashIndex].insertQ(hashTable, currentTable, hashIndex, newNode);
                    }                    
                } //close inner lineScanner
            }
            debugWriter.write("In RSort: After inserting all data from inFile into hashTable[" + currentTable + "]\n");
            printTable(hashTable, currentTable, debugWriter);
        } catch (IOException e){ //close outter scanner
            e.printStackTrace();
        }
        //step 4-11 loop until digitPosition < maxLength
        //starting currentTable = 0
        //maxLength = 3
        //starting digitPosition = 1
        int tableIndex = 0;
        while (digitPosition <= maxLength){
            int dP = digitPosition;
            previousTable = currentTable;
            currentTable = (currentTable + 1) % 2;
            
            try {
                outWriter.write("In RSort(), digitPosition = " + dP + 
                                ", currentTable = " + currentTable +
                                ", previousTable = " + previousTable + "\n");
                outWriter.write("In RSort(), printing previous hashTable\n");
                printTable(hashTable, previousTable, outWriter);
    
            } catch (IOException e) {
                e.printStackTrace();
            }
            //tableIndex = 0
            for (int i = 0; i < 10; i++) {
                if(tableIndex > 9){
                    tableIndex = 0;
                }
            // move nodes from hashTable[previousTable][tableIndex] queue to current table
                while (!(hashTable[previousTable][i].isEmpty()) && i < 10) {
                    listNode newNode = hashTable[previousTable][i].deleteQ();
                    hashIndex = getIndex(newNode.data, digitPosition);
                    hashTable[currentTable][hashIndex].insertQ(hashTable, currentTable, hashIndex, newNode);
                }
                try {
                    debugWriter.write(
                            "In RSort(), finished moving one queue from previousTable "
                            + previousTable + " to currentTable " + currentTable + "; tableIndex = "
                            + tableIndex + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                tableIndex++;
            }
            digitPosition++;
        }
        
        try {
            outWriter.write("Printing the sorted data\n");
            printSortedData(hashTable, currentTable, outWriter, offSet);
        } catch (IOException e) {
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

    void printTable(LLQueue[][] hashTable, int currentTable, FileWriter outfileType){
        //call printQueue()
        try {
            for (int i = 0; i < hashTable[currentTable].length; i++){
                String printQ = hashTable[currentTable][i].printQueue(hashTable, currentTable, i);
                if (printQ != null){
                    outfileType.write(printQ + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void printSortedData(LLQueue[][] hashTable, int currentTable, FileWriter outfileType, int offSet){
        int lineNum = 0;
        try {
            for (int i = 0; i < hashTable[currentTable].length; i++){
                if(!hashTable[currentTable][i].isEmpty()){
                    LLQueue queue = hashTable[currentTable][i];
                    listNode tmp = queue.head.next;
                    while(tmp != null){
                        int data = tmp.data - offSet;
                            if(lineNum != 10){
                                outfileType.write(data + " ");
                                lineNum++;
                            } else {
                                outfileType.write("\n");
                                outfileType.write(data + " ");
                                lineNum = 1;
                            }
                    tmp = tmp.next;
                    }
                }
            }
            outfileType.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }       
    }
}    
