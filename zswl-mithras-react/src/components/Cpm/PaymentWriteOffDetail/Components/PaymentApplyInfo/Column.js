import { FiledFormat, AmountFormat, AmountColumn } from '@/components/Format'
import { Space } from 'antd'
import { hasValue } from '@/utils'
import CommonTips from '@/components/LeasePricing/FeeTipEntries'

const ALL_COLUMNS = ({ isZhiZu }) => {
  return [
    {
      title: '申请付款日期',
      dataIndex: 'applyPaymentDate',
    },
    {
      title: '申请付款金额(元)',
      dataIndex: 'applyPaymentAmount',
      render: (val) => <AmountFormat value={val} />,
    },
    {
      title: '首期租金(元)',
      dataIndex: 'downPayment',
      span: 2,
      render: (val, record) => {
        return (
          <Space>
            <AmountFormat value={val} />
            {hasValue(record?.downPaymentType) && val > 0 ? (
              <span>（{['不包含', '包含'][record?.downPaymentType] + '在本次付款中'}）</span>
            ) : null}
          </Space>
        )
      },
    },
    {
      title: '最低IRR',
      dataIndex: 'lowestIrr',
      render: (val) => <AmountFormat value={val} unit="%" />,
    },
    isZhiZu && AmountColumn({ title: '厂商质保金(元）', dataIndex: 'retentionMoney' }),
    {
      title: '服务费/咨询费(元)',
      dataIndex: 'consultingFee',
      formTooltip: CommonTips.fieldMapTip['consultingFee'],
      render: (val) => <AmountFormat value={val} />,
    },
    {
      title: '手续费(元)',
      dataIndex: 'commission',
      formTooltip: CommonTips.fieldMapTip['commission'],
      render: (val) => <AmountFormat value={val} />,
    },
    {
      title: '首期利息(元)',
      dataIndex: 'firstInstallmentInterest',
      formTooltip: CommonTips.fieldMapTip['firstInstallmentInterest'],
      render: (val) => <AmountFormat value={val} />,
    },
    {
      title: '客户保证金(元)',
      dataIndex: 'earnestMoney',
      render: (val) => <AmountFormat value={val} />,
    },
    {
      title: '名义价款(元)',
      dataIndex: 'nominalPrice',
      render: (val) => <AmountFormat value={val} />,
    },
    {
      title: '备注',
      dataIndex: 'remark',
      render: (val) => <FiledFormat title={val} />,
    },
  ]
}

export default ALL_COLUMNS
