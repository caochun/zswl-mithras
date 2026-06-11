package cn.zswltech.mithras.dto;

import lombok.Data;


/**
 * @author zhouning
 * @date 2025/04/28
 * @description
 */
@Data
public class SystemUserAuthCodeRSP {
    private String data;
    private String code;
    private String message;
    private boolean success;
}
