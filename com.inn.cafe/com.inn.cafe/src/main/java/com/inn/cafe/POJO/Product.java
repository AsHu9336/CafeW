package com.inn.cafe.POJO;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;

@Entity
@Data
@DynamicUpdate
@DynamicInsert
@Table(name = "product")
@NamedQuery(name = "Product.getAllProduct",query = "select new com.inn.cafe.wrapper.ProductWrapper(p.id,p.name,p.description,p.price,p.status,p.category.id,p.category.name) from Product p")

@NamedQuery(name = "Product.updateProductStatus",query = "update Product p set p.status=:status where p.id=:id")

@NamedQuery(name = "Product.getProductByCategory",query = "select new com.inn.cafe.wrapper.ProductWrapper(p.id,p.name,p.price) from Product p where p.category.id=:id and p.status='true'")

@NamedQuery(name = "Product.getProductById",query = "select new com.inn.cafe.wrapper.ProductWrapper(p.id,p.name,p.description,p.price) from Product p where p.id=:id")


public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "price")
    private Integer price;

    @Column(name = "status")
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_fk",nullable = false)
    private Category category;
}
