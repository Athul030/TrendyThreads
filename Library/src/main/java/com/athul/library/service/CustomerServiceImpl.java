package com.athul.library.service;

import com.athul.library.dto.CustomerDto;
import com.athul.library.exception.CustomLazyInitializationException;
import com.athul.library.exception.TwilioApiException;
import com.athul.library.exception.TwilioVerificationException;
import com.athul.library.model.Address;
import com.athul.library.model.Category;
import com.athul.library.model.Customer;
import com.athul.library.model.EmailDetails;
import com.athul.library.repository.CustomerRepository;
import com.athul.library.repository.RoleRepository;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.hibernate.LazyInitializationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.security.SecureRandom;
import java.util.*;

@Service
public class CustomerServiceImpl implements CustomerService{

    private RoleRepository roleRepository;
    private CustomerRepository customerRepository;
    private EmailService emailService;

    private WalletService walletService;


    public CustomerServiceImpl(RoleRepository roleRepository, CustomerRepository customerRepository, EmailService emailService, WalletService walletService) {
        this.roleRepository = roleRepository;
        this.customerRepository = customerRepository;
        this.emailService = emailService;
        this.walletService = walletService;
    }

    private static final long OTP_VALID_DURATION = 1 * 60 * 1000;   // 5 minutes

    public Date getOtpRequestedTime() {
        return otpRequestedTime;
    }

    public void setOtpRequestedTime(Date otpRequestedTime) {
        this.otpRequestedTime = otpRequestedTime;
    }

    private Date otpRequestedTime;

    long  otpRequestedTimeMillis=0;



    @Override
    public CustomerDto save(CustomerDto customerDto) {
        Customer customer=new Customer();
        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        customer.setUsername(customerDto.getUsername());
        customer.setPassword(customerDto.getPassword());
        customer.setPhoneNumber(customerDto.getPhoneNumber());
        customer.setRoles(Arrays.asList(roleRepository.findByName("CUSTOMER")));
        String enteredReferral=customerDto.getReferralCode();
        System.out.println(enteredReferral);

        if(enteredReferral!=null){
            try {
                Customer referralOwnerCustomer = customerRepository.findByReferralCode(enteredReferral);
                if(referralOwnerCustomer!=null){
                    boolean status=walletService.saveReferralOffer(200.00,referralOwnerCustomer);
                    if(!status){
                        throw new RuntimeException("Referral offer transaction failed");
                    }
                }
            }catch (NullPointerException e){
                throw new RuntimeException("No referee found");
            }

        }
        customer.setReferralCode(referralCodeGenerator());
        Customer customerSave=customerRepository.save(customer);
        return mapperDto(customerSave);
    }

    private String referralCodeGenerator(){
        char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new SecureRandom();
        for (int i = 0; i < 8; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        String output = sb.toString();
        return output ;
    }



    @Override
    public Customer findByUsername(String username) {

        return customerRepository.findByUsername(username);
    }

    @Override
    public Customer findByReferralCode(String referralCode) {
        return customerRepository.findByReferralCode(referralCode);
    }

    @Override
    public List<Customer> findAll() {

        return customerRepository.findAll();
    }

    @Override
    public Customer update(Customer customer) {
        Customer customer1=customerRepository.getReferenceById(customer.getId());
        customer1.setUsername(customer.getUsername());
        customer1.setPhoneNumber(customer.getPhoneNumber());
        customer1.setAddress(customer.getAddress());
        customer1.setPhoneNumber(customer.getPhoneNumber());
        customer1.set_blocked(customer.is_blocked());
        return customer1;
    }

    @Override
    public void blockById(Long id) {

        Customer customer=customerRepository.getReferenceById(id);
        customer.set_blocked(true);
        customerRepository.save(customer);


    }

    @Override
    public void unblockById(Long id) {

        Customer customer=customerRepository.getReferenceById(id);
        customer.set_blocked(false);
        customerRepository.save(customer);

    }

    @Override
    public List<CustomerDto> searchCustomer(String keyword) {
        List<Customer> customer=customerRepository.findAllByNameOrEmail(keyword);
        List<CustomerDto> customerDtoList=new ArrayList<>();
        for(Customer customers:customer){
            CustomerDto customerDto=new CustomerDto();
            customerDto.setUsername(customers.getUsername());
            customerDto.setFirstName(customers.getFirstName());
            customerDto.setLastName(customers.getLastName());
            customerDto.setPhoneNumber(customers.getPhoneNumber());
            customerDto.set_blocked(customers.is_blocked());
            customerDtoList.add(customerDto);
        }
        return customerDtoList;

    }

    @Override
    public String genrateOTPAndSendOnMobile(Customer customer) {

        try {
            Customer customer1 = customerRepository.findByUsername(customer.getUsername());
            int otp = (int) (Math.random() * 9000) + 1000;
            customer1.setOtp(otp);
            customerRepository.save(customer1);
            Twilio.init(System.getenv("TWILIO_ACCOUNT_SID"), System.getenv("TWILIO_AUTH_TOKEN"));
            Message message = Message.creator(new PhoneNumber("+91" + customer.getPhoneNumber()), new PhoneNumber("+15124002107"), "OTP for Trendy Threads is: " + "" + otp + " Please verify ASAP!").create();
            if (message.getErrorCode() == null)
                return "success";
            else
                return "error";
        }catch (TwilioVerificationException e){

            throw new TwilioVerificationException("TwilioVerificationException");

        }catch (LazyInitializationException e){
            throw new CustomLazyInitializationException("LazyInitializationException");

        } catch (com.twilio.exception.ApiException twilioException) {
        throw new TwilioApiException("Twilio API error: " + twilioException.getMessage());
        }
    }

    @Override
    public String genrateOTPAndSendOnEmail(String username) {
        Customer customer=customerRepository.findByUsername(username);
        int otp= (int)(Math.random()*9000)+1000;
        customer.setOtp(otp);
        customerRepository.save(customer);
        setOtpRequestedTime(new Date());
        otpRequestedTimeMillis = otpRequestedTime.getTime();;
        return emailService.sendSimpleMail(new EmailDetails(username,"Your OTP for verification is "+otp,"Verify with OTP"));

    }

    public String shareReferralCode(String referralCode, String emailAddress) {
        return emailService.sendSimpleMail(new EmailDetails(emailAddress,"Hey, I wanted to share my referral code for Trendy Threads with you: " + referralCode,"Check out my Trendy Threads referral code!"));

    }

    @Override
    public boolean verifyOTP(long otp, String username) {
        Customer customer=customerRepository.findByUsername(username);
        long currentTimeInMillis=System.currentTimeMillis();
        System.out.println("currentTimeInMillis:"+currentTimeInMillis);
        System.out.println("otpRequestedTimeMillis"+otpRequestedTimeMillis);
        if(otpRequestedTimeMillis + OTP_VALID_DURATION > currentTimeInMillis) {
            if(otp== customer.getOtp())
                return true;
            else
                return false;
        }else{
            return false;
        }

    }

    @Override
    public Customer saveInfo(Customer customer, Address address) {
        Customer customer1 = customerRepository.findByUsername(customer.getUsername());
        List<Address> addressList = customer1.getAddress();
        if (addressList == null) {
            addressList = new ArrayList<>();
        }
        addressList.add(address);
        customer1.setAddress(addressList);
        System.out.println(customer1);
        System.out.println(customer);
        System.out.println(addressList);;

        return customerRepository.save(customer1);
    }




    private CustomerDto mapperDto(Customer customer){

        CustomerDto customerDto=new CustomerDto();
        customerDto.setFirstName(customer.getFirstName());
        customerDto.setLastName(customer.getLastName());
        customerDto.setPassword(customer.getPassword());
        customerDto.setUsername(customer.getUsername());
        customerDto.setPhoneNumber(customer.getPhoneNumber());
        return customerDto;
    }

    @Override
    public Customer findById(long id) {
        return customerRepository.findById(id);
    }

}
