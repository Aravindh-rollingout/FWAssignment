import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import junit.framework.TestCase;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.IOException;

import static java.io.File.separatorChar;

public class TestStoreOperations extends TestCase {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    File clientDataFile;
    File clientStoreFile;
    ClientStore clientStore;
    FreshClient client;

    @Override
    protected void setUp() throws Exception {
        String userName = "John";
        client = FreshClient.createFreshClient(userName);
        client.setFilepath(FreshClient.getDefaultFilePath());
        clientDataFile = FileOperator.getClientDataFile();
        clientStoreFile = new File(client.getFilepath() + separatorChar + client.getUserName() + ".json");
        clientDataFile.delete();
        try {
            FileOperator.writeNewClientToFile(client);
            client.createFreshFile(client.getFilepath());
        } catch (UserAlreadyExistsException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Checks if Default directory and file named <username.json> exist

        // System.out.println("deleted ?" + file.delete());
        clientDataFile = FileOperator.getClientDataFile();
        clientStoreFile = new File(client.getFilepath() + separatorChar + client.getUserName() + ".json");
        clientStore = new ClientStore(client);
    }

    @Override
    protected void tearDown() throws Exception {
        clientDataFile.delete();
    }

    //Testcase to check store and read functionality
    @Test
    public void testStoreKeywithFutureTtl() throws Exception {
        String key = "sampleKey";
        ClientStore.TTLValueObj ttlValueObj = new ClientStore.TTLValueObj(10000l + (System.currentTimeMillis() / 1000));
        JsonObject jsonObject = new JsonObject();
        String jsonString = "{\"name\":\"Aravindh\",\"age\":25,\"position\":[\"Founder\",\"CTO\",\"Developer\"],\"skills\":[\"java\",\"python\",\"Django\",\"kotlin\"],\"salary\":{\"2018\":140000,\"2012\":120000,\"2010\":100000}}";
        JsonElement jsonElement = new JsonParser().parse(jsonString);
        jsonObject = jsonElement.getAsJsonObject();
        ttlValueObj.setValue(jsonObject);
        clientStore.dataMap.put(key, ttlValueObj);
        FileOperator.storeJsonInFile(clientStore.file, clientStore.dataMap);
        ClientStore readFileClient = new ClientStore(client);
        try {
            JsonElement jsonElementRead = new JsonParser().parse(readFileClient.read(key)[1]);
            jsonObject = jsonElementRead.getAsJsonObject();
            boolean isNameFound = jsonObject.get("name").getAsString().equals("Aravindh");
            assertTrue(isNameFound);
        } catch (KeyNotFoundException e) {
            assertFalse(true);
        }
    }

    //Testcase to check for KeyNotFOund Exception with expired ttl Key
    @Test
    public void testStoreKeyWithExpiringTtl() throws KeyNotFoundException {
        String key = "sampleKey";
        ClientStore.TTLValueObj ttlValueObj = new ClientStore.TTLValueObj(0l + (System.currentTimeMillis() / 1000));
        JsonObject jsonObject = new JsonObject();
        String jsonString = "{\"name\":\"Aravindh\",\"age\":25,\"position\":[\"Founder\",\"CTO\",\"Developer\"],\"skills\":[\"java\",\"python\",\"Django\",\"kotlin\"],\"salary\":{\"2018\":140000,\"2012\":120000,\"2010\":100000}}";
        JsonElement jsonElement = new JsonParser().parse(jsonString);
        jsonObject = jsonElement.getAsJsonObject();
        ttlValueObj.setValue(jsonObject);
        ClientStore readFileClient = null;
        clientStore.dataMap.put(key, ttlValueObj);
        try {
            FileOperator.storeJsonInFile(clientStore.file, clientStore.dataMap);
            readFileClient = new ClientStore(client);
        } catch (IOException e) {
            e.printStackTrace();
        }
        readFileClient.read(key);
    }
}
