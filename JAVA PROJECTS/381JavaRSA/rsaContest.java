import java.math.BigInteger;
import java.util.Scanner;

public class rsaContest{
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);

        //Input keys
        //BigInteger used to alloww larger integer values that are not typically allowed
        //using 32/64 bit
        System.out.println("-----------DECRYPT----------");
        System.out.println("Enter Public Key e: ");
        BigInteger e = new BigInteger(scanner.nextLine());
        System.out.println("Enter Modulus N: ");
        BigInteger N = new BigInteger(scanner.nextLine());
        System.out.println("Enter ciphertext c: ");
        BigInteger c = new BigInteger(scanner.nextLine());

        //using the square root method:
        BigInteger f = sqrt(N);
        boolean factorExists = false;
        while(f.compareTo(BigInteger.ONE) > 0){
            if(N.mod(f).equals(BigInteger.ZERO)){
                //we found a factor
                BigInteger p = f;
                BigInteger q = N.divide(p);
                System.out.println("Factors Found: P= " + p + "   Q= " + q);

                //compute z = (p-1)*(q-1)
                BigInteger z = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

                //compute d, multiplicative inverse of public key e.
                BigInteger d = e.modInverse(z);

                //decrypt msg
                BigInteger deCrypt = c.modPow(d, N);
                System.out.println("Cipher Decrypted: " + deCrypt);
                factorExists = true;
                break;
            }
            f = f.subtract(BigInteger.ONE);
        }
        if(!factorExists){
            System.out.println("No Factors Found");
        }
        scanner.close();
    }

    public static BigInteger sqrt(BigInteger n){
        BigInteger int1 = n;
        //n.shiftRight = division by 2, adds 1 
        BigInteger int2 = n.shiftRight(1).add(BigInteger.ONE);

        //while loop copares BitIntegers, if negative int2 is < int 1, if 0 it mean they are equal
        while(int2.compareTo(int1) < 0){
            int1 = int2;
            int2 = n.divide(int1).add(int1).shiftRight(1);
        }
        return int1;
    }
}