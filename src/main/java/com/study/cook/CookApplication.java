package com.study.cook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.filter.HiddenHttpMethodFilter;

@EnableJpaAuditing // 엔티티 객체가 생성이 되거나 변경이 되었을 때 자동으로 값 등록
@SpringBootApplication
public class CookApplication {

	public static final String APPLICATION_LOCATIONS = "spring.config.location=classpath:application.yml, /home/ec2-user/app/config/springboot-webservice/real-application.yml";
	
	public static void main(String[] args) {
//		SpringApplication.run(CookApplication.class, args);
		new SpringApplicationBuilder(CookApplication.class).properties(APPLICATION_LOCATIONS).run(args);
	}

	// put, delete 매핑 사용
	@Bean
	public HiddenHttpMethodFilter hiddenHttpMethodFilter(){
		return new HiddenHttpMethodFilter();
	}

}
