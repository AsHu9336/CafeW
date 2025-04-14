package com.inn.cafe.serviceImpl;

import com.google.common.base.Strings;
import com.inn.cafe.JWT.CustomerUserDetailService;
import com.inn.cafe.JWT.JwtFilter;
import com.inn.cafe.JWT.JwtUtil;
import com.inn.cafe.POJO.User;
import com.inn.cafe.dao.UserDao;
import com.inn.cafe.service.UserService;
import com.inn.cafe.utils.CafeUtils;
import com.inn.cafe.utils.EmailUtils;
import com.inn.cafe.wrapper.UserWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service

public class UserServiceImpl implements UserService {

    @Autowired
    UserDao userDao;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    CustomerUserDetailService customerUserDetailService;

    @Autowired
    EmailUtils emailUtils;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    JwtFilter jwtFilter;
    @Override
    public ResponseEntity<String> signup(Map<String, String> requestMap) {
        try {
            log.info("Inside signup {}", requestMap);
            if (validateSignup(requestMap)) {
                User user = userDao.findEmailById(requestMap.get("email"));
                if (Objects.isNull(user)) {

                    userDao.save(getUserFromMap(requestMap));
                    return CafeUtils.getResponseEntity("Successfully Registered", HttpStatus.OK);

                } else {
                    return CafeUtils.getResponseEntity("Email already exists", HttpStatus.BAD_REQUEST);
                }
            }

            return CafeUtils.getResponseEntity("Invalid Data", HttpStatus.BAD_REQUEST);
        }catch (Exception ex){
            ex.printStackTrace();
        }

        return CafeUtils.getResponseEntity("Something Went Wrong",HttpStatus.INTERNAL_SERVER_ERROR);


    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Inside login {}",requestMap);
        try {

            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requestMap.get("email"),requestMap.get("password")));

            if(auth.isAuthenticated()){
                if(customerUserDetailService.getUserDetail().getStatus().equalsIgnoreCase("true")){
                    return new ResponseEntity<String>("{\"token\":\""+
                            jwtUtil.generateToken(customerUserDetailService.getUserDetail().getEmail(),customerUserDetailService.getUserDetail().getRole()) + "\"}",HttpStatus.OK);
                }
                else{
                   return new ResponseEntity<String>("{\"Message\":\""+"Wait for admin approval."+"\"}",HttpStatus.BAD_REQUEST);
                }
            }


        }catch (Exception ex){
            log.info("Example {}",ex);
            ex.printStackTrace();
        }
        return new ResponseEntity<String>("{\"Message\":\""+"Bad Credentials."+"\"}",HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUser() {
        try {

            if(jwtFilter.isAdmin()){

                return new ResponseEntity<>(userDao.getAllUser(),HttpStatus.OK);

            }else{
                return new ResponseEntity<>(new ArrayList<>(),HttpStatus.UNAUTHORIZED);
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<List<UserWrapper>>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try{

            if(jwtFilter.isAdmin()){
              Optional<User> optional =  userDao.findById(Integer.valueOf(requestMap.get("id")));
              if(!optional.isEmpty()){

                  userDao.updateStatus(requestMap.get("status"),Integer.parseInt(requestMap.get("id")));
                  sendMailToAllAdmin(requestMap.get("status"),optional.get().getEmail(),userDao.getAllAdmin());
                  return    CafeUtils.getResponseEntity("User updated Successfully!",HttpStatus.OK);



              }else{
                  return CafeUtils.getResponseEntity("User not Found!",HttpStatus.OK);
              }
            }else{
                return CafeUtils.getResponseEntity("Unauthorized",HttpStatus.UNAUTHORIZED);
            }

        }catch(Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity("Something Went Wrong!",HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> checkToken() {
        try{

            return CafeUtils.getResponseEntity("true",HttpStatus.OK);

        }catch (Exception ex){
            ex.printStackTrace();

        }
        return CafeUtils.getResponseEntity("Something Went Wrong!",HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try{

            User user = userDao.findEmailById(jwtFilter.getCurrentUser());
            if(!Objects.isNull(user)){
                if(user.getPassword().equals(requestMap.get("old password"))){

                    user.setPassword(requestMap.get("new password"));
                    userDao.save(user);
                    return CafeUtils.getResponseEntity("Password Updated Successfully!",HttpStatus.OK);

                }
                return CafeUtils.getResponseEntity("Incorrect Old Password",HttpStatus.BAD_REQUEST);
            }

        }catch (Exception ex){
            ex.printStackTrace();

        }
        return CafeUtils.getResponseEntity("Somrthing went Wrong",HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        try{

            User user = userDao.findEmailById(requestMap.get("email"));
            if (!Objects.isNull(user) && !Strings.isNullOrEmpty(user.getPassword())){

                emailUtils.forgotMail(user.getEmail(),"Credentials by Cafe Management System", user.getPassword());

                return CafeUtils.getResponseEntity("Check your Email",HttpStatus.OK);



            }else{
                return CafeUtils.getResponseEntity("User not found",HttpStatus.BAD_REQUEST);
            }

        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity("Something Wnt Wrong!",HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void sendMailToAllAdmin(String status, String user, List<String> allAdmin) {

        allAdmin.remove(jwtFilter.getCurrentUser());
        if(status!=null && status.equalsIgnoreCase("true")){

            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(),"Account Approved","USER-:" +user+ "\n is approved by \n ADMIN:-" + jwtFilter.getCurrentUser(),allAdmin);

        }else{

            emailUtils.sendSimpleMessage(jwtFilter.getCurrentUser(),"Account Disabled","USER-:" +user+ "\n is disabled by \n ADMIN:-" + jwtFilter.getCurrentUser(),allAdmin);

        }

    }

    private boolean validateSignup(Map<String,String> requestMap){
        if(requestMap.containsKey("name")&&requestMap.containsKey("contactNumber")&& requestMap.containsKey("email")&& requestMap.containsKey("password")){
            return true;
        }
        return false;
    }

    private User getUserFromMap(Map<String,String> requestMap){
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setEmail(requestMap.get("email"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setPassword(requestMap.get("password"));
        user.setEmail(requestMap.get("email"));
        user.setStatus("false");
        user.setRole("user");
        return user;

    }
}
