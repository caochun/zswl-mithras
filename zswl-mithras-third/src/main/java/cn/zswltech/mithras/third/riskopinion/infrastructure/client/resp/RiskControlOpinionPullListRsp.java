package cn.zswltech.mithras.third.riskopinion.infrastructure.client.resp;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName RiskControlOpinionListRsp
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/3/6 6:54 下午
 * @Version 1.0
 **/
@Data
@Accessors(chain = true)
public class RiskControlOpinionPullListRsp extends RiskControlCommRSP {

    private List<RiskControlOpinionListData> data = new ArrayList<>();

    @Data
    public static class RiskControlOpinionListData {
        //        @ApiModelProperty("id")
        private Long id;

        //        @ApiModelProperty("标题")
        private String title;

        //        @ApiModelProperty("客户名称")
        private String chiName;

        //        @ApiModelProperty("统一社会信用代码")
        private String creditCode;

        //        @ApiModelProperty("信息发布日期")
        private LocalDateTime infoPublDate;

        //        @ApiModelProperty("链接地址")
        private String linkAddress;

        //        @ApiModelProperty("主体机构代码")
        private String majorOrgCode;

        //        @ApiModelProperty("预警星级[1:一星,2:二星,3:三星]")
        private Integer warnStar;

        //        @ApiModelProperty("预警信号[1:绿灯,2:黄灯,3:红灯]")
        private Integer warnLevel;
    }

}
