import { Table } from 'antd'
import { Typography } from 'antd'
import { observer } from '@zswl/admin'
import { saveServer } from '@/utils'

export default observer(function CreditInformation({ id }) {
  const columns = [
    {
      title: '序号',
      dataIndex: 'index',
      key: 'index',
      width: 80,
      align: 'center',
    },
    {
      title: '股东名称',
      dataIndex: 'shareholderName',
      key: 'shareholderName',
      width: 300,
      render: (text) => <a href="#">{text}</a>, // 使用超链接样式
    },
    {
      title: '股东类型',
      dataIndex: 'shareholderType',
      key: 'shareholderType',
      width: 120,
      align: 'center',
    },
    {
      title: '持股比例',
      dataIndex: 'shareholdingRatio',
      key: 'shareholdingRatio',
      width: 120,
      align: 'center',
    },
    {
      title: '持股数（股）',
      dataIndex: 'shareholdingCount',
      key: 'shareholdingCount',
      width: 160,
      align: 'center',
    },
    {
      title: '股东类别',
      dataIndex: 'shareholderCategory',
      key: 'shareholderCategory',
      width: 160,
      align: 'center',
    },
  ]

  const dataSource = [
    {
      key: '1',
      index: 1,
      shareholderName: '湖州市投资发展集团有限公司',
      shareholderType: '其他',
      shareholdingRatio: '54.79%',
      shareholdingCount: '2,124,825,159',
      shareholderCategory: '一般法人',
    },
    {
      key: '2',
      index: 2,
      shareholderName: '台州市金融投资集团有限公司',
      shareholderType: '其他',
      shareholdingRatio: '2.93%',
      shareholdingCount: '113,758,793',
      shareholderCategory: '一般法人',
    },
    {
      key: '3',
      index: 3,
      shareholderName: '西子联合控股有限公司',
      shareholderType: '其他',
      shareholdingRatio: '1.77%',
      shareholdingCount: '68,747,451',
      shareholderCategory: '一般法人',
    },
  ]
  return (
    <div id={id}>
      <Typography.Title level={5}>主要股东（更新时间：2023-10-31）</Typography.Title>
      <div>
        <Table         columnsFilter={'ZX_PrincipalShareholder_1'}
        onFilter={(key,val) => saveServer('ZX_PrincipalShareholder_1',val)} columns={columns} dataSource={dataSource} pagination={false} bordered />
      </div>
    </div>
  )
})
