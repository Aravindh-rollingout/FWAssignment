import java.io.File;
import java.io.IOException;

import static java.io.File.*;

public class FreshClient {

    private final String userName;
    private String filepath;

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public void updateClientFilePath() throws IOException {
        String s = ("Do you wish to enter a custom file path for your data store?\n" +
                "1.Yes\n" +
                "2.No");
        int choice = FreshStore.getIntegerInput(s, 1, 2);
        File dir = null;
        String filepath = null;
        String path = null;
        if (choice == 1) {
            System.out.println("Enter file path only. Do not enter filename. The fileaname will be your username.");
            do {
                System.out.println("Enter file path to proceed");
                path = FreshStore.scan.next();
                dir = new File(path);
                if (!dir.exists()) {
                    if (dir.mkdirs()) {
                        {
                            System.out.println("Directory is created!");
                            createFreshFile(path);
                        }
                    } else {
                        System.out.println("Failed to create directory!");
                        System.out.println("Enter valid file path");
                    }
                } else {
                    createFreshFile(path);
                }
            } while (!dir.exists());
        }
        if (choice == 2) {
            path = getDefaultFilePath();
            dir = new File(path);
            dir.mkdirs();
            System.out.println("Initializing data store in default file path---" + path);
            createFreshFile(path);

        }
        filepath = dir.getAbsolutePath();
        this.filepath = filepath;
    }

    public String getFilepath() {
        return filepath;
    }

    public static String getDefaultFilePath() {
        return File.listRoots()[0].getAbsolutePath() + "FreshStoreDefault";
    }

    public void createFreshFile(String path) throws IOException {
        String out = null;
        while (path.endsWith("/") || path.endsWith(separator) || path.endsWith("\\")) {
            path = path.substring(0, path.length() - 1);
        }
        File dir = new File(path);
        dir.mkdirs();
        out = path + separator + this.userName + ".json";
        System.out.println(out);
        File file = new File(out);
        file.createNewFile();
        System.out.println("Path where file has been created : " + file.getAbsolutePath() + "\n");
    }

    public String getUserName() {
        return userName;
    }

    FreshClient(String userName) {
        this.userName = userName;
        this.filepath = null;
    }

    public static FreshClient createFreshClient(String newUserName) {
        FreshClient fc = new FreshClient(newUserName);
        return fc;
    }
}
