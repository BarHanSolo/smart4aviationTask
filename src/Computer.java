import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import org.json.*;


public class Computer {
    public static void runSystem() throws FileNotFoundException, IOException {

        Properties appProps = new Properties();
        String propertiesPath = Paths.get("").toAbsolutePath().toString()+"\\app.properties";
        appProps.load(new FileInputStream(propertiesPath));
        String weightUnit = appProps.getProperty("weightUnit");

        Scanner scanner = new Scanner(System.in);

        System.out.println("Choose function:");
        System.out.println("Type '1' to get info about flight of requested number");
        System.out.println("Type '2' to get info about airtport");
        System.out.println("Type '3' to get options");
        System.out.println("Type '9' to exit");
        int usersChoice = scanner.nextInt();

        String loadedCargos = (String) loadData("./data/cargoEntity.json");
        JSONArray cargosArray = new JSONArray(loadedCargos);
        String loadedFlights = (String) loadData("./data/flightEntity.json");
        JSONArray flightsArray = new JSONArray(loadedFlights);

        switch(usersChoice) {
            case 1:
                System.out.println("Select flight number:");
                int flightNum = scanner.nextInt();
                System.out.println("Select date (YYYY-MM-DD):");
                String departureDate = scanner.next();
                int flightId = getFlightIdFromFlightNum(flightNum, flightsArray);
                String flightDate = getFlightDateFromFlightNum(flightNum, flightsArray);
                if (flightId<0 || !flightDate.equals(departureDate)){
                    System.out.println("Not a valid flight number or date");
                    break;
                }
                int cargoWeight = getWeightFromFlightId(flightId, cargosArray, "cargo", weightUnit);
                int baggageWeight = getWeightFromFlightId(flightId, cargosArray, "baggage", weightUnit);
                System.out.println("Cargo Weight: "+ cargoWeight +" "+ weightUnit);
                System.out.println("Baggage Weight: "+ baggageWeight +" "+ weightUnit);
                System.out.println("Total Weight: "+ (cargoWeight+baggageWeight) +" "+ weightUnit);
                
            break;
            case 2:
                System.out.println("Select IATA Airport Code number:");
                String airtportCode = scanner.next().toUpperCase();
                List<Integer> flightsDepartingIds = countFlights(airtportCode, "departureAirportIATACode", flightsArray);
                List<Integer> flightsArrivingIds = countFlights(airtportCode, "arrivalAirportIATACode", flightsArray);
                System.out.println("Departures: "+ flightsDepartingIds.size());
                System.out.println("Arrivals: "+ flightsArrivingIds.size());

                //int cargoDeparting = countCargo(flightsDepartingIds, "cargo", cargosArray);
                int baggageDeparting = countCargo(flightsDepartingIds, "baggage", cargosArray);
                //int cargoArriving = countCargo(flightsArrivingIds, "cargo", cargosArray);
                int baggageArriving = countCargo(flightsArrivingIds, "baggage", cargosArray);
                System.out.println("Baggage departing: "+ baggageDeparting);
                System.out.println("Baggage arriving: "+ baggageArriving);

              break;
            case 3:
                System.out.println("To display weight in kg type '1'");
                System.out.println("To display weight in lb type '2'");
                int optionNum = scanner.nextInt();
                if (optionNum==1){
                    appProps.setProperty("weightUnit", "kg");
                } else if (optionNum==2){
                    appProps.setProperty("weightUnit", "lb");
                } else {
                    System.out.println("Enter valid number:");
                    break;
                }
                appProps.store(new FileWriter(propertiesPath), "changed");
                System.out.println("Weight unit set");
                break;
            case 9:
            System.exit(0);
              break;
            default:
                System.out.println("Enter valid number:");
        }

        runSystem();
        scanner.close();
    }

    private static int countCargo(List<Integer> flightsArrivingIds, String type, JSONArray cargosArray) {
        int count = 0;
        for (int i = 0; i < cargosArray.length(); i++)
            {
                if (flightsArrivingIds.contains(cargosArray.getJSONObject(i).getInt("flightId"))){
                    JSONArray flightArray = cargosArray.getJSONObject(i).getJSONArray(type);
                    for (int j = 0; j < flightArray.length(); j++){
                        count += flightArray.getJSONObject(j).getInt("pieces");
                    }
                }
            }
        return count;
    }

    private static List<Integer> countFlights(String airtportCode, String arrivalOrDeparture, JSONArray flightsArray) {
        List<Integer> flightIds = new ArrayList<Integer>();
        for (int i = 0; i < flightsArray.length(); i++)
            {
                if (flightsArray.getJSONObject(i).getString(arrivalOrDeparture).equals(airtportCode)){
                    flightIds.add(flightsArray.getJSONObject(i).getInt("flightId"));
                }
            }
        return flightIds;
    }

    private static String getFlightDateFromFlightNum(int flightNum, JSONArray flightsArray) {
        for (int i = 0; i < flightsArray.length(); i++)
            {
                if (flightsArray.getJSONObject(i).getInt("flightNumber")==flightNum){
                    return flightsArray.getJSONObject(i).getString("departureDate").substring(0,10);
                }
            }
        return "";
    }

    private static int getFlightIdFromFlightNum(int flightNum, JSONArray flightsArray) {
        for (int i = 0; i < flightsArray.length(); i++)
            {
                if (flightsArray.getJSONObject(i).getInt("flightNumber")==flightNum){
                    return flightsArray.getJSONObject(i).getInt("flightId");
                }
            }
        return -1;
    }

    private static int getWeightFromFlightId(int flightId, JSONArray cargosArray, String type, String weightUnit) { ///type: "baggage" or "cargo"
        int weight = 0;
        for (int i = 0; i < cargosArray.length(); i++)
            {
                if (cargosArray.getJSONObject(i).getInt("flightId")==flightId){
                    JSONArray flightArray = cargosArray.getJSONObject(i).getJSONArray(type);
                    for (int j = 0; j < flightArray.length(); j++){
                        if (flightArray.getJSONObject(j).getString("weightUnit").equals(weightUnit)){
                            weight += flightArray.getJSONObject(j).getInt("weight");
                        } else if (flightArray.getJSONObject(j).getString("weightUnit").equals("kg")){
                            weight += flightArray.getJSONObject(j).getInt("weight")*2.20462262185;
                        } else {
                            weight += flightArray.getJSONObject(j).getInt("weight")/2.20462262185;
                        }
                    }
                }
            }
        return weight;
    }

    private static Object loadData(String path) {
        String allData = "";
        try {
                File myObj = new File(path);
                Scanner myReader = new Scanner(myObj);
                while (myReader.hasNextLine()) {
                allData += myReader.nextLine().toString();
            }
            myReader.close();
            return allData;
            }
        catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        return null;
    }
}