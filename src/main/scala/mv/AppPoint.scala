package mv

import org.slf4j.LoggerFactory
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.ComponentScan

object AppPoint extends App {

 val logger = LoggerFactory.getLogger(classOf[AppPoint])
 val applicationContext = SpringApplication.run(classOf[AppPoint])

// TODO ...

}

@EnableAutoConfiguration
@ComponentScan(basePackages = Array("mv/config", "mv/repo", "mv/service"))
@EntityScan(basePackages = Array("mv/model"))
class AppPoint {

}