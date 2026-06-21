import React, { useState } from 'react'
import { Descriptions } from 'antd'
import styles from './index.less'

function CpmMarginBelongContract({ contractInfo = {} }) {
  const { contractCode, projName, clientName, contractType, bizDept, projSponsorUserName } =
    contractInfo
  return (
    <>
      <div style={{ paddingTop: '24px' }} className={styles.contractDetil}>
        <Descriptions
          bordered
          title="所属合同"
          size={'small'}
          labelStyle={{ background: '#F5F6FA', height: '48px' }}
        >
          <Descriptions.Item
            label="合同编号"
            labelStyle={{ width: '230px' }}
            contentStyle={{ width: '300px' }}
          >
            {contractCode}
          </Descriptions.Item>
          <Descriptions.Item
            label="项目名称"
            labelStyle={{ width: '230px' }}
            contentStyle={{ width: '300px' }}
          >
            {projName}
          </Descriptions.Item>
          <Descriptions.Item
            label="客户名称"
            labelStyle={{ width: '230px' }}
            contentStyle={{ width: '300px' }}
          >
            {clientName}
          </Descriptions.Item>
          <Descriptions.Item label="类别">{contractType}</Descriptions.Item>
          <Descriptions.Item label="业务部门">{bizDept}</Descriptions.Item>
          <Descriptions.Item label="项目主办">{projSponsorUserName}</Descriptions.Item>
        </Descriptions>
      </div>
    </>
  )
}

export default CpmMarginBelongContract
