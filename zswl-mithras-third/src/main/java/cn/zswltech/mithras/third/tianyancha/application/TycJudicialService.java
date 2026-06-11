package cn.zswltech.mithras.third.tianyancha.application;

import cn.zswltech.mithras.customer.externaldata.tianyancha.mapper.model.TycJudicial;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 司法协助
 *
 * @author wangchuanhao
 * @date 2022/6/20 11:43 PM
 */
public interface TycJudicialService extends IService<TycJudicial> {

    List<TycJudicial> queryAllFromTyc(Long clientId, String clientName, String uscCode);

    void flushData(Long clientId, List<TycJudicial> newDataList);


}
