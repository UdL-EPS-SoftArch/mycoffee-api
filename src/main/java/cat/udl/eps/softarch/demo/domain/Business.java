package cat.udl.eps.softarch.demo.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.time.LocalTime;
import java.util.Collection;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Business extends User {

    @NotEmpty
    private String name;

    @NotEmpty
    private String address;

    @Min(0)
    @Max(5)
    private Double rating;
    private Integer capacity;
    private Boolean hasWifi;
    private LocalTime openingTime;
    private LocalTime closingTime;
    @Enumerated(EnumType.STRING)
    @JsonProperty("registrationStatus")
    private BusinessStatus status;
    private String imageUrl;

    @Override
    @JsonValue(false)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_BUSINESS");
    }

    @JsonProperty("ownerId")
    public String getOwnerId() {
        return this.getId();
    }

    @JsonProperty("status")
    public String getOpenStatus() {
        if (openingTime == null || closingTime == null) {
            return "Closed";
        }
        LocalTime now = LocalTime.now();
        return (now.isAfter(openingTime) && now.isBefore(closingTime)) ? "Open" : "Closed";
    }
}
