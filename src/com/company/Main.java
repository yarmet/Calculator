package com.company;

/**
 * Created by yarmet on 07.02.2015.
 */
public class Main {
    public static void main(String[] args) {
        String str = "4^-2";
        Calculator c = new Calculator();
        System.out.println(c.getOPN(str));
        System.out.println(c.calculate());
    }
}
