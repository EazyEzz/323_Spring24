import java.util.Scanner;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SanchezE_Project1_Main{

    public static void main(String[] args) throws IOException{
        if (args.length <3){
            System.err.println("Invalid Number of Arguments, 3 required <inputFile> <outputFile> <debugFile>");
            System.exit(1);
        }

        File inFile = new File(args[0]);
        File outFile = new File(args[1]);
        File debugFile = new File(args[2]);

        try(FileWriter debugWriter = new FileWriter(debugFile, true);
            FileWriter outWriter = new FileWriter(outFile, true);){
            debugWriter.write("In main(), after opening all files\n\n");

            LLStack llStack = new LLStack();
    
            llStack.buildStack(inFile, debugWriter);
    
            outWriter.write("In main() after buildStack and printing stack\n");
            outWriter.write(llStack.printStack() + "\n\n");

            LLQueue llQueue = new LLQueue();

            llQueue.buildQueue(inFile, debugWriter);

            outWriter.write("In main() after buildQueue and printing Queue\n");
            outWriter.write(llQueue.printQueue() + "\n\n");

            LLlist llList = new LLlist();

            llList.buildList(inFile, debugWriter);

            outWriter.write("In main() after buildList and printing List\n");
            outWriter.write(llList.printList() + "\n\n");

        } catch (IOException e){
            e.printStackTrace();
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

    //static allows for method access without creating an instance of class
    //print out format: (node data, node next data)
    static String printNode(listNode node){
        String s = "";
        if(node != null){ //does node exist?
            if(node.next != null){
                //System.out.println("(" + node.data + "," + node.next.data + ")");
                s = "(" + node.data + "," + node.next.data + ")";
            } else {
                //System.out.println("(" + node.data + ", null)");
                s = "(" + node.data + ", null)";
            }
        } else {
            //System.out.println("(null,null)");
            s = "(null,null)";
        }
        return s;
    }
}

class LLStack{
    //private prevents any access to the dummy node
    //top is a variable reference that is capable of holding a listNode object. 
    private listNode top;

    LLStack(){
        this.top = new listNode(-9999);
        //new listNode next default = null
    }

    void push(listNode newNode){
        newNode.next = top.next;
        top.next = newNode;
    }

    boolean isEmpty(){
        return top.next == null;
    }

    listNode pop(){
        listNode t = top;
        if (t.next != null){
            listNode tmp = t.next;
            t.next = tmp.next;
            tmp.next = null;
            return tmp;
        } else {
            return null;
        }
    }

    String printStack(){
        StringBuilder sb = new StringBuilder("Top");
        listNode currentNode = top;
        while(currentNode != null){
            sb.append(" -> ").append(listNode.printNode(currentNode));
            currentNode = currentNode.next;
        }
        sb.append(" -> NULL");
        return sb.toString();
    }

    void buildStack(File inFile, FileWriter debugWriter){
        try (
            Scanner scanner = new Scanner(new FileReader(inFile));) {

            debugWriter.write("Entering buildStack()\n");

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (!line.isEmpty()) {
                    char op = line.charAt(0);
                    int data = Integer.parseInt(line.substring(1).trim());

                    debugWriter.write("In buildStack(): op = " + op + ", data = " + data + "\n");

                    if (op == '+') {
                        listNode newNode = new listNode(data);
                        push(newNode);
                    } else if (op == '-') {
                        listNode topNode = pop();
                        if (topNode != null) {
                            debugWriter.write("In buildStack(), top node deleted\n");
                            topNode = null;
                        } else {
                            debugWriter.write("In buildStack(), stack is empty\n");
                        }
                    } else {
                        debugWriter.write("In buildStack(), op = " + op + " is illegal\n");
                    }

                }
            }
            debugWriter.write("In buildStack(), printing Stack\n");
            debugWriter.write(printStack() + "\n");
            debugWriter.write("Leaving buildStack()\n\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class LLQueue{
    private listNode head;
    private listNode tail;

    LLQueue(){
        this.head = new listNode(-9999);
        this.tail = head;
    }

    void insertQ(listNode newNode) {
        tail.next = newNode;
        tail = newNode;
    }

    listNode deleteQ(){
        listNode t = head;
        if(t.next != null) {
            if(tail == t.next){
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

    String printQueue(){
        StringBuilder sb = new StringBuilder("Head");
        listNode currentNode = head;
        while(currentNode != null){
            sb.append(" -> ").append(listNode.printNode(currentNode));
            currentNode = currentNode.next;
        }
        sb.append(" -> NULL");
        return sb.toString();
    }

    void buildQueue(File inFile, FileWriter debugWriter){
        try(
            Scanner scanner = new Scanner(new FileReader(inFile));
            ){
                debugWriter.write("Entering buildQueue()\n");
    
                while (scanner.hasNextLine()){
                    String line = scanner.nextLine();
                    if (!line.isEmpty()) {
                        char op = line.charAt(0);
                        int data = Integer.parseInt(line.substring(1).trim());
    
                        debugWriter.write("In buildQueue(): op = " + op + ", data = " + data + "\n");
    
                        if (op == '+') {
                            listNode newNode = new listNode(data);
                            insertQ(newNode);
                        } else if (op == '-') {
                            listNode tailNode = deleteQ();
                            if (tailNode != null) {
                                debugWriter.write("In buildQueue(), tailNode's data is " + tailNode.data + "\n");
                                tailNode = null;
                            } else {
                                debugWriter.write("In buildQueue(), the Queue is empty\n");
                            }
                        } else {
                            debugWriter.write("In buildQueue(), op = " + op + " is illegal\n");
                        }
    
                    }
                }
                debugWriter.write("In buildQueue(), printing Queue\n");
                debugWriter.write(printQueue() + "\n");
                debugWriter.write("Leaving buildQueue()\n\n");            
            } catch (IOException e) {
                    e.printStackTrace();
            }
    }
}

class LLlist{
    private listNode listHead;

    LLlist(){
        this.listHead = new listNode(-9999);
    }

    listNode findSpot(listNode newNode){
        listNode spot = listHead;
        if(listHead.next != null && newNode.data > spot.next.data){
            spot = spot.next;
        }
        return spot;
    }

    void insertOneNode(listNode spot, listNode newNode){
        newNode.next = spot.next;
        spot.next = newNode;
    }

    listNode deleteOneNode(int data){
        listNode spot = listHead;
        while(spot.next != null && spot.next.data < data){
            spot = spot.next;
        }
        if(spot.next != null && spot.next.data == data){
            listNode tmp = spot.next;
            spot.next = tmp.next;
            tmp.next = null;
            return tmp;
        }
        return null;
    }

    String printList(){
        StringBuilder sb = new StringBuilder("listHead");
        listNode currentNode = listHead;
        while(currentNode != null){
            sb.append(" -> ").append(listNode.printNode(currentNode));
            currentNode = currentNode.next;
        }
        sb.append(" -> NULL");
        return sb.toString();
    }

    void buildList(File inFile, FileWriter debugWriter){
        try(
            Scanner scanner = new Scanner(new FileReader(inFile));
            ){
                debugWriter.write("Entering buildList()\n");
    
                while (scanner.hasNextLine()){
                    String line = scanner.nextLine();
                    if (!line.isEmpty()) {
                        char op = line.charAt(0);
                        int data = Integer.parseInt(line.substring(1).trim());
    
                        debugWriter.write("In buildList(): op = " + op + ", data = " + data + "\n");
    
                        if (op == '+') {
                            listNode newNode = new listNode(data);
                            listNode spot = findSpot(newNode);
                            insertOneNode(spot, newNode);
                        } else if (op == '-') {
                            listNode junk = deleteOneNode(data);
                            if (junk != null) {
                                debugWriter.write("In buildList(), data found and node deleted\n");
                                junk = null;
                            } else {
                                debugWriter.write("In buildList(), data is not in the list\n");
                            }
                        } else {
                            debugWriter.write("In buildList(), op = " + op + " is illegal\n");
                        }
    
                    }
                }
                debugWriter.write("In buildList(), printing List\n");
                debugWriter.write(printList() + "\n");
                debugWriter.write("Leaving buildList()\n\n");            
            } catch (IOException e) {
                    e.printStackTrace();
            }
    }
}