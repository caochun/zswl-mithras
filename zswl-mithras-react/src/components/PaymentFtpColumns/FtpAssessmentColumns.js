import { AmountEditable, AmountColumn, TextAreaColumn, DateColumn } from '@/components/Format'
import { formatPercent, amountFormat } from '@/utils'
import CommonTips from '@/components/CommonTips'
import { Select } from 'antd'
import moment from 'moment'

const ALL_COLUMNS = () => {
  return [
    {
      title: '付款金额(元)',
      dataIndex: 'paymentAmount',
      editable: false,
      render: (val) => amountFormat(formatPercent(val)),
    },
    {
      title: '首期租金(元)',
      dataIndex: 'downPaymentAmount',
      editable: false,
      render: (val) => amountFormat(formatPercent(val)),
    },
    {
      title: '客户保证金(元)',
      dataIndex: 'earnestMoneyAmount',
      editable: false,
      render: (val) => amountFormat(formatPercent(val)),
    },
    {
      title: '厂商保证金(元)',
      dataIndex: 'retentionMoneyAmount',
      editable: false,
      render: (val) => amountFormat(formatPercent(val)),
    },
    {
      title: '服务费/咨询费(元)',
      dataIndex: 'consultingFeeAmount',
      editable: false,
      formTooltip: CommonTips.fieldMapTip['consultingFee'],
      render: (val) => amountFormat(formatPercent(val)),
    },
    {
      title: '手续费(元)',
      dataIndex: 'commission',
      formTooltip: CommonTips.fieldMapTip['commission'],
      editable: false,
      render: (val) => amountFormat(formatPercent(val)),
    },
    {
      title: '首期利息(元)',
      dataIndex: 'firstInstallmentInterest',
      formTooltip: CommonTips.fieldMapTip['firstInstallmentInterest'],
      editable: false,
      render: (val) => amountFormat(formatPercent(val)),
    },
    {
      title: '名义价款(元)',
      dataIndex: 'nominalPriceAmount',
      editable: false,
      render: (val) => amountFormat(formatPercent(val)),
    },
    {
      title: '付款日期',
      dataIndex: 'actualPayDate',
      editable: false,
    },
    AmountColumn({
      title: 'FTP基础价格',
      dataIndex: 'basePrice',
      suffix: '%',
      requiredMark: true,

      editable: (val) =>
        AmountEditable(val, 'basePrice', {
          required: true,
          disabled: false,
          inputConfig: {
            addonAfter: '%',
          },
        }),
    }),
    AmountColumn({
      title: 'FTP山区调整',
      rename: (
        <div>
          <div>FTP山区调整</div>
          <div style={{ color: 'red', fontSize: 12 }}>(是否山区海岛县项目)</div>
        </div>
      ),
      dataIndex: 'mountainAdjustment',
      suffix: '%',
      requiredMark: true,
      editable: (val) =>
        AmountEditable(val, 'mountainAdjustment', {
          required: true,
          disabled: false,
          inputConfig: {
            addonAfter: '%',
            min: -Infinity,
          },
        }),
    }),
    AmountColumn({
      title: 'FTP评级调整',
      dataIndex: 'gradeAdjustment',
      suffix: '%',
      requiredMark: true,
      editable: (val) =>
        AmountEditable(val, 'gradeAdjustment', {
          required: true,
          disabled: false,
          inputConfig: {
            min: -Infinity,
            addonAfter: '%',
          },
        }),
    }),
    AmountColumn({
      title: 'FTP指引价格',
      dataIndex: 'guidePrice',
      suffix: '%',
      editable: false,
    }),
    AmountColumn({
      title: 'FTP是否质押',
      dataIndex: 'pledgePrice',
      suffix: '%',
      requiredMark: true,
      editable: (val) =>
        AmountEditable(val, 'pledgePrice', {
          required: true,
          disabled: false,
          inputConfig: {
            min: -Infinity,
            addonAfter: '%',
          },
        }),
    }),
    AmountColumn({
      title: 'FTP手工调整',
      dataIndex: 'handAdjustment',
      suffix: '%',
      requiredMark: true,
      editable: (val) =>
        AmountEditable(val, 'handAdjustment', {
          required: true,
          disabled: false,
          inputConfig: {
            min: -Infinity,
            addonAfter: '%',
          },
        }),
    }),
    AmountColumn({
      title: '杭甬特殊调整',
      rename: <div style={{ color: 'red', fontSize: 12 }}>FTP杭甬特殊调整</div>,
      formTooltip:
        '杭州全域、宁波海曙区、鄞州区、江北区、镇海区、北仑区、宁海县、慈溪市公用事业类（民生消费类）项目FTP成本在浙江地区基础上下降50bp',
      dataIndex: 'hangyongSpecialAdjustment',
      suffix: '%',
      editable: true,
      wrapItemProps: {
        inputConfig: {
          min: -Infinity,
          addonAfter: '%',
        },
      },
    }),
    AmountColumn({
      title: 'FTP考核价格',
      dataIndex: 'assessmentPrice',
      suffix: '%',
      editable: false,
    }),
    AmountColumn({
      title: '票据FTP价格',
      dataIndex: 'ticketPrice',
      suffix: '%',
      requiredMark: true,
      editable: (val) =>
        AmountEditable(val, 'ticketPrice', {
          required: true,
          disabled: false,
          inputConfig: {
            min: -Infinity,
            addonAfter: '%',
          },
        }),
    }),
    {
      title: '是否为特殊事项',
      dataIndex: 'isSpecialMatter',
      span: 1,
      render: (val) => (val === 1 ? '是' : '否'),
      editable: (val) => (
        <Select
          value={val}
          options={[
            { label: '是', value: 1 },
            { label: '否', value: 0 },
          ]}
        />
      ),
    },
    TextAreaColumn({
      title: 'FTP备注',
      dataIndex: 'remark',
      editable: true,
      span: 2,
    }),
    DateColumn({
      title: '变更差额调整日',
      dataIndex: 'ftpInterestDiffDate',
      requiredMark: true,
      editable: true,
    }),
    DateColumn({
      title: '变更起始日',
      dataIndex: 'effectDate',
      requiredMark: true,
      disabledDate: (current) => {
        return current && current > moment().subtract(1, 'day')
      },
      editable: true,
    }),
    TextAreaColumn({
      title: '变更原因',
      dataIndex: 'remark',
      editable: true,
      span: 3,
    }),
  ]
}

export default ALL_COLUMNS
