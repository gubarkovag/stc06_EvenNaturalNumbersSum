package stc06.gubarkov;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadPoolExecutor;

public class ResultCounter implements Runnable {
    private ThreadPoolExecutor service;
    private int result;
    private boolean threadsToBeInterrupted;
    private List<Integer> curStringsSums;

    ResultCounter(ThreadPoolExecutor service) {
        this.service = service;
        this.curStringsSums = new CopyOnWriteArrayList<>();
    }

    void addToCurStringsSums(int curStringNumberSum) {
        curStringsSums.add(curStringNumberSum);
    }

    boolean isThreadsToBeInterrupted() {
        return threadsToBeInterrupted;
    }

    void setThreadsToBeInterrupted(boolean threadsToBeInterrupted) {
        this.threadsToBeInterrupted = threadsToBeInterrupted;
    }

    private void addToResult(int number) {
        this.result += number;
    }

    @Override
    public void run() {
        while (!service.isTerminated()) {
            try {
                Thread.sleep(2000);
                synchronized(this) {
                    notifyAll();
                    if (!threadsToBeInterrupted) {
                        curStringsSums.stream().forEach(c -> addToResult(c));
                        curStringsSums.clear();
                    }
                    if (!service.isTerminated()) {
                        System.out.println("Текущая сумма всех положительных чётных чисел: " + result);
                    } else {
                        System.out.println("Итоговая сумма всех положительных чётных чисел: " + result);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
