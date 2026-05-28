package cn.zswltech.mithras.service.job.dto;

import lombok.Data;

/**
 * @author yangxiong
 * @date 2024/8/15/16:49
 * @description 用来封装ftp利息计算需要的统计数据
 */
@Data
public class FtpInterestDTO {

    /**
     * 轧差金额
     */
    private long rollingDifferenceAmount;
}
