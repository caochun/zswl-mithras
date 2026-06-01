package cn.zswltech.mithras.service.service.lib.client;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.client.bondinfo.CorpBondInfoListREQ;
import cn.zswltech.mithras.dto.client.bondinfo.CorpBondInfoListRSP;
import cn.zswltech.mithras.service.mapper.model.client.CorpBondInfoLib;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 * @author wangchuanhao
 * @date 2022/6/22 4:11 PM
 */
public interface CorpBondInfoLibService extends IService<CorpBondInfoLib> {
    PageR<CorpBondInfoListRSP> list(CorpBondInfoListREQ req);
}
