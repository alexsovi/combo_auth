package org.figrja.combo_auth;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.figrja.combo_auth.config.configGson;
import org.figrja.combo_auth.config.debuglogger.Debug;
import org.figrja.combo_auth.config.debuglogger.DebugAll;
import org.figrja.combo_auth.config.debuglogger.Logger;
import org.figrja.combo_auth.config.debuglogger.LoggerMain;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;


public class auth implements DedicatedServerModInitializer {

    private static configGson config;
    private static final Gson gson = new Gson();

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger("combo_auth");

    public static LoggerMain Logger;

    @Override
    public void onInitializeServer() {
        LOGGER.info("start loading config");

        File ConfFile = FabricLoader.getInstance().getConfigDir().resolve( "combo_auth.json" ).toFile();
        JsonReader reader = null;
        try {
            reader = new JsonReader(new FileReader(ConfFile));
            config = gson.fromJson(reader,configGson.class);
        } catch (FileNotFoundException e) {
            try {
                LOGGER.info("create new config");
                Files.createFile(ConfFile.toPath());
                InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("combo_auth.json");
                if (inputStream != null) {
                    config = gson.fromJson(new JsonReader(new BufferedReader(new InputStreamReader(inputStream))),configGson.class);
                }else {
                    LOGGER.info("wtf inputStream of config in jar is null");
                }
                PrintWriter printWriter = new PrintWriter(ConfFile);
                Scanner scanner = new Scanner(inputStream);
                while (scanner.hasNextLine()){printWriter.println(scanner.nextLine());}
                scanner.close();
                printWriter.flush();
                printWriter.close();

                inputStream.close();
            } catch (IOException ex) {
                LOGGER.info("can't create new config");
                throw new RuntimeException(ex);
            }
        }

        if (config.getGebugStatus() != null){
            if (config.getGebugStatus().equals("detail")){
                LOGGER.info("debug");
                Logger = new Debug(LOGGER);
            }if (config.getGebugStatus().equals("all")){
                LOGGER.info("ALLdebug");
                Logger = new DebugAll(LOGGER);
            }
        }else{
            Logger = new Logger(LOGGER);
        }


        if (config != null) {
            LOGGER.info("combo_auth has been enabled!");
        }else{
            throw null;
        }

    }

    public static configGson getConfig() {
        return config;
    }
}
