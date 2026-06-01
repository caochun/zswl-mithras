package cn.zswltech.mithras.blackgray.service.external.remote;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JKBaseRSP {
    private String state;

    private Boolean success;

    private String message;

    private String errorCode;

    private Boolean status;

}