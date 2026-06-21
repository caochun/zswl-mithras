import { observer } from '@zswl/admin'

import RowSpan from '../RowSpan'

/**
 * 公交车模版检查总结组件 V1版本
 * @param {Object} props - 组件属性
 * @param {boolean} props.editable - 是否可编辑
 * @param {string} props.namePrefix - 名称前缀，默认为 'BUS'
 */
function AfterLeaseBusSummaryV1({ editable, namePrefix = 'B' }) {
  return (
    <div>
      <RowSpan subTitle={'以上检查事项风险事项披露及异常说明'} name={`${namePrefix}_S_1_01`}>
        <TextAreaReadOnly onlyRead={!editable} />
      </RowSpan>
      <RowSpan subTitle={'有权机构审批意见未落实事项'} name={`${namePrefix}_S_1_02`}>
        <TextAreaReadOnly onlyRead={!editable} />
      </RowSpan>
      <RowSpan subTitle={'检查结论'} name={`${namePrefix}_S_1_03`}>
        <TextAreaReadOnly onlyRead={!editable} />
      </RowSpan>
    </div>
  )
}

export default observer(AfterLeaseBusSummaryV1)
