CIPHER-weather-test

Version:
    Java 17 (openjdk 17.0.6 2023-01-17)


Language:
    The main reason I chose to use Java in my solution was because I am currently the most comfortable with Java syntax and design. However, I do realize that Python can be much easier to write, especially for data processing applications such as this one, and I also do realize that Rust tends to be much more efficient and faster than Java.


Implementation Logic/Documentation:
    When implementing these solutions, I created a static Data class to automatically split and retrieve desired fields from each row of the CSVs and make code clearer. I also created a static Location class that implements the Comparable interface for easier comparison in `similar-day`.
    
    Additionally, to faciltate date conversions, I developed a helper convert() method that would convert from MM/dd/yyyy to yyyy-MM-dd and vice versa. My code basically tokenizes the input date and uses string formatting to output the opposite format.
    
    For the `daylight_temp` problem, my code firstly locates the first row in which the target date appears. Then, it finds the sunrise and sunset time. For each row following that is still on the target date and within the sunrise and sunset time (inclusive), the hourly temperature is stored in an ArrayList and a sum is tallied. Finally, the average and standard deviation are calculated using said variables and printed.
    
    For the `windchills` problem, my code also firstly locates the first row in which the target date appears. Then, it checks if the row has a recorded temperature less than or equal to 40 and a speed; if so, it prints the output of a helper method that calculates the windchill given in LCD_documentation.pdf. 
    
    For the `similar-day` problem, my code sequentially goes through both CSVs and locates the first occurance of the same date. Then, for each date, my code loads all the temperature, humidity, and wind speed data from both locations into the aforementioned Location class. Finally, the two locations are compared using the unweighted Euclidian distance in the change in average temperature, average humidity, and average wind speed; if the distance is smaller than the current smallest distance, the date and distance are stored and the method repeats this process until the end of the CSVs are reached.
    
    I chose to use the average temperature, humidity, and wind speed factors because they seemed to be the most important factors in the short preliminary research I did before tackling this problem. The unweighted Euclidian distance of these factors was used because it seemed to be the most straightforward and balanced way to compare similarities. Factors were left unweighted as an arbitrary weight value assigned by me with no other meteorological research/data may not be accurate.
