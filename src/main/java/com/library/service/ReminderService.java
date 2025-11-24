package com.library.service;

import com.library.communication.EmailServer;
import com.library.model.BorrowRecord;
import com.library.model.EmailMessage;
import com.library.model.User;

import java.time.LocalDate;
import java.util.*;

public class ReminderService {

    private final List<BorrowRecord> borrowRecords;
    private final EmailServer emailServer;

    public ReminderService(List<BorrowRecord> borrowRecords, EmailServer emailServer) {
        this.borrowRecords = borrowRecords;
        this.emailServer = emailServer;
    }

    public int sendReminders(LocalDate currentDate) {

        Map<User, Integer> overdueCount = new HashMap<>();

        for (BorrowRecord record : borrowRecords) {
            if (record.isOverdue(currentDate)) {
                overdueCount.put(
                        record.getUser(),
                        overdueCount.getOrDefault(record.getUser(), 0) + 1
                );
            }
        }

        for (Map.Entry<User, Integer> e : overdueCount.entrySet()) {
            User user = e.getKey();
            int count = e.getValue();

            String msg = "You have " + count + " overdue book(s).";

            emailServer.send(new EmailMessage(user.getUsername(), msg));
        }

        return overdueCount.size();
    }
}
