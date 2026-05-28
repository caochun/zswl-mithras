package cn.zswltech.mithras.dto.riskcontrol.opinion;

import cn.hutool.core.date.DateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * @ClassName RiskControlOpinionListRsp
 * @Description TODO
 * @Author jackerhe
 * @Date 2023/3/6 6:54 下午
 * @Version 1.0
 **/
@Data
public class RiskControlOpinionListRsp {

    private Long id;

    //客户名称
    private String chiName;

    //标题
    private String title;

    //情感方向[FCC0000002QA=负面,FCC0000002QF=中性,FCC0000002Q9=正面]
    private String emotion;

    //情感重要度[FCC0000002QB=零星,FCC0000002QC=一星,FCC0000002QD=二星,FCC0000002QE=三星]
    private String importance;

    //信息发布日期
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private DateTime infoPublDate;

    //链接地址
    private String linkAddress;

    //新闻来源
    private String sourceName;

    //统一社会信用代码
    private String creditCode;
}
