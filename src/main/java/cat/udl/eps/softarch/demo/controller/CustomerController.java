package cat.udl.eps.softarch.demo.controller;

import cat.udl.eps.softarch.demo.domain.Customer;
import cat.udl.eps.softarch.demo.repository.CustomerRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @PostMapping
    public Customer register(@RequestBody Customer customer) {
        customer.encodePassword();              // 🔐 CLAVE
        customer.setRoles(Set.of("CUSTOMER"));  // 🔑 CLAVE
        return customerRepository.save(customer);
    }
}
