package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        String result = "{}"; // default return value; replace later!
        
        try {
        
            // INSERT YOUR CODE HERE
            // Parsing the CSV String
            CSVReader csvReader = new CSVReader(new StringReader(csvString));
            
            // Read all rows from CSV
            List<String[]> csvData = csvReader.readAll();
            csvReader.close();
            
            // Json objects and arrays to store the converted data
            JsonObject jsonObject = new JsonObject(); // Stores final structure
            JsonArray prodNums = new JsonArray(); // Stores the prodNums values
            JsonArray colHeadings = new JsonArray(); // Stores column headings
            JsonArray data = new JsonArray(); // Stores row data
            
            // Checks to see if csvData is empty, if it is it returns the default empty result
            if (csvData.isEmpty()){
                return result;
            }
            
            
            
            // Extracting the column names
            String[] headers = csvData.get(0);
            for (int i = 0; i < headers.length; i++) {
                colHeadings.add(headers[i]);
            }
            // Getting row data
            for (int i = 1; i < csvData.size(); i++) {
                String[] row = csvData.get(i);
                
                // prodNums goes into an array
                prodNums.add(row[0]);
                
                JsonArray rowData = new JsonArray();
                for (int j = 1; j < row.length; j++) {
                    
                    // Converts season and episode numbers to integers
                    if (j == 2 || j == 3){
                        try {
                            rowData.add(Integer.parseInt(row[j]));
                        }
                        catch (NumberFormatException e){
                            rowData.add(row[j]);
                        }
                    }else {
                                rowData.add(row[j]);
                                }
                    }
                data.add(rowData);
            }
            // JSON structure
            jsonObject.put("ProdNums", prodNums);
            jsonObject.put("ColHeadings", colHeadings);
            jsonObject.put("Data", data);
            
            // JSON structure to string
            result = jsonObject.toJson();
                      
          
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        
        String result = ""; // default return value; replace later!
        
        try {
            
            // INSERT YOUR CODE HERE
            // Deserialize Json string to object
            JsonObject jsonObject = (JsonObject) Jsoner.deserialize(jsonString);
            
            // Extract JSON arrays
            JsonArray prodNums = (JsonArray) jsonObject.get("ProdNums");
            JsonArray colHeadings = (JsonArray) jsonObject.get("ColHeadings");
            JsonArray data = (JsonArray) jsonObject.get("Data");
            
            // validates that the fields aren't empty
            if (prodNums == null || colHeadings == null || data == null) {
                throw new IllegalArgumentException("No null values for fields allowed");
            }
            
            // Initialize list to store CSV rows
            List<String[]> csvRows = new ArrayList<>();
            
            // Add column headers as the first row
            String[] headerRow = new String[colHeadings.size()];
            for (int i = 0; i < colHeadings.size(); i++) {
            headerRow[i] = colHeadings.get(i).toString();
        }
            csvRows.add(headerRow);
            //  Loop through data to get episode details
            for (int i = 0;  i < data.size(); i++) {
            JsonArray rowData = (JsonArray) data.get(i);
            String[] row = new String[rowData.size() + 1];
            
            // Adds prodNums to the beginning of the row
            row[0] = (prodNums.get(i) != null) ? prodNums.get(i).toString() : "";
            
            for (int j = 0; j < rowData.size(); j++) {
                Object value = rowData.get(j);
                
                // Converting episode numbers to integers and formatting it while leaving other data as string
                if (j==2) {
                    int episodeNumber = ((Number) rowData.get(j)).intValue();
                    row[j + 1] = String.format("%02d", episodeNumber);
                    } else if (value instanceof Number) {
                        row[j + 1] = rowData.get(j).toString();
                } else {
                        row[j + 1] = value.toString();
                    }
            }
            csvRows.add(row);
        }
            
            StringWriter stringWriter = new StringWriter();
            CSVWriter csvWriter = new CSVWriter(stringWriter);
            
            
            csvWriter.writeAll(csvRows);
            csvWriter.close();
            
            result = stringWriter.toString().trim();
            
            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
}
    