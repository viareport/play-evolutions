package play.modules.evolutions.multitenant;

import play.Logger;
import models.Client;
import models.User;
import multitenant.context.MultiTenantContext;

public class StaticContext extends MultiTenantContext {

    private static Client client;
    private static User user;
    
    @Override
    public Client getClient() {
        if (client.id == null) {
            client = Client.findByName(client.name).first();
        }
        return client;
    }

    @Override
    public User getUser() {
        if (user.id == null) {
            user = User.find("byLogin", user.login).first();
        }
        return user;
    }
    
    public static void reset() {
        current.set(null);
    }

    public void setClientName(String clientName) {
        client = new Client();
        client.name = clientName;
    }

    public void setUserLogin(String userLogin) {
        user = new User();
        user.login = userLogin;
    }
}
