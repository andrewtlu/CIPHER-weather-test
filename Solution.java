import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
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
        int sunrise = -1, sunset = -1, time, temp;
        double avg = 0;
        List<Double> temps = new ArrayList<>();
        date = convert(date);
    
        try (BufferedReader reader = new BufferedReader(new FileReader(path));) {
            while ((line = reader.readLine()) != null) {
                data = line.split(",");
                if (!data[5].contains(date)) continue;
                
                // date found
                if (sunrise == -1) { sunrise = Integer.parseInt(data[35]); sunset = Integer.parseInt(data[36]); }
                time = Integer.parseInt(data[5].split(" ")[1].replaceAll(":", ""));
                
                if (time < sunrise) continue;
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
                    else break; // done
                }
                reached = true;

                if (data[10].isEmpty() || data[17].isEmpty()) continue;

                temp = temp(data[10]);
                if (temp <= 40) System.out.println(windchill(temp, Integer.parseInt(data[17])));
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    /** TODO: Implement
     * Method 3
     * <p>
     * Finds and prints the most similar same day b/t two data sets
     * 
     * @param path1 path of first csv data set
     * @param path2 path of second csv data set
     */
    public void similarDay(String path1, String path2) {

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
}
