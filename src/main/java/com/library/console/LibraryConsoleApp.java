package com.library.console;

import com.library.model.Book;
import com.library.repository.FileAdminRepository;
import com.library.repository.InMemoryBookRepository;
import com.library.service.AuthService;
import com.library.service.BookService;

import java.util.List;
import java.util.Scanner;

public class LibraryConsoleApp {

    public static void main(String[] args) {
        FileAdminRepository adminRepository = new FileAdminRepository("admins.txt");
        AuthService authService = new AuthService(adminRepository);
        InMemoryBookRepository bookRepository = new InMemoryBookRepository();
        BookService bookService = new BookService(bookRepository);

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        System.out.println("===== Library Management System (Console) =====");

        while (!exit) {
            printMenu(authService.isLoggedIn());
            System.out.print("اختر خيار: ");

            String input = scanner.nextLine();
            switch (input) {
                case "1":
                    if (!authService.isLoggedIn()) {
                        handleLogin(scanner, authService);
                    } else {
                        handleAddBook(scanner, bookService, authService);
                    }
                    break;
                case "2":
                    if (!authService.isLoggedIn()) {
                        handleSearch(scanner, bookService);
                    } else {
                        handleLogout(authService);
                    }
                    break;
                case "3":
                    if (!authService.isLoggedIn()) {
                        exit = true;
                    } else {
                        handleSearch(scanner, bookService);
                    }
                    break;
                case "4":
                    if (authService.isLoggedIn()) {
                        exit = true;
                    } else {
                        System.out.println("خيار غير صحيح.");
                    }
                    break;
                default:
                    System.out.println("خيار غير صحيح، حاول مرة أخرى.");
            }
            System.out.println();
        }

        System.out.println("تم إغلاق النظام، إلى اللقاء.");
    }

    private static void printMenu(boolean loggedIn) {
        System.out.println("----------------------------------------------");
        if (!loggedIn) {
            System.out.println("1) Admin Login");
            System.out.println("2) Search Book");
            System.out.println("3) Exit");
        } else {
            System.out.println("** Logged in as admin **");
            System.out.println("1) Add Book");
            System.out.println("2) Admin Logout");
            System.out.println("3) Search Book");
            System.out.println("4) Exit");
        }
    }

    private static void handleLogin(Scanner scanner, AuthService authService) {
        System.out.print("اسم المستخدم: ");
        String username = scanner.nextLine();
        System.out.print("كلمة المرور: ");
        String password = scanner.nextLine();

        boolean success = authService.login(username, password);
        if (success) {
            System.out.println("تسجيل الدخول ناجح.");
        } else {
            System.out.println("بيانات دخول غير صحيحة.");
        }
    }

    private static void handleLogout(AuthService authService) {
        authService.logout();
        System.out.println("تم تسجيل الخروج.");
    }

    private static void handleAddBook(Scanner scanner,
                                      BookService bookService,
                                      AuthService authService) {
        if (!authService.isLoggedIn()) {
            System.out.println("يجب تسجيل الدخول كـ Admin لإضافة كتاب.");
            return;
        }

        System.out.print("عنوان الكتاب: ");
        String title = scanner.nextLine();
        System.out.print("المؤلف: ");
        String author = scanner.nextLine();
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine();

        try {
            Book book = bookService.addBook(title, author, isbn);
            System.out.println("تمت إضافة الكتاب بنجاح: " + book);
        } catch (IllegalArgumentException ex) {
            System.out.println("خطأ: " + ex.getMessage());
        }
    }

    private static void handleSearch(Scanner scanner, BookService bookService) {
        System.out.println("اختر طريقة البحث:");
        System.out.println("1) بالعنوان");
        System.out.println("2) بالمؤلف");
        System.out.println("3) بالـ ISBN");
        System.out.print("اختيار: ");
        String choice = scanner.nextLine();

        switch (choice) {
            case "1":
                System.out.print("أدخل جزء من العنوان: ");
                String title = scanner.nextLine();
                List<Book> byTitle = bookService.searchByTitle(title);
                printBooks(byTitle);
                break;
            case "2":
                System.out.print("أدخل اسم المؤلف أو جزء منه: ");
                String author = scanner.nextLine();
                List<Book> byAuthor = bookService.searchByAuthor(author);
                printBooks(byAuthor);
                break;
            case "3":
                System.out.print("أدخل ISBN: ");
                String isbn = scanner.nextLine();
                Book byIsbn = bookService.searchByIsbn(isbn);
                if (byIsbn != null) {
                    System.out.println(byIsbn);
                } else {
                    System.out.println("لا يوجد كتاب بهذا الـ ISBN.");
                }
                break;
            default:
                System.out.println("خيار غير صحيح.");
        }
    }

    private static void printBooks(List<Book> books) {
        if (books.isEmpty()) {
            System.out.println("لا توجد نتائج.");
        } else {
            for (Book book : books) {
                System.out.println(book);
            }
        }
    }
}
