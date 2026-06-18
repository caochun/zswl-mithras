import { FiledFormat, AmountFormat } from '@/components/Format'

const ALL_COLUMNS = () => {
  return [
    {
      title: '是否结束投放',
      dataIndex: 'contractCode',
    },
    {
      title: '已收首期租金(元)',
      dataIndex: 'clientName',
      render: (val) => <AmountFormat value={val} />,
    },
    {
      title: '已收服务费/咨询费(元)',
      dataIndex: 'projName',
      render: (val) => <AmountFormat value={val} />,
    },
    {
      title: '已收手续费(元)',
      dataIndex: 'projCode',
      render: (val) => <AmountFormat value={val} />,
    },
    {
      title: '已收首期利息(元)',
      dataIndex: 'bizType',
      render: (val) => <AmountFormat value={val} />,
    },
    {
      title: '已收客户保证金(元)',
      dataIndex: 'leaseType',
      render: (val) => <AmountFormat value={val} />,
    },
    {
      title: '租金表收款日',
      dataIndex: 'riskControlIndustryClassify',
      render: (val) => <FiledFormat title={val} />,
    },
  ]
}

export default ALL_COLUMNS
