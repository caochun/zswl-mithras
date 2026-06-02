package cn.zswltech.mithras.third.tianyancha.application.dto;

import cn.hutool.core.annotation.Alias;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;


@Data
public class MithrasCompanyInfo {

    /**
     * 统一社会信用代码。e.g：91520000214434146R
     */
    private String creditCode;

    /**
     * 企业名。e.g：中航重机股份有限公司
     */
    private String companyName;

}
