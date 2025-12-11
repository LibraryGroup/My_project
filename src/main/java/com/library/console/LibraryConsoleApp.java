package com.library.console;

import com.library.communication.EmailServer;
import com.library.communication.MockEmailServer;
import com.library.model.Media;
import com.library.model.User;
import com.library.notifications.ConsoleNotifier;
import com.library.notifications.EmailNotifier;
import com.library.notifications.PushNotifier;
import com.library.notifications.SMSNotifier;
import com.library.repository.FileAdminRepository;
import com.library.repository.FileBorrowRepository;
import com.library.repository.FileMediaRepository;
import com.library.repository.FileUserRepository;
import com.library.repository.UserRepository;
import com.library.service.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class LibraryConsoleApp {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Working directory = " + System.getProperty("user.dir"));

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
        System.out.println("===== Library Management System =====");

        while (!exit) {
            printMenu(authService.isLoggedIn());
            System.out.print("اختر خيار: ");

            String choice = scanner.nextLine().trim();
            System.out.println();

            if (!authService.isLoggedIn()) {
                switch (choice) {
                    case "1": handleLogin(authService); break;
                    case "2": handleRegister(userRepo); break;
                    case "3": handleSearch(mediaService); break;
                    case "4": exit = true; break;
                    default: System.out.println("❌ خيار غير صحيح.");
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
                        System.out.println("✅ تم تسجيل الخروج.");
                        break;
                    case "9": exit = true; break;
                    default: System.out.println("❌ خيار غير صحيح.");
                }
            }
            System.out.println();
        }
        System.out.println("✅ تم إغلاق النظام.");
    }

    private static void printMenu(boolean loggedIn) {
        System.out.println("----------------------------------");
        if (!loggedIn) {
            System.out.println("1) Admin Login");
            System.out.println("2) Register User");
            System.out.println("3) Search Media");
            System.out.println("4) Exit");
        } else {
            System.out.println("** Admin Menu **");
            System.out.println("1) Add Media (Book / CD)");
            System.out.println("2) Borrow Media");
            System.out.println("3) Return Media");
            System.out.println("4) Pay Fine");
            System.out.println("5) Unregister User");
            System.out.println("6) Send Overdue Reminders");
            System.out.println("7) View User Borrowed Items");
            System.out.println("8) Logout");
            System.out.println("9) Exit");
        }
    }

    private static int readInt(String message) {
        while (true) {
            System.out.print(message);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("❌ يجب إدخال رقم صحيح.");
            }
        }
    }

    private static double readDouble(String message) {
        while (true) {
            System.out.print(message);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("❌ يجب إدخال رقم مثل: 20 أو 15.5");
            }
        }
    }

    private static String readText(String message) {
        System.out.print(message);
        return scanner.nextLine().trim();
    }

    private static void handleLogin(AuthService authService) {
        String username = readText("اسم المستخدم: ");
        String password = readText("كلمة المرور: ");
        if (authService.login(username, password)) {
            System.out.println("✅ تسجيل دخول ناجح.");
        } else {
            System.out.println("❌ فشل تسجيل الدخول.");
        }
    }

    private static void handleRegister(UserRepository userRepo) {
        String username = readText("اسم المستخدم الجديد: ");
        if (username.contains(",") || username.contains(" ")) {
            System.out.println("❌ اسم المستخدم يجب أن لا يحتوي فواصل أو مسافات.");
            return;
        }
        if (userRepo.findByUsername(username) != null) {
            System.out.println("❌ المستخدم موجود مسبقًا.");
            return;
        }
        double balance = readDouble("أدخل الغرامة الابتدائية (0 إذا لا يوجد): ");
        User user = new User(username, balance);
        userRepo.save(user);
        System.out.println("✅ تم إنشاء المستخدم بنجاح.");
    }

    private static void handleAddMedia(MediaService mediaService) {
        System.out.println("اختر النوع:");
        System.out.println("1) كتاب");
        System.out.println("2) CD");
        String type = readText("اختيار: ");
        String title = readText("العنوان: ");
        int copies = readInt("عدد النسخ: ");

        switch (type) {
            case "1":
                String author = readText("المؤلف: ");
                String isbn = readText("ISBN: ");
                Media book = mediaService.addBook(title, author, isbn, copies);
                System.out.println("✅ تمت إضافة كتاب: " + book);
                break;
            case "2":
                String artist = readText("الفنان: ");
                Media cd = mediaService.addCD(title, artist, copies);
                System.out.println("✅ تمت إضافة CD: " + cd);
                break;
            default:
                System.out.println("❌ خيار غير صحيح.");
        }
    }

    private static void handleSearch(MediaService mediaService) {
        String keyword = readText("أدخل كلمة للبحث: ");
        List<Media> results = mediaService.searchByTitle(keyword);
        if (results.isEmpty()) {
            System.out.println("لا توجد نتائج.");
        } else {
            results.forEach(System.out::println);
        }
    }

    private static void handleBorrow(BorrowService borrowService, UserRepository userRepo) {
        String username = readText("اسم المستخدم: ");
        User user = userRepo.findByUsername(username);
        if (user == null) {
            System.out.println("❌ المستخدم غير موجود.");
            return;
        }
        int id = readInt("ID العنصر: ");
        try {
            borrowService.borrow(user, id, LocalDate.now());
            System.out.println("✅ تمت عملية الاستعارة.");
        } catch (Exception e) {
            System.out.println("❌ خطأ: " + e.getMessage());
        }
    }

    private static void handleReturn(BorrowService borrowService) {
        String username = readText("اسم المستخدم: ");
        int id = readInt("ID العنصر: ");
        try {
            borrowService.returnItem(username, id, LocalDate.now());
            System.out.println("✅ تمت عملية الإرجاع.");
        } catch (Exception e) {
            System.out.println("❌ خطأ: " + e.getMessage());
        }
    }

    private static void handlePayFine(FineService fineService) {
        String username = readText("اسم المستخدم: ");
        double amount = readDouble("المبلغ: ");
        if (fineService.payFine(username, amount)) {
            System.out.println("✅ تم دفع الغرامة.");
        } else {
            System.out.println("❌ فشل دفع الغرامة.");
        }
    }

    private static void handleUnregister(UserService userService, BorrowService borrowService) {
        String adminName = readText("Admin username: ");
        User admin = new User(adminName, 0);
        String targetName = readText("User to unregister: ");
        User target = new User(targetName, 0);

        try {
            if (userService.unregister(admin, target, borrowService)) {
                System.out.println("✅ تم حذف المستخدم.");
            } else {
                System.out.println("❌ لم يتم الحذف.");
            }
        } catch (Exception e) {
            System.out.println("❌ خطأ: " + e.getMessage());
        }
    }

    private static void handleSendReminders(ReminderService reminderService) {
        int count = reminderService.sendOverdueReminders(LocalDate.now());
        System.out.println("✅ تم إرسال " + count + " رسائل تذكير.");
    }

    private static void handleViewBorrowed(BorrowService borrowService, UserRepository userRepo) {
        String username = readText("اسم المستخدم: ");
        User user = userRepo.findByUsername(username);
        if (user == null) {
            System.out.println("❌ المستخدم غير موجود.");
            return;
        }

        List<?> records = borrowService.getBorrowRecordsForUser(user);
        if (records.isEmpty()) {
            System.out.println("لا توجد استعارات حالية.");
        } else {
            System.out.println("📌 استعارات المستخدم:");
            records.forEach(System.out::println);
        }
    }
}
