package com.inn.cafe.serviceImpl;

import com.inn.cafe.JWT.JwtFilter;
import com.inn.cafe.POJO.Category;
import com.inn.cafe.POJO.Product;
import com.inn.cafe.dao.ProductDao;
import com.inn.cafe.service.ProductService;
import com.inn.cafe.utils.CafeUtils;
import com.inn.cafe.wrapper.ProductWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductDao productDao;

    @Autowired
    JwtFilter jwtFilter;

    @Override
    public ResponseEntity<String> addNewProduct(Map<String, String> requestMap) {
        try{

            if (jwtFilter.isAdmin()){

                if(validateProductMap(requestMap,false)){
                      productDao.save(getProductFromMap(requestMap,false));
                      return CafeUtils.getResponseEntity("Product added successfully..",HttpStatus.OK);
                }
                return CafeUtils.getResponseEntity("Invalid Data",HttpStatus.BAD_REQUEST);

            }else{
                return CafeUtils.getResponseEntity("Not Authorized!",HttpStatus.UNAUTHORIZED);
            }


        }catch(Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity("Something Went Wrong!", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getAllProduct() {
        try{

            return new ResponseEntity<>(productDao.getAllProduct(),HttpStatus.OK);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return  new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateProduct(Map<String, String> requestMap) {
        try {

               log.info("Inside ProductService");

            if(jwtFilter.isAdmin()){
                log.info("I am Admin");
                if(validateProductMap(requestMap,true)){

                    log.info("Inside Validate Product i.e. Product Validated Successfully");

                  Optional<Product> optional =  productDao.findById(Integer.parseInt(requestMap.get("id")));
                  log.info("Optional zzz{}",optional);
                  if(!optional.isEmpty()){
                      Product product = getProductFromMap(requestMap,true);
                      log.info("Product {}",product);
                      product.setStatus(optional.get().getStatus());
                      productDao.save(product);
                      return CafeUtils.getResponseEntity("Product Updated Successfully",HttpStatus.OK);

                  }else{
                      log.info("Inside Else");
                     return CafeUtils.getResponseEntity("Invalid ID product not found with this Id",HttpStatus.BAD_REQUEST);
                  }


                }else{
                    return CafeUtils.getResponseEntity("Invalid Data",HttpStatus.BAD_REQUEST);
                }

            }else{
                return CafeUtils.getResponseEntity("Unauthorized",HttpStatus.UNAUTHORIZED);
            }

        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity("Something Went Wrong",HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> deleteProduct(Integer id) {
        try{

            if(jwtFilter.isAdmin()){

                Optional optional = productDao.findById(id);
                if(!optional.isEmpty()){

                    productDao.deleteById(id);
                    return CafeUtils.getResponseEntity("Product Deleted Successfully",HttpStatus.OK);

                }else{
                    return CafeUtils.getResponseEntity("Invalid Product Id",HttpStatus.BAD_REQUEST);
                }

            }else{
                return CafeUtils.getResponseEntity("Not Authorized",HttpStatus.UNAUTHORIZED);
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity("Something Went Wrong!",HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateStatus(Map<String, String> requestMap) {
        try{

            if(jwtFilter.isAdmin()){

                Optional optional = productDao.findById(Integer.parseInt(requestMap.get("id")));
                if(!optional.isEmpty()){

                    productDao.updateProductStatus(requestMap.get("status"),Integer.parseInt(requestMap.get("id")));
                    return CafeUtils.getResponseEntity("Product Status Updated Successfully..",HttpStatus.OK);

                }else{
                    return CafeUtils.getResponseEntity("Invalid Product Id",HttpStatus.BAD_REQUEST);
                }

            }else{
                return CafeUtils.getResponseEntity("Not Authorized",HttpStatus.UNAUTHORIZED);
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity("Something Went Wrong!",HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Override
    public ResponseEntity<List<ProductWrapper>> getByCategory(Integer id) {
        try{

            return new ResponseEntity<>(productDao.getProductByCategory(id),HttpStatus.OK);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<ProductWrapper> getProductById(Integer id) {
        try{

            return new ResponseEntity<>(productDao.getProductById(id),HttpStatus.OK);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ProductWrapper(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Product getProductFromMap(Map<String, String> requestMap,boolean isAdd) {
        Category category = new Category();
        category.setId(Integer.parseInt(requestMap.get("categoryId")));
        Product product = new Product();
        product.setCategory(category);
        if(isAdd){
            product.setId(Integer.parseInt(requestMap.get("id")));
        }else {
            product.setStatus("true");
        }
        product.setName(requestMap.get("name"));
        product.setDescription(requestMap.get("description"));
        product.setPrice(Integer.parseInt(requestMap.get("price")));
        return product;


    }

    private boolean validateProductMap(Map<String, String> requestMap, boolean validate) {

        if (requestMap.containsKey("name")){
            if(requestMap.containsKey("id") && validate){
                return true;
            } else if (!validate) {
                return true;
                
            }

        }
        return false;

    }
}
