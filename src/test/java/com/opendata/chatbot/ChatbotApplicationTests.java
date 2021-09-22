package com.opendata.chatbot;

import com.netflix.graphql.dgs.DgsQueryExecutor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ChatbotApplicationTests {

	@Test
	void contextLoads() {
	}
	@Autowired
	DgsQueryExecutor dgsQueryExecutor;

	@Test
	void shows() {
		List<String> titles = dgsQueryExecutor.executeAndExtractJsonPath(
				" { shows { title releaseYear }}",
				"data.shows[*].title");

		assertThat(titles).contains("Ozark");
	}
}
