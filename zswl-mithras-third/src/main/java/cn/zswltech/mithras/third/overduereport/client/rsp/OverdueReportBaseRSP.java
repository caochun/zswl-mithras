package cn.zswltech.mithras.third.overduereport.client.rsp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OverdueReportBaseRSP {
    private String state;

    private String message;

    private String errorCode;

    private Boolean status;

    public static OverdueReportBaseRSP fail(String message) {
        return new OverdueReportBaseRSP("false", message, "-1", false);
    }
}
