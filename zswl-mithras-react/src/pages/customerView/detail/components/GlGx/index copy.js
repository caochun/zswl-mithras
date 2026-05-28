import { observer } from '@zswl/admin'
import { Table } from '@zswl/components'
import { Typography } from 'antd'
import store from './store'
import { saveServer } from '@/utils'

function RelatedPartyInformation({ id }) {
  const columns = [
    {
      title: '序号',
      dataIndex: 'index', // 序号列
      key: 'index',
      width: 80,
      align: 'center',
      render: (text, record, index) => index + 1, // 动态生成序号
    },
    {
      title: '关联人名称',
      dataIndex: 'relatedPartyName', // 对应后端字段
      key: 'relatedPartyName',
      width: 300,
      render: (text) => <Typography.Link>{text}</Typography.Link>, // 显示为链接样式
    },
    {
      title: '关联人类型',
      dataIndex: 'relatedPartyType', // 对应后端字段
      key: 'relatedPartyType',
      width: 200,
      align: 'center',
    },
    {
      title: '关联关系描述',
      dataIndex: 'relationshipDescription', // 对应后端字段
      key: 'relationshipDescription',
      width: 200,
      align: 'center',
    },
    {
      title: '持股比例',
      dataIndex: 'shareholdingRatio', // 对应后端字段
      key: 'shareholdingRatio',
      width: 150,
      align: 'center',
    },
    {
      title: '黑灰标识',
      dataIndex: 'blackGrayMark', // 对应后端字段
      key: 'blackGrayMark',
      width: 150,
      align: 'center',
    },
    {
      title: '票据逾期',
      dataIndex: 'overdueBills', // 对应后端字段
      key: 'overdueBills',
      width: 150,
      align: 'center',
    },
    {
      title: '法律诉讼',
      dataIndex: 'legalLitigation', // 对应后端字段
      key: 'legalLitigation',
      width: 150,
      align: 'center',
      render: (text) => <span style={{ color: text > 0 ? 'red' : 'inherit' }}>{text}</span>, // 高亮显示
    },
    {
      title: '经营风险',
      dataIndex: 'operationalRisk', // 对应后端字段
      key: 'operationalRisk',
      width: 150,
      align: 'center',
      render: (text) => <span style={{ color: text > 0 ? 'red' : 'inherit' }}>{text}</span>, // 高亮显示
    },
    {
      title: '负面舆情',
      dataIndex: 'negativePublicOpinion', // 对应后端字段
      key: 'negativePublicOpinion',
      width: 150,
      align: 'center',
      render: (text) => <span style={{ color: text > 0 ? 'red' : 'inherit' }}>{text}</span>, // 高亮显示
    },
  ]

  return (
    <div>
      <Table
              columnsFilter={'components_GlGx_1'}
              onFilter={(key,val) => saveServer('components_GlGx_1',val)}
        store={store.table} // 使用 store 数据
        columns={columns} // 表格列定义
        pagination={false} // 无分页
        bordered // 表格边框
      />
    </div>
  )
}
export default observer(RelatedPartyInformation)
