import { Page, Table, Modal, Form, Button, Select } from '@zswl/components'
import { observer, history } from '@zswl/admin'
import AmountRange from '@/components/AmountRange'
import { FormAmount } from '@/components/Form'
import { formatPercent, amountFormat, rangePresets, hasValue } from '@/utils'
import {
  FounderColumn,
  AmountColumn,
  MatchOptionColumn,
  DateColumn,
  MatchFormat,
} from '@/components/Format'
import { dateRangeTransform } from '@/utils/transform'
import { message } from 'antd'
import IconFont from '@/components/Icon'
import { useState, useMemo, useCallback } from 'react'
import { Summary } from '@/components/Table'
import { ExportAction } from '@/components/Actions'
import PageListDown from '@/components/PageListDown'
import { saveServer } from '@/utils'
import directFinancingApi from '@/api/financial/directFinancingDetail'
function Index({ path }) {
  const [sumData, setSumData] = useState({})
  const [isClear, setClear] = useState(false)
  const getListSum = async (params) => {
    const data = await directFinancingApi.getBaseInfoSum({ ...params })
    setSumData(data)
  }
  const tableStore = Table.useStore({
    request: async (params) => {
      const { createTime, updateTime, ...rest } = params
      const p1 = dateRangeTransform(createTime, 'createTimeFrom', 'createTimeTo')
      const p2 = dateRangeTransform(updateTime, 'updateTimeFrom', 'updateTimeTo')
      let finalParams = { financingStatusList:['NEW','EFFECT','CARRY_INTEREST'], ...rest, ...p1, ...p2 }
      if(isClear){
        finalParams = {...rest, ...p1, ...p2 }
      }
      await getListSum(finalParams)
      const data = await directFinancingApi.getBaseInfoList(finalParams)
      if(data.list === 0){
        message.warn('当前暂无新建/生效/起息状态的融资产品.可通过筛选条件查询历史数据。')
      }
      return data
    },
  }, [isClear])
  const modalStore = Modal.useStore({
    onFinish: async (values) => {
      const id = await directFinancingApi.addBaseInfo(values)
      message.success('新增成功')
      modalStore.close()
      tableStore.search()
      history.push(`/financial/direct/detail/${id}`)
    },
  })

  // 作废
  const invalid = async (id) => {
    await directFinancingApi.obsoleteBaseInfo({ id })
    message.success('作废成功')
    tableStore.search()
  }

  const deleteRow = async (id) => {
    await directFinancingApi.deleteBaseInfo({ id })
    message.success('删除成功')
    tableStore.search()
  }

  const columns = [
    {
      title: '融资编号',
      fixed: 'left',
      search: true,
      dataIndex: 'financingCode',
      actions({ id, financingCode }) {
        return [{ name: financingCode, to: `${path}/detail/${id}` }]
      },
    },
    { title: '产品名称', dataIndex: 'productName', fixed: 'left', search: true },
    AmountColumn({
      title: '融资金额（元）',
      dataIndex: 'financingAmount',
      align: 'right',
      search: {
        element: <AmountRange />,
        itemProps: {
          transform: (val) => {
            const [start, end] = val || []
            return {
              financingAmount: undefined,
              financingAmountFrom: start && start * 10000,
              financingAmountTo: end && end * 10000,
            }
          },
        },
      },
    }),
    AmountColumn({ title: '剩余本金（元）', dataIndex: 'remainingAmount' }),
    AmountColumn({ title: '综合融资成本（%）', dataIndex: 'comprehensiveFinancingCost' }),
    {
      title: '票面加权利率(%)',
      align: 'right',
      dataIndex: 'averageCouponRate',
      render: (val) => amountFormat(formatPercent(val)),
    },
    {
      title: '项目类别',
      dataIndex: 'directFinancingType',
      matchOption: 'directFinancingType',
      search: true,
    },
    MatchOptionColumn({
      title: '融资状态',
      dataIndex: 'financingStatusList',
      matchOption: 'fundFinancingStatusEnum',
      search: {
        element: (
          <Select
            options={'fundFinancingStatusEnum'}
            allowClear
            getPopupContainer={() => document.body}
            mode={'multiple'}
            defaultValue={['NEW','EFFECT','CARRY_INTEREST']}
            onClear={()=>setClear(true)}
            onChange={vals => vals.length === 0 && setClear(true)}
          />
        ),
      },
      render: (val, { financingStatus }) => (
        <MatchFormat value={financingStatus} matchOption={'fundFinancingStatusEnum'} />
      ),
    }),
    DateColumn({ dataIndex: 'carryInterestTime', title: '起息日', search: true }),
    DateColumn({ dataIndex: 'durationTime', title: '到期日', search: true }),
    { title: '创建人', dataIndex: 'createByName' },
    FounderColumn({
      title: '资金经理',
      dataIndex: 'fundManagerId',
      search: true,
      params: { job: 'moneymanager' },
      editable: false,
      hidden: true,
      // functionCode: 'directFounderList',
    }),

    {
      title: '创建日期',
      dataIndex: 'createTime',

      width: 200,
    },
    {
      title: '变更日期',
      dataIndex: 'updateTime',

      width: 200,
    },

    {
      title: '操作',
      dataIndex: 'num',
      width: 160,
      fixed: 'right',
      actions: ({ id, financingStatus }) => {
        const canDelete = ['CLOSE', 'NEW'].includes(financingStatus)
        const canObsolete = ['NEW'].includes(financingStatus)
        return [
          {
            name: '作废',
            confirm: canObsolete,
            onClick: () => invalid(id),
            disabled: !canObsolete,
          },
          {
            name: '删除',
            confirm: canDelete,
            onClick: () => deleteRow(id),
            disabled: !canDelete,
          },
        ].filter(Boolean)
      },
    },
  ]

  return (
    <Page>
      <Table
              columnsFilter={'financial_direct_1'}
              onFilter={(key,val) => saveServer('financial_direct_1',val)}
        resizable
        selectable={{ type: 'radio' }}
        scroll={{
          x: 2200,
        }}
        store={tableStore}
        actions={[
          <PageListDown table={tableStore} module="financialDirect" />,
          {
            name: (
              <span>
                <IconFont type="icon-icon_add" />
                新增产品
              </span>
            ),
            type: 'primary',
            onClick: modalStore.open,
          },
        ]}
        columns={columns}
        searchbar={{
          items: [],
        }}
        summary={() => {
          return (
            <Summary
              columns={tableStore.getOptimizedColumns()}
              sumData={sumData}
              startIndex={0}
            ></Summary>
          )
        }}
      />
      <Modal title="新增产品" store={modalStore} destroyOnClose>
        <Form
          labelCol={{ span: 6 }}
          items={[
            { title: '产品名称', dataIndex: 'productName', rules: [{ required: true }] },
            {
              title: '项目类别',
              dataIndex: 'directFinancingType',
              rules: [{ required: true }],
              element: {
                type: 'select',
                options: 'directFinancingType',
              },
            },
            <FormAmount.Item
              key="amount"
              rules={[
                {
                  required: true,
                  message: '',
                },
              ]}
              label="融资金额(万元)"
              name="financingAmount"
              style={{ width: '100%' }}
            />,
          ]}
        />
      </Modal>
    </Page>
  )
}

export default observer(Index)
