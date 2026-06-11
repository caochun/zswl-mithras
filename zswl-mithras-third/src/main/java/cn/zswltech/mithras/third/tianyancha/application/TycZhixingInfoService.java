package cn.zswltech.mithras.third.tianyancha.application;

import cn.zswltech.mithras.customer.externaldata.tianyancha.mapper.model.TycZhixingInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 被执行人
 *
 * @author wangchuanhao
 * @date 2022/6/20 11:43 PM
 */
public interface TycZhixingInfoService extends IService<TycZhixingInfo> {

    List<TycZhixingInfo> queryAllFromTyc(Long clientId, String clientName, String uscCode);

    void flushData(Long clientId, List<TycZhixingInfo> newDataList);


}
