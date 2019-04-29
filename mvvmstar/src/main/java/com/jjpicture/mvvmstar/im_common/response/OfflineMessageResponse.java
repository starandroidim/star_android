package com.jjpicture.mvvmstar.im_common.response;

import java.util.List;
import java.util.Map;

/**
 * @author lyl
 * @Title: OfflineMessageResponse
 * @Description: 离线消息res
 * @date 2019/3/14 19:31
 */
public class OfflineMessageResponse {
    private Long userId;

    private Map<Long, List<String>> map;

    public Long getUserId() {
        return userId;
    }

    public Map<Long, List<String>> getMap() {
        return map;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setMap(Map<Long, List<String>> map) {
        this.map = map;
    }
}
