package cn.zswltech.mithras.blackgray.vo;

import lombok.Data;

import java.util.List;

/**
 * @ClassName BlackGrayApplyReasonVo
 * @Description TODO
 * @Author jackerhe
 * @Date 2024/1/15 5:13 下午
 * @Version 1.0
 **/
@Data
public class BlackGrayApplyReasonVo {
    /**
     * 业务名称
     **/
    private String businessName;

    /**
     * 业务描述
     */
    private String businessDesc;

    private Integer level;

    private Long mainId;
    /**
     * 父业务名称
     **/
    private String parentBusinessName;

    private List<BlackGrayApplyReasonVo> children;

}
