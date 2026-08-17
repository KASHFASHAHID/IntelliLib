package model;

import java.time.LocalDateTime;

public class PasswordResetOtp {

    private final int resetId;
    private final String userId;
    private final String otpHash;
    private final LocalDateTime expiresAt;
    private final int attemptCount;
    private final boolean verified;
    private final boolean used;

    public PasswordResetOtp(
            int resetId,
            String userId,
            String otpHash,
            LocalDateTime expiresAt,
            int attemptCount,
            boolean verified,
            boolean used
    ) {

        this.resetId = resetId;
        this.userId = userId;
        this.otpHash = otpHash;
        this.expiresAt = expiresAt;
        this.attemptCount = attemptCount;
        this.verified = verified;
        this.used = used;
    }

    public int getResetId() {
        return resetId;
    }

    public String getUserId() {
        return userId;
    }

    public String getOtpHash() {
        return otpHash;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public boolean isVerified() {
        return verified;
    }

    public boolean isUsed() {
        return used;
    }
}