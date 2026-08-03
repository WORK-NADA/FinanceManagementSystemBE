package FinanceManangementSystem.demo.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "purchase")
public class Purchase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long purchaseId;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    private String item;

    private double price;

    private double weight;

    private LocalDate date;

    @Positive(message = "Amount must be greater than 0")
    @DecimalMax(value = "100000000.00", message = "Amount is too large")
    private double amount;

    private double gst;

    @Positive(message = "Amount with GST must be greater than 0")
    @DecimalMax(value = "100000000.00", message = "Amount with GST is too large")
    private double withGstAmount;

    @PrePersist
    public void prePersist(){
        if(this.getDate() == null){
            this.date = LocalDate.now();
        }
    }

}
