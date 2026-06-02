package cn.zswltech.mithras.third.tianyancha.application.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TycQueryCompanyReq {

    private String companyName;

    private Integer page = 1;

    private Integer pageSize = 20;

    private String keyword = "百度网讯科技有限公司";

}
