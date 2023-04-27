import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Solution {
    /**
     * Method 1
     * <p>
     * Calculates and prints average and sample standard deviation of dry-bulb temperatures (F) during daylight hours of specified date
     * 
     * @param path path of csv data set
     * @param date date to process
     */
    public void daylightTemp(String path, String date) {
        String line;
        String[] data;
        double avg = 0;
        int sunrise = -1, sunset = -1, time, temp;
        List<Double> temps = new ArrayList<>();
        date = convert(date);
    
        try (BufferedReader reader = new BufferedReader(new FileReader(path));) {
            while ((line = reader.readLine()) != null) {
                if (!line.contains(date)) continue;
                
                data = line.split(",");
                
                if (sunrise == -1) { sunrise = Integer.parseInt(data[35]); sunset = Integer.parseInt(data[36]); }
                time = Integer.parseInt(data[5].split(" ")[1].replaceAll(":", ""));
                
                if (time < sunrise) continue; // INSTRUCTIONS UNCLEAR, BETWEEN IS INCLUSIVE IN MY IMPLEMENTATION
                else if (time > sunset) break;
                else {
                    if (data[10].isEmpty()) continue;

                    temp = temp(data[10]);
                    avg += temp;
                    temps.add((double)temp);
                }
            }
            
            // calculate sample standard deviation -> sqrt(sum/(sample size - 1))
            avg /= temps.size();
            double sum = 0;
            for (Double i : temps)
                sum += Math.pow(i - avg, 2);
            
            System.out.println(avg);
            System.out.println(Math.sqrt(sum/(temps.size() - 1)));
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    /**
     * Method 2
     * <p>
     * Prints all windchills of date when temperature is <= 40 deg F
     * 
     * @param path path of csv data set
     * @param date date to process
     */
    public void windchills(String path, String date) {
        String line;
        String[] data;
        boolean reached = false;
        int temp;
        date = convert(date);
    
        try (BufferedReader reader = new BufferedReader(new FileReader(path));) {
            while ((line = reader.readLine()) != null) {
                data = line.split(",");

                if (!data[5].contains(date)) {
                    if (!reached) continue;
                    else break;
                }
                reached = true;

                if (data[10].isEmpty() || data[17].isEmpty()) continue;

                temp = temp(data[10]);
                if (temp <= 40) System.out.println(windchill(temp, Integer.parseInt(data[17])));
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    /**
     * Method 3
     * <p>
     * Finds and prints the most similar same day b/t two data sets
     * 
     * @param path1 path of first csv data set
     * @param path2 path of second csv data set
     */
    public void similarDay(String path1, String path2) {
        Day day;
        String line1 = null, line2 = null, match = null;
        String[] data;
        int diff, similar = -1;

        // factor out
        Day[] days = new Day[2];
        String[] lines = {line1, line2}, dates = new String[2];
        String[][] datas = new String[2][];


        try (
            BufferedReader reader1 = new BufferedReader(new FileReader(path1));
            BufferedReader reader2 = new BufferedReader(new FileReader(path2));
        ) {
            reader1.readLine(); reader2.readLine(); // remove header
            BufferedReader[] readers = {reader1, reader2};

            while ((line1 = reader1.readLine()) != null && (line2 = reader2.readLine()) != null) {
                // find the same date
                do {
                    lines[0] = line1; lines[1] = line2;

                    for (int i = 0; i < 2; i++) {
                        datas[i] = lines[i].split(",");
                        dates[i] = datas[i][5].split(" ")[0];
                    }
                    diff = compareDates(dates[0], dates[1]);

                    if (diff > 0) line2 = reader2.readLine();
                    else if (diff < 0) line1 = reader1.readLine();
                } while (diff != 0);

                // load each day's entirety
                for (int i = 0; i < 2; i++) {
                    days[i] = new Day(dates[i]);
                    day = days[i];
                    
                    do {
                        datas[i] = lines[i].split(",");
                        data = datas[i];

                        day.addTemp(data[10]);
                        day.addHumidity(data[16]);
                        day.addSpeed(data[17]);
                    } while ((lines[i] = readers[i].readLine()) != null && lines[i].contains(dates[i]));
                }
                
                // check if more similar
                diff = days[0].compareTo(days[1]);
                if (similar == -1 || similar > diff && diff != -1) {
                    similar = diff;
                    match = dates[0];
                }
            }
            
            System.out.println(convert(match));
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    /**
     * Converts MM/dd/yy to yyyy-MM-dd and vice-versa
     * 
     * @param date  date to convert
     * @return      converted date
     */
    public String convert(String date) {
        String[] sSplit = date.split("-|/");
        int[] split = new int[3];
        
        for (int i = 0; i < sSplit.length; i++)
            split[i] = Integer.parseInt(sSplit[i]);

        if (date.contains("/")) { // convert to yyyy-MM-dd
            return String.format("20%02d-%02d-%02d", split[2], split[0], split[1]);
        }

        // convert to MM/dd/yyyy
        return String.format("%d/%d/%02d", split[1], split[2], split[0] % 100);
    }

    /**
     * Trims and returns integer temperature
     * 
     * @param temp
     * @return
     */
    public int temp(String temp) {
        return Integer.parseInt(temp.replaceAll("s", "")); // remove suspect marker
    }
 
    /**
     * Calculates windchill factor given temp and wind speed
     * 
     * @param temp  temperature
     * @param speed wind speed
     * @return windchill factor
     */
    public int windchill(int temp, int speed) {
        return (int)Math.round(35.74 + 0.6215*temp - 35.75*Math.pow(speed, 0.16) + 0.4275*temp*Math.pow(speed, 0.16));
    }

    public int compareDates(String date1, String date2) {
        int[] split1 = Arrays.stream(date1.split("/")).mapToInt(Integer::parseInt).toArray();
        int[] split2 = Arrays.stream(date2.split("/")).mapToInt(Integer::parseInt).toArray();
        int[] order = {2, 0, 1}; // check year, then month, then day

        for (int i = 0; i < 3; i++) {
            if (split1[order[i]] > split2[order[i]]) return 1;
            else if (split1[order[i]] < split2[order[i]]) return -1;
        }

        return 0;
    }

    public static class Day implements Comparable<Day> {
        int tempCount, humidityCount, speedCount;
        double temp, humidity, speed;
        int d, m, y;

        public Day(String date) {
            int[] split = Arrays.stream(date.split("/")).mapToInt(Integer::parseInt).toArray();

            this.d = split[1];
            this.m = split[0];
            this.y = split[2];

            temp = humidity = speed = 0;
            tempCount = humidityCount = speedCount = 0;
        }

        public void addTemp(String temp) {
            if (temp.isEmpty()) return;
            this.temp += Integer.parseInt(temp.replaceAll("s", ""));
            tempCount++;
        }

        public void addHumidity(String humidity) {
            if (humidity.isEmpty()) return;
            this.humidity += Integer.parseInt(humidity);
            humidityCount++;
        }

        public void addSpeed(String speed) {
            if (speed.isEmpty()) return;
            this.speed += Integer.parseInt(speed.replaceAll("s", ""));
            speedCount++;
        }

        public double[] getAvgs() {
            double[] avgs = new double[3];
            avgs[0] = temp/tempCount;
            avgs[1] = humidity/humidityCount;
            avgs[2] = speed/speedCount;
            return avgs;
        }

        @Override
        public int compareTo(Day other) {
            double[] avg1 = this.getAvgs(), avg2 = other.getAvgs();
            if (tempCount + humidityCount + speedCount == 0) return -1; // if no data for day
            return (int)(Math.sqrt(Math.pow(avg1[0] - avg2[0], 2) + Math.pow(avg1[1] - avg2[1], 2) + Math.pow(avg1[2] - avg2[2], 2))*1000);
        }
    }
}
