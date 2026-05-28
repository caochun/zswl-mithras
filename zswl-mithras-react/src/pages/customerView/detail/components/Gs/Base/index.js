import { Descriptions } from '@zswl/components'
import { Typography } from 'antd'
import { Page } from '@zswl/components'
export default function Creditinformation({ store, id }) {
  const renderEmpty = text => text || '-'

  const tableColumns = [
    { title: '企业名称', dataIndex: 'enterprise_name', key: 'enterprise_name', render: renderEmpty },
    {
      title: '统一社会信用代码',
      dataIndex: 'uniform_social_credit_code',
      key: 'uniform_social_credit_code',
      render: renderEmpty
    },
    { title: '法定代表人', dataIndex: 'legal_repr', key: 'legal_repr', render: renderEmpty },
    { title: '企业状态', dataIndex: 'enterprise_status', key: 'enterprise_status', render: renderEmpty },
    {
      title: '工商登记号',
      dataIndex: 'organ_code',
      key: 'organ_code',
      render: renderEmpty
    },
    { title: '成立日期', dataIndex: 'establishment_date', key: 'establishment_date', render: renderEmpty },
    { title: '注册资本', dataIndex: 'regcapital', key: 'regcapital', render: renderEmpty },
    { title: '实缴资本', dataIndex: 'paidin_capital', key: 'paidin_capital', render: renderEmpty },
    { title: '电子邮箱', dataIndex: 'email', key: 'email', render: renderEmpty },
    { title: '企业类型', dataIndex: 'enterprise_type', key: 'enterprise_type', render: renderEmpty },
    {
      title: '营业期限',
      dataIndex: 'validity_term_from',
      key: 'validity_term',
      render: (text, record) => {
        const from = record.validity_term_from || '-'
        const to = record.validity_term_to || '-'
        return `${from} 至 ${to}`
      }
    },
    { title: '登记机关', dataIndex: 'reg_org', key: 'reg_org', render: renderEmpty },
    { title: '所在地区', dataIndex: 'province', key: 'province', render: renderEmpty },
    { title: '所属行业', dataIndex: 'industry_name', key: 'industry_name', render: renderEmpty },
    { title: '员工人数', dataIndex: 'work_scale', key: 'work_scale', render: renderEmpty },
    { title: '英文名', dataIndex: 'eng_name', key: 'eng_name', render: renderEmpty },
    { title: '注册地址', dataIndex: 'register_address', key: 'register_address', render: renderEmpty },
    { title: '联系方式', dataIndex: 'contact_way', key: 'contact_way', render: renderEmpty },
    { title: '经营范围', dataIndex: 'work_range', key: 'work_range', render: renderEmpty }
  ]

  return (
    <div id={id}>
      <Typography.Title level={5}>工商信息</Typography.Title>
      <div>
        <Descriptions store={store.base} contentStyle={{ width: 300 }} items={tableColumns} />
      </div>
    </div>
  )
}
