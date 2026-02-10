
package com.abdelrahman.e_com.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.abdelrahman.e_com.model.Product;
import com.abdelrahman.e_com.repository.ProductRepository;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(int id) {
        if(productRepository.existsById(id)){
            return productRepository.findById(id);
        }else {
            return null;
        }
    }

    public Product addOrUpdateProduct(Product product, MultipartFile image) throws IOException {
        
        product.setImageName(image.getOriginalFilename());
        product.setImageType(image.getContentType());
        product.setImageData(image.getBytes());
        return productRepository.save(product);
    }

    public  void deleteProduct(int id) {
        productRepository.deleteById(id);
    }


  
}
