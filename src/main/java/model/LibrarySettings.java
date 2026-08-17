package model;

import java.math.BigDecimal;

public class LibrarySettings {

    private boolean examMode;

    private int studentNormalDays;

    private int studentExamDays;

    private int teacherDays;

    private int studentMaxBooks;

    private int teacherMaxBooks;

    private int reservationPickupDays;

    private BigDecimal finePerDay;
    public LibrarySettings(
            boolean examMode,
            int studentNormalDays,
            int studentExamDays,
            int teacherDays,
            int studentMaxBooks,
            int teacherMaxBooks,
            int reservationPickupDays,
            BigDecimal finePerDay
    ) {

        this.examMode = examMode;
        this.studentNormalDays = studentNormalDays;
        this.studentExamDays = studentExamDays;
        this.teacherDays = teacherDays;
        this.studentMaxBooks = studentMaxBooks;
        this.teacherMaxBooks = teacherMaxBooks;
        this.reservationPickupDays = reservationPickupDays;
        this.finePerDay = finePerDay;
    }
    public boolean isExamMode() {
        return examMode;
    }

    public int getStudentNormalDays() {
        return studentNormalDays;
    }

    public int getStudentExamDays() {
        return studentExamDays;
    }

    public int getTeacherDays() {
        return teacherDays;
    }

    public int getStudentMaxBooks() {
        return studentMaxBooks;
    }

    public int getTeacherMaxBooks() {
        return teacherMaxBooks;
    }

    public int getReservationPickupDays() {
        return reservationPickupDays;
    }

    public BigDecimal getFinePerDay() {
        return finePerDay;
    }
    }