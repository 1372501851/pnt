package com.inesv.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class OrderUtil {
    /** 月日 */
    public static final String md = "YYYYMMdd";

    public static String makeOrderCode(){

        Date date = new Date();
        DateFormat df = new SimpleDateFormat(md);
        String part1 = df.format(date);
 
        String part2 = (System.currentTimeMillis() / 1000 + "").substring(5);
        String part3 = (System.nanoTime()+ "").substring(7).substring(0,5);

        return part1 + part2 + part3; 
    }  

    public static String makeOutCode(){

        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String part1 = df.format(date);
        if (part1.length() == 4){
            part1 = part1.substring(1);
        }

        String part2 = (System.currentTimeMillis() / 1000 + "").substring(5);
        String part3 = (System.nanoTime()+ "").substring(7).substring(0,5);

        return part1 + part2 + part3;
    }

    public static String getTime(){
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        return formatter.format(date);
    }

    public static int getRandomCode(){

        return (int) Math.abs(new Random().nextInt(899999) + 100000);
    }

    public static void main(String[] args){
        System.out.println(makeOrderCode());
        System.out.println(makeOutCode());
    }
}
