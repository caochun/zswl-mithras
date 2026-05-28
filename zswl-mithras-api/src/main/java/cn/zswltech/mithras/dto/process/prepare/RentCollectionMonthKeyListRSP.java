package cn.zswltech.mithras.dto.process.prepare;

import cn.zswltech.mithras.dto.version.DiffValue;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author luyi
 */
@Data
public class RentCollectionMonthKeyListRSP {


    /**
     * 租金总额
     */
    private Long totalRent;

    private Integer clientCount;

    private Long deptId;

    private String deptName;



}

