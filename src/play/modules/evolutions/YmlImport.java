package play.modules.evolutions;

import java.beans.PropertyVetoException;
import java.io.File;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import play.Logger;
import play.Play;
import play.db.DBPlugin;
import play.db.jpa.JPAPlugin;
import play.test.Fixtures;


public class YmlImport {

    public static void main(String[] args) throws Exception {

        // we retrieve parameters
        List<String> filenames = new LinkedList<String>();
        Boolean reset = false;
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--")) {
                if (args[i].startsWith("--filename=")) {
                    filenames.add(args[i].substring(11));
                }
                if (args[i].startsWith("--reset")) {
                    reset = true;
                }
            }
        }
        
        if (filenames.size() == 0 ) {
            filenames.add("data");
        }

        // initiate play! framework
        File root = new File(System.getProperty("application.path"));
        Play.init(root, System.getProperty("play.id", ""));
        Thread.currentThread().setContextClassLoader(Play.classloader);
        Class c = Play.classloader.loadClass("play.modules.evolutions.YmlImport");
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
            Fixtures.loadModels(filename + ".yml");
            Logger.info("* Ending import yml " + filename + ".yml");
        }
        JPAPlugin.closeTx(false);

        // ending log
        Logger.info("*****************************************************************************");
        Logger.info("* Ending import yml                                                         *");
        Logger.info("*****************************************************************************");
    }
    
}
