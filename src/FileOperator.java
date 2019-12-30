import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;

import static java.io.File.separatorChar;

public class FileOperator {

    public static void checkClientDuplicate(FreshClient fc) throws FileNotFoundException, UserAlreadyExistsException {
        FreshUserData dataObj = getClientDetailsFromFile();
        if (dataObj == null) {
            dataObj = new FreshUserData();
        }
        if (dataObj.getFreshClients().isEmpty()) {
            dataObj.setFreshClients(new ArrayList<FreshClient>());
        }

        if (dataObj.contains(fc)) {
            throw new UserAlreadyExistsException("User with the same name already exists");
        }
    }

    public static void writeNewClientToFile(FreshClient fc) throws UserAlreadyExistsException, IOException {

        FreshUserData dataObj = getClientDetailsFromFile();
        if (dataObj == null) {
            dataObj = new FreshUserData();
        }
        if (dataObj.getFreshClients().isEmpty()) {
            dataObj.setFreshClients(new ArrayList<FreshClient>());
        }

        if (dataObj.contains(fc)) {
            throw new UserAlreadyExistsException("User with the same name already exists");
        } else {
            dataObj.getFreshClients().add(fc);
        }

        Gson gson = new Gson();
        String json = gson.toJson(dataObj);

        File clientDataFile = getClientDataFile();
        if (clientDataFile == null) {
            throw new FileNotFoundException();
        }
        FileWriter writer = new FileWriter(clientDataFile);
        writer.write(json);
        writer.close();
        System.out.println(json);
    }

    public static FreshClient getClientFromFile(String userName) {
        FreshUserData dataObj = null;
        try {
            dataObj = getClientDetailsFromFile();
            if (dataObj == null) {
                System.out.println("No client match found");
                return null;
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error occured while reading client data file");
            return null;
        }
        FreshClient client = dataObj.getClient(userName);
        return client;
    }

    private static FreshUserData getClientDetailsFromFile() throws FileNotFoundException {
        //If directory and file of client data does not exist create it.
        File file = getClientDataFile();

        //Read file data
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);

        //parse json file data
        Gson gson = new Gson();
        FreshUserData dataObj = gson.fromJson(br, FreshUserData.class);
        return dataObj;
    }

    private static File getClientDataFile() {
        String clientDataPath = File.listRoots()[0].getAbsolutePath() + "FreshClientData";
        File dir = new File(clientDataPath);
        dir.mkdirs();
        File file = null;
        try {
            file = new File(clientDataPath + separatorChar + "clients.json");
            file.createNewFile();
        } catch (IOException e) {
            System.out.println("Error while creating client data file. ");
            return null;
        }
        return file;
    }


}

