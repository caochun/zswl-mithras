package cn.zswltech.mithras.customer.application.client.copyhandler;

import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author dingqi
 * @date 2024/9/11
 * @description
 */
public interface ClientOldDataHelper<T> extends IService<T> {
    void removeByClientId(Long clientId);

    List<T> findByClientId(Long clientId);
}
