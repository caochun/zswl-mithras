import { observer } from '@zswl/admin'
import { Descriptions, Input, InputNumber } from 'antd'
import { useMemo } from 'react'
import styles from '../index.less'
import { amountFormat, amountStrToNumber, getInputNumberAmountProps, hasValue, validatorAmount } from '@/utils'
import IconFont from '@/components/Icon'
import { Form, Modal, Table } from '@zswl/components'
import mathjs from '@/utils/math'
import { saveServer } from '@/utils'

const CashFlowInfo = ({ store }) => {
  const data = store.page.getData()
  const columns = useMemo(() => {
    const {
      planCollectionDate,
      phase,
      cashFlowItem,
      cashFlowAmount,
      principal,
      interest,
      penaltyInterest,
      penaltyInterestChange,
      penaltyInterestUpdate,
    } = data
    return [
      {
        label: '计划收款日期',
        value: planCollectionDate,
      },
      {
        label: '期项',
        value: phase,
      },
      {
        label: '现金流项目',
        value: cashFlowItem,
      },

      {
        label: '现金流金额(元)',
        value: amountFormat(cashFlowAmount / 10000),
      },
      {
        label: '本金(元)',
        value: hasValue(principal) ? amountFormat(principal / 10000) : '-',
      },
      {
        label: '利息(元)',
        value: hasValue(interest) ? amountFormat(interest / 10000) : '-',
      },
      {
        label: '罚息(元)',
        value: (
          <div
            style={{
              width: '100%',
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
              // color: penaltyInterestUpdate === 1 ? 'red' : undefined,
            }}
          >
            {/* {hasValue(store.page.getData().penaltyInterestData?.penaltyInterestAmount)
              ? amountFormat(
                  store.page.getData().penaltyInterestData?.penaltyInterestAmount / 10000
                )
              : '-'} */}
            {hasValue(store.page.getData().penaltyInterestData?.penaltyInterestAmount)
              ? mathjs.toNonExponentialPlus(
                  mathjs.format(
                    mathjs.divide(
                      store.page.getData().penaltyInterestData?.penaltyInterestAmount,
                      10000
                    )
                  )
                )
              : '-'}
            {penaltyInterestChange === 0 && (
              <IconFont
                onClick={() => {
                  store.penaltyInterestModal.open({
                    // penaltyInterestAmount:
                    //   store.page.getData().penaltyInterestData?.penaltyInterestAmount / 10000,
                    penaltyInterestAmount: mathjs.toNonExponentialPlus(
                      mathjs.format(
                        mathjs.divide(
                          store.page.getData().penaltyInterestData?.penaltyInterestAmount,
                          10000
                        )
                      )
                    ),
                    // dailyRate: store.page.getData().penaltyInterestData.dailyRate / 10000,
                    dailyRate: mathjs.toNonExponentialPlus(
                      mathjs.format(
                        mathjs.divide(store.page.getData().penaltyInterestData.dailyRate, 10000)
                      )
                    ),
                    comment: store.page.getData().penaltyInterestData.comment,
                  })
                }}
                style={{ cursor: 'pointer' }}
                type="icon-icon_info"
              />
            )}
          </div>
        ),
      },
      {
        label: '利息(元)',

        labelStyle: {
          display: 'none',
        },
      },
      {
        label: '利息(元)',

        labelStyle: {
          display: 'none',
        },
      },
    ]
  }, [data])

  return (
    <>
      <Descriptions
        title="现金流信息"
        bordered
        column={3}
        style={{
          marginBottom: 20,
        }}
        labelStyle={{ background: '#F5F6FA' }}
        size={'small'}
        className={styles.desSmell}
      >
        {columns.map((item, index) => {
          return (
            <Descriptions.Item key={index} {...item}>
              {item.value}
            </Descriptions.Item>
          )
        })}
      </Descriptions>
      <PenaltyInterestModal store={store} />
    </>
  )
}

const PenaltyInterestModal = ({ store }) => {
  const [form] = Form.useForm()

  const penaltyInterestData = store.page.getData().penaltyInterestData || {}
  const layout = {
    labelCol: { span: 4 },
    wrapperCol: { span: 20 },
  }
  const descriptionsColumns = useMemo(() => {
    const { sumAmount, writeOffAmount, lastAmount, creditAmount } = penaltyInterestData
    return [
      {
        label: '系统计算罚息(元)',
        value: amountFormat(sumAmount / 10000),
      },
      {
        label: '核销罚息(元)',
        value: amountFormat(writeOffAmount / 10000),
      },
      {
        label: '罚息减免金额(元)',
        value: amountFormat(creditAmount / 10000),
      },
      {
        label: '罚息余额(元)',
        value: amountFormat(lastAmount / 10000),
      },
    ]
  }, [])
  const columns = [
    {
      title: '记录日期',
      width: 80,
      dataIndex: 'recordDate',
    },
    {
      title: '逾期金额(元)',
      width: 100,
      dataIndex: 'overdueAmount',
      render: (val) => amountFormat(val / 10000),
    },
    {
      title: '单日产生罚息(元)',
      width: 100,
      dataIndex: 'dayPenaltyInterest',
      render: (val) => amountFormat(val / 10000),
    },
    {
      title: '罚息余额(元)',
      width: 100,
      dataIndex: 'lastPenaltyInterest',
      render: (val) => amountFormat(val / 10000),
    },
  ]
  return (
    <Modal title="罚息信息" store={store.penaltyInterestModal} width={800} footer={null}>
      <Form form={form} {...layout}>
        <Form.Item label="罚息日利率(%)" name="dailyRate">
          <Input disabled style={{ width: '200px' }} placeholder="请输入罚息日率！" />
        </Form.Item>
        {/* <Form.Item
          label="罚息减免金额（元）"
          name="creditAmount"
          rules={[
            validatorAmount,
            ({ getFieldValue }) => ({
              validator(_, value) {
                const valueNumber = amountStrToNumber(value)
                if (value && valueNumber < penaltyInterestData.writeOffAmount / 10000) {
                  return Promise.reject(new Error('罚息金额不能小于核销罚息！'))
                }

                return Promise.resolve()
              },
            }),
          ]}
        >
          <InputNumber
            style={{ width: '200px' }}
            {...getInputNumberAmountProps}
            disabled
            placeholder="请输入罚息金额！"
          />
        </Form.Item> */}
        <Form.Item
          label="罚息金额(元)"
          name="penaltyInterestAmount"
          rules={[
            validatorAmount,
            ({ getFieldValue }) => ({
              validator(_, value) {
                const valueNumber = amountStrToNumber(value)
                if (value && valueNumber < penaltyInterestData.writeOffAmount / 10000) {
                  return Promise.reject(new Error('罚息金额不能小于核销罚息！'))
                }

                return Promise.resolve()
              },
            }),
          ]}
        >
          <InputNumber
            style={{ width: '200px' }}
            {...getInputNumberAmountProps}
            disabled
            placeholder="请输入罚息金额！"
          />
        </Form.Item>
        {/* <Form.Item label="备注" name="comment">
          <Input.TextArea placeholder="请输入备注！" />
        </Form.Item> */}
      </Form>
      <div className={styles.modalTitle}>罚息汇总：</div>
      <Descriptions
        title=""
        bordered
        column={3}
        style={{
          marginBottom: 20,
        }}
        labelStyle={{ background: '#F5F6FA' }}
        size={'small'}
        className={styles.desSmell}
      >
        {descriptionsColumns.map((item, index) => {
          return (
            <Descriptions.Item key={index} {...item}>
              {item.value}
            </Descriptions.Item>
          )
        })}
      </Descriptions>
      <div className={styles.modalTitle}>罚息记录：</div>
      <Table  columnsFilter={'detail_Components_CashFlowInfo'}
              onFilter={(key,val) => saveServer('detail_Components_CashFlowInfo',val)}
               scroll={false} store={store.penaltyInterestModalTable} columns={columns} />
    </Modal>
  )
}
export default observer(CashFlowInfo)
