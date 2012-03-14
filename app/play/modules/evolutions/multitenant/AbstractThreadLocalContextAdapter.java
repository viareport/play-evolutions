package play.modules.evolutions.multitenant;

import multitenant.context.AbstractThreadLocalContext;
import multitenant.context.MultiTenantContext;

public class AbstractThreadLocalContextAdapter extends
        AbstractThreadLocalContext implements ContextAdapterInterface {

    public void setClientName(String clientName) {
        ((StaticContext) MultiTenantContext.current()).setClientName(clientName);
    }
    
    public void setUserLogin(String userLogin) {
        ((StaticContext) MultiTenantContext.current()).setUserLogin(userLogin);
    }
}
