package com.cr999.cn.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件描述：
 *
 * @version 1.0
 * @author wangqiang
 * @date 2021/4/5 0:02 
 */
@SpringBootApplication(scanBasePackages = "com.cr999.cn")
@MapperScan(basePackages = {"com.cr999.cn.user.biz.mapper"})
@EnableSwagger2
public class User_Apper {
    public static void main(String[] args) {
        SpringApplication.run(User_Apper.class,args);
    }

    @Bean // 配置docket以配置Swagger具体参数
    public Docket docket(Environment environment) {

        //配置swaagger可以在header输入参数token值
        ParameterBuilder tokenPar=new ParameterBuilder();
        List<Parameter> parameters=new ArrayList<Parameter>();
        tokenPar.name("token")//参数名
                .description("token值")//参数描述
                .modelRef(new ModelRef("string"))//数据类型
                .parameterType("header")//参数类型
                .required(false).build();
        parameters.add(tokenPar.build());

        return new Docket(DocumentationType.SWAGGER_2)
                .select().apis(RequestHandlerSelectors.any())
                .build().globalOperationParameters(parameters)
                .apiInfo(new ApiInfoBuilder()
                        .title("用户中心-Swagger2集成")
                        .version("1.0")
                        .build())// 关联配置文档
                .enable(true)// 是否启用
                .select()//扫描方法
                .paths(PathSelectors.any())//路径过滤
                .build();//构建
    }
}
