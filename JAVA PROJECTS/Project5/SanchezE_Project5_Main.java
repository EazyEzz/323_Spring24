import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class SanchezE_Project5_Main {
    public static void main(String[] args) throws IOException{
        if(args.length < 3){
            System.err.println("Invalid Number of Arguments, 3 Required: <inputFile> <outputFile> <debugFile>");
            System.exit(1);
        }
        
        File inFile = new File(args[0]);
        File outFile = new File(args[1]);
        File deBugFile = new File(args[2]);

        try (Scanner scanner = new Scanner(new FileReader(inFile));
            FileWriter outWriter = new FileWriter(outFile, true);
            FileWriter debugWriter = new FileWriter(deBugFile, true);){
            
            int n = 0;
            String inLine = scanner.nextLine();
            if(!inLine.isEmpty()){
                n = Integer.parseInt(inLine.trim());
                System.out.println("Testing Input. Number of Nodes = " + n);
            }

            KruskalMST krustalMST = new KruskalMST(n);

            debugWriter.write("*** In main() Printing the Input Graph ***\n");

            while (scanner.hasNextLine()){
                String line = scanner.nextLine();

                try (Scanner lineScanner = new Scanner(line)){
                    if(lineScanner.hasNextInt()){
                        int u = lineScanner.nextInt();
                        int w = lineScanner.nextInt();
                        int c = lineScanner.nextInt();

                        edge newEdge = new edge(u, w, c);

                        debugWriter.write("In main() newEdge from inFile is: ");
                        debugWriter.write(newEdge.printEdge(newEdge, 1) + "\n");

                        edge spot = krustalMST.findSpot(newEdge,1); //spot in edgeList
                        krustalMST.insertEdge(newEdge, spot);
                        debugWriter.write("In main(). Printing edgeList after insert of new edge:\n");
                        debugWriter.write(krustalMST.printList(1));
                        //System.out.println("Spot found: " + spot.printEdge(spot, 1));
                    }
                }
            }
            debugWriter.write("*** In main() At the end of printing all edges of the input graph\n\n");

            while(krustalMST.numSets > 1){
                edge nextEdge = krustalMST.getEdge();
                debugWriter.write("In main() the nextEdge is: " + nextEdge.printEdge(nextEdge,1) + "\n");

                edge spot = krustalMST.findSpot(nextEdge, 2); //spot in mstList
                krustalMST.insertEdge(nextEdge, spot);
                krustalMST.totalMSTCost += nextEdge.cost;
                krustalMST.merge2Sets(nextEdge.nU, nextEdge.nW);
                krustalMST.numSets--;
                debugWriter.write("In main() numSets = " + krustalMST.numSets + "\n");

                debugWriter.write("Printing whichSet array: \n");
                debugWriter.write(krustalMST.printAry() + "\n");
                debugWriter.write("In main(). Printing the remaining of edgeList:\n");
                debugWriter.write(krustalMST.printList(1));
                debugWriter.write("In main(). Printing the growing MST list:\n");
                debugWriter.write(krustalMST.printList(2));
            }
            outWriter.write(krustalMST.printMST());
        } catch (IOException e){
            e.printStackTrace();
        }
    }    
}
class edge {
    int nU;
    int nW;
    int cost;
    edge next;

    edge(int n1, int n2, int cost){
        this.nU = n1;
        this.nW = n2;
        this.cost = cost;
        this.next = null;
    }

    String printEdge(edge e, int choice){
        String s = "";
        if (e!= null){
            if (choice == 1){
                s = "(" + e.nU + ", " + e.nW + ", " + e.cost + ") -> " ;
            } else if (choice == 2){
                s = "<" + e.nU + ", " + e.nW + ", " + e.cost + "> -> " ;
            } else if (choice == 3){
                s = e.nU + " " + e.nW + " " + e.cost;
            }
        } else{
            s = "(null edge)";
        }
        return s;   
    }
}
class KruskalMST{
    int N;
    int[] whichSet; //intially nodes are singleton sets
    int numSets; //remain num of sets, initially N
    int totalMSTCost;
    edge edgeList; //ordered LList of graph, asc
    edge mstList; //ordered LList of edges in MST, asc
    
    KruskalMST(int N){
        this.N = N;
        this.numSets = N;
        this.whichSet = new int[N + 1];

        for(int i = 1; i <= N; i++){
            whichSet[i] = i;
        }

        this.edgeList = new edge(0, 0, 0);
        this.mstList = new edge(0, 0, 0);
        this.totalMSTCost = 0;
    }

    void insertEdge(edge newEdge, edge spot){
        newEdge.next = spot.next;
        spot.next = newEdge;
    }

    edge findSpot(edge newEdge, int choice){
        //choice 1 = edgeList, choice 2 = mstList
        edge spot = null;
        if(choice == 1){
            spot = edgeList;
        } else if (choice == 2){
            spot = mstList;
        }

        //order by edge cost first and then by nU node values.
        while(spot.next != null && (newEdge.cost > spot.next.cost || (newEdge.cost == spot.next.cost && newEdge.nU >= spot.next.nU))){
            spot = spot.next;
        }
        // while(spot.next != null && newEdge.cost == spot.next.cost){
        //     if(newEdge.nU > spot.next.nU){
        //         spot = spot.next;
        //     }
        // }
        return spot;
    }

    edge removeEdge(){
        edge currEdge = edgeList;
        if(currEdge.next != null){
            edge tmp = currEdge.next;
            currEdge.next = tmp.next;
            tmp.next = null;
            return tmp;
        }
        return null;
    }

    String printAry(){
        StringBuilder sb = new StringBuilder("whichSet Array = ");
        for(int i = 1; i <= N; i++){
            sb.append("[" + whichSet[i] + "]");
        }
        sb.append("\n");
        return sb.toString();
    }

    String printList(int choice){
        //choice 1 = print edgeList, choice 2 = print mstList
        StringBuilder sb = new StringBuilder("");
        edge currEdge = null;
        if(choice == 1){
            sb.append("listHead -> ");
            currEdge = edgeList;
        } else if(choice == 2){
            sb.append("mstListHead -> ");
            currEdge = mstList;
        }

        while(currEdge != null){
            sb.append(currEdge.printEdge(currEdge, 2));
            currEdge = currEdge.next;
        }
        sb.append("NULL\n");
        return sb.toString();
    }

    String printMST(){
        StringBuilder sb = new StringBuilder("*** A Kruskal's MST of the input graph is given below: ***\n");
        sb.append("     " + N + "\n");
        edge curEdge = mstList.next;
        while(curEdge != null){
            sb.append("     " + curEdge.printEdge(curEdge, 3) + "\n");
            curEdge = curEdge.next;
        }
        sb.append("*** The total cost of a Kruskal's MST is: " + totalMSTCost + "\n");
        return sb.toString();
    }

    edge getEdge(){
        edge t = null;
        do{
            t = removeEdge();
            if (t == null){
                break;
            }
        }while(whichSet[t.nU] == whichSet[t.nW]);
        return t;
    }

    void merge2Sets(int nU, int nW){
        if(whichSet[nU] < whichSet[nW]){
            updateWhichSet(whichSet[nW], whichSet[nU]);
        } else {
            updateWhichSet(whichSet[nU], whichSet[nW]);
        }
    }

    void updateWhichSet(int a, int b){
        for(int i = 1; i <= N; i++){
            if(whichSet[i] == a){
                whichSet[i] = b;
            }
        }
    }
}
