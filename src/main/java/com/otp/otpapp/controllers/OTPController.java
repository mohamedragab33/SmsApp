package com.otp.otpapp.controllers;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
public class OTPController {

    @Value("${twilio.account.sid}")
    private String twilioAccountSid;

    @Value("${twilio.auth.token}")
    private String twilioAuthToken;

    @Value("${twilio.phone.number}")
    private String twilioPhoneNumber;

    private final Map<String, String> otpMap = new HashMap<>();


    @PostMapping("/sendOTP")
    public String sendOTP(@RequestParam String phoneNumber) {
        // Generate a random 6-digit OTP
        String otp = generateOTP();

        // Initialize Twilio with your account credentials
        Twilio.init(twilioAccountSid, twilioAuthToken);

        otpMap.put(phoneNumber, otp);

        // Send the OTP via SMS
        Message message = Message.creator(
                        new PhoneNumber("+".concat(phoneNumber)),
                        new PhoneNumber(twilioPhoneNumber),
                        "Your OTP is: " + otp)
                .create();


        return "OTP sent successfully!";
    }

    @PostMapping("/verifyOTP")
    public String verifyOTP(@RequestParam String phoneNumber, @RequestParam String otp) {
        // Retrieve the stored OTP for the given phone number (In a real-world scenario, use a database)
        String storedOTP = otpMap.get(phoneNumber);

        if (storedOTP != null && storedOTP.equals(otp)) {
            // OTP is valid, perform further actions (e.g., grant access)
            otpMap.remove(phoneNumber); // Remove the used OTP from the map
            return "OTP is valid. Access granted!";
        } else {
            // OTP is invalid
            return "Invalid OTP. Access denied!";
        }
    }

    private String generateOTP() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
}