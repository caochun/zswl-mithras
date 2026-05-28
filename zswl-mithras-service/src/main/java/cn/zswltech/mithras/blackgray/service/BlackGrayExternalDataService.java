package cn.zswltech.mithras.blackgray.service;

import cn.zswltech.mithras.blackgray.dto.external.*;

import java.util.List;

/**
 * @Description 获取外部数据
 * @Author jackerhe
 * @Date 2023/12/6 1:46 下午
 * @Version 1.0
 **/
public interface BlackGrayExternalDataService {

    //模糊查询企业信息
    List<VagueEnterpriseSearchRSP> vagueEnterpriseSearch(VagueEnterpriseSearchREQ req);

    //所属企业
    AffiliatedEnterpriseSearchRSP affiliatedEnterpriseSearch(AffiliatedEnterpriseSearchREQ req);

    //查询下属企业
    List<AssociatedEnterpriseSearchRSP> associatedEnterpriseSearch(AssociatedEnterpriseSearchREQ req);

    //批量填充下属企业信息
    List<AssociatedEnterpriseBatchSearchRSP> batchAffiliatedEnterpriseSearch(List<AffiliatedEnterpriseSearchREQ> req);

}
