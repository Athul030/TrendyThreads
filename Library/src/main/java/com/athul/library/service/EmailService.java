package com.athul.library.service;

import com.athul.library.model.EmailDetails;
import org.springframework.stereotype.Service;



public interface EmailService {

    String sendSimpleMail(EmailDetails emailDetails);
}
