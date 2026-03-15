package com.example.javaweb.config;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Configuration
public class FastJsonWebConfig implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        FastJsonConfig config = new FastJsonConfig();

        // 配置日期格式
        String dateFormat = "yyyy-MM-dd HH:mm:ss";
        config.setDateFormat(dateFormat);

        config.setSerializerFeatures(
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.DisableCircularReferenceDetect,
                SerializerFeature.WriteDateUseDateFormat
        );

        // 配置 Java 8 日期时间类型序列化
        SerializeConfig serializeConfig = new SerializeConfig();
        // 为 LocalDateTime 配置自定义序列化器
        serializeConfig.put(LocalDateTime.class, new ObjectSerializer() {
            @Override
            public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
                if (object == null) {
                    serializer.writeNull();
                    return;
                }
                LocalDateTime localDateTime = (LocalDateTime) object;
                serializer.write(localDateTime.format(DateTimeFormatter.ofPattern(dateFormat)));
            }
        });

        config.setSerializeConfig(serializeConfig);
        config.setCharset(StandardCharsets.UTF_8);
        converter.setFastJsonConfig(config);
        converter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
        converters.add(0, converter);
    }
}