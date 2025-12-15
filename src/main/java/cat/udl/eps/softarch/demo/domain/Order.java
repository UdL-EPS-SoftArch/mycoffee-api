package cat.udl.eps.softarch.demo.domain;


import com.fasterxml.jackson.annotation.JsonIdentityReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.Set;

@Entity
@Table(name = "order_table")
@Data
public class Order extends UriEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private ZonedDateTime created;

    @NotNull
    private ZonedDateTime serveWhen;

    @NotBlank
    private String paymentMethod;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        SENT, RECEIVED, CANCELLED, IN_PROCESS, READY, PICKED
    }

    @ManyToMany
    @JsonIdentityReference(alwaysAsId = true)
    private Set<Product> products;

    @ManyToOne
    private Customer customer;
}
