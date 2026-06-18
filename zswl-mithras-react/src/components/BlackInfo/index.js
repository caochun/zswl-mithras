import React, { useEffect, useState } from 'react'
import { Tooltip } from 'antd'
import { InfoCircleOutlined } from '@ant-design/icons'
import _ from 'lodash'
import queryExternalDataApi from '@/api/blackGray/queryExternalDataApi'

export const BlackInfo = ({ params, style = {} }) => {
  const clientId = _.isObject(params?.clientId) ? params?.clientId?.value : params?.clientId
  if (!params?.unifiedSocialCreditCode && !clientId) return null
  const [result, setResult] = useState({})
  const getInfo = async () => {
    if (!params?.unifiedSocialCreditCode && !clientId) return
    const data = await queryExternalDataApi.getLibrary(params)
    setResult(data ?? {})
  }
  useEffect(() => {
    getInfo()
  }, [])
  const infoMap = {
    BLACK_LIST: {
      color: '#f1293f',
      text: '黑名单',
      icon: '/public/assets/risk/blackGray/black.svg',
    },
    GRAY_LIST: {
      color: '#ff7738',
      text: '灰名单',
      icon: '/public/assets/risk/blackGray/gray.svg',
    },
  }
  const currentInfo = infoMap[result?.blackGrayType] ?? {
    color: '#3d6cde',
    text: '未命中',
    icon: '/public/assets/risk/blackGray/miss.svg',
  }
  return (
    <div
      style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        minWidth: 100,
        flexShrink: 0,
        ...style,
      }}
    >
      <img src={currentInfo?.icon} style={{ marginLeft: 8 }} />
      <span style={{ color: currentInfo?.color }}>{currentInfo?.text}</span>
      {!!result?.blackGrayType && (
        <Tooltip
          title={
            <div>
              <div>入库原因:{result?.applyReasonNames?.join('、')}</div>
              <div>入库时间:{result?.warehouseTime}</div>
            </div>
          }
        >
          <InfoCircleOutlined style={{ marginLeft: 8 }} />
        </Tooltip>
      )}
    </div>
  )
}

export default BlackInfo
