package com.inn.cafe.dao;

import com.inn.cafe.POJO.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BillDao extends JpaRepository<Bill,Integer> {

    List<Bill> getBillByUsername(@Param("username") String username);

    List<Bill> getAllBills();
}
