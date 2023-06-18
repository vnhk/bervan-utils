package com.bervan.demo.app;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.bervan.demo")
@EntityScan("com.bervan.demo")
@DataJpaTest
public class AppTestConfig {

}
