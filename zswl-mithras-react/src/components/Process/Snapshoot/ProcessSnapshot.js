import React, { useEffect, useMemo } from 'react'
import { Button, Descriptions } from 'antd'
import styles from './index.less'
import { observer } from '@zswl/admin'
import ApprovalHistory from '../ApprovalHistory/ProcessApprovalHistory'
import DetailLayout from '@/components/DetailLayout'
import Store from './store'

const Public = ({ id }) => {
  const store = useMemo(() => {
    return new Store({})
  }, [])

  useEffect(() => {
    store.queryDetail(id, 'processInstanceId')
  }, [id])

  const { detailData } = store
  const { processInstanceId, modelName } = detailData

  return (
    <DetailLayout
      title="审批快照"
      extra={[
        <Button
          style={{ marginRight: '10px' }}
          onClick={() => {
            store.toPrint('center')
          }}
        >
          打印
        </Button>,
        <Button
          style={{ marginRight: '10px' }}
          type="primary"
          onClick={() => {
            return store.downLoadImg('center', id, detailData)
          }}
        >
          下载
        </Button>,
      ]}
    >
      <div id="center">
        <div>
          <Descriptions
            title="流程信息"
            bordered
            column={2}
            labelStyle={{ background: '#F5F6FA' }}
            size={'small'}
            className={styles.des}
          >
            <Descriptions.Item
              label={'流程ID'}
              labelStyle={{ width: '180px' }}
              contentStyle={{ width: '400px' }}
            >
              {processInstanceId}
            </Descriptions.Item>
            <Descriptions.Item
              label="流程类型"
              labelStyle={{ width: '180px' }}
              contentStyle={{ width: '400px' }}
            >
              {modelName}
            </Descriptions.Item>
          </Descriptions>
        </div>
        <div id="spls" className={styles.splct}>
          <h3>审批历史</h3>
          <ApprovalHistory processInstanceId={id} />
        </div>
      </div>
    </DetailLayout>
  )
}

export default observer(Public)
