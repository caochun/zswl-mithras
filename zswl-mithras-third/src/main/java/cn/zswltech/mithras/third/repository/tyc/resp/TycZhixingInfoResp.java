package cn.zswltech.mithras.third.repository.tyc.resp;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 被执行人
 *
 * @author wangchuanhao
 * @date 2022/6/20 5:56 PM
 */
@Data
public class TycZhixingInfoResp extends TycListBaseResp<TycZhixingInfoResp.ItemsDTO> {

    @Data
    public static class ItemsDTO {

        /**
         * 案号
         */
        private String caseCode;

        /**
         * 身份证号／组织机构代码
         */
        private String partyCardNum;

        /**
         * 被执⾏⼈名称
         */
        private String pname;

        /**
         * 执⾏法院
         */
        private String execCourtName;

        /**
         * 创建时间
         */
        private Long caseCreateTime;

        /**
         * 执⾏标的（元）
         */
        private String execMoney;
    }

}
