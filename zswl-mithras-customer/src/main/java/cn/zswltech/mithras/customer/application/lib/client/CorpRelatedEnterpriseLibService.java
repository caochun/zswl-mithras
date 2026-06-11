package cn.zswltech.mithras.customer.application.lib.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.client.relatedenterprise.CorpRelatedEnterpriseListREQ;
import cn.zswltech.mithras.dto.client.relatedenterprise.CorpRelatedEnterpriseListRSP;
import cn.zswltech.mithras.customer.model.client.CorpRelatedEnterpriseLib;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 * @author wangchuanhao
 * @date 2022/6/22 4:11 PM
 */
public interface CorpRelatedEnterpriseLibService extends IService<CorpRelatedEnterpriseLib> {
    PageR<CorpRelatedEnterpriseListRSP> list(CorpRelatedEnterpriseListREQ req, SFunction<CorpRelatedEnterpriseLib, ?> orderBy, String orderType);
}
