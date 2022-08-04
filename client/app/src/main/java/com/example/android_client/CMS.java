package com.example.android_client;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// Java program to calculate MD5 hash value
class MD5 {
    public static BigInteger getMd5(String input)
    {
        try {

//            System.out.println("--------- hash input  -> "+input);
            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            // of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

//             Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
//            System.out.println("----hash string   :  "+ hashtext);

            BigInteger result = new BigInteger(hashtext,16);
//            System.out.println("---------hash result ----> "+hashtext);
//            System.out.println("---------hash result2 ----> "+Integer.valueOf(hashtext));
            return result;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}


class ReturnVal {
    ReturnVal(String v,int j){
        this.v = v;
        this.j = j;
    }
    String v;
    int j;
}

public class CMS {
    Context context;
    CMS(Context context){
        this.context = context;
    }

     int m = 128;
     int k = 256;
     int e = (int)Math.exp(1);


     public ReturnVal run(String d, double epsilon) throws Exception {
         Random rand = new Random();
         int j = rand.nextInt(k);// 0 to k-1 - inclusive
         // hash func
         int[] v = new int[m];
         double p = 1.0/(1+(Math.pow(e,(epsilon/2.0))));
         for (int i = 0; i<m ; i++){
             double a = Math.random();
//             System.out.println(p+"   "+a);
             if (a <= p){
                 v[i] = 1;
             }else{
                 v[i] = -1;
             }
         }
         v [hash(j,d)]*= -1;
         StringBuilder mstr = new StringBuilder();
         for (int i = 0; i<m ; i++){
             mstr.append(v[i]);
         }

//         System.out.println("------------------------- hash test -->> "+hash(1,"0110"));

         return new ReturnVal(mstr.toString(),j);
     }

     public int hash(int index,String data) throws Exception {
         InputStream fs = context.getResources().openRawResource(R.raw.random_hash_strings);
         BufferedReader br = new BufferedReader(new InputStreamReader(fs));
         for(int i = 0; i < index; ++i)
             br.readLine();
         String line = br.readLine();

         // md5:
         int h1 = MD5.getMd5(data+line).mod(new BigInteger(String.valueOf(m))).intValue();
//         System.out.println("----big int  :  "+ MD5.getMd5(data+line));

         if(h1 < 0){
             h1*=-1;
         }
//         System.out.println("----big int  :  "+line);

         return h1;

     }




}
