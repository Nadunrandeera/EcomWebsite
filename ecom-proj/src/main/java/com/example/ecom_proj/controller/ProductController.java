package com.example.ecom_proj.controller;

import com.example.ecom_proj.model.Product;
import com.example.ecom_proj.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService service;

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getProducts() {
        return new ResponseEntity<>(service.getAllProduct(), HttpStatus.OK);
    }

//    @PostMapping("/product")
//    public ResponseEntity<?> addProduct(@RequestPart Product product,
//                                         @RequestPart MultipartFile imageFile) {
//        try {
//            Product product1 = service.addProduct(product, imageFile);
//            return new ResponseEntity<>(product1, HttpStatus.CREATED);
//        }catch (Exception e) {
//            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//
//    }
@PostMapping("/product")
public ResponseEntity<?> addProduct(
        @RequestPart("product") Product product,
        @RequestPart("imageFile") MultipartFile imageFile) {
    try {
        // Validate inputs
        if (product == null || imageFile == null || imageFile.isEmpty()) {
            return new ResponseEntity<>("Invalid input: product or file is missing.", HttpStatus.BAD_REQUEST);
        }

        // Process product and file
        Product savedProduct = service.addProduct(product, imageFile);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    } catch (IOException e) {
        return new ResponseEntity<>("Error processing the file: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
        return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}


    @GetMapping("/product/{id}")
    public ResponseEntity<Product> getById(@PathVariable int id) {
        Product productById = service.getProductById(id);
        if(productById != null) {
            return new ResponseEntity<>(productById, HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/product/{id}/image")
    public ResponseEntity<byte[]> getImageByProductId(@PathVariable int id) {
        Product product = service.getProductById(id);
        byte[] imageData = product.getImageData();

        return ResponseEntity.ok().contentType(MediaType.valueOf(product.getImageType())).body(imageData);

    }

    @PutMapping("/product/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable int id,
                                                @RequestPart Product product,
                                                @RequestPart MultipartFile imageFile) throws IOException {
        Product product1 = null;
        try {
             product1 = service.updateProduct(id,product,imageFile);
        }catch (IOException e){
            return new ResponseEntity<>("Failed to update",HttpStatus.BAD_REQUEST);
        }

        if(product1 != null){
            return new ResponseEntity<>("Updated",HttpStatus.OK);
        }
        else{
            return new ResponseEntity<>("Failed to update",HttpStatus.BAD_REQUEST);
        }

    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable int id){
        Product productById = service.getProductById(id);
        if (productById != null){
            service.deleteProduct(id);
            return new ResponseEntity<>("Deleted",HttpStatus.OK);
        }
        else
            return new ResponseEntity<>("Product not found",HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/products/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String keyword) {
        List<Product> products = service.searchProducts(keyword);
        System.out.println("searching with  : " + keyword);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
}
