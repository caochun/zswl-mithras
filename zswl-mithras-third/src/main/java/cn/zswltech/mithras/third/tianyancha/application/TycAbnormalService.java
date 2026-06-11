package cn.zswltech.mithras.third.tianyancha.application;

import cn.zswltech.mithras.customer.externaldata.tianyancha.mapper.model.TycAbnormal;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 经营异常
 *
 * @author wangchuanhao
 * @date 2022/6/20 11:43 PM
 */
public interface TycAbnormalService extends IService<TycAbnormal> {

    List<TycAbnormal> queryAllFromTyc(Long clientId, String clientName, String uscCode);

    void flushData(Long clientId, List<TycAbnormal> newDataList);

}
