package app;

import service.EmailService;

import java.util.Scanner;

public class EmailTest {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter recipient email: ");
        String recipientEmail = scanner.nextLine();

        EmailService emailService = new EmailService();

        if (!emailService.isConfigured()) {

            System.out.println(
                    "Email variables are not configured."
            );

            scanner.close();
            return;
        }

        boolean sent = emailService.sendPasswordResetOtp(
                recipientEmail,
                "Test User",
                "483921"
        );

        if (sent) {
            System.out.println(
                    "Test OTP email sent successfully."
            );
        } else {
            System.out.println(
                    "Test OTP email could not be sent."
            );
        }

        scanner.close();
    }

    
}