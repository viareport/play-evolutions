package play.modules.evolutions.multitenant;

import models.accesscontrol.User;
import models.multitenant.Client;
import multitenant.context.MultiTenantContext;

public class StaticContext extends MultiTenantContext {

    private static Client client;
    private static User user;

    @Override
    public Client getClient() {
        if (client.id == null) {
            client = Client.findByCode(client.code);
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

    public void setClientCode(String clientCode) {
        client = new Client();
        client.code = clientCode;
    }

    public void setUserLogin(String userLogin) {
        user = new User();
        user.login = userLogin;
    }
}
