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
        String row;
        Data data;

        double avg = 0;
        int sunrise = -1, sunset = -1, time, temp;
        List<Double> temps = new ArrayList<>();
        date = convert(date);
    
        try (BufferedReader reader = new BufferedReader(new FileReader(path));) {
            while ((row = reader.readLine()) != null) {
                if (!row.contains(date)) continue;
                
                data = new Data(row);
                
                if (sunrise == -1) { sunrise = data.getSunrise(); sunset = data.getSunset(); }
                time = data.getTime();
                
                if (time < sunrise) continue; // INSTRUCTIONS UNCLEAR, BETWEEN IS INCLUSIVE IN MY IMPLEMENTATION
                else if (time > sunset) break;
                else {
                    if (!data.hasTemp()) continue;

                    temp = data.getTemp();
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
        String row;
        Data data;

        boolean reached = false;
        int temp;
        date = convert(date);
    
        try (BufferedReader reader = new BufferedReader(new FileReader(path));) {
            while ((row = reader.readLine()) != null) {
                if (!row.contains(date)) {
                    if (!reached) continue;
                    else break;
                }

                reached = true;
                data = new Data(row);

                if (!data.hasTemp() || !data.hasSpeed()) continue;

                temp = data.getTemp();
                if (temp <= 40) System.out.println(windchill(temp, data.getSpeed()));
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    /**
     * Method 3
     * <p>
     * Finds and prints the most similar same day b/t two data sets
     * <p>
     * Utilizes unweighted euclidean distance of temperature, humidity, and wind speed
     * 
     * @param path1 path of first csv data set
     * @param path2 path of second csv data set
     */
    public void similarDay(String path1, String path2) {
        String row1 = null, row2 = null, match = null;
        Day day;
        int diff, similar = -1;

        // factor out
        Day[] days = new Day[2];
        String[] rows = {row1, row2}, dates = new String[2];
        Data[] datas = new Data[2];

        try (
            BufferedReader reader1 = new BufferedReader(new FileReader(path1));
            BufferedReader reader2 = new BufferedReader(new FileReader(path2));
        ) {
            reader1.readLine(); reader2.readLine(); // remove header
            BufferedReader[] readers = {reader1, reader2};

            while ((row1 = reader1.readLine()) != null && (row2 = reader2.readLine()) != null) {
                // 1 find the same date
                do {
                    rows[0] = row1; rows[1] = row2;

                    for (int i = 0; i < 2; i++) {
                        datas[i] = new Data(rows[i]);
                        dates[i] = datas[i].getDate();
                    }
                    diff = compareDates(dates[0], dates[1]);

                    if (diff > 0) row2 = reader2.readLine();
                    else if (diff < 0) row1 = reader1.readLine();
                } while (diff != 0);

                // 2 load each day's entirety
                for (int i = 0; i < 2; i++) {
                    days[i] = new Day(dates[i]);
                    day = days[i];
                    
                    do {
                        datas[i] = new Data(rows[i]);
                        day.addAll(datas[i]);
                    } while ((rows[i] = readers[i].readLine()) != null && rows[i].contains(dates[i]));
                }
                
                // 3 check if more similar
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

    public static class Data {
        String[] data;

        public Data(String row) {
            this.data = row.split(",");
        }

        public int getTime() {
            return Integer.parseInt(data[5].split(" ")[1].replaceAll(":", ""));
        }

        public String getDate() {
            return data[5].split(" ")[0];
        }

        public boolean hasTemp() {
            return !data[10].isEmpty();
        }

        public int getTemp() {
            return Integer.parseInt(data[10].replaceAll("s", "")); 
        }

        public boolean hasHumidity() {
            return !data[16].isEmpty();
        }

        public int getHumidity() {
            return Integer.parseInt(data[16]);
        }

        public boolean hasSpeed() {
            return !data[17].isEmpty();
        }

        public int getSpeed() {
            return Integer.parseInt(data[17].replaceAll("s", ""));
        }

        public int getSunrise() {
            return Integer.parseInt(data[35]);
        }

        public int getSunset() {
            return Integer.parseInt(data[36]);
        }
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

        public void addAll(Data data) {
            addTemp(data);
            addHumidity(data);
            addSpeed(data);
        }

        public void addTemp(Data data) {
            if (!data.hasTemp()) return;
            this.temp += data.getTemp();
            tempCount++;
        }

        public void addHumidity(Data data) {
            if (!data.hasHumidity()) return;
            this.humidity += data.getHumidity();
            humidityCount++;
        }

        public void addSpeed(Data data) {
            if (!data.hasSpeed()) return;
            this.speed += data.getSpeed();
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
