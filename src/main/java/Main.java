import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

public class Main {
    private static BufferedWriter myOutput;
    private static final String PLATE_REGEX = "[a-zA-Z]{3}-[0-9]{4}";


    public static void main(String[] args) throws Exception {

        String selectedFunctionality, selectedExportType;

        DButils db = new DButils();
        db.connect();

        while (true) {
            printMainMenu();
            selectedFunctionality = selectFunctionality();

            if (selectedFunctionality.equals("0")) System.exit(0);

            printExportTypeMenu();
            selectedExportType = selectExportType();

            if (selectedExportType.equals("1")) {
                //output=file
                myOutput = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("output.txt")));
            } else {
                //output=console
                myOutput = new BufferedWriter(new OutputStreamWriter(System.out));
            }

            ArrayList<Vehicle> vList = db.getDBVehicles();
            switch (selectedFunctionality) {
                case "1":
                    //status
                    String usersPlate = readPlate();
                    boolean found = false;
                    for (Vehicle v : vList) {
                        if (v.getPlate().equals(usersPlate)) {
                            printData("Vehicle with plate " + v.getPlate().toUpperCase() + ", has insurance expiration date " + v.getIns_exp_date() + ".");
                            found = true;
                            break;
                        }
                    }
                    if (!found) System.out.println("Plate not found.");
                    break;
                case "2"://forecoming time frame
                    int timeFrame = readTimeFrame();

                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar c = Calendar.getInstance();
                    String now = dateFormat.format(c.getTime());
                    c.setTime(dateFormat.parse(now));
                    c.add(Calendar.DAY_OF_MONTH, timeFrame);
                    String nowPlusTimeFrame = dateFormat.format(c.getTime());
                    printData("Forecoming Expiries\n-------------------\n");
                    for (Vehicle v : vList) {
                        String exp = v.getIns_exp_date();
                        if (exp.compareTo(now) > 0 && exp.compareTo(nowPlusTimeFrame) < 0) {
                            printData("The vehicle with plate: " + v.getPlate().toUpperCase() + " will expire at " + exp + ".\n");
                        }
                    }
                    break;
                case "3":
                    //expiries
                    vList.sort(new VehicleComparator());
                    printData("Vehicles ordered by plate\n-------------------------\n");
                    for(Vehicle v : vList){
                        printData(v.toString());
                    }
                    break;
                case "4":
                    //calc fine

                    break;
            }

            //ask to continue!!
            Scanner input = new Scanner(System.in);
            System.out.println("\n\nPress any key to continue...");
            input.nextLine();
        }
    }

    private static int readTimeFrame() {
        Scanner input = new Scanner(System.in);
        int timeFrame;
        String tf = "";

        System.out.print("Give a time frame (days): ");
        do {
            tf = input.nextLine();
            if (tf.matches("[0-9]*")) {
                timeFrame = Integer.parseInt(tf);
                return timeFrame;
            }
            System.out.print("Give a time frame (ex: 30): ");
        } while (true);
    }

    private static String readPlate() {
        Scanner input = new Scanner(System.in);
        String usersPlate = "";

        System.out.print("Give a plate number: ");
        do {
            usersPlate = input.nextLine();
            if (usersPlate.matches(PLATE_REGEX)) {
                usersPlate = usersPlate.toLowerCase();
                return usersPlate;
            }
            System.out.print("Give a plate number (ex: NHO-2233): ");
        } while (true);
    }

    /**
     * The {@code printData} method prints the parameter {@code stringToPrint}
     * to a file named "output.txt" or to users console window.
     * <p>
     * The destination output depends from the users choice on the {@code Main.printExportTypeMenu()} method.
     *
     * @param  stringToPrint     String to be written
     *
     * @exception  IOException  If an I/O error occurs
     */
    public static void printData(String stringToPrint) {
        try {
            myOutput.write(stringToPrint);
            myOutput.flush();
            //if(selectedExportType.equals("1")) System.out.println("File ready!");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void printMainMenu() {
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        System.out.println("------------------------------------");
        System.out.println("--- Select Functionality to perform:");
        System.out.println("------------------------------------");
        System.out.println("1 - Vehicle Insurance status");                 //F1
        System.out.println("2 - Forecoming Expires (given a time-frame)");  //F2
        System.out.println("3 - Vehicles ordered by plate");                        //F3
        System.out.println("4 - Calculate fine cost per owner");            //F4
        System.out.println("------------------------------------");
        System.out.println("0 - Exit");
        System.out.println("------------------------------------");
    }

    private static void printExportTypeMenu() {
        System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
        System.out.println("----------------------");
        System.out.println("--- Enter export type:");
        System.out.println("----------------------");
        System.out.println("1 - File");
        System.out.println("2 - Console");
        System.out.println("----------------------");
    }

    private static String selectFunctionality() throws SQLException {
        Scanner input = new Scanner(System.in);
        String selection = "";

        System.out.print("Make a selection: ");
        do {
            selection = input.nextLine();
            if (selection.matches("[0-4]")) return selection;
            System.out.print("Make a selection (Options: 0, 1, 2, 3 or 4): ");
        } while (true);
    }

    private static String selectExportType() {
        Scanner input = new Scanner(System.in);
        String selection = "";

        System.out.print("Make a selection: ");
        do {
            selection = input.nextLine();
            if (selection.matches("[1-2]")) return selection;
            System.out.print("Make a selection (1 or 2): ");
        } while (true);
    }
}
