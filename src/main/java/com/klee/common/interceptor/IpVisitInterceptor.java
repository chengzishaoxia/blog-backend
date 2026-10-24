package com.klee.common.interceptor;

import com.klee.system.mapper.LogMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

import static cn.dev33.satoken.SaManager.log;

/**
 * @User: 宇
 * @Date: 2026-04-20
 * @Time: 19:07
 * @Description:
 */
@Component
public class IpVisitInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private LogMapper visitLogMapper; // 假设你使用 MyBatis

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String ip = getRealIp(request);
        String infoIp = request.getRequestURI(); // 获取访问绝对路径
        String today = LocalDate.now().toString();
        String redisKey = "blog:visit:ips:" + today;

        // 🌟 新增核心逻辑：定义该 IP 当天访问次数的 Redis Key
        // 键名格式如：blog:visit:ip:count:2026-05-16:124.236.29.59
        String ipCountKey = "blog:visit:ip:count:" + today + ":" + ip;

        // 🌟 每次请求进来，该 IP 的计数器直接自增 1（放在 if 外面，确保每次重复访问都能计入）
        Long ipVisitCount = redisTemplate.opsForValue().increment(ipCountKey);

        // 如果自增后的值是 1，说明这是该 IP 今天的第一次访问，为其设置 48 小时过期时间
        // 设置 48 小时是为了防止长期占用 Redis 内存，同时方便我们在第二天对账或分析数据
        if (ipVisitCount != null && ipVisitCount == 1) {
            redisTemplate.expire(ipCountKey, 48, TimeUnit.HOURS);
        }

        // 核心逻辑：使用 Redis SISMEMBER 检查 IP 是否已存在
        Boolean isVisited = redisTemplate.opsForSet().isMember(redisKey, ip);

        if (Boolean.FALSE.equals(isVisited)) {
            // 1. Redis 中没有，说明是今天第一次访问
            // 2. 写入数据库持久化
            try {
                visitLogMapper.save(ip, today, infoIp);
                // 3. 写入 Redis 缓存，下次访问就不用查库了
                redisTemplate.opsForSet().add(redisKey, ip);
                // 4. 如果是该 Key 的第一个元素，设置过期时间为 24 小时
                redisTemplate.expire(redisKey, 24, TimeUnit.HOURS);
            } catch (Exception e) {
                // 防止数据库唯一索引冲突导致的报错
                log.error("IP 记录写入失败或已存在: {}", ip);
            }
        }

        // 🌟 此时变量 ipVisitCount 内部就存放着当前 IP 当天的总访问次数
        // 如果后续业务需要用到（比如防刷限流，当次数 > 1000 封禁），可以直接拿来做业务判断

        return true; // 放行请求
    }


    /**
     * 获取客户端真实 IP
     */
    private String getRealIp(HttpServletRequest request) {
        // 1. 优先读取你在 Nginx 中配置的 X-Real-IP 头部
        String ip = request.getHeader("X-Real-IP");

        // 2. 如果没有，尝试读取 X-Forwarded-For（处理多级代理）
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("x-forwarded-for");
            if (ip != null && ip.contains(",")) {
                // 如果有多个 IP，取第一个非 unknown 的
                ip = ip.split(",")[0];
            }
        }

        // 3. 最后如果还是拿不到，才使用最基础的 getRemoteAddr
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 4. 处理本地测试时的 IPv6 地址 (0:0:0:0:0:0:0:1) 转换为 127.0.0.1
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }
}
