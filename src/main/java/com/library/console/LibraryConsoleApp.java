package com.library.console;

import com.library.communication.EmailServer;
import com.library.communication.SMTPEmailServer;
import com.library.model.Media;
import com.library.model.User;
import com.library.notifications.ConsoleNotifier;
import com.library.notifications.EmailNotifier;
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

        FileAdminRepository adminRepo = new FileAdminRepository("admins.txt");
        FileMediaRepository mediaRepo = new FileMediaRepository("media.txt");
        UserRepository userRepo = new FileUserRepository("users.txt");
        FileBorrowRepository borrowRepo = new FileBorrowRepository("borrow.txt", mediaRepo);

        AuthService authService = new AuthService(adminRepo);
        MediaService mediaService = new MediaService(mediaRepo);
        BorrowService borrowService = new BorrowService(mediaRepo, borrowRepo);
        FineService fineService = new FineService(userRepo);
        UserService userService = new UserService(userRepo);

        ReminderService reminderService = new ReminderService(borrowService);

        EmailServer emailServer = new SMTPEmailServer(
                "smtp.gmail.com",
                587,
                "saa123sud53@gmail.com",
                "wdpn kzsp dcjs lfzd"
        );

        reminderService.addObserver(new EmailNotifier(emailServer));
        reminderService.addObserver(new ConsoleNotifier());

        boolean exit = false;
        System.out.println("===== Library Management System =====");

        while (!exit) {
            printMenu(authService.isLoggedIn());
            System.out.print("Choose option: ");
            String choice = scanner.nextLine().trim();
            System.out.println();

            if (!authService.isLoggedIn()) {
                switch (choice) {
                    case "1" -> handleLogin(authService);
                    case "2" -> handleRegister(userRepo);
                    case "3" -> handleSearch(mediaService);
                    case "4" -> exit = true;
                    default -> System.out.println("❌ Invalid option.");
                }
            } else {
                switch (choice) {
                    case "1" -> handleAddMedia(mediaService);
                    case "2" -> handleBorrow(borrowService, userRepo);
                    case "3" -> handleReturn(borrowService);
                    case "4" -> handlePayFine(fineService);
                    case "5" -> handleUnregister(userService, borrowService);
                    case "6" -> handleSendReminders(reminderService, emailServer);
                    case "7" -> handleViewBorrowed(borrowService, userRepo);
                    case "8" -> {
                        authService.logout();
                        System.out.println("✅ Logged out successfully.");
                    }
                    case "9" -> exit = true;
                    default -> System.out.println("❌ Invalid option.");
                }
            }
            System.out.println();
        }

        System.out.println("System closed.");
    }

    private static void printMenu(boolean loggedIn) {
        System.out.println("----------------------------------");
        if (!loggedIn) {
            System.out.println("1) Admin Login");
            System.out.println("2) Register User");
            System.out.println("3) Search Media");
            System.out.println("4) Exit");
        } else {
            System.out.println("1) Add Media");
            System.out.println("2) Borrow Media");
            System.out.println("3) Return Media");
            System.out.println("4) Pay Fine");
            System.out.println("5) Unregister User");
            System.out.println("6) Send Overdue Email Reminders");
            System.out.println("7) View User Borrowed Items");
            System.out.println("8) Logout");
            System.out.println("9) Exit");
        }
    }

    private static String readText(String msg) {
        System.out.print(msg);
        return scanner.nextLine().trim();
    }

    private static int readInt(String msg) {
        while (true) {
            try {
                return Integer.parseInt(readText(msg));
            } catch (NumberFormatException e) {
                System.out.println("❌ Enter a valid number.");
            }
        }
    }

    private static double readDouble(String msg) {
        while (true) {
            try {
                return Double.parseDouble(readText(msg));
            } catch (NumberFormatException e) {
                System.out.println("❌ Enter a valid number.");
            }
        }
    }

    private static void handleLogin(AuthService authService) {
        boolean success = authService.login(
                readText("Username: "),
                readText("Password: ")
        );
        System.out.println(success ? "✅ Login successful." : "❌ Login failed.");
    }

    private static void handleRegister(UserRepository userRepo) {
        try {
            User user = new User(
                    readText("Username: "),
                    readDouble("Initial fine: ")
            );
            user.setEmail(readText("Email: "));
            userRepo.save(user);
            System.out.println("✅ User registered successfully.");
        } catch (Exception e) {
            System.out.println("❌ Failed to register user.");
        }
    }

    private static void handleAddMedia(MediaService mediaService) {
        try {
            String type = readText("1) Book  2) CD : ");
            String title = readText("Title: ");
            int copies = readInt("Copies: ");

            if (type.equals("1")) {
                mediaService.addBook(
                        title,
                        readText("Author: "),
                        readText("ISBN: "),
                        copies
                );
                System.out.println("✅ Book added successfully.");
            } else if (type.equals("2")) {
                mediaService.addCD(
                        title,
                        readText("Artist: "),
                        copies
                );
                System.out.println("✅ CD added successfully.");
            } else {
                System.out.println("❌ Invalid media type.");
            }
        } catch (Exception e) {
            System.out.println("❌ Failed to add media.");
        }
    }

    private static void handleSearch(MediaService mediaService) {
        List<Media> results = mediaService.searchByTitle(readText("Search: "));
        if (results.isEmpty()) {
            System.out.println("❌ No results found.");
        } else {
            results.forEach(System.out::println);
        }
    }

    private static void handleBorrow(BorrowService borrowService, UserRepository userRepo) {
        try {
            User user = userRepo.findByUsername(readText("Username: "));
            borrowService.borrow(user, readInt("Media ID: "), LocalDate.now());
            System.out.println("✅ Borrow successful.");
        } catch (Exception e) {
            System.out.println("❌ Borrow failed.");
        }
    }

    private static void handleReturn(BorrowService borrowService) {
        try {
            borrowService.returnItem(
                    readText("Username: "),
                    readInt("Media ID: "),
                    LocalDate.now()
            );
            System.out.println("✅ Return successful.");
        } catch (Exception e) {
            System.out.println("❌ Return failed.");
        }
    }

    private static void handlePayFine(FineService fineService) {
        boolean success = fineService.payFine(
                readText("Username: "),
                readDouble("Amount: ")
        );
        System.out.println(success ? "✅ Fine paid successfully." : "❌ Fine payment failed.");
    }

    private static void handleUnregister(UserService userService, BorrowService borrowService) {
        try {
            userService.unregister(
                    new User(readText("Admin: "), 0),
                    new User(readText("User: "), 0),
                    borrowService
            );
            System.out.println("✅ User unregistered successfully.");
        } catch (Exception e) {
            System.out.println("❌ Cannot unregister user.");
        }
    }

    private static void handleSendReminders(ReminderService reminderService, EmailServer emailServer) {
        int count = reminderService.sendOverdueReminders(LocalDate.now());

        if (count == 0) {
            emailServer.send(
                    new com.library.model.EmailMessage(
                            "saa123sud53@gmail.com",
                            "No overdue items in the library."
                    )
            );
            System.out.println("📧 No overdue items email sent.");
        } else {
            System.out.println("📧 Overdue reminder emails sent: " + count);
        }
    }

    private static void handleViewBorrowed(BorrowService borrowService, UserRepository userRepo) {
        try {
            User user = userRepo.findByUsername(readText("Username: "));
            List<?> records = borrowService.getBorrowRecordsForUser(user);
            if (records.isEmpty()) {
                System.out.println("No borrowed items.");
            } else {
                records.forEach(System.out::println);
            }
        } catch (Exception e) {
            System.out.println("❌ Failed to load borrowed items.");
        }
    }
}

