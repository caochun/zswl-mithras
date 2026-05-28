import { AmountColumn, InputEditable, FiledFormat } from '@/components/Format'

const ALL_COLUMNS = () => {
  return [
    {
      title: '主办',
      dataIndex: 'sponsorName',
      render: (val) => <FiledFormat value={val} />,
      editable: false,
    },
    {
      title: '合同编号',
      dataIndex: 'contractCode',
      width: 300,
      render: (val) => <FiledFormat value={val} />,
      editable: false,
    },
    {
      title: '期数',
      dataIndex: 'phase',
      render: (val) => <FiledFormat value={val} />,
      editable: false,
    },
    {
      title: '租金支付日',
      dataIndex: 'repayDate',
      render: (val) => <FiledFormat value={val} />,
      editable: false,
    },
    AmountColumn({
      title: '租金',
      dataIndex: 'rent',
      // render: PercentageRender,
      editable: false,
    }),
    AmountColumn({
      title: '租赁成本',
      dataIndex: 'principal',
      editable: false,
    }),
    AmountColumn({
      title: '租赁利息',
      dataIndex: 'interest',
      editable: false,
    }),
    {
      title: '户名',
      dataIndex: 'bankAccountName',
      width: 220,
      render: (val) => <FiledFormat value={val} />,
      editable: InputEditable({ disabled: false }),
    },
    {
      title: '开户行',
      dataIndex: 'bankName',
      width: 220,
      render: (val) => <FiledFormat value={val} />,
      editable: InputEditable({ disabled: false }),
    },
    {
      title: '帐号',
      dataIndex: 'bankAccountNumber',
      width: 250,
      render: (val) => <FiledFormat value={val} />,
      editable: InputEditable({ disabled: false }),
    },
  ]
}

export default ALL_COLUMNS
