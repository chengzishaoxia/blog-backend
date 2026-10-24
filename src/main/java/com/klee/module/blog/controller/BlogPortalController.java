package com.klee.module.blog.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.klee.common.result.Result;
import com.klee.common.util.IPUtils;
import com.klee.module.blog.model.vo.BlogFooterStatsVO;
import com.klee.module.blog.model.vo.BlogWeatherVO;
import com.klee.system.model.vo.VisitStatsVO;
import com.klee.system.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "博客门户")
@RequestMapping("/api/v1/blog/portal")
public class BlogPortalController {

    private final LogService logService;

    @Operation(summary = "前台: 获取当前天气")
    @GetMapping("/weather")
    @SaIgnore
    public Result<BlogWeatherVO> getCurrentWeather(HttpServletRequest request) {
        return Result.success(resolveWeather(request));
    }

    @Operation(summary = "前台: 获取页脚统计")
    @GetMapping("/footer-stats")
    @SaIgnore
    public Result<BlogFooterStatsVO> getFooterStats() {
        VisitStatsVO visitStats = logService.getVisitStats();
        BlogFooterStatsVO result = new BlogFooterStatsVO();
        result.setTotalPvCount(visitStats == null || visitStats.getTotalPvCount() == null ? 0 : visitStats.getTotalPvCount());
        return Result.success(result);
    }

    private BlogWeatherVO resolveWeather(HttpServletRequest request) {
        BlogWeatherVO vo = new BlogWeatherVO();
        vo.setReportTime(LocalDateTime.now());

        String region = IPUtils.getRegion(IPUtils.getIpAddr(request));
        String province = "";
        String city = "";
        if (StrUtil.isNotBlank(region)) {
            String[] regionArray = region.split("\\|");
            if (regionArray.length > 2) {
                province = normalizeRegionPart(regionArray[2]);
            }
            if (regionArray.length > 3) {
                city = normalizeRegionPart(regionArray[3]);
            }
        }
        String location = StrUtil.blankToDefault(city, StrUtil.blankToDefault(province, "石家庄"));
        vo.setProvince(province);
        vo.setCity(city);
        vo.setLocation(location);

        try (HttpResponse response = HttpUtil.createGet("https://wttr.in/" + URLEncoder.encode(location, StandardCharsets.UTF_8) + "?format=j1&lang=zh-cn")
            .timeout(5000)
            .execute()) {
            String body = response.body();
            JSONObject data = JSONUtil.parseObj(body);
            JSONObject current = firstObject(data.getJSONArray("current_condition"));
            JSONObject today = firstObject(data.getJSONArray("weather"));
            vo.setWeather(extractWeatherText(current));
            vo.setTemperature(current.getStr("temp_C"));
            vo.setWindDirection(current.getStr("winddir16Point"));
            vo.setWindScale(current.getStr("windspeedKmph"));
            vo.setHumidity(current.getStr("humidity"));
            vo.setHighTemperature(today.getStr("maxtempC"));
            vo.setLowTemperature(today.getStr("mintempC"));
        } catch (Exception ex) {
            log.warn("Failed to fetch weather for location {}: {}", location, ex.getMessage());
        }
        return vo;
    }

    private JSONObject firstObject(JSONArray array) {
        if (array == null || array.isEmpty()) {
            return new JSONObject();
        }
        return array.getJSONObject(0);
    }

    private String extractWeatherText(JSONObject current) {
        if (current == null || current.isEmpty()) {
            return "";
        }
        JSONArray langZh = current.getJSONArray("lang_zh");
        if (langZh != null && !langZh.isEmpty()) {
            return langZh.getJSONObject(0).getStr("value");
        }
        JSONArray desc = current.getJSONArray("weatherDesc");
        if (desc != null && !desc.isEmpty()) {
            return desc.getJSONObject(0).getStr("value");
        }
        return "";
    }

    private String normalizeRegionPart(String value) {
        if (StrUtil.isBlank(value) || "0".equals(value) || "内网IP".equalsIgnoreCase(value)) {
            return "";
        }
        return value;
    }
}