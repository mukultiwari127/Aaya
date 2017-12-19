package com.ideotic.edioticideas.aaya;

/**
 * Created by Mukul on 16-05-2016.
 */
public class Util {

    public static String mail(String add){
        String add1 = add.replaceAll("dot", ".").replaceAll("dash","-").replaceAll("underscore", "_")
                .replaceAll("at the rate", "@").replaceAll(" ","");
        return add1;
    }

    public static String number(String num)
    {
        String num1;
        for (int i = 0; i<=9;i++ )
            num1 = num.replace(num,num + " ");
        return null;
    }
}
