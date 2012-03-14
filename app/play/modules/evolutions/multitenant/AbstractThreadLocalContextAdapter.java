package play.modules.evolutions.multitenant;

import multitenant.context.AbstractThreadLocalContext;
import multitenant.context.MultiTenantContext;

public class AbstractThreadLocalContextAdapter extends
        AbstractThreadLocalContext implements ContextAdapterInterface {

    public void setClientCode(String clientCode) {
        ((StaticContext) MultiTenantContext.current()).setClientCode(clientCode);
    }
    
    public void setUserLogin(String userLogin) {
        ((StaticContext) MultiTenantContext.current()).setUserLogin(userLogin);
    }
}
