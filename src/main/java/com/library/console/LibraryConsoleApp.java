package com.library.console;

import com.library.communication.EmailServer;
import com.library.communication.MockEmailServer;
import com.library.model.Media;
import com.library.model.User;

import com.library.notifications.EmailNotifier;
import com.library.notifications.SMSNotifier;
import com.library.notifications.PushNotifier;
import com.library.notifications.ConsoleNotifier;

import com.library.repository.FileAdminRepository;
import com.library.repository.FileUserRepository;
import com.library.repository.FileBorrowRepository;
import com.library.repository.FileMediaRepository;
import com.library.repository.UserRepository;

import com.library.service.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LibraryConsoleApp {

    private static final Scanner scanner = new Scanner(System.in);
    private static final Logger logger = Logger.getLogger(LibraryConsoleApp.class.getName());

    public static void main(String[] args) {
        logger.info("Working directory = " + System.getProperty("user.dir"));

        FileAdminRepository adminRepo = new FileAdminRepository("admins.txt");
        FileMediaRepository mediaRepo = new FileMediaRepository("media.txt");
        UserRepository userRepo = new FileUserRepository("users.txt");
        FileBorrowRepository borrowRepo = new FileBorrowRepository("borrow.txt", mediaRepo);

        AuthService authService = new AuthService(adminRepo);
        MediaService mediaService = new MediaService(mediaRepo);
        BorrowService borrowService = new BorrowService(mediaRepo, borrowRepo);
        FineService fineService = new FineService(userRepo);

        EmailServer emailServer = new MockEmailServer();
        ReminderService reminderService = new ReminderService(borrowService);
        reminderService.addObserver(new EmailNotifier(emailServer));
        reminderService.addObserver(new SMSNotifier());
        reminderService.addObserver(new PushNotifier());
        reminderService.addObserver(new ConsoleNotifier());

        UserService userService = new UserService(userRepo);

        boolean exit = false;

        logger.info("===== Library Management System =====");

        while (!exit) {
            printMenu(authService.isLoggedIn());
            String choice = readText("اختر خيار: ");

            if (!authService.isLoggedIn()) {
                switch (choice) {
                    case "1": handleLogin(authService); break;
                    case "2": handleRegister(userRepo); break;
                    case "3": handleSearch(mediaService); break;
                    case "4": exit = true; break;
                    default: logger.warning("❌ خيار غير صحيح.");
                }
            } else {
                switch (choice) {
                    case "1": handleAddMedia(mediaService); break;
                    case "2": handleBorrow(borrowService, userRepo); break;
                    case "3": handleReturn(borrowService); break;
                    case "4": handlePayFine(fineService); break;
                    case "5": handleUnregister(userService, borrowService); break;
                    case "6": handleSendReminders(reminderService); break;
                    case "7": handleViewBorrowed(borrowService, userRepo); break;
                    case "8": 
                        authService.logout();
                        logger.info("✅ تم تسجيل الخروج.");
                        break;
                    case "9": exit = true; break;
                    default: logger.warning("❌ خيار غير صحيح.");
                }
            }
            logger.info(""); // فصل بين العمليات
        }

        logger.info("✅ تم إغلاق النظام.");
    }

    private static void printMenu(boolean loggedIn) {
        logger.info("----------------------------------");
        if (!loggedIn) {
            logger.info("1) Admin Login");
            logger.info("2) Register User");
            logger.info("3) Search Media");
            logger.info("4) Exit");
        } else {
            logger.info("** Admin Menu **");
            logger.info("1) Add Media (Book / CD)");
            logger.info("2) Borrow Media");
            logger.info("3) Return Media");
            logger.info("4) Pay Fine");
            logger.info("5) Unregister User");
            logger.info("6) Send Overdue Reminders");
            logger.info("7) View User Borrowed Items");
            logger.info("8) Logout");
            logger.info("9) Exit");
        }
    }

    private static int readInt(String message) {
        while (true) {
            String input = readText(message);
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                logger.warning("❌ يجب إدخال رقم صحيح.");
            }
        }
    }

    private static double readDouble(String message) {
        while (true) {
            String input = readText(message);
            try {
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                logger.warning("❌ يجب إدخال رقم مثل: 20 أو 15.5");
            }
        }
    }

    private static String readText(String message) {
        System.out.print(message); // يُفضل ترك هذا للإدخال من المستخدم
        return scanner.nextLine().trim();
    }

    private static void handleLogin(AuthService authService) {
        String username = readText("اسم المستخدم: ");
        String password = readText("كلمة المرور: ");
        if (authService.login(username, password))
            logger.info("✅ تسجيل دخول ناجح.");
        else
            logger.warning("❌ فشل تسجيل الدخول.");
    }

    private static void handleRegister(UserRepository userRepo) {
        String username = readText("اسم المستخدم الجديد: ");
        if (username.contains(",") || username.contains(" ")) {
            logger.warning("❌ اسم المستخدم يجب أن لا يحتوي فواصل أو مسافات.");
            return;
        }
        if (userRepo.findByUsername(username) != null) {
            logger.warning("❌ المستخدم موجود مسبقًا.");
            return;
        }
        double balance = readDouble("أدخل الغرامة الابتدائية (0 إذا لا يوجد): ");
        User user = new User(username, balance);
        userRepo.save(user);
        logger.info("✅ تم إنشاء المستخدم بنجاح.");
    }

    private static void handleAddMedia(MediaService mediaService) {
        logger.info("اختر النوع:");
        logger.info("1) كتاب");
        logger.info("2) CD");

        String type = readText("اختيار: ");
        String title = readText("العنوان: ");
        int copies = readInt("عدد النسخ: ");

        switch (type) {
            case "1":
                String author = readText("المؤلف: ");
                String isbn = readText("ISBN: ");
                Media book = mediaService.addBook(title, author, isbn, copies);
                logger.info("✅ تمت إضافة كتاب: " + book);
                break;
            case "2":
                String artist = readText("الفنان: ");
                Media cd = mediaService.addCD(title, artist, copies);
                logger.info("✅ تمت إضافة CD: " + cd);
                break;
            default:
                logger.warning("❌ خيار غير صحيح.");
        }
    }

    private static void handleSearch(MediaService mediaService) {
        String keyword = readText("أدخل كلمة للبحث: ");
        List<Media> results = mediaService.searchByTitle(keyword);
        if (results.isEmpty()) {
            logger.info("لا توجد نتائج.");
        } else {
            results.forEach(record -> logger.info(record.toString()));
        }
    }

    private static void handleBorrow(BorrowService borrowService, UserRepository userRepo) {
        String username = readText("اسم المستخدم: ");
        User user = userRepo.findByUsername(username);
        if (user == null) {
            logger.warning("❌ المستخدم غير موجود.");
            return;
        }
        int id = readInt("ID العنصر: ");
        try {
            borrowService.borrow(user, id, LocalDate.now());
            logger.info("✅ تمت عملية الاستعارة.");
        } catch (Exception e) {
            logger.log(Level.WARNING, "❌ خطأ: " + e.getMessage(), e);
        }
    }

    private static void handleReturn(BorrowService borrowService) {
        String username = readText("اسم المستخدم: ");
        int id = readInt("ID العنصر: ");
        try {
            borrowService.returnItem(username, id, LocalDate.now());
            logger.info("✅ تمت عملية الإرجاع.");
        } catch (Exception e) {
            logger.log(Level.WARNING, "❌ خطأ: " + e.getMessage(), e);
        }
    }

    private static void handlePayFine(FineService fineService) {
        String username = readText("اسم المستخدم: ");
        double amount = readDouble("المبلغ: ");
        if (fineService.payFine(username, amount))
            logger.info("✅ تم دفع الغرامة.");
        else
            logger.warning("❌ فشل دفع الغرامة.");
    }

    private static void handleUnregister(UserService userService, BorrowService borrowService) {
        String adminName = readText("Admin username: ");
        User admin = new User(adminName, 0);
        String targetName = readText("User to unregister: ");
        User target = new User(targetName, 0);
        try {
            if (userService.unregister(admin, target, borrowService))
                logger.info("✅ تم حذف المستخدم.");
            else
                logger.warning("❌ لم يتم الحذف.");
        } catch (Exception e) {
            logger.log(Level.WARNING, "❌ خطأ: " + e.getMessage(), e);
        }
    }

    private static void handleSendReminders(ReminderService reminderService) {
        int count = reminderService.sendOverdueReminders(LocalDate.now());
        logger.info("✅ تم إرسال " + count + " رسائل تذكير.");
    }

    private static void handleViewBorrowed(BorrowService borrowService, UserRepository userRepo) {
        String username = readText("اسم المستخدم: ");
        User user = userRepo.findByUsername(username);
        if (user == null) {
            logger.warning("❌ المستخدم غير موجود.");
            return;
        }
        List<?> records = borrowService.getBorrowRecordsForUser(user);
        if (records.isEmpty()) {
            logger.info("لا توجد استعارات حالية.");
        } else {
            logger.info("📌 استعارات المستخدم:");
            records.forEach(record -> logger.info(record.toString()));
        }
    }
}
