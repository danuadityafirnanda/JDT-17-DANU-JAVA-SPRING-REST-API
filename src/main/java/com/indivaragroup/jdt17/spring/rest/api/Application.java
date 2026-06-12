package com.indivaragroup.jdt17.spring.rest.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
buat crud sederhana


ada 1 entitiy namanya customer ada id,name, email, phone, addres (kota),

tipe data id: uuid format strring

buatkan 4 fitur sederhana dulu

create data customer

get data customer bisa 1 customer atau banyak

update customer

hapus customer




STRUKTURNYA

pintu masuk ada di controller

bisnis logic ada di service

hibernatenya repository


 */

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
