package com.athul.library.service;

import com.athul.library.dto.CategoryDto;
import com.athul.library.model.Category;
import com.athul.library.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class CategoryServiceImpl implements CategoryService {

    private CategoryRepository repo;


    public CategoryServiceImpl(CategoryRepository repo) {

        this.repo = repo;
    }

    @Override
    public List<Category> findAll() {

        return repo.findAll();
    }

    @Override
    public Category save(Category category) {

        try {
            Category categorySave = new Category(category.getName());
            return repo.save(categorySave);
    }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Category findById(Long id) {

        return repo.findById(id).get();
    }

    @Override
    public Category update(Category category) {
        Category updatedCategory = repo.getReferenceById(category.getId());
        updatedCategory.setName(category.getName());
        return repo.save(updatedCategory);
    }

    @Override
    public void deleteById(Long id) {
        Category category=repo.getById(id);
        category.set_deleted(true);
        category.set_activated(false);
        repo.save(category);

    }

    @Override
    public void enabledById(Long id) {

        Category category=repo.getById(id);
        category.set_activated(true);
        category.set_deleted(false);
        repo.save(category);
    }

    @Override
    public List<Category> findAllByActivated() {
        return repo.findAllByActivated();
    }

    @Override
    public List<CategoryDto> searchCategories(String keyword) {
        List<Category> category=repo.findAllByName(keyword);
        List<CategoryDto> categoryDtoList=new ArrayList<>();
        for(Category category1:category){
            CategoryDto categoryDto=new CategoryDto();
            categoryDto.setId(category1.getId());
            categoryDto.setName(category1.getName());
            categoryDtoList.add(categoryDto);
        }
        return categoryDtoList;
    }

    @Override
    public List<CategoryDto> getCategoryAndProduct() {
        return null;
    }

    @Override
    public List<Category> findAllByActivatedTrue() {
        return repo.findAllByActivatedTrue();
    }
}
