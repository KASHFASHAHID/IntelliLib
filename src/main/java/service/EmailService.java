package service;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import model.Role;

public class EmailService {

    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";

    private final String senderEmail;
    private final String appPassword;

    public EmailService() {
        senderEmail = System.getenv("MAIL_USERNAME");
        appPassword = System.getenv("MAIL_PASSWORD");
    }

    public boolean isConfigured() {

        return senderEmail != null
                && !senderEmail.isBlank()
                && appPassword != null
                && !appPassword.isBlank();
    }

    /*
     * ============================================================
     * PASSWORD RESET OTP
     * ============================================================
     */

    public boolean sendPasswordResetOtp(
            String recipientEmail,
            String recipientName,
            String otp
    ) {

        if (recipientEmail == null || recipientEmail.isBlank()) {
            return false;
        }

        if (otp == null || otp.isBlank()) {
            return false;
        }

        if (!isConfigured()) {

            System.err.println(
                    "Email configuration is missing. "
                            + "Check MAIL_USERNAME and MAIL_PASSWORD."
            );

            return false;
        }

        Properties properties = createMailProperties();

        Session session = createMailSession(properties);

        try {

            MimeMessage message =
                    new MimeMessage(session);

            message.setFrom(
                    new InternetAddress(
                            senderEmail,
                            "IntelliLib"
                    )
            );

            message.setRecipient(
                    Message.RecipientType.TO,
                    new InternetAddress(recipientEmail)
            );

            message.setSubject(
                    "IntelliLib - Password Reset OTP",
                    "UTF-8"
            );

            String safeName =
                    recipientName == null || recipientName.isBlank()
                            ? "Library Member"
                            : recipientName;

            String emailBody = """
                    <html>
                    <body style="font-family: Arial, sans-serif;
                                 background-color: #f1f5f9;
                                 padding: 30px;">

                        <div style="max-width: 560px;
                                    margin: auto;
                                    background-color: white;
                                    padding: 32px;
                                    border-radius: 14px;
                                    border: 1px solid #e2e8f0;">

                            <h2 style="color: #0f172a;">
                                IntelliLib
                            </h2>

                            <p>Hello %s,</p>

                            <p>
                                We received a request to reset your
                                library account password.
                            </p>

                            <p>Your verification OTP is:</p>

                            <div style="font-size: 32px;
                                        font-weight: bold;
                                        letter-spacing: 8px;
                                        color: #2563eb;
                                        margin: 24px 0;">
                                %s
                            </div>

                            <p>
                                This OTP is valid for 5 minutes.
                            </p>

                            <p>
                                Do not share this OTP with anyone.
                            </p>

                            <p style="color: #64748b;
                                      margin-top: 28px;">
                                If you did not request a password reset,
                                you can safely ignore this email.
                            </p>

                            <p>
                                IntelliLib Team
                            </p>

                        </div>
                    </body>
                    </html>
                    """.formatted(
                    safeName,
                    otp
            );

            message.setContent(
                    emailBody,
                    "text/html; charset=UTF-8"
            );

            Transport.send(message);

            return true;

        } catch (Exception exception) {

            System.err.println(
                    "OTP email could not be sent: "
                            + exception.getMessage()
            );

            exception.printStackTrace();

            return false;
        }
    }

    /*
     * ============================================================
     * MEMBERSHIP APPROVAL
     * ============================================================
     */

    public boolean sendMembershipApprovalEmail(
            String recipientEmail,
            String recipientName,
            String userId,
            String cardNumber
    ) {

        if (recipientEmail == null || recipientEmail.isBlank()
                || userId == null || userId.isBlank()
                || cardNumber == null || cardNumber.isBlank()) {

            return false;
        }

        if (!isConfigured()) {

            System.err.println(
                    "Email configuration is missing. "
                            + "Check MAIL_USERNAME and MAIL_PASSWORD."
            );

            return false;
        }

        Session session =
                createMailSession(createMailProperties());

        try {

            MimeMessage message =
                    new MimeMessage(session);

            message.setFrom(
                    new InternetAddress(
                            senderEmail,
                            "IntelliLib"
                    )
            );

            message.setRecipient(
                    Message.RecipientType.TO,
                    new InternetAddress(recipientEmail)
            );

            message.setSubject(
                    "IntelliLib - Membership Approved",
                    "UTF-8"
            );

            String safeName =
                    recipientName == null || recipientName.isBlank()
                            ? "Library Member"
                            : recipientName;

            String emailBody = """
                    <html>
                    <body style="font-family:Arial,sans-serif;
                                 background:#f1f5f9;
                                 padding:30px;">

                        <div style="max-width:580px;
                                    margin:auto;
                                    background:white;
                                    padding:32px;
                                    border-radius:14px;
                                    border:1px solid #e2e8f0;">

                            <h2 style="color:#0f172a;">
                                IntelliLib
                            </h2>

                            <p>Hello %s,</p>

                            <p>
                                Congratulations! Your membership request
                                has been approved.
                            </p>

                            <div style="background:#eff6ff;
                                        padding:20px;
                                        border-radius:10px;
                                        margin:22px 0;">

                                <p>
                                    <strong>User ID:</strong> %s
                                </p>

                                <p>
                                    <strong>Library Card:</strong> %s
                                </p>

                            </div>

                            <p>
                                Your account is currently waiting for
                                activation.
                            </p>

                            <p>
                                Open the IntelliLib application,
                                choose <strong>Account Access</strong>,
                                and then select
                                <strong>Activate New Account</strong>.
                            </p>

                            <p>
                                You will receive an OTP and create your own
                                secure password. Library staff cannot view
                                your password.
                            </p>

                            <p style="color:#64748b;
                                      margin-top:28px;">
                                IntelliLib Team
                            </p>

                        </div>
                    </body>
                    </html>
                    """.formatted(
                    safeName,
                    userId,
                    cardNumber
            );

            message.setContent(
                    emailBody,
                    "text/html; charset=UTF-8"
            );

            Transport.send(message);

            return true;

        } catch (Exception exception) {

            System.err.println(
                    "Membership approval email could not be sent: "
                            + exception.getMessage()
            );

            exception.printStackTrace();

            return false;
        }
    }

    /*
     * ============================================================
     * ACCOUNT ACTIVATION OTP
     * ============================================================
     */

    public boolean sendAccountActivationOtp(
            String recipientEmail,
            String recipientName,
            String otp
    ) {

        if (recipientEmail == null || recipientEmail.isBlank()
                || otp == null || !otp.matches("\\d{6}")) {

            return false;
        }

        if (!isConfigured()) {

            System.err.println(
                    "Email configuration is missing. "
                            + "Check MAIL_USERNAME and MAIL_PASSWORD."
            );

            return false;
        }

        Session session =
                createMailSession(createMailProperties());

        try {

            MimeMessage message =
                    new MimeMessage(session);

            message.setFrom(
                    new InternetAddress(
                            senderEmail,
                            "IntelliLib"
                    )
            );

            message.setRecipient(
                    Message.RecipientType.TO,
                    new InternetAddress(recipientEmail)
            );

            message.setSubject(
                    "IntelliLib - Account Activation OTP",
                    "UTF-8"
            );

            String safeName =
                    recipientName == null || recipientName.isBlank()
                            ? "Library Member"
                            : recipientName;

            String emailBody = """
                    <html>
                    <body style="font-family:Arial,sans-serif;
                                 background:#f1f5f9;
                                 padding:30px;">

                        <div style="max-width:560px;
                                    margin:auto;
                                    background:white;
                                    padding:32px;
                                    border-radius:14px;
                                    border:1px solid #e2e8f0;">

                            <h2 style="color:#0f172a;">
                                IntelliLib
                            </h2>

                            <p>Hello %s,</p>

                            <p>
                                Use the following OTP to activate your
                                new library account:
                            </p>

                            <div style="font-size:32px;
                                        font-weight:bold;
                                        letter-spacing:8px;
                                        color:#2563eb;
                                        margin:24px 0;">
                                %s
                            </div>

                            <p>
                                This OTP is valid for 5 minutes.
                            </p>

                            <p>
                                After verification, you will create your
                                own password. Library staff cannot view it.
                            </p>

                            <p>
                                Do not share this OTP with anyone.
                            </p>

                            <p style="color:#64748b;
                                      margin-top:28px;">
                                IntelliLib Team
                            </p>

                        </div>
                    </body>
                    </html>
                    """.formatted(
                    safeName,
                    otp
            );

            message.setContent(
                    emailBody,
                    "text/html; charset=UTF-8"
            );

            Transport.send(message);

            return true;

        } catch (Exception exception) {

            System.err.println(
                    "Account activation OTP email could not be sent: "
                            + exception.getMessage()
            );

            exception.printStackTrace();

            return false;
        }
    }

    /*
     * ============================================================
     * MEMBER ACCOUNT STATUS
     * ============================================================
     */

    public boolean sendAccountStatusEmail(
            String recipientEmail,
            String recipientName,
            String userId,
            String accountStatus,
            String reason
    ) {

        if (recipientEmail == null
                || recipientEmail.isBlank()
                || userId == null
                || userId.isBlank()
                || accountStatus == null
                || accountStatus.isBlank()
                || reason == null
                || reason.isBlank()) {

            return false;
        }

        if (!isConfigured()) {

            System.err.println(
                    "Email configuration is missing. "
                            + "Check MAIL_USERNAME and MAIL_PASSWORD."
            );

            return false;
        }

        String normalizedStatus =
                accountStatus.trim().toUpperCase();

        if (!normalizedStatus.equals("SUSPENDED")
                && !normalizedStatus.equals("BLOCKED")
                && !normalizedStatus.equals("ACTIVE")) {

            return false;
        }

        Session session =
                createMailSession(createMailProperties());

        try {

            MimeMessage message =
                    new MimeMessage(session);

            message.setFrom(
                    new InternetAddress(
                            senderEmail,
                            "IntelliLib"
                    )
            );

            message.setRecipient(
                    Message.RecipientType.TO,
                    new InternetAddress(
                            recipientEmail.trim()
                    )
            );

            String safeName =
                    recipientName == null
                            || recipientName.isBlank()
                            ? "Library Member"
                            : recipientName.trim();

            String subject;
            String heading;
            String statusMessage;
            String statusColor;

            switch (normalizedStatus) {

                case "SUSPENDED" -> {

                    subject =
                            "IntelliLib - Account Suspended";

                    heading =
                            "Library Account Suspended";

                    statusMessage =
                            """
                            Your library account has been temporarily suspended.

                            You may be unable to borrow or reserve new books until the issue is resolved.
                            """;

                    statusColor =
                            "#d97706";
                }

                case "BLOCKED" -> {

                    subject =
                            "IntelliLib - Account Blocked";

                    heading =
                            "Library Account Blocked";

                    statusMessage =
                            """
                            Your library account has been blocked.

                            Login, borrowing and reservation access are currently restricted.
                            """;

                    statusColor =
                            "#dc2626";
                }

                case "ACTIVE" -> {

                    subject =
                            "IntelliLib - Account Reactivated";

                    heading =
                            "Library Account Reactivated";

                    statusMessage =
                            """
                            Your library account has been reactivated successfully.

                            You can now access the available library services again.
                            """;

                    statusColor =
                            "#16a34a";
                }

                default -> {
                    return false;
                }
            }

            message.setSubject(
                    subject,
                    "UTF-8"
            );

            String emailBody = """
                    <html>
                    <body style="font-family:Arial,sans-serif;
                                 background-color:#f1f5f9;
                                 padding:30px;">

                        <div style="max-width:580px;
                                    margin:auto;
                                    background-color:white;
                                    padding:32px;
                                    border-radius:14px;
                                    border:1px solid #e2e8f0;">

                            <h2 style="color:#0f172a;">
                                IntelliLib
                            </h2>

                            <p>Hello %s,</p>

                            <h3 style="color:%s;">
                                %s
                            </h3>

                            <div style="background-color:#f8fafc;
                                        border-left:5px solid %s;
                                        padding:18px;
                                        margin:20px 0;
                                        border-radius:8px;">

                                <p>
                                    <strong>User ID:</strong> %s
                                </p>

                                <p>
                                    <strong>Account Status:</strong> %s
                                </p>

                                <p>
                                    <strong>Reason:</strong> %s
                                </p>

                            </div>

                            <p style="white-space:pre-line;">
                                %s
                            </p>

                            <p>
                                Please contact the library administration
                                if you need further assistance.
                            </p>

                            <p style="color:#64748b;
                                      margin-top:28px;">
                                IntelliLib Team
                            </p>

                        </div>

                    </body>
                    </html>
                    """.formatted(
                    safeName,
                    statusColor,
                    heading,
                    statusColor,
                    userId.trim(),
                    normalizedStatus,
                    reason.trim(),
                    statusMessage
            );

            message.setContent(
                    emailBody,
                    "text/html; charset=UTF-8"
            );

            Transport.send(message);

            return true;

        } catch (Exception exception) {

            System.err.println(
                    "Account status email could not be sent: "
                            + exception.getMessage()
            );

            exception.printStackTrace();

            return false;
        }
    }

    /*
     * ============================================================
     * STAFF ACCOUNT CREATION
     * ============================================================
     */

    public boolean sendStaffAccountCreationEmail(
            String recipientEmail,
            String recipientName,
            String userId,
            Role role
    ) {

        if (recipientEmail == null
                || recipientEmail.isBlank()
                || userId == null
                || userId.isBlank()
                || role == null
                || (role != Role.ADMIN
                && role != Role.LIBRARIAN)) {

            return false;
        }

        if (!isConfigured()) {

            System.err.println(
                    "Email configuration is missing. "
                            + "Check MAIL_USERNAME and MAIL_PASSWORD."
            );

            return false;
        }

        Session session =
                createMailSession(createMailProperties());

        try {

            MimeMessage message =
                    new MimeMessage(session);

            message.setFrom(
                    new InternetAddress(
                            senderEmail,
                            "IntelliLib"
                    )
            );

            message.setRecipient(
                    Message.RecipientType.TO,
                    new InternetAddress(
                            recipientEmail.trim()
                    )
            );

            message.setSubject(
                    "IntelliLib - Staff Account Created",
                    "UTF-8"
            );

            String safeName =
                    recipientName == null
                            || recipientName.isBlank()
                            ? "Staff Member"
                            : recipientName.trim();

            String roleName =
                    role == Role.ADMIN
                            ? "Administrator"
                            : "Librarian";

            String emailBody = """
                    <html>
                    <body style="font-family:Arial,sans-serif;
                                 background-color:#f1f5f9;
                                 padding:30px;">

                        <div style="max-width:580px;
                                    margin:auto;
                                    background-color:white;
                                    padding:32px;
                                    border-radius:14px;
                                    border:1px solid #e2e8f0;">

                            <h2 style="color:#0f172a;">
                                IntelliLib
                            </h2>

                            <p>Hello %s,</p>

                            <p>
                                A new staff account has been created for you
                                in the IntelliLib system.
                            </p>

                            <div style="background-color:#eff6ff;
                                        border-left:5px solid #2563eb;
                                        padding:18px;
                                        margin:22px 0;
                                        border-radius:8px;">

                                <p>
                                    <strong>User ID:</strong> %s
                                </p>

                                <p>
                                    <strong>Role:</strong> %s
                                </p>

                                <p>
                                    <strong>Status:</strong>
                                    PENDING_ACTIVATION
                                </p>

                            </div>

                            <h3 style="color:#0f172a;">
                                Activate your account
                            </h3>

                            <p>
                                1. Open the IntelliLib application.
                            </p>

                            <p>
                                2. Select <strong>Account Access</strong>.
                            </p>

                            <p>
                                3. Select
                                <strong>Activate New Account</strong>.
                            </p>

                            <p>
                                4. Enter your User ID and this email address.
                            </p>

                            <p>
                                5. Verify the OTP sent to your email and create
                                your own secure password.
                            </p>

                            <p>
                                No temporary password has been created.
                                Library staff cannot view your password.
                            </p>

                            <p style="color:#64748b;
                                      margin-top:28px;">
                                IntelliLib Team
                            </p>

                        </div>
                    </body>
                    </html>
                    """.formatted(
                    safeName,
                    userId.trim(),
                    roleName
            );

            message.setContent(
                    emailBody,
                    "text/html; charset=UTF-8"
            );

            Transport.send(message);

            return true;

        } catch (Exception exception) {

            System.err.println(
                    "Staff account creation email could not be sent: "
                            + exception.getMessage()
            );

            exception.printStackTrace();

            return false;
        }
    }

    /*
     * ============================================================
     * STAFF ACCOUNT STATUS
     * ============================================================
     */

    public boolean sendStaffAccountStatusEmail(
            String recipientEmail,
            String recipientName,
            String userId,
            Role role,
            String accountStatus,
            String reason
    ) {

        if (recipientEmail == null
                || recipientEmail.isBlank()
                || userId == null
                || userId.isBlank()
                || role == null
                || accountStatus == null
                || accountStatus.isBlank()
                || reason == null
                || reason.isBlank()) {

            return false;
        }

        if (role != Role.ADMIN
                && role != Role.LIBRARIAN) {

            return false;
        }

        if (!isConfigured()) {

            System.err.println(
                    "Email configuration is missing. "
                            + "Check MAIL_USERNAME and MAIL_PASSWORD."
            );

            return false;
        }

        String normalizedStatus =
                accountStatus.trim().toUpperCase();

        if (!normalizedStatus.equals("SUSPENDED")
                && !normalizedStatus.equals("BLOCKED")
                && !normalizedStatus.equals("ACTIVE")) {

            return false;
        }

        Session session =
                createMailSession(createMailProperties());

        try {

            MimeMessage message =
                    new MimeMessage(session);

            message.setFrom(
                    new InternetAddress(
                            senderEmail,
                            "IntelliLib"
                    )
            );

            message.setRecipient(
                    Message.RecipientType.TO,
                    new InternetAddress(
                            recipientEmail.trim()
                    )
            );

            String safeName =
                    recipientName == null
                            || recipientName.isBlank()
                            ? "Staff Member"
                            : recipientName.trim();

            String roleName =
                    role == Role.ADMIN
                            ? "Administrator"
                            : "Librarian";

            String subject;
            String heading;
            String statusMessage;
            String statusColor;

            switch (normalizedStatus) {

                case "SUSPENDED" -> {

                    subject =
                            "IntelliLib - Staff Account Suspended";

                    heading =
                            roleName + " Account Suspended";

                    statusMessage =
                            """
                            Your staff account has been temporarily suspended.

                            Your administrative login and staff permissions are temporarily restricted.

                            Please contact the Super Administrator for assistance.
                            """;

                    statusColor =
                            "#d97706";
                }

                case "BLOCKED" -> {

                    subject =
                            "IntelliLib - Staff Account Blocked";

                    heading =
                            roleName + " Account Blocked";

                    statusMessage =
                            """
                            Your staff account has been blocked.

                            Login and all authorized staff functions are currently restricted.

                            Please contact the Super Administrator for further information.
                            """;

                    statusColor =
                            "#dc2626";
                }

                case "ACTIVE" -> {

                    subject =
                            "IntelliLib - Staff Account Reactivated";

                    heading =
                            roleName + " Account Reactivated";

                    statusMessage =
                            """
                            Your staff account has been reactivated successfully.

                            You may now sign in and use your authorized library staff functions again.
                            """;

                    statusColor =
                            "#16a34a";
                }

                default -> {
                    return false;
                }
            }

            message.setSubject(
                    subject,
                    "UTF-8"
            );

            String emailBody = """
                    <html>
                    <body style="font-family:Arial,sans-serif;
                                 background-color:#f1f5f9;
                                 padding:30px;">

                        <div style="max-width:580px;
                                    margin:auto;
                                    background-color:white;
                                    padding:32px;
                                    border-radius:14px;
                                    border:1px solid #e2e8f0;">

                            <h2 style="color:#0f172a;">
                                IntelliLib
                            </h2>

                            <p>Hello %s,</p>

                            <h3 style="color:%s;">
                                %s
                            </h3>

                            <div style="background-color:#f8fafc;
                                        border-left:5px solid %s;
                                        padding:18px;
                                        margin:20px 0;
                                        border-radius:8px;">

                                <p>
                                    <strong>User ID:</strong> %s
                                </p>

                                <p>
                                    <strong>Staff Role:</strong> %s
                                </p>

                                <p>
                                    <strong>Account Status:</strong> %s
                                </p>

                                <p>
                                    <strong>Reason:</strong> %s
                                </p>

                            </div>

                            <p style="white-space:pre-line;">
                                %s
                            </p>

                            <p style="color:#64748b;
                                      margin-top:28px;">
                                IntelliLib Team
                            </p>

                        </div>
                    </body>
                    </html>
                    """.formatted(
                    safeName,
                    statusColor,
                    heading,
                    statusColor,
                    userId.trim(),
                    roleName,
                    normalizedStatus,
                    reason.trim(),
                    statusMessage
            );

            message.setContent(
                    emailBody,
                    "text/html; charset=UTF-8"
            );

            Transport.send(message);

            return true;

        } catch (Exception exception) {

            System.err.println(
                    "Staff account status email could not be sent: "
                            + exception.getMessage()
            );

            exception.printStackTrace();

            return false;
        }
    }

    /*
     * ============================================================
     * BOOK READY FOR PICKUP
     * ============================================================
     */

    public boolean sendBookReadyForPickupEmail(
            String recipientEmail,
            String recipientName,
            String bookTitle,
            LocalDate pickupExpiryDate
    ) {

        if (recipientEmail == null
                || recipientEmail.isBlank()
                || bookTitle == null
                || bookTitle.isBlank()
                || pickupExpiryDate == null) {

            return false;
        }

        if (!isConfigured()) {

            System.err.println(
                    "Email configuration is missing. "
                            + "Check MAIL_USERNAME and MAIL_PASSWORD."
            );

            return false;
        }

        Session session =
                createMailSession(createMailProperties());

        try {

            MimeMessage message =
                    new MimeMessage(session);

            message.setFrom(
                    new InternetAddress(
                            senderEmail,
                            "IntelliLib"
                    )
            );

            message.setRecipient(
                    Message.RecipientType.TO,
                    new InternetAddress(
                            recipientEmail.trim()
                    )
            );

            message.setSubject(
                    "IntelliLib - Book Ready for Collection",
                    "UTF-8"
            );

            String safeName =
                    recipientName == null
                            || recipientName.isBlank()
                            ? "Library Member"
                            : recipientName.trim();

            String formattedExpiryDate =
                    pickupExpiryDate.format(
                            DateTimeFormatter.ofPattern(
                                    "dd MMMM yyyy"
                            )
                    );

            String emailBody = """
                    <html>
                    <body style="font-family:Arial,sans-serif;
                                 background-color:#f1f5f9;
                                 padding:30px;">

                        <div style="max-width:580px;
                                    margin:auto;
                                    background-color:white;
                                    padding:32px;
                                    border-radius:14px;
                                    border:1px solid #e2e8f0;">

                            <h2 style="color:#0f172a;">
                                IntelliLib
                            </h2>

                            <p>Hello %s,</p>

                            <h3 style="color:#2563eb;">
                                Your reserved book is ready
                            </h3>

                            <p>
                                The following book is now available
                                for collection:
                            </p>

                            <div style="background-color:#eff6ff;
                                        border-left:5px solid #2563eb;
                                        padding:18px;
                                        margin:22px 0;
                                        border-radius:8px;">

                                <p>
                                    <strong>Book:</strong> %s
                                </p>

                                <p>
                                    <strong>Collect on or before:</strong>
                                    %s
                                </p>

                            </div>

                            <p>
                                Please collect the book from the library
                                before the pickup deadline.
                            </p>

                            <p>
                                If the book is not collected within this
                                period, the reservation will expire
                                automatically and may be offered to the
                                next member in the queue.
                            </p>

                            <p style="color:#64748b;
                                      margin-top:28px;">
                                IntelliLib Team
                            </p>

                        </div>
                    </body>
                    </html>
                    """.formatted(
                    safeName,
                    bookTitle.trim(),
                    formattedExpiryDate
            );

            message.setContent(
                    emailBody,
                    "text/html; charset=UTF-8"
            );

            Transport.send(message);

            return true;

        } catch (Exception exception) {

            System.err.println(
                    "Ready-for-pickup email could not be sent: "
                            + exception.getMessage()
            );

            exception.printStackTrace();

            return false;
        }
    }

    /*
     * ============================================================
     * OVERDUE FINE
     * ============================================================
     */

    public boolean sendOverdueFineEmail(
            String recipientEmail,
            String recipientName,
            String bookTitle,
            LocalDate dueDate,
            long overdueDays,
            java.math.BigDecimal finePerDay,
            java.math.BigDecimal currentFine
    ) {

        if (recipientEmail == null
                || recipientEmail.isBlank()
                || bookTitle == null
                || bookTitle.isBlank()
                || dueDate == null
                || overdueDays <= 0
                || finePerDay == null
                || currentFine == null) {

            return false;
        }

        if (!isConfigured()) {

            System.err.println(
                    "Email configuration is missing. "
                            + "Check MAIL_USERNAME and MAIL_PASSWORD."
            );

            return false;
        }

        Session session =
                createMailSession(createMailProperties());

        try {

            MimeMessage message =
                    new MimeMessage(session);

            message.setFrom(
                    new InternetAddress(
                            senderEmail,
                            "IntelliLib"
                    )
            );

            message.setRecipient(
                    Message.RecipientType.TO,
                    new InternetAddress(
                            recipientEmail.trim()
                    )
            );

            message.setSubject(
                    "IntelliLib - Overdue Book Reminder",
                    "UTF-8"
            );

            String safeName =
                    recipientName == null
                            || recipientName.isBlank()
                            ? "Library Member"
                            : recipientName.trim();

            String formattedDueDate =
                    dueDate.format(
                            DateTimeFormatter.ofPattern(
                                    "dd MMMM yyyy"
                            )
                    );

            String emailBody = """
                    <html>
                    <body style="font-family:Arial,sans-serif;
                                 background-color:#f1f5f9;
                                 padding:30px;">

                        <div style="max-width:580px;
                                    margin:auto;
                                    background-color:white;
                                    padding:32px;
                                    border-radius:14px;
                                    border:1px solid #e2e8f0;">

                            <h2 style="color:#0f172a;">
                                IntelliLib
                            </h2>

                            <p>Hello %s,</p>

                            <h3 style="color:#dc2626;">
                                Overdue Book Reminder
                            </h3>

                            <p>
                                The following borrowed book is overdue:
                            </p>

                            <div style="background-color:#fef2f2;
                                        border-left:5px solid #dc2626;
                                        padding:18px;
                                        margin:22px 0;
                                        border-radius:8px;">

                                <p>
                                    <strong>Book:</strong> %s
                                </p>

                                <p>
                                    <strong>Due Date:</strong> %s
                                </p>

                                <p>
                                    <strong>Overdue Days:</strong> %d
                                </p>

                                <p>
                                    <strong>Fine Per Day:</strong> ₹%s
                                </p>

                                <p>
                                    <strong>Current Estimated Fine:</strong>
                                    ₹%s
                                </p>

                            </div>

                            <p>
                                The estimated fine will continue increasing
                                each day until the book is returned.
                            </p>

                            <p>
                                Once the book is returned, the final fine
                                amount will be frozen.
                            </p>

                            <p>
                                Please return the book as soon as possible.
                            </p>

                            <p style="color:#64748b;
                                      margin-top:28px;">
                                IntelliLib Team
                            </p>

                        </div>
                    </body>
                    </html>
                    """.formatted(
                    safeName,
                    bookTitle.trim(),
                    formattedDueDate,
                    overdueDays,
                    finePerDay.toPlainString(),
                    currentFine.toPlainString()
            );

            message.setContent(
                    emailBody,
                    "text/html; charset=UTF-8"
            );

            Transport.send(message);

            return true;

        } catch (Exception exception) {

            System.err.println(
                    "Overdue fine email could not be sent: "
                            + exception.getMessage()
            );

            exception.printStackTrace();

            return false;
        }
    }

    /*
     * ============================================================
     * PROFILE CONTACT UPDATE
     * ============================================================
     */

    public boolean sendProfileContactUpdateEmail(
            String recipientEmail,
            String recipientName,
            String userId,
            String oldEmail,
            String newEmail,
            String newPhone,
            boolean securityAlert
    ) {

        if (recipientEmail == null
                || recipientEmail.isBlank()
                || userId == null
                || userId.isBlank()
                || newEmail == null
                || newEmail.isBlank()
                || newPhone == null
                || newPhone.isBlank()) {

            return false;
        }

        if (!isConfigured()) {

            System.err.println(
                    "Email configuration is missing. "
                            + "Check MAIL_USERNAME and MAIL_PASSWORD."
            );

            return false;
        }

        Session session =
                createMailSession(createMailProperties());

        try {

            MimeMessage message =
                    new MimeMessage(session);

            message.setFrom(
                    new InternetAddress(
                            senderEmail,
                            "IntelliLib"
                    )
            );

            message.setRecipient(
                    Message.RecipientType.TO,
                    new InternetAddress(
                            recipientEmail.trim()
                    )
            );

            String safeName =
                    recipientName == null
                            || recipientName.isBlank()
                            ? "Library Member"
                            : recipientName.trim();

            String changedAt =
                    LocalDateTime.now()
                            .format(
                                    DateTimeFormatter.ofPattern(
                                            "dd MMMM yyyy, h:mm a"
                                    )
                            );

            String subject;
            String heading;
            String mainMessage;
            String actionMessage;
            String accentColor;

            if (securityAlert) {

                subject =
                        "IntelliLib - Security Alert: Email Address Changed";

                heading =
                        "Security Alert";

                mainMessage =
                        """
                        The registered email address associated with your
                        IntelliLib account was changed.
                        """;

                actionMessage =
                        """
                        If you requested this change, no further action is required.

                        If you did not make this change, contact the library
                        administration immediately and change your account password.
                        """;

                accentColor =
                        "#dc2626";

            } else {

                subject =
                        "IntelliLib - Contact Details Updated";

                heading =
                        "Contact Details Updated";

                mainMessage =
                        """
                        Your registered email address and phone number
                        were updated successfully.
                        """;

                actionMessage =
                        """
                        If you made this change, no further action is required.

                        If you did not request this update, contact the library
                        administration immediately and change your account password.
                        """;

                accentColor =
                        "#16a34a";
            }

            message.setSubject(
                    subject,
                    "UTF-8"
            );

            String emailBody = """
                    <html>
                    <body style="font-family:Arial,sans-serif;
                                 background-color:#f1f5f9;
                                 padding:30px;">

                        <div style="max-width:600px;
                                    margin:auto;
                                    background-color:white;
                                    padding:32px;
                                    border-radius:14px;
                                    border:1px solid #e2e8f0;">

                            <h2 style="color:#0f172a;">
                                IntelliLib
                            </h2>

                            <p>Dear %s,</p>

                            <h3 style="color:%s;">
                                %s
                            </h3>

                            <p style="white-space:pre-line;">
                                %s
                            </p>

                            <div style="background-color:#f8fafc;
                                        border-left:5px solid %s;
                                        padding:18px;
                                        margin:22px 0;
                                        border-radius:8px;">

                                <p>
                                    <strong>User ID:</strong> %s
                                </p>

                                <p>
                                    <strong>Previous Email:</strong> %s
                                </p>

                                <p>
                                    <strong>Registered Email:</strong> %s
                                </p>

                                <p>
                                    <strong>Registered Phone:</strong> %s
                                </p>

                                <p>
                                    <strong>Date and Time:</strong> %s
                                </p>

                            </div>

                            <p style="white-space:pre-line;">
                                %s
                            </p>

                            <p style="color:#64748b;
                                      margin-top:28px;">
                                Regards,<br>
                                IntelliLib Team
                            </p>

                        </div>
                    </body>
                    </html>
                    """.formatted(
                    safeName,
                    accentColor,
                    heading,
                    mainMessage,
                    accentColor,
                    userId.trim(),
                    oldEmail == null || oldEmail.isBlank()
                            ? "Not available"
                            : oldEmail.trim(),
                    newEmail.trim(),
                    newPhone.trim(),
                    changedAt,
                    actionMessage
            );

            message.setContent(
                    emailBody,
                    "text/html; charset=UTF-8"
            );

            Transport.send(message);

            return true;

        } catch (Exception exception) {

            System.err.println(
                    "Profile contact update email could not be sent: "
                            + exception.getMessage()
            );

            exception.printStackTrace();

            return false;
        }
    }

    /*
     * ============================================================
     * RESERVATION EXPIRED
     * ============================================================
     */

    public boolean sendReservationExpiredEmail(
            String recipientEmail,
            String recipientName,
            String bookTitle
    ) {

        if (recipientEmail == null
                || recipientEmail.isBlank()
                || bookTitle == null
                || bookTitle.isBlank()) {

            return false;
        }

        if (!isConfigured()) {

            System.err.println(
                    "Email configuration is missing. "
                            + "Check MAIL_USERNAME and MAIL_PASSWORD."
            );

            return false;
        }

        Session session =
                createMailSession(createMailProperties());

        try {

            MimeMessage message =
                    new MimeMessage(session);

            message.setFrom(
                    new InternetAddress(
                            senderEmail,
                            "IntelliLib"
                    )
            );

            message.setRecipient(
                    Message.RecipientType.TO,
                    new InternetAddress(
                            recipientEmail.trim()
                    )
            );

            message.setSubject(
                    "IntelliLib - Reservation Expired",
                    "UTF-8"
            );

            String safeName =
                    recipientName == null
                            || recipientName.isBlank()
                            ? "Library Member"
                            : recipientName.trim();

            String emailBody = """
                    <html>
                    <body style="font-family:Arial,sans-serif;
                                 background-color:#f1f5f9;
                                 padding:30px;">

                        <div style="max-width:580px;
                                    margin:auto;
                                    background-color:white;
                                    padding:32px;
                                    border-radius:14px;
                                    border:1px solid #e2e8f0;">

                            <h2 style="color:#0f172a;">
                                IntelliLib
                            </h2>

                            <p>Hello %s,</p>

                            <h3 style="color:#dc2626;">
                                Reservation Expired
                            </h3>

                            <p>
                                Your reservation for the following book
                                has expired:
                            </p>

                            <div style="background-color:#fef2f2;
                                        border-left:5px solid #dc2626;
                                        padding:18px;
                                        margin:22px 0;
                                        border-radius:8px;">

                                <p>
                                    <strong>Book:</strong> %s
                                </p>

                                <p>
                                    <strong>Status:</strong> EXPIRED
                                </p>

                            </div>

                            <p>
                                The book was not collected within the
                                permitted pickup period.
                            </p>

                            <p>
                                The reserved copy may now be offered to
                                the next member in the queue or returned
                                to the available inventory.
                            </p>

                            <p>
                                You may search the library catalogue and
                                place another reservation when eligible.
                            </p>

                            <p style="color:#64748b;
                                      margin-top:28px;">
                                IntelliLib Team
                            </p>

                        </div>
                    </body>
                    </html>
                    """.formatted(
                    safeName,
                    bookTitle.trim()
            );

            message.setContent(
                    emailBody,
                    "text/html; charset=UTF-8"
            );

            Transport.send(message);

            return true;

        } catch (Exception exception) {

            System.err.println(
                    "Reservation-expired email could not be sent: "
                            + exception.getMessage()
            );

            exception.printStackTrace();

            return false;
        }
    }

    /*
     * ============================================================
     * MAIL CONFIGURATION HELPERS
     * ============================================================
     */

    private Properties createMailProperties() {

        Properties properties =
                new Properties();

        properties.put(
                "mail.smtp.auth",
                "true"
        );

        properties.put(
                "mail.smtp.starttls.enable",
                "true"
        );

        properties.put(
                "mail.smtp.starttls.required",
                "true"
        );

        properties.put(
                "mail.smtp.host",
                SMTP_HOST
        );

        properties.put(
                "mail.smtp.port",
                SMTP_PORT
        );

        properties.put(
                "mail.smtp.connectiontimeout",
                "10000"
        );

        properties.put(
                "mail.smtp.timeout",
                "10000"
        );

        properties.put(
                "mail.smtp.writetimeout",
                "10000"
        );

        return properties;
    }

    private Session createMailSession(
            Properties properties
    ) {

        return Session.getInstance(
                properties,
                new Authenticator() {

                    @Override
                    protected PasswordAuthentication
                    getPasswordAuthentication() {

                        return new PasswordAuthentication(
                                senderEmail,
                                appPassword.replace(
                                        " ",
                                        ""
                                )
                        );
                    }
                }
        );
    }
}