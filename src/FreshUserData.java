import java.util.ArrayList;
import java.util.List;

public class FreshUserData {
    List<FreshClient> freshClients;

    public FreshUserData() {
        this.freshClients = new ArrayList<FreshClient>();
    }

    public FreshUserData(List<FreshClient> freshClients) {
        this.freshClients = freshClients;
    }

    public List<FreshClient> getFreshClients() {
        return freshClients;
    }

    public void setFreshClients(List<FreshClient> freshClients) {
        this.freshClients = freshClients;
    }

    public boolean contains(FreshClient newClient) {
        String newUserName = newClient.getUserName();
        for (FreshClient client : this.freshClients) {
            if (client.getUserName().equalsIgnoreCase(newUserName)) {
                return true;
            }
        }
        return false;
    }

    public FreshClient getClient(String newUserName) {
        for (FreshClient client : this.freshClients) {
            if (client.getUserName().equalsIgnoreCase(newUserName)) {
                return client;
            }
        }
        return null;

    }
}
