package cn.master.yukio;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 11's papa
 */
@SpringBootApplication
@MapperScan("cn.master.yukio.mapper")
public class YukioApplication {

    public static void main(String[] args) {
        SpringApplication.run(YukioApplication.class, args);
    }

}
