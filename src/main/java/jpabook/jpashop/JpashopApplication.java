package jpabook.jpashop;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpashopApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpashopApplication.class, args);
	}

	//엔티티를 직접 노출 V1
	//jackson 라이브러리는 지연 로딩에 때문에 엔티티 대신 존재하는 프록시 객체를 json으로 어떻게 생성할지 모른다.
	//이를 Hibernate5Module을 통해 해결한다.
	//다음과 같이 설정시 강제로 지연 로딩 가능
	//단 양방향 연관관계가 걸린 곳은 꼭 한쪽에 @JsonIgnore로 처리
	@Bean
	Hibernate5Module hibernate5Module() {
		Hibernate5Module hibernate5Module = new Hibernate5Module();
		// hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true);
		return hibernate5Module;
	}

}
