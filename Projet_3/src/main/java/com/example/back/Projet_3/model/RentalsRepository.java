package com.example.back.Projet_3.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentalsRepository extends JpaRepository<Rentals, Integer>{
	public Optional<Rentals> findById(Integer id);
}
