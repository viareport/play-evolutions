package play.modules.evolutions.multitenant;

public interface ContextAdapterInterface {
    public void setClientCode(String clientCode);
    
    public void setUserLogin(String userLogin);
}
