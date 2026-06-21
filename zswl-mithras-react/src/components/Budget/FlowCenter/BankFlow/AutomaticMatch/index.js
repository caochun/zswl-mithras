import { FormAmount } from '@/components/Form'
import { observer } from '@zswl/admin'
import { Button, Form, Modal, ModalStore, Table, TableStore, Tabs } from '@zswl/components'
import { Divider, message, Space, Tag, Tooltip } from 'antd'
import { forwardRef, useEffect, useImperativeHandle, useMemo, useRef, useState } from 'react'
import { AmountColumn, DateColumn, InputColumn, MatchOptionColumn } from '@/components/Format'
import autoWriteOffApi from '@/api/budget/flowCenter/autoWriteOffApi'
import AddCashFlow from './AddCashFlow'
import { ExclamationCircleOutlined, QuestionCircleOutlined } from '@ant-design/icons'
import styles from '../index.less'
import IconFont from '@/components/Icon'
import { saveServer } from '@/utils'

const writeOffBusinessModel = 'PROJECT_COLLECT'

const modalValidate = (validator, modalParams) => {
  return new Promise(async (resolve, reject) => {
    try {
      if (validator) {
        Modal.confirm({
          title: '提示',
          onOk: async () => resolve(true),
          onCancel: () => reject(false),
          ...modalParams,
        })
      } else {
        resolve(true)
      }
    } catch (e) {
      reject(e)
    }
  })
}
const Content = forwardRef(({ financeFlowTabMainInfoId, plusDays }, ref) => {
  const [receivableAmount, setReceivableAmount] = useState(undefined)
  const [editableKey, setEditableKey] = useState(null)
  const [bankFlowTotal, setBankFlowTotal] = useState(undefined)
  const [prefect, setPrefect] = useState(false)
  const [isWriteOff, setIsWriteOff] = useState(false)
  const getList = async () => {
    if (!financeFlowTabMainInfoId) return
    const {
      bankFlowList,
      businessFlowList,
      bankFlowAmountSum,
      businessFlowAmountSum,
      perfectMatch,
      isWriteOff,
    } = await autoWriteOffApi.postSingleTab({
      financeFlowTabMainInfoId,
      writeOffBusinessModel,
    })
    cashFlowTable.setList(bankFlowList)
    setPrefect(perfectMatch)
    setIsWriteOff(isWriteOff)
    setReceivableAmount(businessFlowAmountSum)
    setBankFlowTotal(bankFlowAmountSum)
    infoTable.setList(businessFlowList)
  }
  const cashFlowTable = useMemo(() => new TableStore({ request: async () => {} }), [])
  const infoTable = useMemo(() => new TableStore({ request: async () => {} }), [])
  useEffect(() => {
    getList()
  }, [financeFlowTabMainInfoId])
  const [form] = Form.useForm()
  const reMatch = async () => {
    const values = form.getFieldsValue()
    const params = {
      ...values,
      writeOffBusinessModel,
      financeFlowTabMainInfoId,
    }
    const res = await autoWriteOffApi.postRematchTab(params)
    await getList()
    message.success('匹配成功')
  }
  const deleteBankFlow = async (id) => {
    const list = cashFlowTable.getList()
    if (list.length === 1) {
      message.error('账户至少保留一条流水，或者去删除账户')
      return
    }
    await autoWriteOffApi.postBankFlowDelete({
      flowIdList: [id],
      financeFlowTabMainInfoId,
      writeOffBusinessModel,
    })
    await getList()
    message.success('删除成功')
  }
  const save = async (record) => {
    const { id } = record
    const { values } = await infoTable.submit()
    const data = values[id]
    await autoWriteOffApi.postBusinessFlowUpdate({
      id,
      financeFlowTabMainInfoId,
      thisWriteOffAmount: data.thisWriteOffAmount * 10000,
      writeOffBusinessModel,
    })
    setEditableKey(null)
    await getList()
    message.success('保存成功')
  }
  const deleteCashFlow = async (id) => {
    await autoWriteOffApi.postBusinessFlowDelete({
      businessFlowIdList: [id],
      financeFlowTabMainInfoId,
      writeOffBusinessModel,
    })
    await getList()
    message.success('删除成功')
  }
  const bankFlowColumns = [
    InputColumn({ title: '对方户名', dataIndex: 'otherName', width: 200 }),
    InputColumn({ title: '对方账号', dataIndex: 'otherBankAccount', width: 200 }),
    AmountColumn({ title: '收款金额（元）', dataIndex: 'collectionAmount', width: 150 }),
    AmountColumn({ title: '付款金额（元）', dataIndex: 'paymentAmount', width: 150 }),
    InputColumn({ title: '摘要', dataIndex: 'mainInfo' }),
    DateColumn({ title: '交易日期', dataIndex: 'transactionDate' }),
    InputColumn({ title: '交易编号', dataIndex: 'transactionDetailsNumber' }),
    {
      title: '操作',
      fixed: 'right',
      dataIndex: 'action',
      actions: (record) => {
        return [
          {
            name: '删除',
            onClick: async () => await deleteBankFlow(record.id),
            type: 'link',
            confirm: true,
          },
        ]
      },
    },
  ]
  const infoColumns = useMemo(
    () => [
      { title: '客户名称', dataIndex: 'clientName', width: 140, editable: false },
      { title: '合同编号', dataIndex: 'sourceBusinessCode', width: 260, editable: false },
      MatchOptionColumn({
        title: '现金流项目',
        dataIndex: 'cashFlowItem',
        matchOption: 'collectionWriteOffOrderEnum',
        width: 120,
        editable: false,
      }),
      { title: '现金流编号', dataIndex: 'cashFlowCode', width: 200, editable: false },
      AmountColumn({
        title: '应收金额（元）',
        dataIndex: 'shouldWriteOffAmount',
        width: 180,
        editable: false,
      }),
      DateColumn({
        title: '应收时间',
        dataIndex: 'shouldWriteOffTime',
        width: 140,
        editable: false,
      }),
      AmountColumn({
        dataIndex: 'noWriteOffAmount',
        title: '未收金额（元）',
        width: 180,
        editable: false,
      }),
      AmountColumn({
        title: '本次核销金额（元）',
        dataIndex: 'thisWriteOffAmount',
        width: 220,
        editable: true,
      }),

      {
        title: '操作',
        dataIndex: 'action',
        fixed: 'right',
        actions: ({ id }) => {
          return [
            {
              name: '编辑',
              hidden: id === editableKey,
              onClick: () => setEditableKey(id),
              type: 'link',
              key: 'edit',
            },
            { name: '保存', hidden: id !== editableKey, onClick: save, key: 'save', type: 'link' },
            {
              name: '取消',
              hidden: id !== editableKey,
              onClick: () => setEditableKey(null),
              key: 'cancel',
              type: 'link',
            },
            {
              name: '删除',
              onClick: async () => await deleteCashFlow(id),
              key: 'delete',
              type: 'link',
            },
          ]
        },
      },
    ],
    [editableKey]
  )
  const addModal = useMemo(() => new ModalStore({}), [])
  const onFinish = async (collectionIds) => {
    const params = {
      collectionIds,
      financeFlowTabMainInfoId,
      writeOffBusinessModel,
    }
    await autoWriteOffApi.postBusinessFlowAdd(params)
    await getList()
    message.success('新增成功')
    addModal.close()
  }
  const addCash = () => {
    addModal.open()
  }
  useImperativeHandle(ref, () => ({
    prefect,
    isWriteOff,
  }))
  return (
    <>
      <div className="z-title">银行流水信息</div>
      <Table
        columnsFilter="BankFlow_AutomaticMatch_1"
        onFilter={(key,val) => saveServer('BankFlow_AutomaticMatch_1',val)}

        store={cashFlowTable}
        columns={bankFlowColumns}
        actions={[
          <Space>
            本次流水待核销金额合计（元）：
            <FormAmount value={bankFlowTotal} disabled />
          </Space>,
          <Tag color={prefect ? 'green' : 'red'}>{prefect ? '完美匹配' : '不完美匹配'}</Tag>,
          isWriteOff ? (
            <Tag color="green">核销</Tag>
          ) : (
            <Tag color="red">
              <Tooltip title="存在银行流水交易日期在当前时间之前且业务账单存在罚息（核销金额>0）需要核销的情况或者存在监管账户不核销，实际核销的时候会跳过当前tab页，请确认">
                <Space>
                  不核销
                  <QuestionCircleOutlined />
                </Space>
              </Tooltip>
            </Tag>
          ),
        ]}
      />
      <Divider />
      <div className="z-title">应收信息</div>
      <Table
        columnsFilter="BankFlow_AutomaticMatch_2"
        onFilter={(key,val) => saveServer('BankFlow_AutomaticMatch_2',val)}

        columns={infoColumns}
        editable={(record) => record.id === editableKey}
        store={infoTable}
        columnWidth={180}
        scroll={{ x: 1300 }}
        actions={[
          <Space>
            本次应收账单待核销金额合计（元）：
            <FormAmount value={receivableAmount} disabled />
          </Space>,
        ]}
        extra={[
          <Form form={form} initialValues={{ plusDays }}>
            <Space>
              <Form.Item label="提前核销" required style={{ marginBottom: 0 }} name="plusDays">
                <FormAmount
                  initFormat={1}
                  addonAfter={'天'}
                  style={{ width: 100 }}
                  min={-Infinity}
                />
              </Form.Item>
              <Button type="primary" onClick={reMatch}>
                重新自动匹配
              </Button>
            </Space>
          </Form>,
          <Button.Add onClick={addCash}>新增现金流项目</Button.Add>,
        ]}
      />
      <AddCashFlow modal={addModal} onFinish={onFinish} />
    </>
  )
})

const BudgetFlowCenterBankFlowAutomaticMatch = ({ baseStore }) => {
  const [form] = Form.useForm()
  const [items, setItems] = useState([])
  const refList = useRef([])

  const modal = useMemo(() => new ModalStore({}), [])

  const handleOK = async (values) => {
    const onOk = async () => {
      const { batchNumber } = modal.getInitialValues() ?? {}
      await autoWriteOffApi.postWriteOff({ batchNumber, writeOffBusinessModel })
      message.success('处理成功')
      modal.close()
      baseStore.table.search()
    }
    const notPerfect = !!refList.current.filter(
      (v) => v.accountType === 'SUPERVISION_ACCOUNT' && !v?.prefect
    ).length
    const content = (
      <div>
        <div>
          您所选流水存在收款账户被融资监管的情况，并且收款无法达到完美匹配的场景，因此本次所选流水均不进行核销，需手工处理特殊情况。
        </div>
        <div style={{ marginTop: 24 }}>说明：“完美匹配”指的是“流水金额=账单金额”</div>
      </div>
    )
    await modalValidate(notPerfect, { content })
    await onOk()
  }
  const handleClick = async () => {
    try {
      const values = await form.validateFields()
      const bankFlowIds = baseStore.selectedRecord.selectedRowKeys
      const params = {
        ...values,
        writeOffBusinessModel,
        bankFlowIds,
      }
      const { unSelectedBankFlowMap } = await autoWriteOffApi.postBeforeImport(params)
      const onOk = async () => {
        const res = await autoWriteOffApi.postMatchResult(params)
        setItems(
          res.map(({ tabName, id, accountType, isWriteOff }, index) => ({
            label: tabName,
            forceRender: true,
            key: id,
            children: (
              <Content
                financeFlowTabMainInfoId={id}
                plusDays={values.plusDays}
                ref={(node) => {
                  refList.current[index] = { ...node, isWriteOff, tabName, accountType }
                }}
              />
            ),
          }))
        )
        modal.open({ batchNumber: res[0].batchNumber, plusDays: values.plusDays })
      }
      const tipsList = []
      Object.values(unSelectedBankFlowMap).forEach((v) => tipsList.push(...v))
      if (tipsList.length) {
        const tipsText = tipsList.map(
          (v, index) => `${index + 1}.账户名称:${v.otherName},银行账号:${v.otherBankAccount}`
        )
        const title = (
          <div>
            <div>
              存在账户流水未被选择，继续操作会只按照您选择的流水进行匹配核销，可能导致流水遗漏，请注意！
            </div>
            {tipsText.map((text) => (
              <div style={{ fontSize: 14, color: '#1e1e1e' }}>{text}</div>
            ))}
          </div>
        )
        Modal.confirm({ width: 800, title, onOk })
      } else {
        onOk()
      }
    } catch (e) {}
  }
  const onCancel = async () => {
    const { batchNumber } = modal.getInitialValues() ?? {}
    try {
      await autoWriteOffApi.postBankFlow({ batchNumber })
      baseStore.table.search()
      modal.close()
    } catch (e) {
      modal.close()
    }
  }
  const onEdit = async (financeFlowTabMainInfoId, action) => {
    Modal.confirm({
      title: '确认删除吗？',
      onOk: async () => {
        if (items.length === 1) {
          modal.close()
        } else {
          await autoWriteOffApi.postDeleteTab({ financeFlowTabMainInfoId, writeOffBusinessModel })
          const newItems = items.filter((item) => item.key !== financeFlowTabMainInfoId)
          setItems(newItems)
          message.success('删除成功')
        }
      },
    })
  }

  return (
    <Form form={form} initialValues={{ plusDays: 0 }}>
      <Space>
        <Form.Item
          label="提前核销"
          style={{ marginBottom: 0 }}
          name="plusDays"
          required
          rules={[{ required: true, message: '请填写' }]}
        >
          <FormAmount initFormat={1} addonAfter={'天'} style={{ width: 100 }} min={-Infinity} />
        </Form.Item>
        <Button type="primary" onClick={handleClick}>
          自动匹配
        </Button>
      </Space>
      <Modal
        store={modal}
        title="自动核销"
        width="100%"
        onCancel={onCancel}
        onOk={handleOK}
        okText="确定核销"
        className={styles.writeModal}
      >
        <div style={{ maxHeight: 'calc(100vh - 160px)', scroll: 'auto' }}>
          <Tabs items={items} type="editable-card" onEdit={onEdit} hideAdd></Tabs>
        </div>
      </Modal>
    </Form>
  )
}

export default observer(BudgetFlowCenterBankFlowAutomaticMatch)
