package com.athul.library.service;

import com.athul.library.dto.AddressDto;
import com.athul.library.model.Address;
import jakarta.persistence.Id;
import org.springframework.stereotype.Service;


public interface AddressService {



    Address save(AddressDto addressDto, String username);

    Address update(AddressDto addressDto, long id);

    Address update(AddressDto addressDto);

    AddressDto findById(long id);

    void deleteAddress(long id);

    void enable(long id);

    void disable(long id);

    Address findDefaultAddress(long customer_id);

    Address findByIdOrder(long id);


}
