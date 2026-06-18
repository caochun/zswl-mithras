// 我的发起-待发起
import { EditDescription } from '@/components/Table'
import { observer } from '@zswl/admin'
import { useEffect, useState } from 'react'
import { ApiSelect, FounderSelect } from '@/components/Select'
import { FiledFormat, AmountFormat, MatchFormat, MatchOptionColumn } from '@/components/Format'
import { Select, App, Page } from '@zswl/components'
import { DatePicker, Input, Space, Tag } from 'antd'
import moment from 'moment'
import { timeFormat } from '@/utils'
import Api from '@/api/afterLease/checkPlan'

const Index = ({ params: { id, commonId, businessData }, refresh }) => {
  const [showRiskManage, setShowRiskManage] = useState(true)
  const { optionsType } = App.getData()
  const page = Page.useStore(
    {
      request: async () => {
        let result = await Api.postCheckPlanCommonlyDetail({
          clientId: id,
          todoId: commonId,
          checkPlanId:businessData,
        })
        if (!result) {
          // 没有就表示新建
          result = await Api.postCheckPlanClient({ clientId: id, todoId: commonId })
        }
        const newResult = {
          ...result,
          // 两个接口 存量风险敞口 字段不一样
          stockRiskExposure: result.riskExposure ?? result.stockRiskExposure,
          belongSponsorId: result.belongSponsorId
            ? {
                label: result.belongSponsorName,
                value: result.belongSponsorId,
              }
            : undefined,
          riskManagerId: result.riskManagerId
            ? {
                label: result.riskManagerName,
                value: result.riskManagerId,
              }
            : undefined,
          deadLine: result.deadLine ? moment(result.deadLine) : undefined,
        }
        setShowRiskManage(newResult.checkWay === 'SITE')
        return newResult
      },
    },
    [id, businessData]
  )

  const detail = page.getData()

  const saveData = async (values) => {
    const { belongDeptId, term, planId, planClientId, stockRiskExposure } = detail
    const { endDate, belongSponsorId, deadLine, riskManagerId, ...rest } = values
    const res = await Api.addCheckPlan({
      ...rest,
      commonId,
      belongDeptId,
      term,
      planId,
      planClientId,
      stockRiskExposure,
      clientId: id,
      planType: 'COMMONLY',
      belongSponsorId: belongSponsorId?.value,
      riskManagerId: riskManagerId?.value,
      deadLine: deadLine ? timeFormat(deadLine) : undefined,
    })
    if(!businessData){
      refresh()
    }else{
      page.init()
    }
    // const res = await store.getPreDetail(commonId)
    // page.init({checkPlanId:res.data})
  }

  return (
    <Page store={page} params={{ id }}>
      <EditDescription
        title="基本信息"
        saveData={saveData}
        detail={detail}
        canEdit={true}
        columns={[
          {
            title: '计划类型',
            dataIndex: 'planType',
            editable: false,
            render: (val) => (
              <MatchFormat value={'COMMONLY'} matchOption="afterLeaseCheckPlanTypeEnum" />
            ),
          },
          {
            title: '客户名称',
            dataIndex: 'clientName',
            editable: false,
            render: (val) => <FiledFormat title={val} />,
          },
          {
            title: '计划名称',
            dataIndex: 'planName',
            requiredMark: true,
            editable: {
              element: <Input />,
              rules: [{ required: true, message: '请输入' }],
            },
          },
          {
            title: '客户主办',
            dataIndex: 'belongSponsorId',
            requiredMark: true,
            editable: {
              element: (
                <FounderSelect
                  labelInValue
                  placeholder="请选择"
                  queryParams={{ job: 'projmanager' }}
                  functionCode="assetStrategySelectFounder"
                />
              ),
              rules: [{ required: true, message: '请选择' }],
            },
            render: (val, { belongSponsorName }) => {
              return <FiledFormat title={belongSponsorName} />
            },
          },
          {
            title: '检查形式',
            dataIndex: 'checkWay',
            matchOption: 'afterLeaseCheckWayEnum',
            requiredMark: true,
            editable: {
              element: (
                <Select
                  options={optionsType.afterLeaseCheckWayEnum.filter(
                    (item) => item.value !== 'WITHOUT_CHECK'
                  )}
                  onChange={(value) => {
                    setShowRiskManage(value === 'SITE')
                  }}
                />
              ),
              rules: [{ required: true, message: '请选择' }],
            },
          },
          showRiskManage && {
            title: '协查风控经理',
            dataIndex: 'riskManagerId',
            requiredMark: true,
            editable: {
              element: <ApiSelect api={Api.postRiskManagerList} labelInValue />,
              rules: [{ required: true, message: '请选择' }],
            },
            render: (val, { riskManagerName }) => <FiledFormat title={riskManagerName} />,
          },
          {
            title: '租后检查截止日',
            dataIndex: 'deadLine',
            requiredMark: true,
            editable: {
              element: <DatePicker style={{ width: '100%' }}></DatePicker>,
              rules: [{ required: true, message: '请选择' }],
            },
            render: (val, { deadlineLabel }) => {
              const colorMap = {
                SYSTEM_CALCULATE: '#c7e984',
                ASSERT_MANAGER_CONFIRM: '#cf7a2b',
                FIRST_CHECK: '#339fbe',
              }
              if (!val) return
              return (
                <Space>
                  <div style={{ width: '90px' }}>{val.format('yyyy-MM-DD')}</div>
                  {deadlineLabel.map((v) => (
                    <Tag color={colorMap[v]}>
                      {App.matchOption('afterLeaseDeadlineLabelEnum', v).label}
                    </Tag>
                  ))}
                </Space>
              )
            },
          },
          MatchOptionColumn({
            title: '检查报告模板',
            dataIndex: 'reportType',
            matchOption: 'afterLeaseCheckReportTypeEnum',
            requiredMark: true,
            rules: [{ required: true, message: '请选择' }],
          }),
          {
            title: '存量风险敞口(元)',
            dataIndex: 'stockRiskExposure',
            editable: false,
            render: (val) => <AmountFormat value={val} />,
          },
        ].filter(Boolean)}
      />
    </Page>
  )
}

export default observer(Index)
