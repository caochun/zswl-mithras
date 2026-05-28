package cn.zswltech.mithras.service.service.share.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 苍穹2期
 **/
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CQ2AttachmentSaveReq {
    private String cico_entity_number; // 目标单据主实体编码：er_dailyreimbursebill 员工报销单;ap_finapbill 财务应付单;ap_payapply 付款申请单; ar_finarbill财务应收单;fr_glreim_pay
    private String cico_billno; // 目标单据编码 必填
    private List<CicoEntry> cico_entryentity = new ArrayList<>(); // 外部系统附件

    public void addEntry(String cico_filename, String cico_filetype, String cico_viewurl) {
        CicoEntry entry = new CicoEntry(cico_filename, cico_filetype, cico_viewurl);
        cico_entryentity.add(entry);
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CicoEntry {
        private String cico_filename; // 外部系统附件.附件名称 必填
        private String cico_filetype; // 外部系统附件.附件类型
        private String cico_viewurl; // 外部系统附件.附件地址
    }
}