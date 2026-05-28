package cn.zswltech.mithras.dto.version;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 值差异比对二级list
 *
 * @author jackerhe
 * @date 2022/6/30 9:27 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiffValueList extends DiffValue {

    List<Map<String, DiffValue>> lsitMap;

}