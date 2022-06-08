package com.epam.training.LibraryThread.classes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;


public class Book implements Runnable {
    static Logger bookLogger = LogManager.getLogger(Book.class.getName());


    private String bookName;
    private int timeReady = 2000;
    private boolean byReadyRoom = false;
    private Queue<PeopleReader> lstPeoplesReader = new ConcurrentLinkedQueue<PeopleReader>();
    public Book(String bookName, boolean byReadyRoom, int timeReady) {
        this.bookName = bookName;
        this.byReadyRoom = byReadyRoom;
        this.timeReady = timeReady;

        bookLogger.info("Создается новая книга: название = " + bookName +
                ", byReadyRoom = " + byReadyRoom + ", timeReady = " + timeReady);
    }

    public String getBookName() {
        return bookName;
    }
    public Queue<PeopleReader> getLstPeoplesReader() {
        return lstPeoplesReader;
    }
    public boolean isByReadyRoom() {
        return byReadyRoom;
    }
    public void addToQueueForReady(PeopleReader peopleReader) {
        lstPeoplesReader.offer(peopleReader);
    }

    @Override
    public void run() {
        try {
            while (!lstPeoplesReader.isEmpty()) {
                PeopleReader peopleReader = lstPeoplesReader.poll();

                readyBook(new Random().nextInt(this.timeReady), peopleReader.getPeopleName());
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            bookLogger.error(e.getMessage());

        } catch (Exception e) {
            e.printStackTrace();
            bookLogger.error(e.getMessage());
        }
    }

    public void readyBook(int timeReady, String peopleName) throws InterruptedException{
                bookLogger.info(peopleName + ": " + this.bookName + " начинает читать...");
                Thread.sleep(timeReady);
                bookLogger.info(peopleName + ": " + this.bookName + " перестает читать...");
    }
}
