import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import static java.io.File.separatorChar;

public class FileOperator {

    public static void checkClientDuplicate(FreshClient fc) throws IOException, UserAlreadyExistsException {
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
            throw new UserAlreadyExistsException("User with the same name already exists. Try again.\n");
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
        //System.out.println(json);
    }

    public static FreshClient getClientFromFile(String userName) {
        FreshUserData dataObj = null;
        try {
            dataObj = getClientDetailsFromFile();
            if (dataObj == null) {
                System.out.println("No client match found");
                return null;
            }
        } catch (IOException e) {
            System.out.println("Error occured while reading client data file");
            return null;
        }
        FreshClient client = dataObj.getClient(userName);
        if(client == null)
        {
            System.out.println("Client not found. Returning to main menu");
        }
        return client;
    }

    private static FreshUserData getClientDetailsFromFile() throws IOException {
        //If directory and file of client data does not exist create it.
        File file = getClientDataFile();

        //Read file data
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);

        //parse json file data
        Gson gson = new Gson();
        FreshUserData dataObj = gson.fromJson(br, FreshUserData.class);
        fr.close();
        br.close();
        return dataObj;
    }

    public static File getClientDataFile() {
        String clientDataPath = getClientdataFilePath();
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

    public static String getClientdataFilePath() {
        return File.listRoots()[0].getAbsolutePath() + "FreshClientData";
    }


    public static void storeJsonInFile(File file, HashMap dataMap) throws IOException {
        final long maxSizeOneGB = 1073741824L;
        FileWriter writer = new FileWriter(file);
        Gson gson = new Gson();
        String json = gson.toJson(dataMap);
        if ((json.getBytes().length * 2) > maxSizeOneGB) {
            throw new IOException("File size will exceed 1GB on adding this key-value pair");
        }
        writer.write(json);
        writer.close();
    }
}

