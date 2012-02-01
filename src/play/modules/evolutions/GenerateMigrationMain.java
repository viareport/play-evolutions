package play.modules.evolutions;

import org.apache.commons.lang.StringUtils;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import play.Logger;
import play.Play;
import play.db.DB;
import play.db.DBPlugin;
import play.db.jpa.JPA;
import play.db.jpa.JPAPlugin;
import play.libs.IO;
import play.templates.JavaExtensions;
import play.utils.Utils;

import javax.persistence.Entity;
import java.io.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * @author huljas
 * @author goldoraf : adapted huljas code to work with evolutions
 */
public class GenerateMigrationMain {
    
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Please give description for your migration:");
        String description = reader.readLine();
        System.out.println("Please give a version number:"); // TODO : le trouver automatiquement
        String version = reader.readLine();

        String appPath = System.getProperty("application.path",".");
        String id = System.getProperty("play.id", "");
        Play.init(new File(appPath), id);
        
        String migrationsPath = Play.applicationPath.getPath() + "/db/evolutions";
        
        Ejb3Configuration configuration = new Ejb3Configuration();
        String driver = Play.configuration.getProperty("db.driver");
        String delimeter = Play.configuration.getProperty("evolutions.delimeter", ";");
        String comment = Play.configuration.getProperty("evolutions.comment", "--");
        
        String migrationBody = "";
        if (driver != null) {
            String dialectName = getDefaultDialect(driver);
            configuration.setProperty("hibernate.dialect", dialectName);
            Properties fromConf = (Properties) Utils.Maps.filterMap(Play.configuration, "^hibernate\\..*");
            configuration.addProperties(fromConf);
            List<Class> classes = Play.classloader.getAnnotatedClasses(Entity.class);
            Thread.currentThread().setContextClassLoader(Play.classloader);
            if (classes.isEmpty()) {
                Logger.warn("No entities detected!");
            }
            for (Class clazz : classes) {
                configuration.addAnnotatedClass(clazz);
            }
            configuration.buildEntityManagerFactory();
            DBPlugin plugin = new DBPlugin();
            plugin.onApplicationStart();
            Dialect dialect = (Dialect) Class.forName(dialectName).newInstance();
            DatabaseMetadata metadata = new DatabaseMetadata(DB.getConnection(), dialect);
            String[] content = configuration.getHibernateConfiguration().generateSchemaUpdateScript(dialect, metadata);
            if (content.length == 0) {
                Logger.warn("No changes from schema update!");
            } else {
                migrationBody = StringUtils.join(content, delimeter + "\n");
                migrationBody += delimeter;
                Logger.warn("Changes from schema update:\n" + migrationBody);
            }
        } else {
            Logger.warn("Property 'db.driver' not defined in Play configuration, ignoring schema update!");
        }
        
        File directory = new File(migrationsPath);
        if (!directory.exists()) {
            Logger.warn("Creating non-existent directory " + directory.getAbsolutePath());
            directory.mkdirs();
        }
        
        File migrationFile = new File(directory, version + ".sql");
        migrationFile.createNewFile();
        PrintWriter writer = new PrintWriter(new FileWriter(migrationFile));
        writer.println("# " + description);
        writer.println();
        writer.println("# --- !Ups");
        writer.println();
        writer.println(migrationBody);
        writer.println();
        writer.println("# --- !Downs");
        writer.println();
        writer.close();
        
        Logger.warn("New evolution file created: " + migrationFile.getAbsolutePath());
    }
    
    public static String getDefaultDialect(String driver) {
        String dialect = Play.configuration.getProperty("jpa.dialect");
        if (dialect != null) {
            return dialect;
        } else if (driver.equals("org.hsqldb.jdbcDriver")) {
            return "org.hibernate.dialect.HSQLDialect";
        } else if (driver.equals("com.mysql.jdbc.Driver")) {
            return "play.db.jpa.MySQLDialect";
        } else if (driver.equals("org.postgresql.Driver")) {
            return "org.hibernate.dialect.PostgreSQLDialect";
        } else if (driver.toLowerCase().equals("com.ibm.db2.jdbc.app.DB2Driver")) {
            return "org.hibernate.dialect.DB2Dialect";
        } else if (driver.equals("com.ibm.as400.access.AS400JDBCDriver")) {
            return "org.hibernate.dialect.DB2400Dialect";
        } else if (driver.equals("com.ibm.as400.access.AS390JDBCDriver")) {
            return "org.hibernate.dialect.DB2390Dialect";
        } else if (driver.equals("oracle.jdbc.driver.OracleDriver")) {
            return "org.hibernate.dialect.Oracle9iDialect";
        } else if (driver.equals("com.sybase.jdbc2.jdbc.SybDriver")) {
            return "org.hibernate.dialect.SybaseAnywhereDialect";
        } else if ("com.microsoft.jdbc.sqlserver.SQLServerDriver".equals(driver)) {
            return "org.hibernate.dialect.SQLServerDialect";
        } else if ("com.sap.dbtech.jdbc.DriverSapDB".equals(driver)) {
            return "org.hibernate.dialect.SAPDBDialect";
        } else if ("com.informix.jdbc.IfxDriver".equals(driver)) {
            return "org.hibernate.dialect.InformixDialect";
        } else if ("com.ingres.jdbc.IngresDriver".equals(driver)) {
            return "org.hibernate.dialect.IngresDialect";
        } else if ("progress.sql.jdbc.JdbcProgressDriver".equals(driver)) {
            return "org.hibernate.dialect.ProgressDialect";
        } else if ("com.mckoi.JDBCDriver".equals(driver)) {
            return "org.hibernate.dialect.MckoiDialect";
        } else if ("InterBase.interclient.Driver".equals(driver)) {
            return "org.hibernate.dialect.InterbaseDialect";
        } else if ("com.pointbase.jdbc.jdbcUniversalDriver".equals(driver)) {
            return "org.hibernate.dialect.PointbaseDialect";
        } else if ("com.frontbase.jdbc.FBJDriver".equals(driver)) {
            return "org.hibernate.dialect.FrontbaseDialect";
        } else if ("org.firebirdsql.jdbc.FBDriver".equals(driver)) {
            return "org.hibernate.dialect.FirebirdDialect";
        } else {
            throw new UnsupportedOperationException("I do not know which hibernate dialect to use with "
                    + driver + " and I cannot guess it, use the property jpa.dialect in config file");
        }
    }
}