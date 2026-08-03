package FinanceManangementSystem.demo.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "supplier_address")
public class SupplierAddress{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int houseNo;

    private String societyName;

    private String area;

    private String city;

    private int pincode;

    private String state;

    private String country;

    @OneToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @PrePersist
    public void prePersist(){
        if(this.country == null){
            this.country = "India";
        }
    }


}
