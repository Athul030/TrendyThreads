package com.athul.customer.controller;

import com.athul.library.exception.TwilioVerificationException;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Random;

@RestController
public class SMSController {

    @GetMapping("/sendOTP")
    public ResponseEntity<String> sendSMS() {


            Twilio.init(System.getenv("TWILIO_ACCOUNT_SID"), System.getenv("TWILIO_AUTH_TOKEN"));


            String toPhoneNumber = "+919633513774";  // Replace with the recipient's phone number
            String fromPhoneNumber = "+15124002107";  // Replace with your Twilio phone number


            String otp = generateOTP(6);


            Message.creator(new PhoneNumber(toPhoneNumber), new PhoneNumber(fromPhoneNumber), "The OTP: "+otp).create();

            return new ResponseEntity<String>(otp, HttpStatus.OK);


    }

    private static String generateOTP(int length) {
        String allowedChars = "0123456789";
        StringBuilder otp = new StringBuilder();
        Random random = new Random();

        // Generate random digits and append them to the OTP
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(allowedChars.length());
            char digit = allowedChars.charAt(index);
            otp.append(digit);
        }

        // Convert the StringBuilder to a String and return the OTP
        return otp.toString();
    }
}
