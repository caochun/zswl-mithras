import { App, Page, SearchBar, Table } from '@zswl/components'
import { observer } from '@zswl/admin'
import store from './store'
import { useMemo } from 'react'
import { Tooltip, Radio, Space } from 'antd'
import styles from './index.less'
import IconFont from '@/components/Icon'
import { getKeyOptionsLabelMapPlus } from '@/utils'
import classNames from 'classnames'
import { FormAmount } from '@/components/Form'
import { ClientSelect, OrgSelect, FounderSelect } from '@/components'
import { processTypeList } from './Context'
import { saveServer } from '@/utils'

const { Item } = SearchBar

function ProjectLifeCycle() {
  const { page, setSelectedType, selectedType } = store

  const isEstablishStage = selectedType === 'PROJESTABLISH_STAGE'
  const isReviewStage = selectedType === 'PROJREVIEW_STAGE'

  const isEstablishOrReviewStage = isEstablishStage || isReviewStage

  const data = page.getData()

  const columns = useMemo(() => {
    return [
      {
        title: '客户名称',
        width: 320,
        dataIndex: 'clientName',
        fixed: 'left',
        actions({
          clientName: name,
          clientId,
          clientType,
          domesticOrAbroad,
          paymentProcessStatus,
        }) {
          return [
            {
              name,
              disabled: paymentProcessStatus === 'CLOSED',
              className: 'z-single-line',
              to: `/customer/maintain/detail/${clientId}?clientType=${clientType}&domesticOrAbroad=${domesticOrAbroad}&flag=info&typeId=create`,
            },
          ]
        },
      },
      {
        title: '项目名称',
        dataIndex: 'projectName',
        width: 320,
        fixed: 'left',
        actions({
          projectName,
          projectId,
          paymentProcessStatus,
          dataType,
          establishId,
          clientId,
          reviewId,
        }) {
          return [
            {
              name: projectName,
              disabled: paymentProcessStatus === 'CLOSED',
              to: `/lifeCycle/projectLifeCycle/detail/${clientId}?establishId=${establishId}&reviewId=${reviewId}`,
              // style: { width: 160 },
              className: 'z-single-line',
            },
          ]
        },
      },
      {
        title: '项目阶段',
        dataIndex: 'projLifecycleStatus',
        width: 180,
        render: (item) => {
          return getKeyOptionsLabelMapPlus('projLifecycleStatus')[item]
        },
      },
      {
        title: '授信金额（元）',
        dataIndex: isEstablishStage ? 'establishApplyCreditAmount' : 'applyCreditAmount',
        align: 'right',
        render: (val, { establishApplyCreditAmount, applyCreditAmount }) => {
          let newVal = isEstablishStage ? establishApplyCreditAmount : applyCreditAmount
          if (selectedType === '') {
            // 总项目，授信金额：优先取项目评审中的授信金额，无评审数据则取立项中的授信金额
            newVal = applyCreditAmount ?? establishApplyCreditAmount
          }
          return <FormAmount.Format value={newVal} />
        },
        width: 180,
      },
      isEstablishStage && {
        title: '立项状态',
        dataIndex: 'establishStatus',
        render: (item) => {
          return getKeyOptionsLabelMapPlus('projEstablishStatus')[item]
        },
      },
      isReviewStage && {
        title: '项目状态',
        dataIndex: 'reviewStatus',
        render: (item) => {
          return getKeyOptionsLabelMapPlus('projReviewStatus')[item]
        },
      },
      isEstablishOrReviewStage && {
        title: '审批状态',
        dataIndex: isEstablishStage ? 'establishProcessStatus' : 'reviewProcessStatus',
        render: (item, { processType, currentNode, applyTime }) => {
          const processStatusLable = getKeyOptionsLabelMapPlus('projProcessStatus')[item] || ''
          if (processStatusLable.indexOf('审批中') > -1) {
            return (
              <div>
                <Tooltip
                  placement="topLeft"
                  title={
                    <div>
                      <div>流程类型：{processType}</div>
                      <div>当前审批人：{currentNode}</div>
                      <div>申请时间：{applyTime}</div>
                    </div>
                  }
                >
                  <Space>
                    {processStatusLable}
                    <IconFont type="icon-icon_info" />
                  </Space>
                </Tooltip>
              </div>
            )
          }
          return processStatusLable
        },
      },
      ['CONTRACT_STAGE', 'CONTRACTSETTLE_STAGE'].includes(selectedType) && {
        title: '合同金额（元）',
        children: [
          {
            title: '已申请',
            align: 'right',
            dataIndex: 'contractAmountApplied',
            render: (val) => <FormAmount.Format value={val} />,
          },
          {
            title: '已生效',
            align: 'right',
            dataIndex: 'contractAmountEffected',
            render: (val) => <FormAmount.Format value={val} />,
          },
        ],
      },
      ['CONTRACT_STAGE', 'CONTRACTSETTLE_STAGE'].includes(selectedType) && {
        title: '付款金额（元）',
        children: [
          {
            title: '已申请',
            align: 'right',
            dataIndex: 'paymentAmountApplied',
            render: (val) => <FormAmount.Format value={val} />,
          },
          {
            title: '已生效',
            dataIndex: 'paymentAmountEffected',
            align: 'right',
            render: (val) => <FormAmount.Format value={val} />,
          },
          {
            title: '已投放',
            align: 'right',
            dataIndex: 'paymentAmountWrittenOff',
            render: (val) => <FormAmount.Format value={val} />,
          },
        ],
      },
      !isEstablishOrReviewStage && {
        title: '剩余本金（元）',
        dataIndex: 'remainingPrincipal',
        align: 'right',
        render: (val) => <FormAmount.Format value={val} />,
        width: 180,
      },
      !isEstablishOrReviewStage && {
        title: '在途流程',
        dataIndex: 'processType',
      },
      !isEstablishOrReviewStage && {
        title: '当前审批人',
        dataIndex: 'currentNode',
      },
      !isEstablishOrReviewStage && {
        title: '申请时间',
        width: 180,
        tooltip: true,
        dataIndex: 'applyTime',
      },
      {
        title: '业务类型',
        dataIndex: 'bizType',
        width: 140,
        render: (item) => {
          return getKeyOptionsLabelMapPlus('projEstablishBizType')[item]
        },
      },
      {
        title: '项目主办',
        width: 150,
        tooltip: true,
        dataIndex: 'projSponsorUserName',
      },
      {
        title: '业务部门',
        width: 180,
        tooltip: true,
        dataIndex: 'bizDeptName',
      },
    ].filter(Boolean)
  }, [isEstablishOrReviewStage, selectedType, isReviewStage, isEstablishStage])

  const currentProcessTypeList = useMemo(() => {
    if (JSON.stringify(data) === '{}') {
      return processTypeList
    }
    return processTypeList.map((item, index) => {
      return {
        ...item,
        num: data.allTableListData?.[index]?.total ?? 0,
        add: data.allTableCount?.[item.key]?.currentMonthAdded ?? 0,
      }
    })
  }, [data])

  return (
    <Page store={store.page} className={styles.page}>
      <div className={styles.wrap}>
        <div className={styles.processTypeList}>
          {currentProcessTypeList.map((item, index) => {
            const { num, name, add, params } = item
            return (
              <div
                key={index}
                className={classNames(styles.item, {
                  [styles.selected]: params === selectedType,
                })}
                onClick={() => {
                  setSelectedType(params)
                }}
              >
                <div className={styles.itemTitle}>{name}</div>
                <div className={styles.count}>{num}</div>
                <div className={styles.desc}>
                  <div className={styles.text}>本月新增</div>
                  <div className={styles.num}>{add}</div>
                  <IconFont type="icon-arrow_up" />
                </div>
              </div>
            )
          })}
        </div>
        <div className={styles.tableWrap}>
          <Table
            columnsFilter={'lifeCycle_projectLifeCycle_1'}
            onFilter={(key, val) => saveServer('lifeCycle_projectLifeCycle_1', val)}
            resizable
            store={store.table}
            rowKey="key"
            columnWidth={150}
            searchbar={{
              labelCol: { span: 6 },
              items: [
                {
                  label: '项目名称',
                  name: 'projName',
                },
                <Item label="客户名称" name="clientId" key="clientId">
                  <ClientSelect
                    canJump={false}
                    functionCode="clientlist-projectLifeCycle"
                  ></ClientSelect>
                </Item>,
                {
                  label: '项目阶段',
                  name: 'projLifecycleStatus',
                  options: 'projLifecycleStatus',
                },
                {
                  label: '业务类型',
                  name: 'bizType',
                  options: 'projEstablishBizType',
                  allowClear: true,
                },

                <Item label="业务部门" name="bizDeptId" key="bizDeptId">
                  <OrgSelect functionCode="selectorgs-projectLifeCycle"></OrgSelect>
                </Item>,
                <Item label="项目主办" name="projSponsorUserId" key="projSponsorUserId">
                  <FounderSelect functionCode="selectfounder-projectLifeCycle"></FounderSelect>
                </Item>,
                {
                  label: '立项时间',
                  name: 'projestablish',
                  type: 'rangePicker',
                },
                isEstablishStage && {
                  label: '立项状态',
                  name: 'establishStatus',
                  options: 'projEstablishStatus',
                },
                isEstablishStage && {
                  label: '审批状态',
                  name: 'establishProcessStatus',
                  options: 'projProcessStatus',
                },
                isReviewStage && {
                  label: '项目状态',
                  name: 'reviewStatus',
                  options: 'projReviewStatus',
                },
                isReviewStage && {
                  label: '审批状态',
                  name: 'reviewProcessStatus',
                  options: 'projProcessStatus',
                },
                // !isEstablishOrReviewStage && (
                //   <Item name="fastFilter" label="" style={{ width: 400 }}>
                //     <Radio.Group>
                //       <Radio value={1}>显示全部 </Radio>
                //       <Radio value={2}>只看合同已生效</Radio>
                //       <Radio value={3}>只看已投放</Radio>
                //     </Radio.Group>
                //   </Item>
                // ),
              ].filter(Boolean),
            }}
            scroll={{
              x: 1200,
            }}
            columns={columns}
          />
        </div>
      </div>
    </Page>
  )
}

export default observer(ProjectLifeCycle)
