package com.lmlasmo.gioscito;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import com.lmlasmo.gioscito.data.MongoDBTestContainerConf;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes =  {MongoDBTestContainerConf.class})
class GioscitoApplicationTests {

	@Test
	void contextLoads() {
	}

}
