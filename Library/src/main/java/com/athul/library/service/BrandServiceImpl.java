package com.athul.library.service;

import com.athul.library.model.Brand;
import com.athul.library.model.Category;
import com.athul.library.repository.BrandRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    private BrandRepository brandRepo;

    public BrandServiceImpl(BrandRepository brandRepo) {
        this.brandRepo = brandRepo;
    }

    @Override
    public List<Brand> findAll() {
        return brandRepo.findAll();
    }

    @Override
    public Brand save(Brand brand) {

        try {
            Brand brandSave=new Brand(brand.getName());
            return brandRepo.save(brandSave);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }



    @Override
    public Brand getById(Long id) {
        return brandRepo.getById(id);
    }

    @Override
    public Brand update(Brand brand) {
        Brand brandUpdate=new Brand();
        brandUpdate.setName(brand.getName());
        brandUpdate.set_activated(brand.is_activated());
        brandUpdate.set_deleted(brand.is_deleted());
        return brandRepo.save(brandUpdate);
    }

    @Override
    public void deleteById(Long id) {
        Brand brand=brandRepo.getById(id);
        brand.set_deleted(true);
        brand.set_activated(false);
        brandRepo.save(brand);

    }

    @Override
    public void enableById(Long id) {

        Brand brand=brandRepo.getById(id);
        brand.set_activated(true);
        brand.set_deleted(false);
        brandRepo.save(brand);

    }
}
