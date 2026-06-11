package cn.zswltech.mithras.third.tianyancha.application;

import cn.zswltech.mithras.customer.externaldata.tianyancha.model.TycConsumptionRestriction;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 法律诉讼
 *
 * @author wangchuanhao
 * @date 2022/6/20 11:43 PM
 */
public interface TycConsumptionRestrictionService extends IService<TycConsumptionRestriction> {

    List<TycConsumptionRestriction> queryAllFromTyc(Long clientId, String clientName, String uscCode);

    void flushData(Long clientId, List<TycConsumptionRestriction> newDataList);


}
