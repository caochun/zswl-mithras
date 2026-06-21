import { useRef, useState } from 'react'
import { observer } from '@zswl/admin'
import ActualTable from '../../ActualTable'
import { bizTypeMapText } from '../../bizTypeConfig'
import { DatePicker, Form } from 'antd'
import moment from 'moment'
import ContractIRR from '../../ContractIRR'
import styles from './index.less'

function ContractApplicationActualRent({ canEditFlag = true, baseStore, isFormChangeType: _isFormChangeType }) {
  const { bizType, contractStatus, leaseType } = baseStore
  const { contractId, businessVersion } = baseStore.page.getParams()

  const [hideImportFromStatus, setHideImportFromStatus] = useState(true)
  const [showTitle, setShowTitle] = useState(true)
  const $tableRef = useRef(null)
  const [actualStartDate, setActualStartDate] = useState()

  // 合同明细不展示导入按钮控制，
  const onReceiveDataFromChild = (values) => {
    if (values && values.length > 0) {
      setActualStartDate(values[0].actualStartDate ? moment(values[0].actualStartDate) : undefined)
      setShowTitle(true)
      let $contractProcessStatus = values[0].contractProcessStatus
      // 审批中流程 && 结清审批通过
      let flag = ['START_RENT_COMMIT', 'NEW_RECEIPT_COMMIT', 'SETTLE_COMMIT', 'SETTLE_PASS']
      setHideImportFromStatus(flag.includes($contractProcessStatus))
    } else {
      setShowTitle(false)
    }
  }

  return (
    <div className={styles.page}>
      {showTitle && (
        <div className={styles.header}>
          <div className={styles.title}>{`实际${bizTypeMapText[bizType]?.rentTitle}`}</div>
          <div style={{ display: 'flex', alignItems: 'center' }}>
            <ContractIRR contractId={contractId}  />
            {actualStartDate && (
              <div>
                <span>{`实际${bizTypeMapText[bizType]?.dateText}`}：</span>
                <DatePicker
                  placeholder={'请选择'}
                  style={{ width: 200 }}
                  disabled
                  value={actualStartDate}
                />
              </div>
            )}
          </div>
        </div>
      )}
      <ActualTable
        extra={(item, _index) => {
          if (item.actualStartDate) {
            return (
              <Form
                initialValues={{
                  receiptStartDate: item.actualStartDate ? moment(item.actualStartDate) : undefined,
                }}
              >
                <Form.Item label="借据起租日期" name={'receiptStartDate'}>
                  <DatePicker placeholder={'请选择'} disabled />
                </Form.Item>
              </Form>
            )
          }
        }}
        businessVersion={businessVersion}
        ref={$tableRef}
        headerTitle={`实际${bizTypeMapText[bizType]?.rentTitle}`}
        contractStatus={contractStatus}
        bizType={bizType}
        contractId={contractId}
        onReceiveDataFromChild={onReceiveDataFromChild}
        showImportBtn={canEditFlag && !hideImportFromStatus}
        scene={'create'}
        // 债券转让、经营性租赁不显示,
        showTax={bizType !== 'ZR' && leaseType !== 'jyx_zu'}
      ></ActualTable>
    </div>
  )
}

export default observer(ContractApplicationActualRent)
