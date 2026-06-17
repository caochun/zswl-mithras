import { getDescColumns } from '@/utils'
import { observer } from '@zswl/admin'
import { Descriptions, Table } from '@zswl/components'
import ALl_COLUMNS from '@/components/BlackGray/Columns'
import { EnterTable } from './BreakDetail'

const Index = ({ detail }) => {
  const columns = getDescColumns(
    ALl_COLUMNS,
    [
      '申请人',
      {
        title: '申请时间',
        dataIndex: 'createTime',
      },
      '报告机构',
      '企业名称',
      '统一社会信用代码',
      {
        title: '业务类型',
        dataIndex: 'businessType',
      },

      // '申请原因描述',
      '审批状态',
    ].filter(Boolean)
  )
  return (
    <div>
      <Descriptions
        items={columns}
        dataSource={detail}
        editable={false}
        labelStyle={{ width: '160px' }}
        contentStyle={{ width: 230 }}
      />
      <div className="z-sub-title ">入库信息</div>
      <EnterTable dataSource={detail.applyReason} />
    </div>
  )
}
export default observer(Index)
