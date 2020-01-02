
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static java.io.File.separatorChar;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestClient extends TestCase {
    @Test
    public void testAdd() {
        String str = "Junit is working fine";
        assertEquals("Junit is working fine", str);
    }

    @Override
    protected void setUp() throws Exception {
        File clientDataFile = FileOperator.getClientDataFile();
        clientDataFile.delete();
    }

    @Override
    protected void tearDown() throws Exception {
        File clientDataFile = FileOperator.getClientDataFile();
        clientDataFile.delete();
    }

    //test case to check if FreshClientData/clients.json is created
    @Test
    public void testClientDataFileCreation() {
        String userName = "John";
        FreshClient client;
        client = FreshClient.createFreshClient(userName);
        try {
            FileOperator.writeNewClientToFile(client);
        } catch (UserAlreadyExistsException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File clientDataFile = FileOperator.getClientDataFile();
        boolean isFound = clientDataFile.exists();
        clientDataFile.delete();
        assertTrue(isFound);
    }

    //test case to check if FreshStoreDefault/<username>.json is created
    @Test
    public void testDefaultPathClientFileCreation() {
        String userName = "John";
        FreshClient client;
        client = FreshClient.createFreshClient(userName);
        client.setFilepath(FreshClient.getDefaultFilePath());
        try {
            FileOperator.writeNewClientToFile(client);
            client.createFreshFile(client.getFilepath());
        } catch (UserAlreadyExistsException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Checks if Default directory and file named <username.json> exist
        File file = new File(client.getFilepath() + separatorChar + client.getUserName() + ".json");
        boolean isFound = file.exists();
       // System.out.println("deleted ?" + file.delete());
        File clientDataFile = FileOperator.getClientDataFile();
        clientDataFile.delete();
        assertTrue(isFound);
    }


}