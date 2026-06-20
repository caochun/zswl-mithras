import { Descriptions } from '@zswl/components'
import { AmountColumn } from '@/components/Format'

const labelStyle = {
  background: '#F5F6FA',
}
const BaseInfo = ({ id, store }) => {
  const data = store.page.getData()

  const baseInfoColumns = [
    { title: '合同编号', dataIndex: 'contractCode' },
    AmountColumn({
      title: '合同金额',
      dataIndex: 'applyCreditAmount',
    }),
    { title: '合同约定起租日', dataIndex: 'actualLeaseDate' },
    { title: '合同约定终止日', dataIndex: 'actualFinishDate' },

    { title: '项目名称', dataIndex: 'projName' },

    { title: '客户名称', dataIndex: 'clientName' },

    { title: '项目主办', dataIndex: 'projSponsorUserName' },
    { title: '项目协办', dataIndex: 'projCosponsorUserNames' },
  ]

  return (
    <Descriptions
      title={'合同信息'}
      bordered
      column={2}
      dataSource={data}
      items={baseInfoColumns}
      className={'z-description'}
      labelStyle={labelStyle}
    />
  )
}

export default BaseInfo
