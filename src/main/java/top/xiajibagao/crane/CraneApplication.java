package top.xiajibagao.crane;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import top.xiajibagao.crane.annotation.EnableCrane;

@EnableCrane
@SpringBootApplication
public class CraneApplication {

    public static void main(String[] args) {
        SpringApplication.run(CraneApplication.class, args);
    }

}
