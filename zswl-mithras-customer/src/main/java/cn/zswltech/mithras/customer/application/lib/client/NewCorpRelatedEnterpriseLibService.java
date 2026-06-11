package cn.zswltech.mithras.customer.application.lib.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.client.relatedenterprise.CorpRelatedEnterpriseListREQ;
import cn.zswltech.mithras.dto.client.relatedenterprise.NewCorpRelatedEnterpriseListRSP;
import cn.zswltech.mithras.customer.model.client.NewCorpRelatedEnterpriseLib;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 * @author wangchuanhao
 * @date 2022/6/22 4:11 PM
 */
public interface NewCorpRelatedEnterpriseLibService extends IService<NewCorpRelatedEnterpriseLib> {
    PageR<NewCorpRelatedEnterpriseListRSP> list(CorpRelatedEnterpriseListREQ req, SFunction<NewCorpRelatedEnterpriseLib, ?> orderBy, String orderType);
}
