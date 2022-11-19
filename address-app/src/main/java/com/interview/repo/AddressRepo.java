package com.interview.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.interview.model.Address;

public interface AddressRepo extends JpaRepository<Address, Integer> {

	@Query(value = "select s.* from address_table s where s.id like %:keyword% or s.line1 like %:keyword% or s.line2 like %:keyword%"
			+ " or s.line3 like %:keyword% or s.line4 like %:keyword% + or s.postcode like %:keyword% ", nativeQuery = true)
	Page<Address> findByKeyword(Pageable pageble, @Param("keyword") String keyword);

	List<Address> findByUserid(Integer userid);
}
