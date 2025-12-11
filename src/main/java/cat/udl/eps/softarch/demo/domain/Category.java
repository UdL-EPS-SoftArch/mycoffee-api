package cat.udl.eps.softarch.demo.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@EqualsAndHashCode(callSuper = true)
@Entity(name = "Category")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Category extends UriEntity<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Length(min = 1, max = 50)
    @Column(unique = true)
    private String name;

    @NotBlank
    @Length(min = 1, max = 255)
    @Column(length = 255)
    private String description;

}
