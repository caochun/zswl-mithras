package cn.zswltech.mithras.third.datashare.service.rsp;

import cn.zswltech.mithras.third.financialshare.client.resp.FinancialBaseRSP;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CQ2AcchmentSaveRsp extends FinancialBaseRSP {

    private DealData data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public class DealData{
        private Integer successCount ;
        private Integer failCount;
        private List<String> result ;
    }
}
