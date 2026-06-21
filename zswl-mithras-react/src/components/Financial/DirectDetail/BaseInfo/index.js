import { useState } from 'react'
import { message } from 'antd'
import { Descriptions } from '@zswl/components'
import { observer } from '@zswl/admin'
import { dateRangeTransform } from '@/utils/transform'
import { rangePresets, amountFormat, formatPercent } from '@/utils'
import Api from '@/api/financial/directFinancingDetail'
import moment from 'moment'
import { AmountEditable, FiledFormat } from '@/components/Format'

function FinancialDirectDetailBaseInfo({ id, disabled }) {
  const [editable, setEditable] = useState(false)
  const store = Descriptions.useStore({
    request: () => {
      return Api.getBaseInfo({ id })
    },
  })
  // 保存
  const save = async () => {
    const values = await store.submit()
    const { duration, firstPaymentDate, ...rest } = values
    const _values = dateRangeTransform(duration, 'durationFrom', 'durationTo')
    await Api.editBaseInfo({
      id,
      ...rest,
      ..._values,
      firstPaymentDate: firstPaymentDate?.format('YYYY-MM-DD'),
    })
    message.success('保存成功')
    await store.init()
    setEditable(false)
  }
  return (
    <Descriptions
      title="基本信息"
      store={store}
      editable={editable}
      labelStyle={{ width: 220 }}
      extra={[
        {
          name: '取消',
          hidden: !editable,
          onClick: () => setEditable(false),
        },
        { name: '保存', hidden: !editable, type: 'primary', onClick: save },
        {
          name: '编辑',
          type: 'primary',
          hidden: editable,
          disabled,
          onClick: () => setEditable(true),
        },
      ]}
      items={[
        { title: '产品名称', dataIndex: 'productName', editable: false },
        { title: '融资编号', dataIndex: 'financingCode', editable: false },
        {
          title: '发行规模（万元）',
          dataIndex: 'issuingScale',
          requiredMark: true,
          editable: (val) =>
            AmountEditable(val, 'issuingScale', { required: true, disabled: false }),
          render: (val) => amountFormat(formatPercent(val)),
        },
        {
          title: '承销商',
          dataIndex: 'consignee',
          requiredMark: true,
          editable: {
            rules: [{ required: true, message: '请输入承销商' }],
          },
        },
        {
          title: '项目类别',
          dataIndex: 'directFinancingType',
          requiredMark: true,
          matchOption: 'directFinancingType',
        },
        {
          title: '交易流通场所',
          dataIndex: 'tradingVenues',
          requiredMark: true,
          editable: {
            rules: [{ required: true, message: '请输入交易流通场所' }],
          },
        },
        {
          title: '发行方式',
          dataIndex: 'issuanceMethod',
          requiredMark: true,
          matchOption: 'issuanceMethod',
          editable: {
            rules: [{ required: true, message: '请选择发行方式' }],
          },
        },
        {
          title: '存续时间',
          dataIndex: 'duration',
          requiredMark: true,
          editable({ durationFrom, durationTo }) {
            return {
              initialValue: durationFrom && [moment(durationFrom), moment(durationTo)],
              element: 'rangePicker',
              ranges: rangePresets,
              rules: [{ required: true, message: '请选择存续时间' }],
            }
          },
          render: (_, { durationFrom, durationTo }) => (
            <span>
              {durationFrom || '-'} ~ {durationTo || '-'}
            </span>
          ),
        },
        {
          title: '首个兑付日',
          dataIndex: 'firstPaymentDate',
          requiredMark: true,
          editable({ firstPaymentDate }) {
            return {
              initialValue: (firstPaymentDate && moment(firstPaymentDate)) || undefined,
              element: 'datePicker',
              rules: [{ required: true, message: '请选择首个兑付日' }],
            }
          },
        },
        {
          title: '备注',
          dataIndex: 'remark',
          span: 2,
          editable: {
            element: 'textArea',
            rows: 4,
          },
          render: (val) => <FiledFormat title={val} />,
        },
        { title: '资金经理', dataIndex: 'fundManagerName', editable: false },
        { title: '所属部门', dataIndex: 'deptName', editable: false },
        { title: '部门负责人', dataIndex: 'bizHeaderName', editable: false },
        { title: '分管领导', dataIndex: 'leaderName', editable: false },
      ]}
    />
  )
}

export default observer(FinancialDirectDetailBaseInfo)
