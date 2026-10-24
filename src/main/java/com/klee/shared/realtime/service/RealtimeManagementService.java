package com.klee.shared.realtime.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.klee.shared.realtime.model.RealtimeConnectionDetailVO;
import com.klee.shared.realtime.model.RealtimeConnectionPageQuery;
import com.klee.shared.realtime.model.RealtimeConnectionPageVO;
import com.klee.shared.realtime.model.RealtimeSummaryVO;

public interface RealtimeManagementService {

    RealtimeSummaryVO getSummary();

    Page<RealtimeConnectionPageVO> getConnectionPage(RealtimeConnectionPageQuery query);

    RealtimeConnectionDetailVO getConnectionDetail(String connectionId);

    boolean disconnect(String connectionId, String reason);

    boolean banChat(String connectionId, String reason);

    boolean unbanChat(String connectionId);
}