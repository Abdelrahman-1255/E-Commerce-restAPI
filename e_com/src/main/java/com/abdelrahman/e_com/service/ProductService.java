
package com.abdelrahman.e_com.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.abdelrahman.e_com.model.Product;
import com.abdelrahman.e_com.repository.ProductRepository;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ChatClient chatClient;;

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

    @Transactional(readOnly = true)
    public  List<Product> searchProducts(String keyword) {
       return productRepository.searchProducts(keyword);
    }

    public String generateDescription(String productName, String productCategory) {
        
         String descPrompt = String.format("""
                
                Write a concise and professional product description for an e-commerce listing.
                
                Product Name: %s
                Category: %s
                
                Keep it simple, engaging, and highlight its primary features or benefits.
                Avoid technical jargon and keep it customer-friendly.
                Limit the description to 250 characters maximum.
                
                """, productName , productCategory );

        String desc = chatClient.prompt(descPrompt)
                .call()
                .chatResponse()
                .getResult()
                .getOutput()
                .getText();

        return desc;
    }


  
}
