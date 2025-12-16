package com.library.service;

import com.library.model.BorrowRecord;
import com.library.model.User;
import com.library.notifications.Observer;

import java.time.LocalDate;
import java.util.*;

/**
 * ReminderService: خدمة إرسال التذكيرات للمستخدمين الذين لديهم عناصر متأخرة.
 * الآن جاهز لإرسال SMS أو أي نوع من الإشعارات عن طريق Observer.
 */
public class ReminderService {

    private final BorrowService borrowService;

    // قائمة المراقبين (Observers) لكل أنواع الإشعارات
    private final List<Observer> observers = new ArrayList<>();

    public ReminderService(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    /**
     * إضافة Observer جديد
     */
    public void addObserver(Observer obs) {
        observers.add(obs);
    }

    /**
     * إخطار جميع المراقبين برسالة محددة لمستخدم معين
     */
    private void notifyAllObservers(User user, String message) {
        for (Observer o : observers) {
            o.notify(user, message); // Observer يجب أن يحتوي على notify(User, String)
        }
    }

    /**
     * إرسال رسائل تذكير للمستخدمين الذين لديهم عناصر متأخرة
     * @param today تاريخ اليوم
     * @return عدد المستخدمين الذين تم إرسال تذكيرات لهم
     */
    public int sendOverdueReminders(LocalDate today) {

        Map<User, Integer> affectedUsers = new HashMap<>();

        // الحصول على كل المستخدمين الذين لديهم سجلات استعارة
        for (User user : borrowService.getAllUsersWithRecords()) {

            List<BorrowRecord> records = borrowService.getBorrowRecordsForUser(user);
            int overdueCount = 0;

            // عد العناصر المتأخرة
            for (BorrowRecord r : records) {
                if (r.isOverdue(today)) {
                    overdueCount++;
                }
            }

            // إذا كان لديه عناصر متأخرة
            if (overdueCount > 0) {

                affectedUsers.put(user, overdueCount);

                String msg = "You have " + overdueCount + " overdue item(s).";

                // إخطار جميع المراقبين
                notifyAllObservers(user, msg);
            }
        }

        return affectedUsers.size(); 
    }
}
