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

    public ClientStore(FreshClient fc) throws FileNotFoundException {
        file = new File(fc.getFilepath() + separatorChar + fc.getUserName() + ".json");
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        Gson gson = new Gson();
        dataMap = gson.fromJson(br, HashMap.class);
        if (dataMap == null) {
            dataMap = new HashMap<>();
        }
    }

    public void storeKey(String key) throws IOException {
        lock.writeLock().lock();
        final long maxSizeOneGB = 1073741824L;
        try {
            TTLValueObj ttlValueObj = null;
            String prompt = ("Do you want to enter a ttl value for this Key\n" +
                    "1.Yes.\n" +
                    "2.No\n");
            int choice = FreshStore.getIntegerInput(prompt, 1, 2);
            Long ttl = 0l;
            if (choice == 1) {
                System.out.println("Enter ttl value");
                Long inputTtl = FreshStore.scan.nextLong();
                ttl = inputTtl + (System.currentTimeMillis() / 1000);
            } else {
                ttl = -1l;
            }
            ttlValueObj = new TTLValueObj(ttl);
            JsonObject jsonObject = null;
            FreshStore.scan.nextLine();
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

            ttlValueObj.setValue(jsonObject);
            dataMap.put(key, ttlValueObj);
            if ((file.length() + valueLength + ttl.toString().getBytes().length) > maxSizeOneGB) {
                throw new IOException("File size will exceed 1GB on adding this key-value pair");
            }
            FileWriter writer = new FileWriter(file);
            Gson gson = new Gson();
            String json = gson.toJson(dataMap);
            writer.write(json);
            writer.close();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void read(String key) {
        lock.readLock().lock();
        try {
            LinkedTreeMap valueObj = (LinkedTreeMap) dataMap.get(key);
            if (valueObj == null || valueObj.isEmpty()) {
                System.out.println("Key not found");
                return;
            }
            long ttl = (long) Double.parseDouble(valueObj.get("ttl").toString());
            if (ttl > 0 && ttl < (System.currentTimeMillis() / 1000)) {
                System.out.println("Key not found");
                return;
            }

            Gson gson = new Gson();
            String value = gson.toJson(valueObj.get("value"));
            prettyPrint(value);
            return;
        } finally {
            lock.readLock().unlock();
        }
    }

    private void prettyPrint(String value) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(value);
        String prettyJsonString = gson.toJson(je);
        System.out.println(prettyJsonString);
    }

    public void remove(String key) throws IOException {
        lock.readLock().lock();
        try {
            LinkedTreeMap valueObj = (LinkedTreeMap) dataMap.get(key);
            if (valueObj == null || valueObj.isEmpty()) {
                System.out.println("Key not found");
                return;
            }
            long ttl = (long) Double.parseDouble(valueObj.get("ttl").toString());
            if (ttl > 0 && ttl < (System.currentTimeMillis() / 1000)) {
                System.out.println("Key not found");
                return;
            } else {
                LinkedTreeMap deletedObj = (LinkedTreeMap) dataMap.remove(key);
                System.out.println("Key Removed");
                FileWriter writer = new FileWriter(file);
                Gson gson = new Gson();
                String json = gson.toJson(dataMap);
                writer.write(json);
                writer.close();
                return;
            }
        } finally {
            lock.readLock().unlock();
        }


    }

    class TTLValueObj {
        public long getTtl() {
            return ttl;
        }

        public void setTtl(long ttl) {
            this.ttl = ttl;
        }

        public JsonObject getValue() {
            return value;
        }

        public void setValue(JsonObject value) {
            this.value = value;
        }

        public TTLValueObj(long ttl, JsonObject value) {
            this.ttl = ttl;
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