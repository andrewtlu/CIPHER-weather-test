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
        int sunrise = -1, sunset = -1, time;
        double avg = 0, temp;
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
                    temp = Double.parseDouble(data[10].replaceAll("s", "")); // remove suspect marker
                    avg += temp;
                    temps.add(temp);
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

    /** TODO: Implement
     * Method 2
     * <p>
     * Prints all windchills of date when temperature is < 40 deg F
     * 
     * @param path path of csv data set
     * @param date date to process
     */
    public void windchills(String path, String date) {

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
}
