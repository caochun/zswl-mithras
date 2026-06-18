
import { observer } from '@zswl/admin'
import { Button, Table, TableStore } from '@zswl/components'
import { Dropdown, Menu, message } from 'antd'
import { forwardRef, useMemo } from 'react'
import DataUpload from '@/components/DataUpload'
import styles from './index.less'
// import Api from '@/api/project/projMeetingApi'
import Api from '@/api/project/projReviewMeetingMinute'
import _ from 'lodash'


const Index = ({ form,  detail, taskActivityId,showValue,store ,projReviewType,params }, ref) => {
  const table = useMemo(() => {
    return new TableStore({
      request: async (searchParams) => {
        let functionCode = projReviewType === 'PROJ_REVIEW_BASE'? 'projReviewQuotationProposalCashflowplanList': 'groupCreditReviewQuotationProposalCashflowplanList'
        const res = await Api.postCashflowplanList({
          ...searchParams,
          ...params,
        },functionCode)
        console.log("res",res)
        // if (!res||res.length === 0) {
        //   form.setFieldsValue({ VotingResultsOfReviewMeetingLock: true })
        // }else {
        //   form.setFieldsValue({ VotingResultsOfReviewMeetingLock: false })
        // }
        return res
      },
    })
  }, [params])
  console.log('table',table)
  const importFiles = async (file) => {
    const { fileList } = DataUpload.classify(file)
    let functionCode = projReviewType === 'PROJ_REVIEW_BASE'? 'projReviewQuotationProposalCashflowplanUpload': 'groupCreditReviewQuotationProposalCashflowplanUpload'
    const res = await Api.postCashflowplanUpload({
      ...params,
      file: fileList[0],
    },functionCode)
    message.success('导入成功')
    // form.setFieldsValue({ VotingResultsOfReviewMeetingLock: false })
    table.search()
  }
  const columns = [
    InputColumn({ title: '日期', dataIndex: 'date' }),
    InputColumn({ title: '期项', dataIndex: 'phase' }),
    AmountColumn({
      title: <div style={{ textAlign: 'right' }}>{'租金(元)'}</div>,
      dataIndex: 'rent',
    }),
    AmountColumn({
      title: <div style={{ textAlign: 'right' }}>本金(元)</div>,
      dataIndex: 'principal',
    }),
    AmountColumn({
      title: <div style={{ textAlign: 'right' }}>利息(元)</div>,
      dataIndex: 'interest',
    }),
    AmountColumn({
      title: <div style={{ textAlign: 'right' }}>剩余本金(元)</div>,
      dataIndex: 'remainingPrincipal',
    }),
  ]

  const exportRent = async (filename) => {
    let functionCode = projReviewType === 'PROJ_REVIEW_BASE'? 'projReviewQuotationProposalCashflowplanRentExport': 'groupCreditReviewQuotationProposalCashflowplanRentExport'
    await Api.postRentExport({ ...params, filename },functionCode)
  }
  const exportCashFlow = async (filename) => {
    let functionCode = projReviewType === 'PROJ_REVIEW_BASE'? 'projReviewQuotationProposalCashflowplanCashflowExport': 'groupCreditReviewQuotationProposalCashflowplanCashflowExport'
    await Api.postCashflowExport({ ...params, filename },functionCode)
  }
  const menu = (
    <Menu
      items={[
        {
          key: '1',
          label: (
            <div
              onClick={() => {
                exportRent('租金表.xlsx')
              }}
            >
              导出租金表
            </div>
          ),
        },
        {
          key: '2',
          label: (
            <div
              onClick={() => {
                exportCashFlow('现金流量表.xlsx')
              }}
            >
              导出现金流量表
            </div>
          ),
        },
      ]}
    />
  )

  return (
    <div>
      <div className={styles.titleWrap}>
        <div className={styles.btnWrap}>
          <DownloadTemplate
            params={{
              templateName: 'TEMPLATE_OSS_NAME_ESTIMATE_PAYMENT_ITEM',
              moduleType: 'CONTRACT',
            }}
          />
          {/* taskActivityId=== 'userTask_jurySecretaryCollect'|| */}
          {
            taskActivityId&&
           (
            taskActivityId=== 'userTask_startUser' ||
            taskActivityId=== 'userTask_jurySecretaryCollect'
            )&&
            <DataUpload accept=".xlsx" maxCount={1} onChange={importFiles}>
            <Button type="primary" style={{ marginRight: 8 }}>
              数据导入
            </Button>
          </DataUpload>
          }

          <Dropdown overlay={menu} placement="bottomLeft">
            <Button>导出</Button>
          </Dropdown>
        </div>
      </div>
      <Table store={table} columns={columns} />
      {/* {
        form.getFieldsValue('VotingResultsOfReviewMeetingLock') &&
        !showValue &&
        <div style={{color:'red'}}>请导入租金概算表</div>
      } */}
      
    </div>
  )
}

export default observer(forwardRef(Index))
