package db_praktikum.entities_collection;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity 
@Table(name = "cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @OneToMany(mappedBy = "cart")
    private List<Cartposition> cartPositions = new ArrayList<>();
    @Column(name = "cart_id", nullable =false)
    private Integer cartId;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "cart_time", nullable =false)
    private LocalDateTime cartTime;

    @Column(name = "shipping_address", nullable =false)  
    private String shippingAddress;

    @Column(name = "bank_account", nullable =false)
    private String bankAccount;

    public Cart() {
    }

public Cart(Customer customer, LocalDateTime cartTime, String shippingAddress, String bankAccount) {
    this.customer = customer;
    this.cartTime = cartTime;
    this.shippingAddress = shippingAddress;
    this.bankAccount = bankAccount;
}

    public Integer getCartId() {
        return cartId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public LocalDateTime getCartTime() {
        return cartTime;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setCartTime(LocalDateTime cartTime) {
        this.cartTime = cartTime;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "cartId=" + cartId +
                ", customer=" + customer +
                ", cartTime=" + cartTime +
                ", shippingAddress='" + shippingAddress + '\'' +
                ", bankAccount='" + bankAccount + '\'' +
                '}';
    }
}
