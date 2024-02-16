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

        FileWriter debugWriter = new FileWriter(debugFile);
        FileWriter writer = new FileWriter(outFile);

        debugWriter.write("In main(), after open all files");

        LLStack llStack = new LLStack();

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
    static void printNode(listNode node){
        if(node != null){ //does node exist?
            if(node.next != null){
                System.out.println("(" + node.data + "," + node.next.data + ")");
            } else {
                System.out.println("(" + node.data + ", null)");
            }
        } else {
            System.out.println("(null,null)");
        }
    }
}

class LLStack{
    //private prevents any access to the dummy node
    //top is a variable reference that is capable of holding a listNode object. 
    private listNode top;

    LLStack(){
        this.top = new listNode(-9999);
        //new listNode default = null
    }

    void push(int data){
        listNode newNode = new listNode(data);
        newNode.next = top.next;
        top.next = newNode;
    }

    boolean isEmpty(){
        return top.next == null;
    }

    void pop(){
        if (isEmpty()){
            throw new RuntimeException("Stack is Empty");
        }else {
            listNode popNode = top.next;
            top.next = popNode.next;
        }
    }

    void buildStack(LLStack currStack, File inFile, File debugFile){
    try(
        Scanner scanner = new Scanner(new FileReader(inFile));
        FileWriter debugWriter = new FileWriter(debugFile)){

        } catch (IOException e) {
                e.printStackTrace();
        }
    }
}