package com.athul.library.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Entity
@Table(name="customers", uniqueConstraints = @UniqueConstraint(columnNames = {"username"}))
public class Customer implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long id;
    @Size(min=3,max = 15,message ="first name should have 3-15 characters" )
    private String firstName;
    @Size(min=3,max = 15,message ="last name should have 3-15 characters" )

    private String lastName;
    @Column(name = "username")
    private String username;

    private String password;
    @Column(name="phone_number")
    private String phoneNumber;


    @ToString.Exclude
    @ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinTable(name="customer_roles",
            joinColumns = @JoinColumn(name="customer_id",referencedColumnName = "customer_id"),
            inverseJoinColumns = @JoinColumn(name="role_id",referencedColumnName = "role_id"))
    private Collection<Role> roles;

    @ToString.Exclude
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Address> address;



    private boolean is_blocked;

    private long otp;

    private boolean isActive;

    @ToString.Exclude
    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL)
    private ShoppingCart cart;

    @ToString.Exclude
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Order> orders;

    @ToString.Exclude
    @OneToOne(mappedBy = "customer")
    private Wallet wallet;

    @ToString.Exclude
    private String referralCode;




//    public Customer() {
//        this.cart = new ShoppingCart();
//    }

//    @Override
//    public String toString() {
//        String cartIdString = (cart != null) ? cart.getId().toString() : "null";
//        return "Customer{" +
//                "id=" + id +
//                ", firstName='" + firstName + '\'' +
//                ", lastName='" + lastName + '\'' +
//                ", username='" + username + '\'' +
//                ", password='" + password + '\'' +
//                ", phoneNumber='" + phoneNumber + '\'' +
//                ", isActive=" + isActive +
//                '}';
//    }
}
