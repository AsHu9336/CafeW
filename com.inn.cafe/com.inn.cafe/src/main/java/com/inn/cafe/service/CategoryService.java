package com.inn.cafe.service;


import com.inn.cafe.POJO.Category;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface CategoryService {
   public ResponseEntity<String> addNewCategory(Map<String, String> requestMap);

   public ResponseEntity<List<Category>> getAllCategory(String filterValue);

    public ResponseEntity<String> updateCategory(Map<String, String> requestMap);
}
