package cn.zswltech.mithras.third.service.tyc;

import cn.zswltech.mithras.client.externaldata.tianyancha.infrastructure.model.TycEquityInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 股权出质
 *
 * @author wangchuanhao
 * @date 2022/6/20 11:43 PM
 */
public interface TycEquityInfoService extends IService<TycEquityInfo> {

    List<TycEquityInfo> queryAllFromTyc(Long clientId, String clientName, String uscCode);

    void flushData(Long clientId, List<TycEquityInfo> newDataList);


}
