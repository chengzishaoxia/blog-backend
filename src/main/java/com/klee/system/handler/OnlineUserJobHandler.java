package com.klee.system.handler;


import com.klee.shared.realtime.RealtimeMessagingService;
import com.klee.system.service.UserOnlineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 在线用户定时任务
 *
 * @since 2024/10/7
 *
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class OnlineUserJobHandler {

    private final UserOnlineService userOnlineService;
    private final RealtimeMessagingService realtimeMessagingService;

    // 每3分钟统计一次在线用户数，减少服务器压力
    @Scheduled(cron = "0 */3 * * * ?")
    public void execute() {
        log.info("定时任务：统计在线用户数");
        userOnlineService.getOnlineUserCount();
        realtimeMessagingService.publishOnlineCount();
    }

}
