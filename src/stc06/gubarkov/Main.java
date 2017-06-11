package stc06.gubarkov;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {
    static int result;

    public static void main(String[] args) {
        List<String> fileNames = new ArrayList<>();
        fileNames.add("numbers1.txt");
        fileNames.add("numbers2.txt");
        fileNames.add("numbers3.txt");

        int fileNamesSize = fileNames.size();

        ThreadPoolExecutor service = new ThreadPoolExecutor(fileNamesSize, fileNamesSize,
                5, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(fileNamesSize));

        ResultCounter resultCounter = new ResultCounter(service);
        Thread resultThread = new Thread(resultCounter);

        for (String fileName: fileNames) {
            Runnable runnable = new NumbersContainer(resultCounter, fileName);
            service.execute(runnable);
        }
        service.shutdown();
        resultThread.start();
    }
}