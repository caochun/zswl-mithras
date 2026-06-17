import { observer } from '@zswl/admin'
import { Radio, InputNumber } from 'antd'
import { Form, Modal, Select } from '@zswl/components'
import { useMemo, useState, useRef } from 'react'
import Project from './Project'
import { AmountColumn, DateColumn, MatchOptionColumn } from '@/components/Format'
import RepayCalcType from '@/components/RepayCalcType'
import { CommonTips } from '@/components'

const commonProps = {
  wrapItemProps: { required: true },
  editable: true,
  requiredMark: true,
}

const Index = ({ store }) => {
  const [radioValue, setRadioValue] = useState(0)
  const [form] = Form.useForm()
  const projectRef = useRef(null)

  const onInterestWayChange = () => {
    projectRef.current.descRef.setFieldValue('rentalCalcType', undefined)
    projectRef.current.descRef.setFieldValue('repayCalcType', undefined)
  }

  const columns = useMemo(() => {
    if (radioValue === 0) {
      return [
        AmountColumn({
          title: '项目金额(元）',
          dataIndex: 'creditAmount',
          ...commonProps,
        }),
        {
          title: '租赁期限(月)',
          dataIndex: 'leaseMonthCount',
          requiredMark: true,
          editable: {
            element: <InputNumber min={0} style={{ width: '100%' }} placeholder="请输入" />,
            required: true,
            rules: [{ required: true, message: '请输入租赁期限(月)' }],
          },
        },
        AmountColumn({ title: '保证金(元)', dataIndex: 'earnestMoney', ...commonProps }),
        MatchOptionColumn({
          title: '还款频率',
          dataIndex: 'repayRate',
          matchOption: 'repayRateEnum',
          ...commonProps,
        }),
        AmountColumn({ title: '首期租金(元)', dataIndex: 'downPayment', ...commonProps }),
        AmountColumn({
          title: '首期利息(元)',
          dataIndex: 'firstInstallmentInterest',
          ...commonProps,
        }),
        AmountColumn({
          title: '手续费(元)',
          dataIndex: 'commission',
          ...commonProps,
        }),
        {
          title: '还款期数',
          dataIndex: 'repayTimes',
          requiredMark: true,
          editable: {
            required: true,
            rules: [{ required: true, message: '请输入还款期数' }],
          },
        },
        AmountColumn({ title: '服务费/咨询费(元)', dataIndex: 'consultingFee', ...commonProps }),
        MatchOptionColumn({ title: '支付方式', dataIndex: 'payType', ...commonProps }),
        AmountColumn({ title: '名义价款(元)', dataIndex: 'nominalPrice', ...commonProps }),
        {
          title: <CommonTips fieldName={'interestWay'}> 利息计算方式</CommonTips>,
          dataIndex: 'interestWay',
          requiredMark: true,
          matchOption: 'interestWayEnum',
          editable: {
            element: <Select options={'interestWayEnum'} onChange={onInterestWayChange} />,
            rules: [{ required: true, message: '请选择' }],
          },
        },
        {
          title: '还款方式',
          dataIndex: 'rentalCalcType',
          matchOption: 'repayCalcType',
          requiredMark: true,
          // editable: {
          //   element: <Select options={'repayCalcType'} />,
          //   rules: [{ required: true, message: '请选择' }],
          // },
          editable: (val) => {
            return <RepayCalcType fieldName="rentalCalcType" />
          },
        },

        AmountColumn({
          ...commonProps,
          title: '租赁利率',
          dataIndex: 'interestRate',
          wrapItemProps: {
            required: true,
            inputConfig: {
              addonAfter: '%',
            },
          },
        }),
        DateColumn({
          title: '计划起租日',
          dataIndex: 'startDate',
          span: 2,
          editable: true,
          requiredMark: true,
          rules: [{ required: true, message: '请输入计划起租日' }],
        }),
      ].filter(Boolean)
    }
    //1. 项目金额（元）2. 额度有效期（月）3. 保证金（元）4. 手续费（元）5. 还款频率6. 还款计算方式7. 保理费率： 不用选择是固定还是浮动，直接输入一个利率。
    return [
      AmountColumn({ title: '项目金额(元）', dataIndex: 'creditAmount', ...commonProps }),
      {
        title: '额度有效期(月)',
        dataIndex: 'leaseMonthCount',
        requiredMark: true,
        editable: {
          element: <InputNumber min={0} style={{ width: '100%' }} />,
          required: true,
          rules: [{ required: true, message: '请输入额度有效期(月)' }],
        },
      },
      {
        title: '还款期数',
        dataIndex: 'repayTimes',
        requiredMark: true,
        editable: {
          required: true,
          rules: [{ required: true, message: '请输入还款期数' }],
        },
      },
      AmountColumn({ title: '保证金(元)', dataIndex: 'earnestMoney', ...commonProps }),
      AmountColumn({ title: '手续费(元)', dataIndex: 'consultingFee', ...commonProps }),
      MatchOptionColumn({
        title: '还款频率',
        dataIndex: 'repayRate',
        matchOption: 'repayRateEnum',
        ...commonProps,
      }),
      {
        title: <CommonTips fieldName={'interestWay'}> 利息计算方式</CommonTips>,
        dataIndex: 'interestWay',
        requiredMark: true,
        matchOption: 'interestWayEnum',
        editable: {
          element: <Select options={'interestWayEnum'} onChange={onInterestWayChange} />,
          rules: [{ required: true, message: '请选择' }],
        },
      },
      {
        title: '还款方式',
        dataIndex: 'rentalCalcType',
        matchOption: 'repayCalcType',
        requiredMark: true,
        // editable: {
        //   element: <Select options={'repayCalcType'} />,
        //   rules: [{ required: true, message: '请选择' }],
        // },
        editable: (val) => {
          return <RepayCalcType fieldName="rentalCalcType" />
        },
      },
      AmountColumn({
        ...commonProps,
        title: '保理费率',
        dataIndex: 'interestRate',
        wrapItemProps: {
          required: true,
          inputConfig: {
            addonAfter: '%',
          },
        },
      }),
      DateColumn({
        title: '计划起租日',
        dataIndex: 'startDate',
        span: 2,
        editable: true,
        requiredMark: true,
        rules: [{ required: true, message: '请输入计划起租日' }],
      }),
    ]
  }, [radioValue, form])
  return (
    <Modal store={store.toolModal} title={'IRR 测算工具'} okText={'确定'} width={900} footer={null}>
      <Form
        form={form}
        // initialValues={{
        //   interestWay: 'ACTUAL_RATE',
        // }}
      >
        <Form.Item label="测算内容">
          <Radio.Group
            value={radioValue}
            onChange={(e) => {
              setRadioValue(e.target.value)
              form.resetFields()
            }}
          >
            <Radio value={0}>租赁</Radio>
            <Radio value={1}>保理</Radio>
          </Radio.Group>
        </Form.Item>
        <Project columns={columns} radioValue={radioValue} form={form} ref={projectRef} />
      </Form>
    </Modal>
  )
}

export default observer(Index)
