package com.github.bwar.sb.ms.productservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.github.bwar.sb.ms.productservice.model.Product;

public interface ProductRepository extends MongoRepository<Product, String> {

}
