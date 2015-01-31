package org.msh.pharmadex.domain;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: usrivastava
 * Date: 1/11/12
 * Time: 11:22 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "pur_prod")
public class PurProd extends CreationDetail implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 255, nullable = false)
    private String productNo;

    @Column(length = 255, nullable = false)
    private String productName;

    @Column(length = 255, nullable = true)
    private String productDesc;

    @OneToOne
    private Product product;

    @Column(length = 255, nullable = false)
    private String unit;

    @Column(length = 255, nullable = false)
    private String quantity;

    @Column(length = 255, nullable = false)
    private String unitPrice;

    @Column(length = 255)
    private String totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purorder_id", nullable = false)
    private PurOrder purOrder;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public PurOrder getPurOrder() {
        return purOrder;
    }

    public void setPurOrder(PurOrder purOrder) {
        this.purOrder = purOrder;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }
}
