package model;

import java.math.BigDecimal;

public class DashboardStatistics {

    private int totalBookTitles;
    private int totalBookCopies;
    private int availableCopies;
    private int issuedCopies;
    private int overdueLoans;
    private int activeMembers;
    private int pendingMembershipRequests;
    private int activeReservations;
    private BigDecimal pendingFineAmount;

    public DashboardStatistics() {
        pendingFineAmount = BigDecimal.ZERO;
    }

    public int getTotalBookTitles() {
        return totalBookTitles;
    }

    public void setTotalBookTitles(int totalBookTitles) {
        this.totalBookTitles = totalBookTitles;
    }

    public int getTotalBookCopies() {
        return totalBookCopies;
    }

    public void setTotalBookCopies(int totalBookCopies) {
        this.totalBookCopies = totalBookCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }

    public int getIssuedCopies() {
        return issuedCopies;
    }

    public void setIssuedCopies(int issuedCopies) {
        this.issuedCopies = issuedCopies;
    }

    public int getOverdueLoans() {
        return overdueLoans;
    }

    public void setOverdueLoans(int overdueLoans) {
        this.overdueLoans = overdueLoans;
    }

    public int getActiveMembers() {
        return activeMembers;
    }

    public void setActiveMembers(int activeMembers) {
        this.activeMembers = activeMembers;
    }

    public int getPendingMembershipRequests() {
        return pendingMembershipRequests;
    }

    public void setPendingMembershipRequests(
            int pendingMembershipRequests
    ) {
        this.pendingMembershipRequests =
                pendingMembershipRequests;
    }

    public int getActiveReservations() {
        return activeReservations;
    }

    public void setActiveReservations(
            int activeReservations
    ) {
        this.activeReservations = activeReservations;
    }

    public BigDecimal getPendingFineAmount() {
        return pendingFineAmount;
    }

    public void setPendingFineAmount(
            BigDecimal pendingFineAmount
    ) {
        this.pendingFineAmount =
                pendingFineAmount == null
                        ? BigDecimal.ZERO
                        : pendingFineAmount;
    }
}