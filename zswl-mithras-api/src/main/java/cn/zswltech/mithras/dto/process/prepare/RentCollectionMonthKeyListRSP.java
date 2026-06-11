package cn.zswltech.mithras.dto.process.prepare;

import lombok.Data;


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

