package com.github.bwar.sb.ms.inventoryservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.github.bwar.sb.ms.inventoryservice.dto.InventoryResponse;
import com.github.bwar.sb.ms.inventoryservice.service.InventoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
	
	private final InventoryService inventoryService;
	
	
	@GetMapping("/{sku-code}")
	@ResponseStatus(HttpStatus.OK)
	public boolean isInStock(@PathVariable("sku-code") String skuCode){
		return inventoryService.isInStockS(skuCode);
	}
	
	
	//http://localhost:8082/api/inventory/iphone-13,iphone13-red
	//http://localhost:8082/api/inventory?sku-code=iphone-13&sku-code=iphone13-red
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<InventoryResponse> isInStock(@RequestParam List<String> skuCode){	
		return inventoryService.isInStock(skuCode);
	}

}
