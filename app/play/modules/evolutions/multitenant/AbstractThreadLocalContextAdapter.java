package play.modules.evolutions.multitenant;


public class AbstractThreadLocalContextAdapter implements ContextAdapterInterface {

    public void setClientCode(String clientCode) {
//        ((StaticContext) MultiTenantContext.current()).setClientCode(clientCode);
    }

    public void setUserLogin(String userLogin) {
//        ((StaticContext) MultiTenantContext.current()).setUserLogin(userLogin);
    }
}
