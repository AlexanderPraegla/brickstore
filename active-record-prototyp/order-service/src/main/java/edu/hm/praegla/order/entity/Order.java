package edu.hm.praegla.order.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Min(1)
    @Column(name = "account_id")
    private long accountId;
    @DecimalMin(value = "1")
    @Column(precision = 7, scale = 2)
    private BigDecimal total;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private ShippingAddress shippingAddress;

    @Column(name = "created_on", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdOn;

    @Column(name = "modified_on", columnDefinition = "TIMESTAMP")
    private LocalDateTime modifiedOn;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<OrderItem> orderItems;

    @PrePersist
    public void prePersist() {
        createdOn = LocalDateTime.now();
        modifiedOn = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        modifiedOn = LocalDateTime.now();
    }

}
