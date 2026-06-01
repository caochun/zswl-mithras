package cn.zswltech.mithras.service.service.third.financial.req;

import cn.zswltech.mithras.service.enums.third.ExceptionSourceENUM;
import lombok.Data;

/**
 * @ClassName CQ2CommonReq
 * @Description TODO
 * @Author jackerhe
 * @Date 2024/7/24 14:32
 * @Version 1.0
 **/
@Data
public class CQ2CommonReq {

    /**
     * 来源途径
     * {@link ExceptionSourceENUM#name()}
     **/
    private String source;

    /**
     * 业务主建 用于寻找对应业务信息
     **/
    private String businessKey;

    /**
     * 业务模块用于存放页面展示信息，用于帮助业务区分记录
     **/
    private String businessTitle;

}
