package stc06.gubarkov;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class NumbersContainer implements Runnable {
    private final ResultCounter resultCounter;
    private String fileName;

    NumbersContainer(ResultCounter resultCounter, String fileName) {
        this.resultCounter = resultCounter;
        this.fileName = fileName;
    }

    @Override
    public void run() {
        readLinesFromFile();
    }

    private void readLinesFromFile() {
        String fileNameLine;
        try (BufferedReader bin =
                 new BufferedReader(new InputStreamReader(new FileInputStream(fileName)))) {
            int curStrNum = 0;
            while ((fileNameLine = bin.readLine()) != null) {
                curStrNum++;
                synchronized(resultCounter) {
                    if (resultCounter.isThreadsToBeInterrupted()) {
                        interruptCurrentThread(null);
                    }
                    resultCounter.wait();
                    resultCounter.addToCurStringsSums(countCurStringNumbersSum(fileNameLine, curStrNum));
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            String message = (ex.getMessage() != null ? ex.getMessage() + "\n" : "") +
                    "Выполнение потока чтения " +
                    " данных из файла " + fileName + " прервано";
            System.out.println(message);
        }
    }

    private int countCurStringNumbersSum(String string, int strNum) throws InterruptedException {
        String[] stringNumbers = string.split(" ");
        int curStringNumbersSum = 0;
        for (String stringNumber : stringNumbers) {
            if (!isReadNumberCorrect(stringNumber)) {
                resultCounter.setThreadsToBeInterrupted(true);
                interruptCurrentThread("Некорректное значение в строке номер " +
                        strNum + " в файле " + fileName);
            } else {
                int number = Integer.parseInt(stringNumber);
                if (isEvenNaturalNumber(number)) {
                    curStringNumbersSum += number;
                }
            }
        }
        return curStringNumbersSum;
    }

    private void interruptCurrentThread(String description) throws InterruptedException {
        Thread.currentThread().interrupt();
        throw new InterruptedException(description);
    }

    private boolean isReadNumberCorrect(String stringNumber) {
        return stringNumber.matches("-?\\d+");
    }

    private boolean isEvenNaturalNumber(int number) {
        return number % 2 == 0 && number > 0;
    }
}
