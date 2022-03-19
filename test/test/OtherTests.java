/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Shamo Humbatli
 */
public class OtherTests {

    public static void main(String[] args) {

        Date dt = new Date();
        SimpleDateFormat sfg = new SimpleDateFormat("HH:mm");
        printStr("TF: " + sfg.format(dt));

        sfg = new SimpleDateFormat("yyyy-MM-dd");
        printStr("DF: " + sfg.format(dt));

        sfg = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        printStr("DTF: " + sfg.format(dt));

        printStr("Number format: " + String.format("%02d", 99));
        printStr("Number format: " + String.format("%02d", 9));
    }

    static void printStr(String val) {
        System.out.println("->> " + val);
    }
}
