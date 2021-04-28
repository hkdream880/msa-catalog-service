package com.example.catalogservice.messageque;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.catalogservice.jpa.CatalogEntity;
import com.example.catalogservice.jpa.CatalogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KafkaConsumer {

	CatalogRepository catalogRepository;
	
	@Autowired
	public KafkaConsumer(CatalogRepository catalogRepository) {
		this.catalogRepository = catalogRepository;
	}
	
	@KafkaListener(topics = "example-catalog-topic")
	public void pu(String kafkaMessage) {
		log.info("kafka message: => " + kafkaMessage);
		
		Map<Object, Object> map = new HashMap<Object, Object>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			map = mapper.readValue(kafkaMessage, new TypeReference<Map<Object, Object>>() {});
		} catch (JsonProcessingException ex) {
			ex.printStackTrace();
		}
		
		CatalogEntity entity = catalogRepository.findByProductId((String)map.get("productId"));
		if(entity != null) {
			entity.setStock(entity.getStock() - (Integer)map.get("qty"));
			catalogRepository.save(entity); 
		}
		
				
	}
}
