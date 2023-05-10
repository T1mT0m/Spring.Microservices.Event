package com.github.bwar.sb.ms.orderservice.service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;

import com.github.bwar.sb.ms.orderservice.dto.InventoryResponse;
import com.github.bwar.sb.ms.orderservice.dto.OrderLineItemsDto;
import com.github.bwar.sb.ms.orderservice.dto.OrderRequest;
import com.github.bwar.sb.ms.orderservice.event.OrderPlacedEvent;
import com.github.bwar.sb.ms.orderservice.model.Order;
import com.github.bwar.sb.ms.orderservice.model.OrderLineItems;
import com.github.bwar.sb.ms.orderservice.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
	
	private final OrderRepository orderRepository;
	private final WebClient.Builder webClient;
	private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;
	
	public void placeOrder(OrderRequest orderRequest) {
		Order order = new Order();
		order.setOrderNumber(UUID.randomUUID().toString());
		
		List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
			.stream()
			.map(this:: mapToDo)
			.toList();

		order.setOrderLineItems(orderLineItems);
		/*
		// call the inventory service and place order only if the product is available
		Boolean getskuData = webClient.get()
			.uri("http://localhost:8082/api/inventory")					
			.retrieve()
			.bodyToMono(Boolean.class)//to get the return type
			.block(); //to make the call synchronous

		if (getskuData) {
			orderRepository.save(order);
		}else {
			throw new IllegalArgumentException("Product not in stock, please try again later");
		}
		*/
		//if there are a list of SKUCODES to be checked
		List<String> skuCodes = order.getOrderLineItems().stream()
				.map(OrderLineItems::getSkuCode)
				.toList();
		
		
		// call the inventory service and place order only if the products is available
		InventoryResponse[] inventoryResponseArray = webClient.build().get()
			//.uri("http://localhost:8082/api/inventory",
			.uri("http://inventory-service/api/inventory",
					uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
			.retrieve()
			.bodyToMono(InventoryResponse[].class)//to get the return type
			.block(); //to make the call synchronous
		
		boolean allProductsinStock = Arrays.stream(inventoryResponseArray)
				.allMatch(InventoryResponse::isInStock);
		
		if (allProductsinStock) {
			orderRepository.save(order);
			kafkaTemplate.send("notificationTopic",new OrderPlacedEvent(order.getOrderNumber()));
		}else {
			throw new IllegalArgumentException("Product not in stock, please try again later");
		}
		
	}
	
	private OrderLineItems mapToDo(OrderLineItemsDto orderLineItemsDto) {
		OrderLineItems orderLineItems = new OrderLineItems();
		orderLineItems.setPrice(orderLineItemsDto.getPrice());
		orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
		orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
		return orderLineItems;
	}

}
