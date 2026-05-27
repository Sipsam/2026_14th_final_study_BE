package com.example.demo;

import com.likelion.finalstudy.FinalStudyApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = FinalStudyApplication.class)
@ActiveProfiles("test")
class FinalApplicationTests {

	@Test
	void contextLoads() {
	}

}
