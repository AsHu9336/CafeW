package com.inn.cafe.serviceImpl;

import com.google.common.base.Strings;
import com.inn.cafe.JWT.JwtFilter;
import com.inn.cafe.POJO.Category;
import com.inn.cafe.dao.CategoryDao;
import com.inn.cafe.service.CategoryService;
import com.inn.cafe.utils.CafeUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
@Slf4j
@Transactional
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private  CategoryDao categoryDao;

    @Autowired
    private JwtFilter jwtFilter;
    @Override
    public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {

        try {
            if (jwtFilter.isAdmin()) {
                log.info("Yes, it is an ADMIN");

                if (validateCategoryMap(requestMap, false)) {
                    log.info("Validated RequestMap");
                    categoryDao.save(getCategoryFromMap(requestMap, false));
                    log.info("Saved Category");
                    return CafeUtils.getResponseEntity("Category Added Successfully", HttpStatus.OK);
                } else {
                    log.warn("Validation failed for RequestMap: {}", requestMap);
                    return CafeUtils.getResponseEntity("Invalid Request Data", HttpStatus.BAD_REQUEST);
                }
            } else {
                log.warn("Unauthorized access attempt by non-admin user.");
                return CafeUtils.getResponseEntity("Not Authorized to Access", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            log.error("Exception occurred while adding new category: ", ex);
            return CafeUtils.getResponseEntity("Something Went Wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
        try {
            List<Category> categories;

            if (!Strings.isNullOrEmpty(filterValue) && filterValue.equalsIgnoreCase("true")) {
                log.info("Inside if condition: filterValue is true");
                categories = categoryDao.getAllCategory();
            } else {
                log.info("Inside else condition: fetching all categories");
                categories = categoryDao.findAll();
            }

            log.info("Fetched categories: {}", categories);

            return new ResponseEntity<>(categories, HttpStatus.OK);
        } catch (Exception ex) {
            log.error("Exception occurred while fetching categories", ex);
            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
        try{

            if(jwtFilter.isAdmin()){

                if(validateCategoryMap(requestMap,true)){
                   Optional optional =  categoryDao.findById(Integer.valueOf(requestMap.get("id")));
                   if(!optional.isEmpty()){

                       categoryDao.save(getCategoryFromMap(requestMap,true));
                       return CafeUtils.getResponseEntity("Category Updated Successfully",HttpStatus.OK);


                   }else{
                       return CafeUtils.getResponseEntity("CategoryId doesn't exists",HttpStatus.BAD_REQUEST);
                   }
                }
                return CafeUtils.getResponseEntity("Invalid Data",HttpStatus.BAD_REQUEST);

            }else{
                return CafeUtils.getResponseEntity("Unauthorized",HttpStatus.UNAUTHORIZED);
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity("Something Went Wrong!",HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private boolean validateCategoryMap(Map<String, String> requestMap, boolean validateId) {

        if (requestMap.containsKey("name")){
            if (requestMap.containsKey("id") && validateId){
                return true;

            } else if (!validateId) {
                return true;
            }
        }
        return false;

    }
    private Category getCategoryFromMap(Map<String,String> categoryMap, Boolean isAdd){
        log.info("Inside getCategory");
        Category category = new Category();
        if(isAdd){
            category.setId(Integer.parseInt(categoryMap.get("id")));

        }
        category.setName(categoryMap.get("name"));
        log.info("Category Object {}",category);
        return category;

    }
}
