package com.athul.library.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="products", uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))

public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="product_id")
    private Long id;
    private String name;
    private String description;
    private double costPrice;
    private double salePrice;
    private int currentQuantity;

    @ElementCollection
    @CollectionTable(name = "product_image_urls", joinColumns = @JoinColumn(name = "product_id"))
    private List<String> imageUrls;
    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name="category_id",referencedColumnName = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinColumn(name="brand_id",referencedColumnName = "brand_id")
    private Brand brand;

    @OneToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinTable(name="product_size",
            joinColumns=@JoinColumn(name="product_id",referencedColumnName = "product_id"),
            inverseJoinColumns=@JoinColumn(name="size_id",referencedColumnName = "size_id"))
    private Collection<Size> size;

    private boolean productDeleted;
    private boolean productActivated;



}
