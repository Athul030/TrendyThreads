package com.athul.library.service;

import com.athul.library.dto.ProductDto;
import com.athul.library.model.Category;
import com.athul.library.model.Product;
import com.athul.library.repository.ProductRepository;
import com.athul.library.utils.ImageUpload;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService{

    private ProductRepository productRepository;
    private ImageUpload imageUpload;


    public ProductServiceImpl(ProductRepository productRepository, ImageUpload imageUpload) {
        this.productRepository = productRepository;
        this.imageUpload = imageUpload;
    }



    /*Admin*/

    @Override
    public List<ProductDto> findAll() {
        List<ProductDto> productDtoList=new ArrayList<>();
        List<Product> products=productRepository.findAll();
        for(Product product:products){
            ProductDto productDto=new ProductDto();
            productDto.setId(product.getId());
            productDto.setName(product.getName());
            productDto.setDescription(product.getDescription());
            productDto.setCostPrice(product.getCostPrice());
            productDto.setSalePrice(product.getSalePrice());
            productDto.setCurrentQuantity(product.getCurrentQuantity());
            Category category=product.getCategory();
            if(category.is_activated()){
                productDto.setCategory(category);
            }else{
                Category category1=new Category();
                category1.setName(category.getName()+"  ALERT: CATEGORY IS DISABLED");
                productDto.setCategory(category1);
            }

            productDto.setBrand(product.getBrand());
            List<String> imageUrls=product.getImageUrls();
            productDto.setImageUrls((imageUrls));
            productDto.setProductActivated(product.isProductActivated());
            productDto.setProductDeleted(product.isProductDeleted());
            productDtoList.add(productDto);
        }
        return productDtoList;
    }

    @Override
    public List<ProductDto> findByCategory(Category category) {
        List<ProductDto> productDtoList=new ArrayList<>();
        List<Product> products=productRepository.findAllByCategory(category);
        for(Product product:products){
            ProductDto productDto=new ProductDto();
            productDto.setId(product.getId());
            productDto.setName(product.getName());
            productDto.setDescription(product.getDescription());
            productDto.setCostPrice(product.getCostPrice());
            productDto.setSalePrice(product.getSalePrice());
            productDto.setCurrentQuantity(product.getCurrentQuantity());
            productDto.setCategory(product.getCategory());
            productDto.setBrand(product.getBrand());
            List<String> imageUrls=product.getImageUrls();
            productDto.setImageUrls((imageUrls));
            productDto.setProductActivated(product.isProductActivated());
            productDto.setProductDeleted(product.isProductDeleted());
            productDtoList.add(productDto);
        }
        return productDtoList;
    }

    @Override
    public List<Product> findProductsByCategory(long id) {
        return productRepository.findAllByCategoryId(id);
    }

    @Override
    public Product save(List<MultipartFile> imageFiles, ProductDto productDto) {
        try {
            Product product = new Product();
            if (imageFiles != null && !imageFiles.isEmpty()) {
                List<String> uniqueFileNames = imageUpload.uploadImages(imageFiles);
                product.setImageUrls(uniqueFileNames);
            } else {
                product.setImageUrls(null);  // Handle the case when no images are uploaded
            }
            product.setName(productDto.getName());
            product.setDescription(productDto.getDescription());
            product.setCategory(productDto.getCategory());
            product.setCostPrice(productDto.getCostPrice());
            product.setCurrentQuantity(productDto.getCurrentQuantity());
            product.setProductActivated(true);
            product.setProductDeleted(false);
            return productRepository.save(product);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

//    @Override
//    public Product findById(Long id) {
//        return null;
//    }

    @Override
    public Product update(List<MultipartFile> imageFiles, ProductDto productDto, Long id) {
        try{
            Product productUpdate=productRepository.getById(id);
            System.out.println("imageurlexist:"+productUpdate.getImageUrls());
            System.out.println(imageFiles);
            System.out.println(imageFiles.isEmpty());

            if (imageFiles != null && !imageFiles.isEmpty()) {
                List<String> uniqueFileNames = imageUpload.uploadImages(imageFiles);
                System.out.println("uniqfilenames"+uniqueFileNames);
                System.out.println("uniqfilenameboolean"+uniqueFileNames.isEmpty());
                if(uniqueFileNames.isEmpty()){
                    List<String> existingImageUrls = productUpdate.getImageUrls();
                    if (existingImageUrls != null) {
                        productUpdate.setImageUrls(existingImageUrls);
                    }
                }else{
                    productUpdate.setImageUrls(uniqueFileNames);
                }
            }
            productUpdate.setId(productUpdate.getId());
            productUpdate.setCategory(productDto.getCategory());
            productUpdate.setName(productDto.getName());
            productUpdate.setDescription(productDto.getDescription());
            productUpdate.setSalePrice(productDto.getSalePrice());
            productUpdate.setCostPrice(productDto.getCostPrice());
            productUpdate.setCurrentQuantity(productDto.getCurrentQuantity());
            return productRepository.save(productUpdate);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public void deleteById(Long id) {

        Product product=productRepository.getReferenceById(id);
        product.setProductDeleted(true);
        product.setProductActivated(false);
        productRepository.save(product);
    }

    @Override
    public void enableById(Long id) {

        Product product=productRepository.getReferenceById(id);
        product.setProductActivated(true);
        product.setProductDeleted(false);
        productRepository.save(product);

    }

    @Override
    public ProductDto getById(Long id) {
        Product product=productRepository.getReferenceById(id);
        ProductDto productDto=new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setDescription(product.getDescription());
        productDto.setSalePrice(product.getSalePrice());
        productDto.setCostPrice(product.getCostPrice());
        productDto.setCurrentQuantity(product.getCurrentQuantity());
        productDto.setImageUrls(product.getImageUrls());
        productDto.setCategory(product.getCategory());
        productDto.setProductDeleted(product.isProductDeleted());
        productDto.setProductActivated(product.isProductActivated());
        return productDto;
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.getReferenceById(id);
    }

    @Override
    public List<ProductDto> searchProducts(String keyword) {
        List<Product> product=productRepository.findAllByNameOrDescription(keyword);
        List<ProductDto> productDtoList=new ArrayList<>();
        for(Product products:product){
            ProductDto productDto=new ProductDto();
            productDto.setId(products.getId());
            productDto.setName(products.getName());
            productDto.setDescription(products.getDescription());
            productDto.setImageUrls(products.getImageUrls());
            productDto.setSalePrice(products.getSalePrice());
            productDto.setCostPrice(products.getCostPrice());
            productDto.setCurrentQuantity(products.getCurrentQuantity());
            productDto.setCategory(products.getCategory());
            productDto.setProductDeleted(products.isProductDeleted());
            productDto.setProductActivated(products.isProductActivated());
            productDtoList.add(productDto);

        }
        return productDtoList ;
    }

    @Override
    public List<ProductDto> searchProductsUserSide(String keyword) {
        List<Product> product=productRepository.listViewProductsUserSide(keyword);
        List<ProductDto> productDtoList=new ArrayList<>();
        for(Product products:product){
            ProductDto productDto=new ProductDto();
            productDto.setId(products.getId());
            productDto.setName(products.getName());
            productDto.setDescription(products.getDescription());
            productDto.setImageUrls(products.getImageUrls());
            productDto.setSalePrice(products.getSalePrice());
            productDto.setCostPrice(products.getCostPrice());
            productDto.setCurrentQuantity(products.getCurrentQuantity());
            productDto.setCategory(products.getCategory());
            productDto.setProductDeleted(products.isProductDeleted());
            productDto.setProductActivated(products.isProductActivated());
            productDtoList.add(productDto);

        }
        return productDtoList ;
    }

    @Override
    public Page<Product> getProductsPaginated(int page,int pageSize) {
        Pageable pageable= PageRequest.of(page-1,pageSize);
        Page<Product> productPage=productRepository.findAllPageable(pageable);

        return productPage;
    }


    @Override
    public List<ProductDto> findAllProducts() {
        List<Product> products=productRepository.findAllByActivatedTrue();
        List<ProductDto>productDtoList = transferData(products);
        return productDtoList;
    }

    @Override
    public Product findBYId(long id) {
        return productRepository.findById(id);
    }

    private List<ProductDto> transferData(List<Product> products) {
        List<ProductDto> productDtos = new ArrayList<>();
        for (Product product : products) {
            ProductDto productDto = new ProductDto();
            productDto.setId(product.getId());
            productDto.setName(product.getName());
            productDto.setCurrentQuantity(product.getCurrentQuantity());
            productDto.setCostPrice(product.getCostPrice());
            productDto.setSalePrice(product.getSalePrice());
            productDto.setDescription(product.getDescription());
            productDto.setBrand(product.getBrand());
            productDto.setImageUrls(product.getImageUrls());
            productDto.setCategory(product.getCategory());
            productDto.setProductActivated(product.isProductActivated());
            productDto.setProductDeleted(product.isProductDeleted());
            productDtos.add(productDto);
        }
        return productDtos;
    }

    /*Customer*/

    @Override
    public List<Product> getAllProduct() {
        return productRepository.getAllProducts();
    }

    @Override
    public List<ProductDto> listViewProduct() {


        List<Product> product=productRepository.listViewProducts();
        List<ProductDto> productDtoList=new ArrayList<>();
        for(Product products:product){
            ProductDto productDto=new ProductDto();
            productDto.setId(products.getId());
            productDto.setName(products.getName());
            productDto.setDescription(products.getDescription());
            productDto.setImageUrls(products.getImageUrls());
            productDto.setSalePrice(products.getSalePrice());
            productDto.setCostPrice(products.getCostPrice());
            productDto.setCurrentQuantity(products.getCurrentQuantity());
            productDto.setCategory(products.getCategory());
            productDto.setProductDeleted(products.isProductDeleted());
            productDto.setProductActivated(products.isProductActivated());
            productDtoList.add(productDto);

        }
        return productDtoList ;

    }

    @Override
    public List<Product> getProductsInCategory(Long categoryId) {
        return null;
    }

    @Override
    public List<Product> filterHighPrice() {
        return productRepository.filterHighPrice();
    }

    @Override
    public List<Product> filterLowPrice() {
        return productRepository.filterLowPrice();
    }
}
