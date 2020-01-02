import com.google.gson.*;
import com.google.gson.internal.LinkedTreeMap;

import java.io.*;
import java.util.HashMap;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.io.File.separatorChar;

public class ClientStore {
    HashMap dataMap;
    File file;
    ReadWriteLock lock = new ReentrantReadWriteLock();

    public ClientStore(FreshClient fc) throws IOException {
        file = new File(fc.getFilepath() + separatorChar + fc.getUserName() + ".json");
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        Gson gson = new Gson();
        dataMap = gson.fromJson(br, HashMap.class);
        if (dataMap == null) {
            dataMap = new HashMap<>();
        }
        br.close();
        fr.close();
    }

    public void storeKey(String key) throws IOException, KeyNotFoundException {
        lock.writeLock().lock();
        try {
            if (isKeyFound(key)) {
                throw new KeyNotFoundException("Key already exists.");
            }

            TTLValueObj ttlValueObj = null;
            String prompt = ("Do you want to enter a ttl value for this Key\n" +
                    "1.Yes.\n" +
                    "2.No\n");
            int choice = FreshStore.getIntegerInput(prompt, 1, 2);
            Long ttl = 0l;
            if (choice == 1) {

                System.out.println("Enter ttl value (in seconds)");
                Long inputTtl = FreshStore.scan.nextLong();
                ttl = inputTtl + (System.currentTimeMillis() / 1000);
            } else {
                ttl = -1l;
            }
            ttlValueObj = new TTLValueObj(ttl);
            JsonObject jsonObject = null;
            FreshStore.scan.nextLine();
            jsonObject = getJsonObjectFromUser();
            ttlValueObj.setValue(jsonObject);
            dataMap.put(key, ttlValueObj);
            FileOperator.storeJsonInFile(file, dataMap);
            System.out.println("Key has been stored");
        } finally {
            lock.writeLock().unlock();
        }
    }


    public String[] read(String key) throws KeyNotFoundException {
        lock.readLock().lock();
        String prettyJson = null;
        try {
            if (!isKeyFound(key)) {
                throw new KeyNotFoundException("Key not found. Try some other key name");
            }
            LinkedTreeMap valueObj = (LinkedTreeMap) dataMap.get(key);
            checkTtlValidity(valueObj);

            Gson gson = new Gson();
            String value = gson.toJson(valueObj.get("value"));
            prettyJson = prettyPrint(value);
            return new String[]{prettyJson,value};
        } finally {
            lock.readLock().unlock();
        }
    }

    public void remove(String key) throws IOException, KeyNotFoundException {
        lock.readLock().lock();
        try {
            if (!isKeyFound(key)) {
                throw new KeyNotFoundException("Key not found.");
            }
            LinkedTreeMap valueObj = (LinkedTreeMap) dataMap.get(key);
            checkTtlValidity(valueObj);
            dataMap.remove(key);
            FileWriter writer = new FileWriter(file);
            Gson gson = new Gson();
            String json = gson.toJson(dataMap);
            writer.write(json);
            writer.close();
            System.out.println("Key removed");
            return;

        } finally {
            lock.readLock().unlock();
        }
    }

    void checkTtlValidity(LinkedTreeMap valueObj) throws KeyNotFoundException {
        long ttl = (long) Double.parseDouble(valueObj.get("ttl").toString());
        if (ttl > 0 && ttl < (System.currentTimeMillis() / 1000)) {
            throw new KeyNotFoundException("Key not found. Key might have expired");
        }
    }

    boolean isKeyFound(String key) {
        LinkedTreeMap valueObj = (LinkedTreeMap) dataMap.get(key);
        if (valueObj == null || valueObj.isEmpty()) {
            return false;
        }
        return true;
    }

    private JsonObject getJsonObjectFromUser() throws IOException {
        JsonObject jsonObject = null;
        long valueLength = 0l;
        do {
            System.out.println("Enter valid json string");

            String jsonString = FreshStore.scan.nextLine();
            valueLength = jsonString.getBytes().length;
            if (valueLength > 16384l) {
                throw new IOException("Size of value exceeds 16KB limit");
            }
            try {
                JsonElement jsonElement = new JsonParser().parse(jsonString);
                jsonObject = jsonElement.getAsJsonObject();
            } catch (JsonSyntaxException | IllegalStateException e) {
                System.out.println("Invalid json string");
            }
        } while (jsonObject == null);
        return jsonObject;
    }

    private String prettyPrint(String value) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(value);
        String prettyJsonString = gson.toJson(je);
        return prettyJsonString;
    }

    //Inner class for incorporating ttl into every value
    static class TTLValueObj {
        public void setValue(JsonObject value) {
            this.value = value;
        }

        long ttl;
        JsonObject value;

        public TTLValueObj(Long ttl) {
            this.ttl = ttl;
            this.value = null;
        }
    }
}