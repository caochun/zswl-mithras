package cn.zswltech.mithras.third.tianyancha.application;

import cn.zswltech.mithras.customer.externaldata.tianyancha.model.TycMortgageInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 动产抵押
 *
 * @author wangchuanhao
 * @date 2022/6/20 11:43 PM
 */
public interface TycMortgageInfoService extends IService<TycMortgageInfo> {

    List<TycMortgageInfo> queryAllFromTyc(Long clientId, String clientName, String uscCode);

    void flushData(Long clientId, List<TycMortgageInfo> newDataList);


}
