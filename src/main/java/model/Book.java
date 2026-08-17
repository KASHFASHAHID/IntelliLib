package model;

public class Book {

    private String isbn;
    private String title;
    private String categoryName;
    private String publisher;
    private String edition;
    private String language;
    private int publicationYear;
    private String authors;
    private int availableCopies;
    private int totalCopies;

    public Book(String isbn, String title, String categoryName,
                String publisher, String edition, String language,
                int publicationYear, String authors,
                int availableCopies, int totalCopies) {

        this.isbn = isbn;
        this.title = title;
        this.categoryName = categoryName;
        this.publisher = publisher;
        this.edition = edition;
        this.language = language;
        this.publicationYear = publicationYear;
        this.authors = authors;
        this.availableCopies = availableCopies;
        this.totalCopies = totalCopies;
    }

    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getCategoryName() { return categoryName; }
    public String getPublisher() { return publisher; }
    public String getEdition() { return edition; }
    public String getLanguage() { return language; }
    public int getPublicationYear() { return publicationYear; }
    public String getAuthors() { return authors; }
    public int getAvailableCopies() { return availableCopies; }
    public int getTotalCopies() { return totalCopies; }
}