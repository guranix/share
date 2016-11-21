package main;

import dbService.DBException;
import dbService.DBService;
import dbService.dataSets.LogsDataSet;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * Created by guran on 11/20/16.
 */
public class Main {
    public static void main(String[] args) throws IOException, ParseException, ExecutionException, InterruptedException, DBException, SQLException, URISyntaxException {

        DBService dbService = new DBService();

        ArgsHandler argsHandler = new ArgsHandler(args);

        if (argsHandler.processArgs()) {


            String logsDir = System.getProperty("user.dir");

            List<File> filesList = null;
            try {
                filesList = Files.walk(Paths.get(logsDir)).filter(n-> n.toString().endsWith(argsHandler.getProp().getProperty("logFileExtention"))).map(n-> new File(n.toString())).collect(Collectors.toList());
            } catch (IOException e) {
                System.out.println("There is no directory " + logsDir + " on your disc");
            }

            ExecutorService threadPool = Executors.newFixedThreadPool(argsHandler.getNumberOfThreads());

            if (filesList != null) {
                if (filesList.isEmpty()) {
                    System.out.println("There are no .log files in your " + logsDir + " directory");
                } else {
                    for (File file : filesList) {
                        threadPool.submit(new LogFileHandler(file, dbService, argsHandler)).get();
                    }

                    threadPool.shutdown();

                    List<LogsDataSet> result = dbService.getAll();

                    if (argsHandler.getGroupByUsername() != null) {
                        System.out.println(dbService.getGroupedByUsernameRecordsCount(argsHandler.getGroupByUsername()) +
                        " records grouped by username " + argsHandler.getGroupByUsername());
                    }

                    if (argsHandler.getGroupByHour() != null) {
                        System.out.println(dbService.getGroupedByHourRecordsCount(argsHandler.getGroupByHour()) +
                                " records grouped by " + argsHandler.getGroupByHour() + " hour time unit.");
                    }

                    dbService.cleanUp();

                    try (FileWriter writer = new FileWriter(argsHandler.getOutputFilePath())) {
                        for(LogsDataSet str: result) {
                            writer.write(str.toString() + '\n');
                        }
                    } catch (FileNotFoundException e) {
                        System.out.println("Default output file: " + argsHandler.getProp().getProperty("defaultFileName") + ".txt");
                        FileWriter writer = new FileWriter(argsHandler.getProp().getProperty("defaultFileName") + ".txt");
                        for(LogsDataSet str: result) {
                            writer.write(str.toString() + '\n');
                        }
                        writer.close();
                    }

                }
            }

        } else {
            System.out.println("Try again!");
        }

    }
}
