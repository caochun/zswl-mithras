package cn.zswltech.mithras.service.service.client.copyhandler;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author dingqi
 * @date 2024/9/11
 * @description
 */
public interface ClientNewDataHelper<T> extends IService<T> {
    void removeByClientUser(Long clientId, Long userId);

    List<T> findByClientUser(Long clientId, Long userId);
}
