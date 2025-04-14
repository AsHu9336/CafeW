package com.inn.cafe.restImpl;

import com.inn.cafe.POJO.Category;
import com.inn.cafe.rest.CategoryRest;
import com.inn.cafe.service.CategoryService;
import com.inn.cafe.utils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@RestController
@Slf4j
public class CategoryRestImpl implements CategoryRest {
    @Autowired
    private  CategoryService categoryService;
    @Override
    public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
        try {

            log.info("Inside CategoryRest/addNewCategory {}",requestMap );

            categoryService.addNewCategory(requestMap);
            log.info("Yes it is added i am in categoryService Again");
            return CafeUtils.getResponseEntity("Category Added Successfully",HttpStatus.OK);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity("Something Went Wrong!", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
        try {

            return categoryService.getAllCategory(filterValue);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
        try {

            return categoryService.updateCategory(requestMap);



        }catch (Exception ex){
            ex.printStackTrace();

        }
        return CafeUtils.getResponseEntity("Something Went Wrong!",HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
