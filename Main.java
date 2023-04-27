public class Main {
    public static void main(String[] args) {
        Solution sol = new Solution();

        switch (args[0]) {
            case "daylight_temp" -> sol.daylightTemp(args[1], args[2]);
            case "windchills" -> sol.windchills(args[1], args[2]);
            case "similar-day" -> sol.similarDay(args[1], args[2]);
        }

        // sol.daylightTemp("./1089419.csv", "2017-01-01");
        // sol.windchills("./1089419.csv", "2017-01-06");
        // sol.similarDay("./1089419.csv", "./1089441.csv");
    }
}