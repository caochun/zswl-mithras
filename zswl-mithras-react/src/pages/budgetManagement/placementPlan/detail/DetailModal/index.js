import { observer } from '@zswl/admin'
import EditDescription from '@/components/Table/EditDescription'
import { Table, Modal, Button, Form, Select, TableStore, App } from '@zswl/components'
import styles from './style.less'
import { DetailLayout } from '@/components'
import { MatchOptionColumn, DateColumn, AmountColumn } from '@/components/Format'
import {
  forwardRef,
  useCallback,
  useEffect,
  useImperativeHandle,
  useMemo,
  useRef,
  useState,
} from 'react'
import deliveryPlanDetailApi from '@/api/budgetManagement/deliveryPlanDetailApi'
import { Input, message, Space } from 'antd'
import { rules } from '@/utils'
import FormAmount from '@/components/Form/FormAmount'
import BaseInfoComponent from './BaseInfo'
import { ImportAction } from '@/components/Actions'
import Api from './api'
import DownloadTemplate from '@/components/Actions/DownloadTemplate'

const amountCommonProps = {
  editable: true,
  wrapItemProps: {
    required: true,
  },
  requiredMark: true,
}

// 报价方案描述列表配置
const QuoteSchemeComponent = forwardRef(({ detail, canEdit, handleSave }, ref) => {
  const { payType } = App.getData().optionsType
  const newPayType = payType.filter((item) => item.value === 'AFTERWARD')
  const desc = useRef(null)
  const columnName = [
    AmountColumn({
      title: '项目金额(元)',
      dataIndex: 'projectAmount',
      ...amountCommonProps,
    }),
    AmountColumn({
      title: '首期租金率(%)',
      dataIndex: 'firstRentRate',
      ...amountCommonProps,
      suffix: '%',
    }),
    AmountColumn({
      title: '租赁期限(月)',
      dataIndex: 'termMonth',
      ...amountCommonProps,
      initFormat: 1,
    }),
    AmountColumn({
      title: '保证金率(%)',
      dataIndex: 'depositRate',
      ...amountCommonProps,
    }),
    MatchOptionColumn({
      title: '还款频率',
      dataIndex: 'repayFrequency',
      matchOption: 'repaymentFrequencyEnum',
      editable: true,
      requiredMark: true,
    }),
    AmountColumn({
      title: '咨询费率(%)',
      dataIndex: 'consultingFeeRate',
      ...amountCommonProps,
    }),

    AmountColumn({
      title: '还款期数',
      dataIndex: 'repayTimesTotal',
      editable: true,
      initFormat: 1,
      wrapItemProps: {
        required: true,
      },
      requiredMark: true,
    }),
    AmountColumn({
      title: '手续费率(%)',
      dataIndex: 'commissionRate',
      editable: true,
      requiredMark: true,
      wrapItemProps: {
        required: true,
      },
    }),
    MatchOptionColumn({
      title: '支付方式',
      dataIndex: 'payType',
      matchOption: newPayType,
      editable: true,
      requiredMark: true,
    }),
    AmountColumn({ title: '名义价款(元)', dataIndex: 'nominalPrice', ...amountCommonProps }),
    MatchOptionColumn({
      title: '利息计算方式',
      dataIndex: 'interestCalculateWay',
      matchOption: 'repayCalcType',
      requiredMark: true,
      editable: true,
    }),

    {
      title: '合同利率(%)',
      dataIndex: 'contractInterestRate',
      required: true,
      requiredMark: true,
      span: 2,
      editable: (val) => {
        return (
          <Input.Group compact className="z-input-group">
            <Form.Item
              name="contractInterestRateType"
              style={{ width: '100px' }}
              rules={[rules.required('请选择')]}
            >
              <Select options={'rateType'} placeholder="请选择" />
            </Form.Item>
            <Form.Item
              name="contractInterestRate"
              style={{ width: 'calc(100% - 120px)' }}
              rules={[rules.required()]}
            >
              <FormAmount suffix="%" />
            </Form.Item>
          </Input.Group>
        )
      },
      render: (val, record) => {
        const { contractInterestRateType, contractInterestRate } = record

        return (
          <Space>
            <span>{App.matchOption('rateType', contractInterestRateType)?.label}</span>
            <FormAmount.Format value={contractInterestRate} suffix="%" />
          </Space>
        )
      },
    },
    DateColumn({ title: '投放日', dataIndex: 'payDate', editable: true, requiredMark: true }),
    AmountColumn({ title: 'IRR(%)', dataIndex: 'irr', editable: false }),
  ]
  useImperativeHandle(ref, () => ({
    desc: () => desc.current,
  }))

  useEffect(() => {
    desc.current.setBaseEdit(canEdit)
  }, [])
  return (
    <>
      <div className="z-flex-jsb" style={{ marginBottom: 8 }}>
        <h3>报价方案</h3>
        {canEdit && (
          <Button onClick={handleSave} type="primary">
            测算并保存
          </Button>
        )}
      </div>
      <EditDescription title="" detail={detail} columns={columnName} ref={desc} hiddenButton />
    </>
  )
})

// 现金流计划表
const CashFlowTableComponent = observer(({ table, quoteRef, updateIrr }) => {
  const columns = [
    DateColumn({
      title: '日期',
      dataIndex: 'cashFlowDate',
      width: 140,
    }),
    AmountColumn({
      title: '期项',
      dataIndex: 'cashFlowPhase',
      width: 80,
      initFormat: 1,
    }),
    AmountColumn({
      title: '现金流金额(元)',
      dataIndex: 'cashFlowAmount',
      width: 150,
    }),
    AmountColumn({
      title: '租金(元)',
      dataIndex: 'rent',
      width: 150,
      align: 'right',
    }),
    AmountColumn({
      title: '本金(元)',
      dataIndex: 'principal',
      width: 150,
      align: 'right',
    }),
    AmountColumn({
      title: '利息(元)',
      dataIndex: 'interest',
      width: 150,
      align: 'right',
    }),
    AmountColumn({
      title: '剩余本金(元)',
      dataIndex: 'remainingPrincipal',
      width: 150,
      align: 'right',
    }),
  ]

  const importCashFlow = async ({ file }) => {
    const { interestCalculateWay } = quoteRef.current.desc().form.getFieldsValue()
    if (interestCalculateWay !== 'BGZHK') {
      message.error('只有不规则分期才可导入现金流')
      return Promise.reject()
    }
    const res = await Api.importCashFlow({
      file,
    })
    table.setList(res.data)
    updateIrr()
  }
  return (
    <Table
      extra={[
        <DownloadTemplate
          params={{
            templateName: 'TEMPLATE_OSS_NAME_ACTUAL_PAYMENT_ITEM',
            moduleType: 'CONTRACT',
          }}
        />,
        <ImportAction upload={importCashFlow} beforeUpload={({ data }) => table.setList(data)} />,
      ]}
      actions={[<h3>现金流计划表</h3>]}
      columns={columns}
      store={table}
      scroll={{ x: 'max-content' }}
      pagination={false}
      className={styles.cashFlowTable}
    />
  )
})

// 收入情况表
const DynamicTableComponent = forwardRef(({ title, func, id, restTitle }, ref) => {
  const [headerList, setHeaderList] = useState([])
  const [rest, setRest] = useState({})
  const table = useMemo(() => {
    return new TableStore({
      request: async (params) => {
        if (!params.id && !id) return []
        const res = await func({
          id,
          ...params,
        })
        const { dataList, headerList, ...rest } = res
        setRest(rest)
        const newHeaderList = headerList.map((item, index) => ({
          title: item || '类别',
          dataIndex: `col${index + 1}`,
          width: 120,
        }))
        setHeaderList(newHeaderList)
        const newTableList = dataList.map((items) => {
          const newItem = {}
          items.forEach((item, index) => {
            newItem[`col${index + 1}`] = item
          })
          return newItem
        })
        return newTableList
      },
    })
  }, [])

  useImperativeHandle(ref, () => ({
    table,
  }))
  return (
    <>
      <div className="z-flex-jsb">
        <h3>{title} （元） </h3>
        <Space>
          {restTitle?.map((item) => (
            <span key={item}>
              {item.label}:
              <FormAmount.Format
                value={rest?.[item.dataIndex]}
                suffix={item.suffix}
                initFormat={item.initFormat}
              />
            </span>
          ))}
        </Space>
      </div>
      <Table
        columns={headerList}
        store={table}
        scroll={{ x: 'max-content' }}
        pagination={false}
        className={styles.incomeTable}
      />
    </>
  )
})

const DetailModal = observer(({ modal, tableRefetch, canEdit }) => {
  const detail = modal?.getInitialValues() ?? {}

  const baseRef = useRef(null)
  const quoteRef = useRef(null)
  const incomeTableRef = useRef(null)
  const costTableRef = useRef(null)
  const periodTableRef = useRef(null)
  const anchorList = [
    { label: '基本信息' },
    { label: '报价方案' },
    { label: '现金流计划表' },
    { label: '收入情况表' },
    { label: '资金成本情况表' },
    { label: '期间费用' },
  ].filter(Boolean)

  const [budgetPlanPayDetailId, setBudgetPlanPayDetailId] = useState(detail.id)
  const updateIrr = async () => {
    const priceDetail = await deliveryPlanDetailApi.postDetailPrice({
      id: detail.id ?? budgetPlanPayDetailId,
    })
    const { irr } = priceDetail
    if (irr) {
      detail.irr = irr
    }
  }
  const handleSave = async () => {
    const baseDesc = await baseRef.current.desc?.validateFields()
    const baseForm = baseRef.current.desc.form.getFieldsValue()
    const quoteDesc = await quoteRef.current.desc()?.validateFields()
    const cashFlowList = cashFlowTable.getList()

    const { areaName, ...restBaseForm } = baseForm
    const [province, city, district] = areaName ?? []
    const mappedBaseForm = {
      ...restBaseForm,
      ...(province && { province }),
      ...(city && { city }),
      ...(district && { district }),
    }

    const res = await deliveryPlanDetailApi.postDetailCalculate({
      cashFlowList,
      budgetPlanPayDetailId: detail.id ?? budgetPlanPayDetailId,
      ...quoteDesc,
      ...mappedBaseForm,
    })

    setBudgetPlanPayDetailId(res)
    setParams({ id: res })
    tableRefetch()
    updateIrr()
    setTimeout(() => {
      refetch()
    }, 50)
    message.success('测算成功')
  }
  const cashFlowTable = useMemo(() => {
    return new TableStore({
      request: async (params) => {
        console.log('params: ', params, detail.id)
        if (!params.id && !detail.id) return []
        const res = await deliveryPlanDetailApi.postDetailCashFlow({
          id: detail.id,
          ...params,
        })
        return res
      },
    })
  }, [detail.id])

  const setParams = (params) => {
    cashFlowTable.setParams(params)
    incomeTableRef.current?.table?.setParams(params)
    costTableRef.current?.table?.setParams(params)
    periodTableRef.current?.table?.setParams(params)
  }
  const refetch = () => {
    cashFlowTable.search()
    incomeTableRef.current?.table?.search()
    costTableRef.current?.table?.search()
    periodTableRef.current?.table?.search()
  }

  const handleCancel = () => {
    setBudgetPlanPayDetailId(undefined)
    cashFlowTable.setParams({ id: undefined })
    modal.close()
  }
  return (
    <Modal
      title="详情"
      store={modal}
      width={1000}
      footer={null}
      destroyOnClose
      onCancel={handleCancel}
      bodyStyle={{ maxHeight: '90vh' }}
    >
      <DetailLayout
        anchorList={anchorList}
        getContainer={() => document.getElementsByClassName('ant-modal-body')[0]}
      >
        <BaseInfoComponent detail={detail} canEdit={canEdit} ref={baseRef} />
        <QuoteSchemeComponent
          detail={detail}
          canEdit={canEdit}
          ref={quoteRef}
          handleSave={handleSave}
        />
        <CashFlowTableComponent table={cashFlowTable} quoteRef={quoteRef} updateIrr={updateIrr} />
        <DynamicTableComponent
          title="收入情况表"
          func={deliveryPlanDetailApi.postDetailIncomeSharing}
          id={detail.id}
          ref={incomeTableRef}
        />
        <DynamicTableComponent
          title="资金成本情况表"
          func={deliveryPlanDetailApi.postDetailFundCost}
          ref={costTableRef}
          restTitle={[{ label: 'FTP', dataIndex: 'ftp', suffix: '%', initFormat: 10000 }]}
          id={detail.id}
        />
        <DynamicTableComponent
          title="期间费用"
          func={deliveryPlanDetailApi.postDetailExpense}
          ref={periodTableRef}
          id={detail.id}
        />
      </DetailLayout>
    </Modal>
  )
})

export default DetailModal
