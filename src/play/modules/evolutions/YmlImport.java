package play.modules.evolutions;

import java.io.File;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import play.Logger;
import play.Play;
import play.db.DBPlugin;
import play.db.jpa.JPAPlugin;
import play.modules.evolutions.multitenant.ContextAdapterInterface;
import play.test.Fixtures;


public class YmlImport {

    public static void main(String[] args) throws Exception {

        Thread.currentThread().sleep(5000);
        
        // we retrieve parameters
        List<String> filenames = new LinkedList<String>();
        Boolean reset = false;
        String clientCode = "";
        String userLogin = "";
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--")) {
                if (args[i].startsWith("--filename=")) {
                    filenames.add(args[i].substring(11));
                }
                if (args[i].startsWith("--reset")) {
                    reset = true;
                }
                if (args[i].startsWith("--clientCode=")) {
                    clientCode = args[i].substring(13);
                }
                if (args[i].startsWith("--userLogin=")) {
                    userLogin = args[i].substring(12);
                }
            }
        }
        
        if (filenames.size() == 0 ) {
            filenames.add("data");
        }
        if (clientCode.isEmpty() || userLogin.isEmpty()) {
            Logger.error("Both '--clientCode=' and '--userLogin=' parameters are required.");
            System.exit(-1);
        }
        
        //multitenant.ctx.factory=play.modules.evolutions.multitenant.StaticContextFactory
        // initiate play! framework
        File root = new File(System.getProperty("application.path"));
        Play.init(root, System.getProperty("play.id", ""));
        Thread.currentThread().setContextClassLoader(Play.classloader);
        Class c = Play.classloader.loadClass("play.modules.evolutions.YmlImport");
        
        Play.configuration.setProperty("multitenant.ctx.factory", "play.modules.evolutions.multitenant.StaticContextFactory");
        
        ContextAdapterInterface contextAdapter = (ContextAdapterInterface)Play.classloader.getAssignableClasses(ContextAdapterInterface.class).get(0).newInstance();
        
        contextAdapter.setClientCode(clientCode);
        contextAdapter.setUserLogin(userLogin);
        
        
        Method m = c.getMethod("mainWork", List.class, Boolean.class);
        m.invoke(c.newInstance(), filenames, reset);
        System.exit(0);

    }

    public static void mainWork(List<String> filenames, Boolean reset) {
        // starting play DB plugin
        new DBPlugin().onApplicationStart();
        new JPAPlugin().onApplicationStart();
        JPAPlugin.startTx(false);
        if (reset) {
            Fixtures.deleteDatabase();
        }
        for (String filename : filenames) {
            Fixtures.loadModels(filename);
            Logger.info("* Ending import yml " + filename);
        }
        JPAPlugin.closeTx(false);

        // ending log
        Logger.info("*****************************************************************************");
        Logger.info("* Ending import yml                                                         *");
        Logger.info("*****************************************************************************");
    }
    
}
