package com.klee.module.blog.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "博客天气信息")
public class BlogWeatherVO {

    @Schema(description = "省份")
    private String province;

    @Schema(description = "城市")
    private String city;

    @Schema(description = "展示位置")
    private String location;

    @Schema(description = "天气描述")
    private String weather;

    @Schema(description = "当前温度")
    private String temperature;

    @Schema(description = "最高温度")
    private String highTemperature;

    @Schema(description = "最低温度")
    private String lowTemperature;

    @Schema(description = "风向")
    private String windDirection;

    @Schema(description = "风速")
    private String windScale;

    @Schema(description = "湿度")
    private String humidity;

    @Schema(description = "更新时间")
    private LocalDateTime reportTime;
}