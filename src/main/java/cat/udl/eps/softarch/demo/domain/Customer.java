package cat.udl.eps.softarch.demo.domain;

import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.Collection;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Customer extends User {

    @NotEmpty
    private String name;

    @NotEmpty
    private String phoneNumber;


    @Override
    @JsonValue(value = false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_CUSTOMER");
    }


    //TODO
    // @OneToMany(mappedBy = "products")
    // private Set<Order> orders;

    //TODO
    // @OneToOne
    // private Loyalty loyalty;
}
