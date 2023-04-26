import java.io.BufferedReader;
import java.io.FileReader;

public class Solution {
    /** TODO: Implement
     * Method 1
     * <p>
     * Calculates and prints average and sample standard deviation of dry-bulb temperatures (F) from sunrise to sunset of specified date
     * 
     * @param path path of csv data set
     * @param date date to process
     */
    public void daylightTemp(String path, String date) {
        date = convert(date); // convert
        String line;
        String[] parsed;
        int sunrise = -1;
        int sunset = -1;
    
        try (BufferedReader reader = new BufferedReader(new FileReader(path));) {
            while ((line = reader.readLine()) != null) {
                parsed = line.split(",");
                
                if (!parsed[5].contains(date)) continue;
                if (sunrise == -1) { sunrise = Integer.parseInt(parsed[35]); sunset = Integer.parseInt(parsed[36]); }
                
                System.out.println(sunrise + " " + sunset);
                return;
            }    
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
     * @param date date to convert
     * @return converted date
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
