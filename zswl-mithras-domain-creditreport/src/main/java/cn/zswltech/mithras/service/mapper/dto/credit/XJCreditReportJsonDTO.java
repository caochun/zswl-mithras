/**
  * Copyright 2025 bejson.com 
  */
package cn.zswltech.mithras.service.mapper.dto.credit;

/**
 * 信加征信解析json实体类
 * @author: jackerhe
 * @date: 2025/11/14 09:23
 **/
public class XJCreditReportJsonDTO {

    private Document Document;

    public XJCreditReportJsonDTO(cn.zswltech.mithras.service.mapper.dto.credit.Document document, cn.zswltech.mithras.service.mapper.dto.credit.Document DOCUMENT) {
        Document = document;
        this.DOCUMENT = DOCUMENT;
    }

    public XJCreditReportJsonDTO() {
    }

    private Document DOCUMENT;

    public Document getDocument() {
        return Document;
    }

    public void setDocument(cn.zswltech.mithras.service.mapper.dto.credit.Document document) {
        Document = document;
    }

    public Document getDOCUMENT() {
        return DOCUMENT;
    }

    public void setDOCUMENT(cn.zswltech.mithras.service.mapper.dto.credit.Document DOCUMENT) {
        this.DOCUMENT = DOCUMENT;
    }

}