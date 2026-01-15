package cat.udl.eps.softarch.demo.domain;

import cat.udl.eps.softarch.demo.domain.UriEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Inventory extends UriEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    private String description;

    @NotBlank
    private String location;

    private int totalStock;

    @ManyToOne
    private Business business;


}
