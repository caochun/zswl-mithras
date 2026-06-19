import { InputColumn, MatchOptionColumn } from '@/components/Format'
import { observer, history } from '@zswl/admin'
import { Descriptions } from '@zswl/components'
import { Card } from 'antd'

const Detail = ({ detail }) => {
  const goCustom = () => {
    history.push(
      `/customer/maintain/detail/${detail?.id}?clientType=${detail.clientType}&flag=info&typeId=create`
    )
  }
  const columns = [
    InputColumn({
      title: '客户名称',
      dataIndex: 'clientCode',
      render: (val, { clientName, id }) => <a onClick={goCustom}>{clientName}</a>,
    }),
    { title: '所属部门', dataIndex: 'belongDeptName' },
    { title: '所属主办', dataIndex: 'belongSponsorName' },
    MatchOptionColumn({ title: '客户类型', dataIndex: 'clientType' }),
    { title: '创建时间', dataIndex: 'createTime' },
    { title: '创建人', dataIndex: 'creatorName' },
  ]
  return (
    <Card title="客户基础信息">
      <Descriptions column={3} bordered dataSource={detail} items={columns} />
    </Card>
  )
}

export default observer(Detail)
