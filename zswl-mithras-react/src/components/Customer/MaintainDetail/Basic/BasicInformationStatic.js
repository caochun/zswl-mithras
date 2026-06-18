import React, { useEffect, useState } from 'react'
import { Descriptions } from 'antd'
import { observer, getQuery } from '@zswl/admin'
import styles from './index.less'
import Api from '@/api/customer/maintainApi'
import { certTypeList, clientTypeList, genderTypeList, marriageTypeList } from '../general'

const BasicInformation = ({ id, clientType, save, startUserId, businessVersion, store }) => {
  const [result, setResult] = useState({})
  const [compare, setCompare] = useState({})
  const initDetail = async () => {
    const data = await Api.naturalDetail({ clientId: id, businessVersion, startUserId })
    setResult(data)
    store.setBaseInfo(data)
  }
  useEffect(() => {
    initDetail()
  }, [id, clientType, save])
  // 对比接口
  const getApprovalNormaldetail = async () => {
    const data = await Api.getApprovalNormaldetail({ clientId: id, businessVersion, startUserId })
    setCompare(data)
  }
  useEffect(() => {
    if (getQuery('typeId') == 'approval') {
      getApprovalNormaldetail()
    }
  }, [getQuery('typeId')])
  const starDom = (name, must, obj) => {
    return (
      <span className={styles.colorsWrap} style={{ position: 'relative' }}>
        {must && (
          <span
            className={styles.colors}
            style={{ color: ' #eb2222', padding: ' 4px', position: 'absolute', left: ' -13px' }}
          >
            *
          </span>
        )}
        <span style={{ color: obj?.isChange ? 'red' : 'rgba(0, 0, 0, 0.85)' }}>{name}</span>
      </span>
    )
  }
  return (
    <div>
      <Descriptions
        title="基本信息"
        bordered
        column={2}
        labelStyle={{ background: '#F5F6FA' }}
        size={'small'}
        className={styles.des}
      >
        <Descriptions.Item
          label={starDom('客户名称', true, compare.clientName)}
          labelStyle={{ width: '180px' }}
          contentStyle={{ width: '400px' }}
        >
          {result.clientName}
        </Descriptions.Item>
        <Descriptions.Item
          label={starDom('客户分类', false, compare.clientType)}
          labelStyle={{ width: '180px' }}
          contentStyle={{ width: '400px' }}
        >
          {clientTypeList[result.clientType]}
        </Descriptions.Item>
        <Descriptions.Item label={starDom('客户编号', false, compare.clientCode)}>
          {result.clientCode}
        </Descriptions.Item>
        <Descriptions.Item label={starDom('证件类型', true, compare.certType)}>
          {certTypeList[result.certType]}
        </Descriptions.Item>
        <Descriptions.Item label={starDom('证件号码', true, compare.certNumber)}>
          {result.certNumber}
        </Descriptions.Item>
        <Descriptions.Item label={starDom('性别', true, compare.gender)}>
          {genderTypeList[result.gender]}
        </Descriptions.Item>
        <Descriptions.Item label={starDom('婚姻状况', true, compare.marriageType)}>
          {marriageTypeList[result.marriageType]}
        </Descriptions.Item>
        <Descriptions.Item label={starDom('国别', true, compare.country)}>
          {result.countryName}
        </Descriptions.Item>
        <Descriptions.Item label={starDom('年龄', false, compare.age)}>
          {result.age}
        </Descriptions.Item>
        <Descriptions.Item label={starDom('手机号', false, compare.mobileNumber)}>
          {result.mobileNumber}
        </Descriptions.Item>
        <Descriptions.Item label={starDom('家庭住址', false, compare.homeAddress)}>
          {result.homeAddress}
        </Descriptions.Item>
        <Descriptions.Item label={starDom('邮箱', false, compare.mail)}>
          {result.mail}
        </Descriptions.Item>
      </Descriptions>
    </div>
  )
}

export default observer(BasicInformation)
