package com.epam.training.LibraryThread.classes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class BookLibrary {
    static Logger libraryLogger = LogManager.getLogger(BookLibrary.class.getName());

    private static final int TIME_BY_READY = 2000;
    private int booksFreeCount;
    private int booksByReadyRoomCount;
    private List<Book> lstBooksLibrary = new ArrayList<>();
    private int peoplesCount;
    private List<PeopleReader> lstPeoplesReader = new ArrayList<>();
    private List<Thread> threadsPeoplesReader = new ArrayList<>();

    public BookLibrary(int booksFreeCount, int booksByReadyRoomCount, int peoplesCount) {
        this.booksFreeCount = booksFreeCount;
        libraryLogger.info("Добавляем книги, которые читаются вне зала...");
        fillBooksLibrary(false, booksFreeCount);

        this.booksByReadyRoomCount = booksByReadyRoomCount;
        libraryLogger.info(" Добавляем книги, которые читаются в зале...");
        fillBooksLibrary(true, booksByReadyRoomCount);

        libraryLogger.info("Добавляем читателей в библиотеку...");
        for (int i = 0; i < peoplesCount; i++) {
            PeopleReader people = new PeopleReader("People" + i, lstBooksLibrary);
            lstPeoplesReader.add(people);
        }
    }

    public List<Book> getLstBooksLibrary() {
        return lstBooksLibrary;
    }
    public List<PeopleReader> getLstPeoplesReader() {
        return lstPeoplesReader;
    }
    private void fillBooksLibrary(boolean byReadyRoom, int count) {
        int firstIndex = lstBooksLibrary.size();

        for (int i = firstIndex; i < (firstIndex) + count; i++) {
            Book book = new Book("Книга" + i, byReadyRoom, new Random().nextInt(TIME_BY_READY));
            lstBooksLibrary.add(book);
        }
    }

    public void startWorkLibrary() throws InterruptedException {
        for (PeopleReader people : lstPeoplesReader) {
            Thread thread = new Thread(people);
            threadsPeoplesReader.add(thread);
            thread.start();
        }

        int countThreads = threadsPeoplesReader.size();

        while (countThreads > 0) {
            for (Thread thd : threadsPeoplesReader) {
                if (thd.getState() == Thread.State.TERMINATED) {
                    countThreads --;
                }

                Thread.sleep(200);
            }
        }
    }

}
