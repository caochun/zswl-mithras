import { useEffect, useMemo } from 'react'
import { Table, App } from '@zswl/components'
import { observer } from '@zswl/admin'
import RenderColumn from '@/components/RenderColumn'
import CreateModal from './CreateModal'
import Store from './store'
import styles from './index.less'
import { MatchOptionColumn } from '@/components/Format'
import { saveServer } from '@/utils'
import JumpClient from '../../JumpClient'

const Index = ({ canEditFlag, baseStore }) => {
  const { bizType } = baseStore
  const { isFormApproval, contractId, businessVersion } = baseStore.page.getParams()
  const { detail, newDetail } = baseStore.page.getData()
  const tData = isFormApproval ? newDetail : detail
  const isChangYe = !['PUBLIC_UTILITIES', 'CIVIL_CONSUMPTION', 'TRAVEL'].includes(
    tData?.riskControlIndustryClassify
  )
  const store = useMemo(() => {
    return new Store({ baseStore, isFormApproval, contractId, businessVersion })
  }, [baseStore, isFormApproval, contractId, businessVersion])
  baseStore.chengZhuRenStore = store
  const BL_ZR = bizType === 'BL' || bizType === 'ZR'

  const columns = [
    {
      title: BL_ZR ? '类型' : '承租人类型',
      dataIndex: 'lesseeType',
      width: 120,
      render(val) {
        return (
          <RenderColumn
            data={val}
            isCompare={isFormApproval}
            selectEnum={BL_ZR ? 'creditorDebtorTypeEnum' : 'lesseeypeEnum'}
          ></RenderColumn>
        )
      },
    },

    {
      title: BL_ZR ? '名称' : '承租人名称',
      dataIndex: 'lesseeName',
      render: (v, t) => {
        if (isFormApproval) {
          return (
            <JumpClient
              isChange={t.lesseeName.isChange}
              value={[
                {
                  clientId: t.lesseeClient?.value?.clientId,
                  clientType: t.lesseeClient?.value?.clientType,
                  clientName: t.lesseeClient?.value?.clientName,
                },
              ]}
            ></JumpClient>
          )
        }
        return (
          <JumpClient
            value={[
              {
                clientId: t.lesseeClient?.clientId,
                clientType: t.lesseeClient?.clientType,
                clientName: t.lesseeClient?.clientName,
              },
            ]}
          ></JumpClient>
        )
      },
    },
    bizType === 'ZL' &&
      MatchOptionColumn({
        title: '决议类型',
        dataIndex: 'resolutionType',
        matchOption: 'resolutionTypeEnum',
      }),
    bizType === 'ZL' &&
      isChangYe &&
      MatchOptionColumn({
        title: '租赁物文件类型',
        dataIndex: 'leaseItemFileType',
        matchOption: 'contractLeaseItemFileTypeEnum',
      }),
    {
      title: '存量风险敞口(元)',
      dataIndex: 'stockRiskExposure',
      align: 'right',
      width: 200,
      render: (val) => {
        return <RenderColumn data={val} isCompare={isFormApproval} formatNum></RenderColumn>
      },
    },
    {
      title: '指定联系人',
      dataIndex: 'contactName',
      render(val) {
        return <RenderColumn data={val} isCompare={isFormApproval}></RenderColumn>
      },
    },
    {
      title: '租金往来方',
      dataIndex: 'rentConcatAccountName',
      render(val) {
        return <RenderColumn data={val} isCompare={isFormApproval}></RenderColumn>
      },
    },
    {
      title: '是否上报征信',
      dataIndex: 'isReport',
      width: 120,
      render(val) {
        return (
          <RenderColumn data={val} isCompare={isFormApproval} selectEnum="isConfirm"></RenderColumn>
        )
      },
    },
    {
      title: '操作',
      dataIndex: 'id',
      fixed: 'right',
      width: 100,
      actions(record) {
        return [
          {
            name: '编辑',
            onClick: () => store.$createModal.open(record),
            disabled: !canEditFlag,
          },
        ]
      },
    },
  ]

  useEffect(() => {
    if (contractId) {
      store.$table.search({ contractId })
    }
  }, [contractId])

  return (
    <div className={styles.page}>
      <div className={styles.header}>
        <div className={styles.title}>{BL_ZR ? '债权人/债务人' : '承租人'}</div>
      </div>
      <Table
              columnsFilter={'detail_ChengZuRen_1'}
              onFilter={(key,val) => saveServer('detail_ChengZuRen_1',val)}
        scroll={{ x: 1500 }}
        store={store.$table}
        columns={columns}
        autoRequest={false}
        columnWidth={120}
        resizable
      ></Table>
      <CreateModal bizType={bizType} store={store} isChangYe={isChangYe}></CreateModal>
    </div>
  )
}

export default observer(Index)
