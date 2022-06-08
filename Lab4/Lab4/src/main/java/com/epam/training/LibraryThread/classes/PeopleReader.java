package com.epam.training.LibraryThread.classes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class PeopleReader implements Runnable {
    static Logger peopleLogger = LogManager.getLogger(PeopleReader.class.getName());

    // имя читателя
    private String peopleName;

    private static final Random random = new Random();

    private static final int ACTION_NO_VISIT = 0;
    private static final int ACTION_IN_LIBRARY = 1;

    private static final int ACTION_IN_LIBRARY_NO_ACTION = 0;
    private static final int ACTION_IN_LIBRARY_GET_BOOK = 1;
    private static final int ACTION_IN_LIBRARY_OUT_LIBRARY = 2;


    private List<Book> lstBooksLibrary;
    private List<Book> lstBooksByReady = new ArrayList<>();
    private List<Thread> threadsBooksByReady = new ArrayList<>();
    public PeopleReader(String peopleName, List<Book> lstBooksLibrary) {
        this.peopleName = peopleName;
        this.lstBooksLibrary = lstBooksLibrary;

        peopleLogger.info("Создание нового читателя: Имя = " + peopleName);
    }


    public String getPeopleName() {
        return peopleName;
    }
    private int getActionReader(int actionValuesCount) {
        return random.nextInt(actionValuesCount);
    }
    private Book getBookForReady() {
        Book book = null;

        if (lstBooksLibrary.size() > 0) {
            int indexBook = random.nextInt(lstBooksLibrary.size() + 1);

            int i = 0;
            for (Book bookChose : lstBooksLibrary) {
                if (i == indexBook) {
                    book = bookChose;
                    break;
                }

                i++;
            }
        }


        if (book != null && !lstBooksByReady.contains(book)) {
            lstBooksByReady.add(book);
        } else {
            book = null;
        }

        if (book != null)
            peopleLogger.info(this.peopleName + " Выбрал книгу = " + book.getBookName());

        return book;
    }
    @Override
    public void run() {
        try {
            int action = getActionReader(2);
            if (action == ACTION_NO_VISIT) {
                peopleLogger.info(this.peopleName + " Не захотел приходить в библиотеку");
                return;
            }

            if (action == ACTION_IN_LIBRARY) {
                Thread.sleep(random.nextInt(100));

                peopleLogger.info(this.peopleName + " Пришел в библиотеку");
            }

            action = -1;

            while (action != ACTION_IN_LIBRARY_OUT_LIBRARY) {
                action = getActionReader(3);
                if (action == ACTION_IN_LIBRARY_NO_ACTION) {
                    continue;
                }

                if (action == ACTION_IN_LIBRARY_GET_BOOK) {
                    // выбор книги для чтения
                    Book book = getBookForReady();

                    if (book != null) {
                        if (book.getLstPeoplesReader().isEmpty()) {
                            book.addToQueueForReady(this);
                            Thread thread = new Thread(book);
                            threadsBooksByReady.add(thread);
                            thread.start();
                        } else {
                            book.addToQueueForReady(this);
                        }
                    }

                    Thread.sleep(random.nextInt(100));

                    continue;
                }
                if (action == ACTION_IN_LIBRARY_OUT_LIBRARY) {
                    int countThreads = threadsBooksByReady.size();

                    while (countThreads > 0) {
                        for (Thread thd : threadsBooksByReady) {
                            if (thd.getState() == Thread.State.TERMINATED) {
                                countThreads --;
                            }

                            Thread.sleep(200);
                        }
                    }

                    peopleLogger.info(this.peopleName + " Покинул библиотеку");
                    return;
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();

            peopleLogger.error(e.getMessage());
        }
    }
}
