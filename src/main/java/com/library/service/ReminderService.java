package com.library.service;

import com.library.model.BorrowRecord;
import com.library.model.User;
import com.library.notifications.Observer;

import java.time.LocalDate;
import java.util.*;

public class ReminderService {

    private final BorrowService borrowService;

    
    private final List<Observer> observers = new ArrayList<>();

    public ReminderService(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    
    public void addObserver(Observer obs) {
        observers.add(obs);
    }

    
    private void notifyAllObservers(User user, String message) {
        for (Observer o : observers) {
            o.notify(user, message);
        }
    }

    
    public int sendOverdueReminders(LocalDate today) {

        Map<User, Integer> affectedUsers = new HashMap<>();

       
        for (User user : borrowService.getAllUsersWithRecords()) {

            List<BorrowRecord> records = borrowService.getBorrowRecordsForUser(user);
            int overdueCount = 0;

            
            for (BorrowRecord r : records) {
                if (r.isOverdue(today)) {
                    overdueCount++;
                }
            }

            
            if (overdueCount > 0) {

                affectedUsers.put(user, overdueCount);

                String msg = "You have " + overdueCount + " overdue item(s).";

               
                notifyAllObservers(user, msg);
            }
        }

        return affectedUsers.size(); 
    }
}


