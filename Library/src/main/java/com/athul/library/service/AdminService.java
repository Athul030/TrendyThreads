package com.athul.library.service;

import com.athul.library.dto.AdminDto;
import com.athul.library.model.Admin;
import com.athul.library.repository.AdminRepository;

public interface AdminService {


    Admin findByUsername(String username);

    Admin save(AdminDto adminDto);
}
