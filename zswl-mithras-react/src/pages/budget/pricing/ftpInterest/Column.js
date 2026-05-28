import { hasValue } from '@/utils'
import { ClientSelect, FounderSelect, OrgSelect } from '@/components'
import { FiledFormat, AmountColumn, MatchOptionColumn } from '@/components/Format'
import FormAmount from '@/components/Form/FormAmount'
import { history } from '@zswl/admin'
import styles from './index.less'

const renderDivide = (value) => {
  if (window.isNaN(value)) {
    return value
  }
  return hasValue(value) ? value : '-'
}

const ALL_COLUMNS = ({ pathname } = {}) => {
  return [
    {
      title: '借据编号',
      dataIndex: 'receiptCode',
    },
    {
      title: '合同编号',
      dataIndex: 'contractCode',
    },
    {
      title: '客户名称',
      dataIndex: 'clientId',
      editable: {
        element: <ClientSelect canJump={false} />,
        functionCode: 'ftpinterestclientlist',
      },
      render: (val, { clientName }) => <span>{clientName}</span>,
    },
    { title: '项目名称', dataIndex: 'projName' },
    AmountColumn({
      title: 'FTP价格(%)',
      dataIndex: 'lastCashFtp',
      // render: (_, { lastCashFtp, receiptCode, id }) => {
      //   return (
      //     <a
      //       onClick={() => {
      //         history.push(`${pathname}/priceDetail/${id}?receiptCode=${receiptCode}`)
      //       }}
      //     >
      //       <FormAmount.Format value={lastCashFtp} />
      //     </a>
      //   )
      // },
    }),
    {
      title: '业务部门',
      dataIndex: 'bizDeptId',
      editable: {
        element: <OrgSelect />,
        functionCode: 'ftpinterestselectorgs',
      },
      render: (val, { bizDeptName }) => <FiledFormat title={bizDeptName} />,
    },
    {
      title: '项目主办',
      dataIndex: 'sponsorUserId',
      render: (val, { sponsorUserName }) => <FiledFormat title={sponsorUserName} />,
      editable: {
        element: <FounderSelect />,
        functionCode: 'ftpinterestselectfounder',
      },
    },
    {
      title: '更新日期',
      dataIndex: 'lastUpdateDate',
    },
    {
      title: '日期',
      dataIndex: 'interestDateText',
    },
    AmountColumn({
      title: '本日现金支出(元)',
      dataIndex: 'cashOut',
    }),
    AmountColumn({
      title: '本日现金收入(元)',
      dataIndex: 'cashIn',
    }),
    AmountColumn({
      title: '资金占用(元)',
      dataIndex: 'cashOccupy',
    }),
    {
      title: '现金FTP价格(%)',
      dataIndex: 'cashFtp',
      render: renderDivide,
    },
    {
      title: '现金FTP日利率(%)',
      dataIndex: 'cashFtpDay',
      render: renderDivide,
    },
    AmountColumn({
      title: '资金计息(元)',
      dataIndex: 'cashInterest',
      align: 'right',
      className: styles.linghtBgcolor,
    }),
    AmountColumn({
      title: '本日票据支出(元)',
      dataIndex: 'billOut',
    }),
    AmountColumn({
      title: '本日票据收入(元)',
      dataIndex: 'billIn',
    }),
    AmountColumn({
      title: '票据占用(元)',
      dataIndex: 'billOccupy',
    }),
    {
      title: '票据FTP价格(%)',
      dataIndex: 'billFtp',
      render: renderDivide,
    },
    {
      title: '票据FTP日利率(%)',
      dataIndex: 'billFtpDay',
      render: renderDivide,
    },
    AmountColumn({
      title: '票据计息(元)',
      dataIndex: 'billInterest',
      className: styles.linghtBgcolor,
    }),
    MatchOptionColumn({
      title: '是否逾期',
      dataIndex: 'isOverdue',
      matchOption: 'yesOrNo',
    }),
    AmountColumn({
      title: '当年累计计息(元)-列表',
      dataIndex: 'totalInterestAmount',
      render: (_, { totalInterestAmount, id }) => {
        return (
          <a
            onClick={() => {
              history.push(`${pathname}/detail/${id}`)
            }}
          >
            <FormAmount.Format value={totalInterestAmount} />
          </a>
        )
      },
    }),
    AmountColumn({
      title: '当年累计计息(元)',
      dataIndex: 'totalInterestThisYear',
      className: styles.linghtBgcolor,
    }),
    {
      title: '选择日期',
      dataIndex: 'interestDate',
      type: 'rangePicker',
      dateFormat: 'yyyy-MM-DD',
      itemProps: {
        transform: (val) => {
          const [startDataTime, endDataTime] = val || []
          return {
            interestDate: undefined,
            interestDateFrom: startDataTime?.format('yyyy-MM-DD'),
            interestDateTo: endDataTime?.format('yyyy-MM-DD'),
          }
        },
      },
    },
  ]
}

export default ALL_COLUMNS
