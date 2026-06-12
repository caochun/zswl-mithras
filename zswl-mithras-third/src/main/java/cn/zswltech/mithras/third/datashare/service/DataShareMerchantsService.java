package cn.zswltech.mithras.third.datashare.service;

import cn.zswltech.mithras.dto.client.share.DataShareREQ;
import cn.zswltech.mithras.third.datashare.persistence.model.DataShareMerchants;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.util.MultiValueMap;

public interface DataShareMerchantsService extends IService<DataShareMerchants> {

    DataShareMerchants getMerchants(DataShareREQ req);

    String getMerchantsMock(MultiValueMap<String, String> paramMap);

}
