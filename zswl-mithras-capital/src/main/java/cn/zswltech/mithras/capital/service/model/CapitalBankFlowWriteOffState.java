package cn.zswltech.mithras.capital.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapitalBankFlowWriteOffState {

    private String writeOffStatus;

    private String financingFlowType;
}
