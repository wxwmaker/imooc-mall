package com.imooc.mall.config;

import com.imooc.mall.filter.AdminFilter;
import com.imooc.mall.filter.UserFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 描述：     user过滤器的配置
 */
@Configuration
public class UserFilterConfig {

    @Bean
    public UserFilter userFilter() {
        return new UserFilter();
    } //吧filter定义出来
//把filter 放到整个过滤器的链路中去
    @Bean(name = "userFilterConf")
    public FilterRegistrationBean adminFilterConfig() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(userFilter());
        filterRegistrationBean.addUrlPatterns("/cart/*"); //要拦截的url
        filterRegistrationBean.addUrlPatterns("/order/*"); //要拦截的url
        filterRegistrationBean.setName("userFilterConf");//过滤器设置名字
        return filterRegistrationBean;
    }
}
