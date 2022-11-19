package com.interview.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.interview.model.Address;
import com.interview.repo.AddressRepo;

@RestController
public class AddressController {

	@Autowired
	AddressRepo addressService;

	@PostMapping("/addresses")
	public ResponseEntity<Address> addAddress(@Valid @RequestBody Address address) {
		final var addressdb = addressService.save(address);
		final var location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(addressdb.getId()).toUri();
		return ResponseEntity.created(location).build();
	}

	@DeleteMapping("/addresses/{id}")
	public ResponseEntity<Address> deleteAddress(@PathVariable String id) {
		final var addressdb = addressService.findById(Integer.parseInt(id))
				.orElseThrow(() -> new RuntimeException(String.format("Address with ID as %s  not found", id)));
		addressService.delete(addressdb);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/addresses/{id}")
	public ResponseEntity<EntityModel<Address>> retriveAddressById(@PathVariable String id) {
		final var addressdb = addressService.findById(Integer.parseInt(id))
				.orElseThrow(() -> new RuntimeException(String.format("Address with ID as %s  not found", id)));

		final var newLinK = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).retriveAllAddress())
				.withRel("Additional Address info Link");

		return ResponseEntity.ok(EntityModel.of(addressdb, newLinK));
	}

	@GetMapping("/addresses")
	public ResponseEntity<List<Address>> retriveAllAddress() {
		return ResponseEntity.ok(addressService.findAll());
	}

	@GetMapping("/addresses/users/{id}")
	public ResponseEntity<List<Address>> retriveAllAddressByUserId(@PathVariable String id) {
		return ResponseEntity.ok(addressService.findByUserid(Integer.parseInt(id)));
	}

	@GetMapping("/v1/addresses")
	public ResponseEntity<Page<Address>> retriveAllAddressWithsearch(
			@RequestParam(required = false, defaultValue = "0") int pageNo,
			@RequestParam(required = false, defaultValue = "10") int pageSize,
			@RequestParam(required = false, defaultValue = "id#desc") String[] sortAndOrder,
			@RequestParam(required = false, defaultValue = "") String searchCriteria) {

		// return ResponseEntity.ok(addressService.findAll());
		final List<Order> orders = Arrays.stream(sortAndOrder).filter(s -> s.contains("#")).map(s -> s.split("#"))
				.map(arr -> new Order(Direction.fromString(arr[1]), arr[0])).collect(Collectors.toList());

		final Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(orders));

		final var data = searchCriteria.isBlank() ? addressService.findAll(paging)
				: addressService.findByKeyword(paging, searchCriteria);

		return ResponseEntity.ok(data);
	}

	@PutMapping("/addresses/{id}")
	public ResponseEntity<Address> updateAddress(@PathVariable String id, @Valid @RequestBody Address address) {
		final var addressdb = addressService.findById(Integer.parseInt(id))
				.orElseThrow(() -> new RuntimeException(String.format("Address with ID as %s  not found", id)));
		address.setId(addressdb.getId());
		addressService.save(address);
		return ResponseEntity.accepted().build();
	}

}
