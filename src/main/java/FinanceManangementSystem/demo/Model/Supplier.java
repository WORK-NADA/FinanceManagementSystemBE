package FinanceManangementSystem.demo.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

@Entity(name = "supplier")
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long supplierId;

    private String name;

    private long contact;

    private String email;

    private String gstNo;

    private String panNo;

    @OneToOne(cascade = CascadeType.ALL,mappedBy = "supplier")
    private SupplierAddress address;

}
