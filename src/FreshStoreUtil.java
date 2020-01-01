public class FreshStoreUtil {
    public static String getKey(int userChoice) {
        String key = null;
        do {
            System.out.println("Enter key to be " + getDisplayName(userChoice));
            key = FreshStore.scan.next();
            if (key.length() > 32) {
                System.out.println("Kindly enter a key of size 32 characters or less\n");
            }
        } while (key.length() > 32);
        return key;
    }

    private static String getDisplayName(int userChoice) {
        switch (userChoice) {
            case 1:
                return "stored";

            case 2:
                return "read";
            case 3:
                return "deleted";
            default:
                return null;
        }
    }

}
