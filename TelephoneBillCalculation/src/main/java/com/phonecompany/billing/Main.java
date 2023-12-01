package com.phonecompany.billing;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        BillCalculator calculator = new BillCalculator();
        BigDecimal bill = calculator.calculate("420774577453,13-01-2020 15:10:15,13-01-2020 15:12:57");
        System.out.println(bill);
        //****************************
        BillCalculator calculator2 = new BillCalculator();
        BigDecimal bill2 = calculator2.calculate("420776562353,18-01-2020 07:59:15,18-01-2020 08:02:57");
        System.out.println(bill2);
        //****************************
        BillCalculator calculator3 = new BillCalculator();
        BigDecimal bill3 = calculator3.calculate("420776562353,18-01-2020 15:59:15,18-01-2020 16:02:40");
        System.out.println(bill3);
    }
}