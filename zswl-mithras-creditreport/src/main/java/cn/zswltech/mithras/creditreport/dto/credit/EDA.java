/**
  * Copyright 2025 bejson.com 
  */
package cn.zswltech.mithras.creditreport.dto.credit;
import java.util.List;

/**
 * Auto-generated: 2025-11-12 18:31:28
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class EDA {

    private List<ED01> ED01;
    private List<ED02> ED02;
    private List<String> ED03;
    public void setED01(List<ED01> ED01) {
         this.ED01 = ED01;
     }
     public List<ED01> getED01() {
         return ED01;
     }

    public void setED02(List<ED02> ED02) {
         this.ED02 = ED02;
     }
     public List<ED02> getED02() {
         return ED02;
     }

    public void setED03(List<String> ED03) {
         this.ED03 = ED03;
     }
     public List<String> getED03() {
         return ED03;
     }

}