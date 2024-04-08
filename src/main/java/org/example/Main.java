package org.example;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

interface BorrowReturnActions {
    void borrowBook(Book book, LocalDate borrowDate);
    void returnBook(Book book, LocalDate returnDate);
}

class Book {
    private String title;
    private String author;
    private int year;
    private LocalDate borrowDate;
    private LocalDate returnDate;

    public Book(String title, String author, int year) {
        this.title = title;
        this.author = author;
        this.year = year;
    }

    // Getter và setter cho borrowDate và returnDate
    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    // Getter cho title, author, và year
    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getYear() {
        return year;
    }

    // Override phương thức toString để hiển thị thông tin của sách
    @Override
    public String toString() {
        return title + " by " + author + " (" + year + ")";
    }
}

class EBook extends Book {
    private String fileFormat;
    private double fileSize;
    private boolean isBorrowed;

    public EBook(String title, String author, int year, String fileFormat, double fileSize) {
        super(title, author, year);
        this.fileFormat = fileFormat;
        this.fileSize = fileSize;
        this.isBorrowed = false;
    }

    // Getter và setter cho fileFormat và fileSize
    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public double getFileSize() {
        return fileSize;
    }

    public void setFileSize(double fileSize) {
        this.fileSize = fileSize;
    }

    // Phương thức để kiểm tra trạng thái của sách điện tử (đã mượn hay chưa)
    public boolean isBorrowed() {
        return isBorrowed;
    }

    public void setBorrowed(boolean borrowed) {
        isBorrowed = borrowed;
    }

    // Override phương thức toString để hiển thị thông tin của sách điện tử
    @Override
    public String toString() {
        return super.toString() + "\nFile Format: " + fileFormat + "\nFile Size: " + fileSize + " MB";
    }
}

class Library implements BorrowReturnActions {
    private ArrayList<Book> books;

    public Library() {
        this.books = new ArrayList<>();
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public void removeBook(Book book) {
        books.remove(book);
    }

    public void displayBooks() {
        System.out.println("Library Books:");
        for (Book book : books) {
            System.out.println(book);
        }
    }

    public ArrayList<Book> getBooks() {
        return books;
    }

    // Method to find a book by its title
    public Book findBookByTitle(String title) {
        for (Book book : books) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                return book;
            }
        }
        return null;
    }

    // Implementing interface methods
    @Override
    public void borrowBook(Book book, LocalDate borrowDate) {
        if (books.contains(book)) {
            book.setBorrowDate(borrowDate);
            System.out.println("Book borrowed: " + book.getTitle());
        } else {
            System.out.println("Book not available for borrowing: " + book.getTitle());
        }
    }

    @Override
    public void returnBook(Book book, LocalDate returnDate) {
        if (books.contains(book)) {
            book.setReturnDate(returnDate);
            System.out.println("Book returned: " + book.getTitle());
        } else {
            System.out.println("Book not borrowed: " + book.getTitle());
        }
    }
}
public class Main {
    private static final String FILE_PATH = "library_data.txt";
    private Library library;

    public Main() {
        this.library = new Library();
        loadLibraryData();
    }
    private void loadLibraryData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String title = parts[0];
                String author = parts[1];
                int year = Integer.parseInt(parts[2]);
                boolean isEbook = Boolean.parseBoolean(parts[3]);
                if (isEbook) {
                    String fileFormat = parts[4];
                    double fileSize = Double.parseDouble(parts[5]);
                    library.addBook(new EBook(title, author, year, fileFormat, fileSize));
                } else {
                    library.addBook(new Book(title, author, year));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading library data from file: " + e.getMessage());
        }
    }

    // Method to save library data to file
    private void saveLibraryData() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Book book : library.getBooks()) {
                if (book instanceof EBook) {
                    EBook ebook = (EBook) book;
                    writer.write(ebook.getTitle() + "," + ebook.getAuthor() + "," + ebook.getYear() + ",true," +
                            ebook.getFileFormat() + "," + ebook.getFileSize());
                } else {
                    writer.write(book.getTitle() + "," + book.getAuthor() + "," + book.getYear() + ",false");
                }
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving library data to file: " + e.getMessage());
        }
    }

    // Method to display menu options
    private void displayMenu() {
        System.out.println("\nLibrary Management System");
        System.out.println("1. Add Book");
        System.out.println("2. Borrow Book");
        System.out.println("3. Return Book");
        System.out.println("4. Display Books");
        System.out.println("5. Exit");
        System.out.print("Choose an option: ");
    }

    private void performAction(int choice) {
        Scanner scanner = new Scanner(System.in);
        switch (choice) {
            case 1:
                System.out.print("Enter title: ");
                String title = scanner.nextLine();
                System.out.print("Enter author: ");
                String author = scanner.nextLine();
                System.out.print("Enter year: ");
                int year = scanner.nextInt();
                scanner.nextLine(); // consume newline character
                System.out.print("Is it an EBook? (true/false): ");
                boolean isEbook = scanner.nextBoolean();
                scanner.nextLine(); // consume newline character
                if (isEbook) {
                    System.out.print("Enter file format: ");
                    String fileFormat = scanner.nextLine();
                    System.out.print("Enter file size (MB): ");
                    double fileSize = scanner.nextDouble();
                    scanner.nextLine(); // consume newline character
                    library.addBook(new EBook(title, author, year, fileFormat, fileSize));
                } else {
                    library.addBook(new Book(title, author, year));
                }
                saveLibraryData();
                break;
            case 2:
                System.out.print("Enter the title of the book to borrow: ");
                String borrowTitle = scanner.nextLine();
                Book borrowBook = library.findBookByTitle(borrowTitle);
                if (borrowBook != null) {
                    library.borrowBook(borrowBook, LocalDate.now());
                } else {
                    System.out.println("Book not found in library.");
                }
                break;
            case 3:
                System.out.print("Enter the title of the book to return: ");
                String returnTitle = scanner.nextLine();
                Book returnBook = library.findBookByTitle(returnTitle);
                if (returnBook != null) {
                    library.returnBook(returnBook, LocalDate.now());
                } else {
                    System.out.println("Book not found in library.");
                }
                break;
            case 4:
                library.displayBooks();
                break;
            case 5:
                saveLibraryData();
                System.out.println("Exiting...");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid option. Please choose again.");
        }
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            displayMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline character
            performAction(choice);
        }
    }

    public static void main(String[] args) {
        Main app = new Main();
        app.run();

    }
}