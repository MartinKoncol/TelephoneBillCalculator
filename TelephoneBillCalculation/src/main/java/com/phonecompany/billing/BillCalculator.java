package com.phonecompany.billing;


import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class BillCalculator implements TelephoneBillCalculator {

    String telephoneNumber;
    Calendar start = Calendar.getInstance();
    Calendar end = Calendar.getInstance();
    Date callStart;
    Date callEnd;
    Double cost = 0.0D;

    public BigDecimal calculate(String phoneLog) {

        parse(phoneLog);

        start.setTime(callStart);
        end.setTime(callEnd);

        outputText();

        whatShouldBePaidBasedOnPeriod();

        BigDecimal bill = new BigDecimal(cost);
        return bill;
    }

    public void parse(String textInput) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH);
        String[] parseInput = textInput.split(",");

        try {

            telephoneNumber = parseInput[0];
            callStart = sdf.parse(parseInput[1]);
            callEnd = sdf.parse(parseInput[2]);

        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public void whatShouldBePaidBasedOnPeriod() {

        long duration = Duration.between(callStart.toInstant(), callEnd.toInstant()).toSeconds();
        long fiveMinutes = 300L;

        if (duration < fiveMinutes) {
            checkHourlyRateForShorterCalls(duration);
        } else {
            duration -= fiveMinutes;
            multiplyCostByRate(duration, 0.20D);
        }

    }

    public void checkHourlyRateForShorterCalls(Long duration) {

        if (start.get(Calendar.HOUR_OF_DAY) > 8 && end.get(Calendar.HOUR_OF_DAY) < 16) {
            multiplyCostByRate(duration, 1.0D);
        } else if (start.get(Calendar.HOUR_OF_DAY) < 8 && end.get(Calendar.HOUR_OF_DAY) >= 8) {

            long[] durations = betweenBusyHours(start,8);

            multiplyCostByRate(durations[0], 0.50D);
            multiplyCostByRate(durations[1], 1.0D);

        } else if (start.get(Calendar.HOUR_OF_DAY) < 16 && end.get(Calendar.HOUR_OF_DAY) >= 16) {

            long[] durations = betweenBusyHours(end,16);

            multiplyCostByRate(durations[0], 0.50D);
            multiplyCostByRate(durations[1], 1.0D);

            System.out.println("Duration lower 16 " + durations[0] + "from: " + callStart.toInstant() + " to " + end.getTime().toInstant());
            System.out.println("Duration higher 16 " + durations[1] + "from: " + end.getTime().toInstant() + " to " + callEnd.toInstant());

        } else {
            multiplyCostByRate(duration, 0.50D);
        }

    }

    public long[] betweenBusyHours(Calendar period,int hour) {
        period.set(Calendar.HOUR_OF_DAY, hour);
        period.set(Calendar.MINUTE, 0);
        period.set(Calendar.SECOND, 0);

        long durationLower = Duration.between(callStart.toInstant(), period.getTime().toInstant()).toSeconds();
        long durationHigher = Duration.between(period.getTime().toInstant(), callEnd.toInstant()).toSeconds();

        long[] durations = {durationLower, durationHigher};

        return durations;
    }

    public void multiplyCostByRate(Long duration, Double rate) {
        if (duration % 60 != 0) {
            cost += rate;
        }
        int i = (int) (duration / 60);
        cost += i * rate;
    }

    public void outputText() {
        String textBlock = """
                \u2022 TelNumber : %s
                \u2022 CallStart : %s
                \u2022 CallEnd   : %s
                    """.formatted(telephoneNumber, callStart, callEnd);

        System.out.println(textBlock);
    }
}
