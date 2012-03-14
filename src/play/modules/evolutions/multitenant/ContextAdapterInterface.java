package play.modules.evolutions.multitenant;

public interface ContextAdapterInterface {
    public void setClientName(String clientName);
    
    public void setUserLogin(String userLogin);
}
