import { AmountColumn, AmountEditable, InputNumberEditable } from '@/components/Format'
import FormGuaranteeScheme from '@/pages/financial/fund/Component/FormGuaranteeScheme'
import YearRate from '@/components/Financial/FundYearRate'
import { amountFormat, formatPercent } from '@/utils'
import { App, Form, Select } from '@zswl/components'
import { Col, Row, Tag } from 'antd'

import FormGuarantee from './FormGuarantee'

const { Item } = Form

const ALL_COLUMNS = ({
  isLog,
  handleCalc,
  showInterestRateType,
  setShowInterestRateType,
  baseInfoDetail,
  isOtherChange,
}) => {
  const isYT = baseInfoDetail?.businessType === 'SYNDICATIONS'
  return [
    {
      title: '融资金额(元)',
      dataIndex: 'financingAmount',
      requiredMark: true,
      width: 150,
      editable: (val) =>
        AmountEditable(val, 'financingAmount', {
          required: true,
          disabled: isYT,
        }),
      render: (val) => amountFormat(formatPercent(val)),
    },
    {
      title: '预计利息金额(元)',
      dataIndex: 'interestAmount',
      requiredMark: true,
      width: 150,
      editable: (val) => AmountEditable(val, 'interestAmount', { required: true, disabled: false }),
      render: (val) => amountFormat(formatPercent(val)),
    },
    {
      title: '增信方式',
      dataIndex: 'guaranteeInfoList',
      span: 2,
      editable: ({ guaranteeInfoList }) => {
        return isOtherChange ? (
          false
        ) : (
          <Form.Item dependencies={[]} noStyle>
            {({ getFieldValue }) => {
              return (
                <FormGuarantee
                  listName="guaranteeInfoList"
                  value={guaranteeInfoList}
                  baseInfoDetail={baseInfoDetail}
                  orgList={baseInfoDetail.organizationInfoList}
                  isYT={isYT}
                />
              )
            }}
          </Form.Item>
        )
      },
      render: (val) => <FormGuarantee.Detail value={val} isYT={isYT} />,
    },
    {
      title: '融资期限(月)',
      dataIndex: 'financingMonth',
      requiredMark: true,
      width: 150,
      editable: () => InputNumberEditable({ required: true }),
      render: (val) => val,
    },
    AmountColumn({ title: '费用合计 (元)', dataIndex: 'totalFee' }),
    {
      title: '还款期数',
      dataIndex: 'repayTimes',
      requiredMark: true,
      width: 150,
      editable: () => InputNumberEditable({ required: true }),
      render: (val) => val,
    },
    {
      title: '保证金金额(元)',
      dataIndex: 'earnestMoneyAmount',
      width: 150,
      editable: (val) =>
        AmountEditable(val, 'earnestMoneyAmount', { required: false, disabled: false }),
      render: (val) => amountFormat(formatPercent(val)),
    },
    {
      title: '还款频率',
      dataIndex: 'repayFrequency',
      width: 150,
      requiredMark: true,
      matchOption: 'repaymentFrequencyEnum',
      editable: {
        element: <Select options="repaymentFrequencyEnum" />,
        rules: [{ required: true, message: '请输入' }],
      },
    },

    {
      title: '还款方式',
      dataIndex: 'repayWay',
      requiredMark: true,
      width: 150,
      matchOption: 'fundFinancingRepayWayEnum',
      editable: {
        element: <Select options="fundFinancingRepayWayEnum" />,
        rules: [{ required: true, message: '请输入' }],
      },
    },

    {
      title: '担保费',
      dataIndex: 'guaranteeAmountInfoList',
      span: 2,
      editable: ({ guaranteeAmountInfoList } = {}) => {
        return (
          <Form.Item dependencies={['financingAmount', 'financingMonth']} noStyle>
            {({ getFieldValue }) => {
              const financingAmount = getFieldValue('financingAmount')
              const financingMonth = getFieldValue('financingMonth')
              return (
                <FormGuaranteeScheme
                  listName="guaranteeAmountInfoList"
                  value={guaranteeAmountInfoList ?? []}
                  financingAmount={financingAmount}
                  financingMonth={financingMonth}
                />
              )
            }}
          </Form.Item>
        )
      },
      render: (val) => <FormGuaranteeScheme.Detail value={val ?? []} />,
    },
    AmountColumn({
      title: 'FTP收益率(%)',
      dataIndex: 'ftpYieldRate',
      requiredMark: false,
      span: 2,
      editable: false,
    }),

    {
      title: '借款年利率',
      dataIndex: 'interestRateType',
      span: 2,
      requiredMark: true,
      editable: (val) => {
        return (
          <YearRate
            data={val}
            onRateTypeChange={(value) => {
              setShowInterestRateType(value === 'FLOAT')
            }}
          />
        )
      },
      render: (val, record) => <YearRate.Detail data={record} isLog={isLog} />,
    },
    AmountColumn({
      title: '综合融资成本（%）',
      dataIndex: 'comprehensiveFinancingCost',
      editable: false,
    }),
    {
      title: '还款日',
      dataIndex: 'repayDay',
      width: 150,
      editable: () =>
        InputNumberEditable({
          required: false,
          addonAfter: '日',
          min: 1,
          max: 31,
        }),
      render: (val) => val,
    },
    showInterestRateType && {
      title: 'LPR调整方式',
      dataIndex: 'lprArrangeMode',
      span: 2,
      requiredMark: true,
      editable: (record) => {
        return (
          <Form.Item dependencies={['interestRateType']} noStyle>
            {({ getFieldValue }) => {
              const interestRateType = getFieldValue('interestRateType')
              if (interestRateType === 'FLOAT') {
                return (
                  <Row gutter={12} align="middle" wrap={false}>
                    <Col flex="none">
                      <span style={{ whiteSpace: 'nowrap', lineHeight: '32px' }}>
                        实际贷款日期后
                      </span>
                    </Col>
                    <Col flex="none">
                      <Item
                        name="lprArrangeMode"
                        label="实际贷款日期后"
                        rules={[{ required: true, message: '请选择' }]}
                        colon={false}
                        required={false}
                        noStyle
                        initialValue={record.lprArrangeMode}
                      >
                        <Select options={'lprArrangeModeEnum'} style={{ width: 120 }} />
                      </Item>
                    </Col>
                    <Form.Item
                      dependencies={[
                        'lprArrangeMode',
                        'lprAdjustmentMonth',
                        'lprAdjustmentDayTemp',
                        'lprAdjustmentDay',
                      ]}
                      noStyle
                    >
                      {({ getFieldValue, setFieldValue }) => {
                        const lprArrangeMode = getFieldValue('lprArrangeMode')
                        const isYear = lprArrangeMode === 'YEAR'
                        // 当选择"年"选项时，如果当前值不是 MM-DD 格式（说明是之前非"年"选项的值），则清空
                        if (isYear && lprArrangeMode) {
                          const currentValue = getFieldValue('lprAdjustmentDay')
                          const isMMDDFormat = currentValue && /^\d{2}-\d{2}$/.test(currentValue)
                          if (currentValue && !isMMDDFormat) {
                            setFieldValue('lprAdjustmentDay', undefined)
                            setFieldValue('lprAdjustmentMonth', undefined)
                            setFieldValue('lprAdjustmentDayTemp', undefined)
                          }
                        }

                        // 当选择不是"年"的选项时，如果当前值是 MM-DD 格式（说明是之前"每年"时填写的），则设置为默认值
                        if (!isYear && lprArrangeMode) {
                          const currentValue = getFieldValue('lprAdjustmentDay')
                          const isMMDDFormat = currentValue && /^\d{2}-\d{2}$/.test(currentValue)
                          if (isMMDDFormat) {
                            setFieldValue('lprAdjustmentDay', 'CARRY_INTEREST_DAY')
                            setFieldValue('lprAdjustmentMonth', undefined)
                            setFieldValue('lprAdjustmentDayTemp', undefined)
                          }
                        }

                        if (isYear) {
                          const currentValue = getFieldValue('lprAdjustmentDay')
                          const initialValue = currentValue || record.lprAdjustmentDay
                          const [initialMonth, initialDay] =
                            initialValue && /^\d{2}-\d{2}$/.test(initialValue)
                              ? initialValue.split('-')
                              : [undefined, undefined]

                          const getDaysInMonth = (month) => {
                            if (!month) return 31
                            const monthNum = parseInt(month, 10)
                            if ([1, 3, 5, 7, 8, 10, 12].includes(monthNum)) {
                              return 31
                            }
                            if ([4, 6, 9, 11].includes(monthNum)) {
                              return 30
                            }
                            if (monthNum === 2) {
                              return 29
                            }
                            return 31
                          }

                          const selectedMonth = getFieldValue('lprAdjustmentMonth') || initialMonth
                          const daysInMonth = getDaysInMonth(selectedMonth)

                          const monthOptions = Array.from({ length: 12 }, (_, i) => ({
                            label: `${i + 1}月`,
                            value: String(i + 1).padStart(2, '0'),
                          }))

                          const dayOptions = Array.from({ length: daysInMonth }, (_, i) => ({
                            label: `${i + 1}日`,
                            value: String(i + 1).padStart(2, '0'),
                          }))

                          const updateCombinedValue = () => {
                            const month = getFieldValue('lprAdjustmentMonth')
                            const day = getFieldValue('lprAdjustmentDayTemp')
                            if (month && day) {
                              setFieldValue('lprAdjustmentDay', `${month}-${day}`)
                            }
                          }

                          const handleMonthChange = (month) => {
                            setFieldValue('lprAdjustmentMonth', month)
                            const currentDay = getFieldValue('lprAdjustmentDayTemp')
                            const newDaysInMonth = getDaysInMonth(month)
                            if (currentDay && parseInt(currentDay, 10) > newDaysInMonth) {
                              setFieldValue('lprAdjustmentDayTemp', undefined)
                              setFieldValue('lprAdjustmentDay', undefined)
                            } else {
                              updateCombinedValue()
                            }
                          }

                          return (
                            <>
                              <Col flex="none">
                                <Item
                                  name="lprAdjustmentMonth"
                                  noStyle
                                  initialValue={initialMonth}
                                  rules={[{ required: true, message: '请选择月份' }]}
                                >
                                  <Select
                                    options={monthOptions}
                                    placeholder="月份"
                                    style={{ width: 80 }}
                                    onChange={handleMonthChange}
                                  />
                                </Item>
                              </Col>
                              <Col flex="none">
                                <Item
                                  name="lprAdjustmentDayTemp"
                                  noStyle
                                  initialValue={initialDay}
                                  rules={[{ required: true, message: '请选择日期' }]}
                                >
                                  <Select
                                    options={dayOptions}
                                    placeholder="日期"
                                    style={{ width: 80 }}
                                    onChange={(day) => {
                                      setFieldValue('lprAdjustmentDayTemp', day)
                                      updateCombinedValue()
                                    }}
                                  />
                                </Item>
                              </Col>
                              <Item name="lprAdjustmentDay" noStyle hidden>
                                <input type="hidden" />
                              </Item>
                            </>
                          )
                        }

                        // 计算初始值：如果选择非"每年"，且 record.lprAdjustmentDay 是 MM-DD 格式（说明是之前"每年"时填写的），则使用默认值
                        let initialValue
                        if (isYear) {
                          initialValue = record.lprAdjustmentDay
                        } else {
                          // 如果 record.lprAdjustmentDay 是 MM-DD 格式，则使用默认值
                          const isMMDDFormat =
                            record.lprAdjustmentDay && /^\d{2}-\d{2}$/.test(record.lprAdjustmentDay)
                          initialValue = isMMDDFormat ? 'CARRY_INTEREST_DAY' : (record.lprAdjustmentDay || 'CARRY_INTEREST_DAY')
                        }

                        return (
                          <Col flex="none">
                            <Item
                              label=" "
                              name="lprAdjustmentDay"
                              rules={[{ required: true, message: '请选择' }]}
                              colon={false}
                              noStyle
                              initialValue={initialValue}
                            >
                              <Select
                                options="lprAdjustmentDayEnum"
                                disabled
                                style={{ width: 120 }}
                                placeholder="请选择"
                              />
                            </Item>
                          </Col>
                        )
                      }}
                    </Form.Item>
                    <Col flex="none">
                      <span style={{ whiteSpace: 'nowrap', lineHeight: '32px' }}>调整</span>
                    </Col>
                  </Row>
                )
              }
            }}
          </Form.Item>
        )
      },

      render: (value, { lprArrangeMode, lprAdjustmentDay }) => {
        if (lprArrangeMode) {
          const isYear = lprArrangeMode === 'YEAR'

          const adjustmentDayLabel = isYear
            ? lprAdjustmentDay
            : App.matchOption('lprAdjustmentDayEnum', lprAdjustmentDay)?.label || lprAdjustmentDay

          return (
            <div>
              实际贷款日期后
              <Tag>{App.matchOption('lprArrangeModeEnum', lprArrangeMode).label}</Tag>
              {adjustmentDayLabel && <Tag>{adjustmentDayLabel}</Tag>}调整
            </div>
          )
        }
        return '-'
      },
    },
  ].filter(Boolean)
}
export default ALL_COLUMNS
