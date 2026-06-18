import { http, observer } from '@zswl/admin'
import {
  Button,
  Drawer,
  DrawerStore,
  Form,
  Modal,
  ModalStore,
  Select,
  Table,
  TableStore,
} from '@zswl/components'
import { Alert, Checkbox, DatePicker, Divider, Input, InputNumber, message, Space, Tag } from 'antd'

import styles from './index.less'
import { forwardRef, useImperativeHandle, useMemo, useRef, useState } from 'react'

import { getTableColumns, options } from '@/utils'
import writeOffFlowCenterApi from '@/api/cpm/payment/writeOffFlowCenterApi'
import { saveServer } from '@/utils'

const { financingFlowWriteOffStatusEnum } = options

const width = 400
const NOT_FLOW_LIST = ['票据', '信用证', '保证金抵扣']
const cashFlowColumns = [
  {
    title: '核销类型',
    dataIndex: 'flowStatus',
    width: 120,
    render: (val) => {
      const { label, color } = financingFlowWriteOffStatusEnum.find((v) => v.value === val) ?? {}
      return (
        <Tag style={{ border: 0 }} color={color}>
          {label}
        </Tag>
      )
    },
  },
  InputColumn({
    title: '交易明细编号',
    dataIndex: 'transactionDetailsNumber',
    search: true,
    width: 240,
  }),
  InputColumn({ title: '资金组织', dataIndex: 'financialOrganization' }),
  InputColumn({ title: '银行帐号', dataIndex: 'bankAccount', search: true }),
  InputColumn({ title: '开户银行', dataIndex: 'bankName', search: true }),
  InputColumn({ title: '币别', dataIndex: 'currency', width: 80 }),
  // InputColumn({ title: '业务项目', dataIndex: 'projName' }),
  DateColumn({ title: '交易时间', dataIndex: 'transactionDate', search: true, width: 120 }),
  InputColumn({ title: '摘要', dataIndex: 'mainInfo', search: true }),
  AmountColumn({ title: '收款金额', dataIndex: 'collectionAmount', width: 140 }),
  AmountColumn({ title: '付款金额', dataIndex: 'paymentAmount', width: 140 }),
  AmountColumn({ title: '余额', dataIndex: 'depositAmount', width: 120 }),
  AmountColumn({ title: '手续费', dataIndex: 'handingFees', width: 120 }),
  InputColumn({ title: '对方户名', dataIndex: 'otherName', search: true }),
  InputColumn({ title: '对方账号', dataIndex: 'otherBankAccount', search: true }),
  InputColumn({ title: '对方开户行', dataIndex: 'otherBankName', search: true }),
  InputColumn({ title: '明细流水号', dataIndex: 'detailSerialNumber' }),
  InputColumn({ title: '数据来源', dataIndex: 'dataSource', width: 120 }),
  InputColumn({ title: '最后更新时间', dataIndex: 'updateTime' }),
  InputColumn({ title: '保融流水', dataIndex: 'cicoBruid' }),
]
const AddCashFlow = forwardRef(({}, ref) => {
  const [selectedRows, setSelectedRows] = useState([])
  const selectedRowKeys = selectedRows.map((item) => item?.id)
  const modal = useMemo(() => new ModalStore({}), [])
  const table = useMemo(() => new TableStore({ request: async () => [] }), [])
  const pullFlow = async () => {
    const res = await writeOffFlowCenterApi.postPullFlow({})
    if (res.length) {
      Modal.info({
        title: '提示',
        content: `交易明细编号为${res?.join('、')}的流水，融租易系统未删除，请及时处理！`,
      })
    }
    message.success('拉取成功')
    table.search()
  }

  const onOk = async () => {
    const list = table.getList()
    const hasRepeat = selectedRows.filter((item) => list.find((v) => v.id === item.id))
    if (hasRepeat.length) {
      const repeatText = hasRepeat.map((v) => v.transactionDetailsNumber).join('、')
      message.error(`交易明细编号为${repeatText}的流水已经添加!`)
      return
    }
    selectedRows.forEach((item) => {
      table.addRow(item)
    })
    modal.close()
    setSelectedRows([])
  }
  const columns = [
    { title: '对方户名', dataIndex: 'otherName' },
    { title: '交易时间', dataIndex: 'transactionDate' },
    AmountColumn({ title: '收款金额（元）', dataIndex: 'collectionAmount' }),
    AmountColumn({ title: '付款金额（元）', dataIndex: 'paymentAmount' }),
    AmountColumn({ title: '本次核销金额（元）', dataIndex: 'paymentAmount' }),
    { title: '摘要', dataIndex: 'mainInfo' },
    {
      title: '操作',
      dataIndex: 'operation',
      fixed: 'right',
      actions: () => {
        return [
          {
            name: '删除',
            onClick: (record) => table.deleteRow(record.id),
          },
        ]
      },
    },
  ]

  const cashFlowTable = useMemo(
    () =>
      new TableStore({
        request: async (params) => {
          return writeOffFlowCenterApi.postCenterList({
            ...params,
            collectionPaymentType: 'PAYMENT',
            tabType: 'PROCESSING_CENTER',
          })
        },
      }),
    []
  )
  const addCash = () => {
    modal.open()
  }

  const clearSelected = () => {
    cashFlowTable.clearSelected()
    setSelectedRows([])
  }
  const searchItem = [
    {
      title: '收付款类型',
      dataIndex: 'collectionPaymentType',
      element: (
        <Select
          options={'bankFlowPaymentCollectionTypeEnum'}
          allowClear
          getPopupContainer={() => document.body}
          disabled
        />
      ),
    },
  ]

  const handleSelectChange = (selectedRowKeys, selectedRows) => {
    setSelectedRows(selectedRows)
  }
  useImperativeHandle(ref, () => {
    return {
      getList: table.getList,
    }
  })

  return (
    <>
      <div>
        <Table
                columnsFilter={'Components_RecordList_WriteOffModal_1'}
                onFilter={(key,val) => saveServer('Components_RecordList_WriteOffModal_1',val)}
          columns={columns}
          store={table}
          rowClassName={styles.rowClassName}
          serial
          columnWidth={180}
          actions={[
            <div className={styles.title}>流水中心</div>,
            <Button.Add onClick={addCash}>新增流水</Button.Add>,
          ]}
        />
        <Modal store={modal} title="交易流水" width={1200} onOk={onOk}>
          <Table
            title={() => {
              return <Alert message={`已选中${selectedRows.length}条记录`}></Alert>
            }}
            store={cashFlowTable}
            columns={cashFlowColumns}
            columnWidth={200}
            scroll={{ x: true }}
            columnsFilter="flowCenter"
            onFilter={(key,val) => saveServer('flowCenter',val)}
            resizable
            serial
            extra={[
              {
                name: '清空选中',
                key: 'clearSelected',
                onClick: clearSelected,
              },
            ]}
            actions={[
              <Button type="primary" onClick={pullFlow} key={'update'}>
                流水更新
              </Button>,
            ]}
            editable={false}
            selectable={{
              type: 'checkbox',
              selectedRowKeys: selectedRowKeys,
              preserveSelectedRowKeys: true,
              onChange: handleSelectChange,
            }}
            searchbar={{
              labelCol: { span: 6 },
              initialValues: {
                collectionPaymentType: 'PAYMENT',
              },
              items: searchItem,
            }}
          />
        </Modal>
      </div>
    </>
  )
})
const { Item } = Form
function WriteOffModal({ record, detail, actualDetail, store }) {
  const cashTableRef = useRef()
  const canWriteOffAmount = (record.paidInAmount ?? 0) - (record.paidAmount ?? 0)
  const [cashFlowCodeList, setCashFlowCodeList] = useState([])
  const [form] = Form.useForm()
  const cashFlowCodeChange = (val) => {
    const { writeOffedAmount, payInAmount } =
      cashFlowCodeList.find((item) => item.cashFlowCode === val) ?? {}
    form.setFieldsValue({ paidAmount: writeOffedAmount, paymentAmount: payInAmount })
  }
  const writeOffModal = useMemo(
    () =>
      new DrawerStore({
        onOpen: async (values) => {
          const {
            paidInDate,
            paidInAmount,
            id: paymentActualDetailId,
            paidAmount,
            paymentMethod,
            paymentId,
            ...rest
          } = values
          const res =
            await writeOffFlowCenterApi.postCollectionFlowCenterBusinessPaymentManualCashFlowList(
              { paymentId, paymentMethod, paymentActualDetailId }
            )
          setCashFlowCodeList(res)
          return {
            ...rest,
            paymentMethod,
            cashFlowCode: res?.[0]?.cashFlowCode,
            paymentId,
            paidAmount: res?.[0]?.writeOffedAmount,
            paymentAmount: res?.[0]?.payInAmount,
            cashFlowItem: 'CREDIT_PAYMENT',
            paymentActualDetailId,
          }
        },
        onFinish: async (values) => {
          const thisWriteOffAmount = (values.paymentAmount ?? 0) - (values.paidAmount ?? 0)
          if (!NOT_FLOW_LIST.includes(values.paymentMethod)) {
            const newList = cashTableRef.current.getList()
            if (!newList.length) {
              message.error('请先添加流水')
              return
            }

            const financeFlowIds = newList.map((item) => item.id)
            const listDataJson = JSON.stringify([
              {
                ...values,
                cashFlowItemName: '投放款',
                cashFlowCode: `${detail.paymentCode}-${record.seqCode}`,
                shouldPayTime: record.paidInDate,
                noPayAmount: actualDetail.obligation,
                shouldPayAmount: actualDetail.payableAmount,
                thisWriteOffAmount,
              },
            ])

            if (newList.length) {
              await writeOffFlowCenterApi.postWriteOff({
                financeFlowIds,
                sideType: 'PROJ_SIDE',
                writeOffType: 'PAYMENT',
                listDataJson,
              })
            }

            store.unConfirmCollectionTable.search()
            message.success('核销成功')
            writeOffModal.close()
            return
          }
          const { billCode, billAmount, billExpireDate, ...rest } = values

          if (rest.paidInAmount > thisWriteOffAmount) {
            message.error('本次核销金额不能大于核销总金额')
            return
          }
          const billManagementAddREQ = {
            billCode,
            billAmount,
            billExpireDate,
            mainId: values.paymentId,
            billType: 'PAYMENT',
          }

          await writeOffFlowCenterApi.postManualRecord({
            ...rest,
            billManagementAddREQ,
          })
          writeOffModal.close()
          message.success('核销成功')
          store.unConfirmCollectionTable.search()
        },
      }),
    [record, store]
  )
  const handleWrite = () => {
    writeOffModal.open(record)
  }
  const isWY = !NOT_FLOW_LIST.includes(record.paymentMethod)
  const disabled = ['CLOSED'].includes(record.writeOffStatus) || canWriteOffAmount <= 0
  return (
    <>
      <Button type="link" onClick={handleWrite} disabled={disabled} className={styles.button}>
        付款核销
      </Button>
      <Drawer
        title={'手工核销'}
        store={writeOffModal}
        okText={'确定'}
        width={isWY ? 1200 : 600}
        destroyOnClose
      >
        <div style={{ maxHeight: 900, overflowY: 'auto', padding: '0 10px' }}>
          <Form labelCol={{ span: 8 }} form={form}>
            <div className={styles.title}>流水信息</div>

            <div style={{ width }}>
              <Item name={'paymentId'} hidden>
                <Input disabled />
              </Item>
              <Item name={'paymentActualDetailId'} hidden>
                <Input disabled />
              </Item>

              <Item name={'serialNo'} hidden>
                <Input disabled />
              </Item>

              <Item label={'客户名称'} name={'clientName'}>
                <Input disabled />
              </Item>
              <Item label={'合同编号'} name={'contractCode'}>
                <Input disabled />
              </Item>
              <Item label={'现金流项目'} name={'cashFlowItem'}>
                <Select disabled options={'paymentFlowItemEnum'} />
              </Item>
              <Item
                name={'cashFlowCode'}
                label={'现金流编号'}
                rules={[{ required: true, message: '请输入现金流编号' }]}
              >
                <Select
                  options={cashFlowCodeList}
                  fieldNames={{ label: 'cashFlowCode', value: 'cashFlowCode' }}
                  onChange={cashFlowCodeChange}
                />
              </Item>
              <FormAmount.Item
                disabled
                label={'应付金额（元）'}
                name={'paymentAmount'}
                isRequired={false}
              />

              <FormAmount.Item
                disabled
                label={'已付金额（元）'}
                name={'paidAmount'}
                isRequired={false}
              />
            </div>
            <Divider />
            <div className={styles.title}> 本次核销</div>
            <div style={{ width }}>
              <Item
                label={'付款方式'}
                name={'paymentMethod'}
                rules={[{ required: true, message: '请选择付款方式！' }]}
              >
                <Input disabled />
              </Item>
            </div>
            <Item dependencies={['paymentMethod']} noStyle>
              {({ getFieldValue }) => {
                const paymentMethod = getFieldValue('paymentMethod')
                if (!NOT_FLOW_LIST.includes(paymentMethod)) {
                  return <AddCashFlow ref={cashTableRef} />
                }

                const defaultComponent = (
                  <div style={{ width }}>
                    <Item
                      label={'实付日期'}
                      name={'paidInDate'}
                      rules={[{ required: true }]}
                      transform={(value) => value && value.format('YYYY-MM-DD')}
                    >
                      <DatePicker />
                    </Item>

                    <FormAmount.Item
                      label={'付款金额（元）'}
                      name={'paidInAmount'}
                      min={-Infinity}
                    />
                  </div>
                )

                if (paymentMethod === '票据') {
                  return (
                    <div style={{ width }}>
                      <Item
                        label={'票据号'}
                        name={'billCode'}
                        rules={[{ required: true, message: '请输入票据号！' }]}
                      >
                        <Input />
                      </Item>
                      <FormAmount.Item label={'票据面额'} name={'billAmount'} />
                      <Item
                        label={'票据到期日'}
                        name={'billExpireDate'}
                        transform={(value) => value && value.format('YYYY-MM-DD')}
                        rules={[{ required: true, message: '请输入票据到期日！' }]}
                      >
                        <DatePicker />
                      </Item>

                      {defaultComponent}
                    </div>
                  )
                }
                return <div style={{ width }}>{defaultComponent}</div>
              }}
            </Item>
          </Form>
        </div>
      </Drawer>
    </>
  )
}

export default observer(WriteOffModal)
