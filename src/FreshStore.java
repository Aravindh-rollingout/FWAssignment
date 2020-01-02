
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class FreshStore {
    final static Scanner scan = new Scanner(System.in);

    public static void main(String args[]) {
        System.out.println("Welcome");
        FreshClient freshClient = null;
        do {
            //Login page returns either new user or a logged in existing user
            freshClient = getClientFromLoginPage();
            if (freshClient == null) {
                return;
            } else {
                storeOperations(freshClient);
            }
        } while (freshClient != null);
    }

    private static FreshClient getClientFromLoginPage() {
        FreshClient client = null;
        String message = "--LOGIN PAGE--" +
                "\nPlease enter your input as a number or press 3 to exit.\n" +
                "1.Are you an existing user?\n" +
                "2.Are you a new user?\n" +
                "3.Exit\n";
        do {
            int userChoice = getIntegerInput(message, 1, 3);
            int loginChoice = 0;
            switch (userChoice) {
                case 1:
                    System.out.println("Sign In !!!\n" +
                            "Enter username to login\n");
                    String userName = scan.next();
                    client = getUserDetails(userName);
                    break;
                case 2:
                    client = getUserDetails();
                    break;
                case 3:
                    System.out.println("Thank you\n");
                    return null;
            }
        } while (client == null);
        return client;
    }

    static void storeOperations(FreshClient freshClient) {
        //After user has logged in. CRD operations can be performed
        String message = "Welcome " + freshClient.getUserName() +
                " \nPlease enter your input as a number\n" +
                "1.Store new Key in data set\n" +
                "2.Read value of existing key in Data Store\n" +
                "3.Remove a key from the data store\n" +
                "4.Logout\n";
        ClientStore clientStore = null;
        int userChoice = 0;
        do {
            try {
                //load clientStore of the logged in client
                clientStore = new ClientStore(freshClient);
            } catch (IOException e) {
                System.out.println("Error occured while fetching data for client store from file");
                return;
            }
            userChoice = getIntegerInput(message, 1, 4);

            //All operations require a key so key is retrieved beforehand.
            String key = null;
            if (userChoice == 1 || userChoice == 2 || userChoice == 3) {
                key = FreshStoreUtil.getKey(userChoice);
            }

            //CRD operations for a key
            try {
                switch (userChoice) {
                    case 1:
                        clientStore.storeKey(key);
                        break;
                    case 2:
                        //pretty Printing json Object
                        System.out.println(clientStore.read(key)[0]);
                        break;
                    case 3:
                        clientStore.remove(key);
                        break;
                    case 4:
                        return;
                }
            } catch (IOException | KeyNotFoundException e) {
                System.out.println(e.getMessage());
            }
        } while (userChoice != 4);
    }


    private static FreshClient getUserDetails(String userName) {
        return FileOperator.getClientFromFile(userName);
    }

    private static FreshClient getUserDetails() {
        FreshClient client;
        String newUserName = getValidatedUsername();
        client = FreshClient.createFreshClient(newUserName);
        try {
            FileOperator.checkClientDuplicate(client);
        } catch (IOException e) {
            System.out.println("Exception occured while fetching client data file\n");
            return null;
        } catch (UserAlreadyExistsException e) {
            System.out.println("A user with this name already exists. Redirecting to login page\n");
            return null;
        }
        do {
            try {
                client.updateClientFilePath();
            } catch (IOException e) {
                System.out.println("Error creating filepath. Please try again\n");
            }
        } while (client.getFilepath() == null);

        try {
            FileOperator.writeNewClientToFile(client);
        } catch (IOException e) {
            System.out.println("Exception occured while initialising client\n");
            return null;
        } catch (UserAlreadyExistsException e) {
            System.out.println("A user with this name already exists.\n");
            return null;
        }
        return client;
    }

    private static String getValidatedUsername() {
        //Username is validated with regex to ensure alphanumeric values for username.
        String newUserName;
        System.out.println("Welcome new user.\n" +
                "To proceed with the signup, \n" +
                "Kindly enter a username with only letters and digits\n");

        while (!scan.hasNext("[A-Za-z0-9]+")) {
            System.out.println("Please enter a valid username\n");
            scan.next();
        }
        newUserName = scan.next();
        return newUserName;
    }

    static int getIntegerInput(String message, int min, int max) {
        int userChoice = 0;
        do {
            System.out.println(message);
            while (!scan.hasNextInt()) {
                System.out.println("Kindly enter a number between " + min + " & " + max);
                scan.next();
            }
            userChoice = scan.nextInt();
            if (!(userChoice >= min && userChoice <= max)) {
                System.out.println("Invalid choice\n");
            }
        } while (!(userChoice >= min && userChoice <= max));
        return userChoice;
    }
}


