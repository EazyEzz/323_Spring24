import java.util.Scanner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SanchezE_Project4_Main {
    public static void main(String args[]) throws IOException{
        if(args.length < 3){
            System.err.println("Invalid Number of Arguments, 3 Required: <inputFile> <outputFile> <debugFile>");
            System.exit(1);
        }

        File inFile = new File(args[0]);
        File SSSfile = new File(args[1]);
        File deBugFile = new File(args[2]);

        try (BufferedReader reader = new BufferedReader( new FileReader(inFile));
            FileWriter outFile = new FileWriter(SSSfile, true);
            FileWriter debugFile = new FileWriter(deBugFile, true);){
            
            int n = 0;
            //BufferedReader has less overhead compared to Scanner for reading only one line
            String inLine = reader.readLine();
            if(inLine != null){
                n = Integer.parseInt(inLine.trim()); //trim used to remove leading/trailing spaces
                System.out.println("Num of Node Input = " + n);
            }

            DijktraSSS dijktra = new DijktraSSS(n);
            dijktra.loadCostMatrix(inFile);

            //ask for valid user source node inputs
            System.out.print("Enter Source Nodes (Range 1 - " + n + ") Or Enter 0 To Exit (Ex. 1 2 3 0)\n");
            
            Scanner userInput = new Scanner(System.in);
            while (true){
                System.out.print("Enter Source Node: ");
                int u;
                if (userInput.hasNextInt()){
                    u = userInput.nextInt();
                    if (u < 0 || u > n){
                        System.out.println("Invalid Source Node Entered: " + u + "\n");
                        System.out.print("Try Again or Enter 0 To Exit:");
                    } else if (u == 0){
                        break;
                    } else {
                        outFile.write("In main(): Source Node is " + u + "\n");
                        dijktra.sourceNode = u;
                        dijktra.setBestAry();
                        dijktra.setFatherAry();
                        dijktra.setToDoAry();
                        dijktra.deBugPrint(debugFile);

                        //loop until checkToDo == True
                        while (!dijktra.checkToDo()) {
                            //step 3
                            dijktra.findMinNode(debugFile);
                            int minN = dijktra.minNode;

                            outFile.write("In main(): minNode found is: " + minN + "\n");
                            dijktra.toDoAry[minN] = 0;

                            //step 4
                            dijktra.currentNode = 1;
                            outFile.append("In main(): currentNode is: " + dijktra.currentNode + "\n");
                            //step 5
                            while(dijktra.currentNode <= n){
                                if(dijktra.toDoAry[dijktra.currentNode] == 1){
                                    dijktra.newCost = dijktra.bestAry[minN] + dijktra.costMatrix[minN][dijktra.currentNode];
                                    if(dijktra.newCost < dijktra.bestAry[dijktra.currentNode]){
                                        dijktra.bestAry[dijktra.currentNode] = dijktra.newCost;
                                        dijktra.fatherAry[dijktra.currentNode] = minN;
                                    }
                                }
                                //step 6
                                dijktra.currentNode++;
                            }
                            //step 7: repeat 5-6 whole currN <= numNodes
                            //step 8: repeat 3-7 until checkToDo == 
                            dijktra.deBugPrint(debugFile);
                        }
                        //step 9: printShortestPath from sourceNode
                        dijktra.printShortestPath(outFile);
                        //step10: repeat 1-9 while sourceNode > 0
                    }
                } else {
                    System.out.print("Invalid Node Input, Try Again:");
                    userInput.next(); //clears scanner input
                }
            }
            userInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
class DijktraSSS {
    //uninitialized int = 0, arrays = NULL by default.
    int numNodes;
    int sourceNode;
    int minNode;
    int currentNode;
    int newCost;
    int costMatrix[][];
    int fatherAry[];
    int toDoAry[];
    int bestAry[];

    DijktraSSS(int n){
        this.numNodes = n;
        int size = n + 1;
        costMatrix = new int[size][size];
        fatherAry = new int[size];
        toDoAry = new int[size];
        bestAry = new int[size];
        
        for(int i = 1; i <= numNodes; i++){
            toDoAry[i] = 1;
            for(int j = 1; j <= numNodes ; j++){
                if(i == j){
                    //node path cost to itself = 0. ex: <1,1>, <2,2>,...
                    costMatrix[i][j] = 0;
                } else {
                    costMatrix[i][j] = 9999;
                }
            }
        }
    }
    void loadCostMatrix(File inFile){
        try (Scanner scanner = new Scanner(new FileReader(inFile))){
            //skip the first line that has the number of nodes.
            if(scanner.hasNextLine()){
                scanner.nextLine();
            }
            while (scanner.hasNextLine()){
                String line = scanner.nextLine();

                //new scanner to scan the entire String line
                //populate costMatrix with input data
                try (Scanner lineScanner = new Scanner(line)){
                    if(lineScanner.hasNextInt()){
                        int row = lineScanner.nextInt();
                        int col = lineScanner.nextInt();
                        int cost = lineScanner.nextInt();
                        costMatrix[row][col] = cost;
                    }
                } //inner lineScanner closes, no catch for Strings
            }
        } catch (IOException e){
            e.printStackTrace();
        } 
    }
    void setBestAry(){
        for(int i = 1; i <= numNodes; i++){
            bestAry[i] = costMatrix[sourceNode][i];
        }
    }
    void setFatherAry(){
        for(int i = 1; i <= numNodes; i++){
            fatherAry[i] = sourceNode;
        }
    }
    void setToDoAry(){
        for(int i = 1; i <= numNodes; i++){
            if(i == sourceNode) {
                toDoAry[i] = 0;
            } else {
                toDoAry[i] = 1;
            }
        }
    }
    void findMinNode(FileWriter debugFile) throws IOException{
        debugFile.write("Entering findMinNode()\n");
        int minCost = 99999;
        minNode = 0;

        for(int index = 1; index <= numNodes; index++){
            if(toDoAry[index] == 1 && bestAry[index] < minCost){
                minCost = bestAry[index];
                minNode = index;
            }
        }
        debugFile.write("In findMinNode(), minNode found is: " + minNode + "\n");
    }
    Boolean checkToDo(){
        Boolean isEmpty = false;
        int d = numNodes - 1;
        int check = d; //check starts out with the num of nodes to check
        //with data1 numNodes - 1 = 4 since upon initialization, source node index = 0.
        for(int i = 1; i <= numNodes; i++){
            check += toDoAry[i];
        }
        //if final sum is larger than what needed to be checked
        //then toDoAry does not contain all 0's, if it does contain all 0's
        //check will = same num of nodes to check.
        if(check == d){
            isEmpty = true;
        }
        return isEmpty;
    }
    void deBugPrint(FileWriter debugFile) throws IOException{
        StringBuilder fatherPrint = new StringBuilder("***Below Is The 'Father' Array***\n");
        fatherPrint.append("    node    father\n");
        StringBuilder todoPrint = new StringBuilder("***Below Is The 'ToDo' Array***\n");
        todoPrint.append("    node     todo\n");
        StringBuilder bestAryPrint = new StringBuilder("***Below Is The 'Best' Ary***\n");
        bestAryPrint.append("    node    best cost\n");
        
        for(int i = 1; i <= numNodes; i++){
            fatherPrint.append("      " + i + "       " + fatherAry[i] + "\n");
            todoPrint.append("      " + i + "       " + toDoAry[i] + "\n");
            bestAryPrint.append("      " + i + "       " + bestAry[i] + "\n");
        }

        debugFile.write(fatherPrint.toString() + "\n");
        debugFile.write(todoPrint.toString() + "\n");
        debugFile.write(bestAryPrint.toString() + "\n");
        //clear all String
        fatherPrint.setLength(0);
        todoPrint.setLength(0);
        bestAryPrint.setLength(0);        
    }
    void printShortestPath(FileWriter outFile) throws IOException{
        StringBuilder path = new StringBuilder("=====================================\n"
                                             + "There are " + numNodes + " nodes in the input graph.\n"
                                             + "=====================================\n");
        path.append("Source Node = " + sourceNode + "\n");
        path.append("The shortest paths from source node " + sourceNode + " are:\n\n");
        for(int i = 1; i <= numNodes; i++){
            path.append("The path from " + sourceNode + " to " + i + ": " + i);
            int t = fatherAry[i];
            while(t != sourceNode){
                path.append(" <- " + t);
                t = fatherAry[t];
            }
            path.append(" <- " + sourceNode + ": cost= " + bestAry[i] + "\n\n");
        }

        outFile.write(path.toString() + "\n");
        //clear path String
        path.setLength(0);
    }                                              
}
