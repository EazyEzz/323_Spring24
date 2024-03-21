#include <iostream>
#include <fstream>
#include <cstdlib> //used for exit()
#include <string>
#include <sstream>

using namespace std;

class HtreeNode {
    public:
        string chStr;
        int prob;
        string code;
        HtreeNode* left;
        HtreeNode* right;
        HtreeNode* next;
        
        //constructor using an initializer list
        HtreeNode(string newChar, int newProb){
            this->chStr = newChar;
            this->prob = newProb;
            this->code = "";
            this->left = nullptr;
            this->right = nullptr;
            this->next = nullptr;
        }

        static string printNode (HtreeNode* T){
            string s;
            //ternary operators used to resolve nullptr issues
            if (T != nullptr){
                s = "(" + T->chStr + ", " + to_string(T->prob) + ", " + T->code;
                s += ", " + (T->next ? T->next->chStr : "null"); //if T->next != null then expr1 else expr2.
                s += ", " + (T->left ? T->left->chStr : "null");
                s += ", " + (T->right ? T->right->chStr : "null");
                s += ")";
            } else {
                s = "Node is Null";
            }
            return s;
        }
};

class HuffmanBinaryTree {
    private:
        HtreeNode* listHead;
    public:
        HtreeNode* Root;

    HuffmanBinaryTree(){
        this->listHead = new HtreeNode("", 0);
    }

    HtreeNode* findSpot(HtreeNode* newNode){
        HtreeNode* spot = listHead;
        while(spot->next != nullptr && newNode->prob > spot->next->prob){
            spot = spot->next;
        }
        return spot;
    }

    void listInsert(HtreeNode* spot, HtreeNode* newNode){
        newNode->next = spot->next;
        spot->next = newNode;
    }

    HtreeNode* constructHuffmanLList(ifstream& inFile, ofstream& debugFile){
        //step0
        debugFile << "Entering constructHuffmanLList() method" << endl;
        debugFile << "---------------------------------------" << endl;
        //step1: listHead contains HtreeNode("dummy", 0) when HuffmanBinaryTree initialized
        //step2: get data from inFile
        string line, chr;
        int prob;
        while (getline(inFile, line)){
            //input stream string to allow using '>>' to read the mixed data <char,int> tuples.
            //converts char "40" to int 40.
            istringstream iss(line);
            //if reading the line fails, print it, skip it and continue with the rest of the data
            if (!(iss >> chr >> prob)){
                cerr << "Error reading input data line: " << line << endl;
                continue;
            }

            debugFile << "In constructHuffmanLList(), chr = " + chr + " , prob = " + to_string(prob) << endl;
            HtreeNode* newNode = new HtreeNode(chr, prob);
            debugFile << "In constructHuffmanLList(), printing newNode: " << endl;
            debugFile << newNode->printNode(newNode);
            debugFile << endl;


            HtreeNode* spot = findSpot(newNode);
            listInsert(spot, newNode);

            debugFile << "In constructHuffmanLList() printing list: " << endl;
            //print list
            debugFile << printList() << endl;
        }
        return listHead;
    }

    HtreeNode *constructHuffmanBinTree(HtreeNode* listHead, ofstream &debugFile){
        debugFile << "\n---------------------------------" << endl;
        debugFile << "Entering constructHuffmanBinTree:" << endl;
        debugFile << "-----------------------------------" << endl;
        HtreeNode* currPos = listHead->next;
        string nodeChars;
        int sumProb;
        while (currPos->next != nullptr){
            nodeChars = currPos->chStr;
            sumProb = currPos->prob;
            nodeChars += currPos->next->chStr;
            sumProb += currPos->next->prob;

            HtreeNode *newNode = new HtreeNode(nodeChars, sumProb);
            newNode->left = currPos;
            newNode->right = currPos->next;
            currPos = currPos->next;

            debugFile << "In constructHuffmanBinTree, printing newNode: " << HtreeNode::printNode(newNode) << endl;

            HtreeNode *spot = findSpot(newNode);
            listInsert(spot, newNode);
            currPos = currPos->next;
            listHead->next = currPos;
            printList();
            nodeChars = "";
            sumProb = 0;
        }
        return listHead->next; // can return Null
    }

    void preOrderTraversal (HtreeNode* root, ofstream& outfile){
        if(isLeaf(root)){
            outfile << HtreeNode::printNode(root) << endl;
        } else {
            outfile << HtreeNode::printNode(root) << endl;
            preOrderTraversal(root->left, outfile);
            preOrderTraversal(root->right, outfile);
        }
    }

    void inOrderTraversal (HtreeNode* root, ofstream& outfile){
        if (root != nullptr) {
            inOrderTraversal(root->left, outfile); //vist leftmost child first
            outfile << HtreeNode::printNode(root) << endl;
            inOrderTraversal(root->right, outfile);
        }
    }

    void postOrderTraversal (HtreeNode* root, ofstream& outfile){
        if (root != nullptr) {
            postOrderTraversal(root->left, outfile);
            postOrderTraversal(root->right, outfile);
            outfile << HtreeNode::printNode(root) <<endl;
        }
    }

    void constructCharCode (HtreeNode* node, string code, ofstream& outfile){
        if(isLeaf(node)){
            node->code = code;
            string outString = node->chStr + "    | " + node->code;
            outfile << outString << endl;
        } else {
            constructCharCode(node->left, code += "0", outfile);
            constructCharCode(node->right, code += "1", outfile);
        }
    }
    
    bool isLeaf (HtreeNode* node){
        return (node->left == nullptr && node->right == nullptr); 
    }

    string printList(){
        string sb = "listHead";
        HtreeNode* currNode = listHead->next;
        while (currNode != nullptr){
            string currData = 
            sb += " -> " + HtreeNode::printNode(currNode);
            currNode = currNode->next;
        }
        sb += " -> NULL";
        return sb;
    }

        string printList(HtreeNode* listHead){
        string sb = "listHead";
        HtreeNode* currNode = listHead->next;
        while (currNode != nullptr){
            string currData = 
            sb += " -> " + HtreeNode::printNode(currNode);
            currNode = currNode->next;
        }
        sb += " -> NULL";
        return sb;
    }
};

int main(int argc, char* argv[]){
    if (argc < 4){ //argv[0] = name of file, arguments for input files begin at argv[1].
        //errors: cerr used for immediate non-buffered output to terminal
        cerr << "Invalid Number of Arguments, 3 required <inputFile> <outputFile> <debugFile>" << endl;
        exit(1);
    }

    //try-catch automatic closing of files when out of scope, outside try-catch.  
    try{
        // open files
        ifstream inputFile(argv[1]);
        if (!inputFile.is_open()){
            throw runtime_error("Input file failed to open.");
        }

        ofstream outputFile(argv[2]);
        if (!outputFile.is_open()){
            throw runtime_error("Output file failed to open.");
        }

        ofstream debugFile(argv[3]);
        if (!debugFile.is_open()){
            throw runtime_error("Debug file failed to open.");
        }

        HuffmanBinaryTree huffmanTree;

        HtreeNode* listHead = huffmanTree.constructHuffmanLList(inputFile, debugFile);

        outputFile << "In main() printing list: \n" << huffmanTree.printList(listHead) << endl;

        HtreeNode* Root = huffmanTree.constructHuffmanBinTree(listHead, debugFile);

        outputFile << "----------------" << endl;
        outputFile << "char | code" << endl;
        outputFile << "----------------" << endl;
        huffmanTree.constructCharCode(Root, "", outputFile);
        outputFile << "----------------" << endl;

        outputFile << "PreOrder Traversal:" << endl;
        huffmanTree.preOrderTraversal(Root,outputFile);
        outputFile << "-------------------------------------" << endl;

        outputFile << "InOrder Traversal:" << endl;
        huffmanTree.inOrderTraversal(Root,outputFile);
        outputFile << "-------------------------------------" << endl;

        outputFile << "PostOrder Traversal:" << endl;
        huffmanTree.postOrderTraversal(Root,outputFile);
        outputFile << "-------------------------------------" << endl;

    } catch (const exception& e){  //all input files automatically closed here
        cerr <<"Error with Files: " << e.what() << endl;
        return 1;
    }

    return 0;
}