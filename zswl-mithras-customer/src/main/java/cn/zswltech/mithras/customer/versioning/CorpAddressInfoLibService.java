package cn.zswltech.mithras.customer.versioning;

import cn.zswltech.mithras.api.common.PageR;
import cn.zswltech.mithras.dto.client.addressinfo.CorpAddressInfoListREQ;
import cn.zswltech.mithras.dto.client.addressinfo.CorpAddressInfoListRSP;
import cn.zswltech.mithras.customer.model.client.CorpAddressInfoLib;
import cn.zswltech.mithras.customer.versioning.dto.CorpAddressInfoLibDto;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Set;

/**
 *
 * @author wangchuanhao
 * @date 2022/6/22 4:11 PM
 */
public interface CorpAddressInfoLibService extends IService<CorpAddressInfoLib> {
    PageR<CorpAddressInfoListRSP> list(CorpAddressInfoListREQ req);

    List<Long> getClientIdsByProvince(CorpAddressInfoLibDto dto);

    List<CorpAddressInfoLib> listNewestAddressByClientId(Set<Long> clientIds);
}
