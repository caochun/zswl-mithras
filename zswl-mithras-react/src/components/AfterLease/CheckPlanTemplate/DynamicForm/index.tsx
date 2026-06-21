import { useEffect, useMemo, useState } from 'react'
import { observer } from '@zswl/admin'
import { Anchor, message, Tooltip } from 'antd'
import { ExclamationCircleOutlined } from '@ant-design/icons'
import BaseInfo from './BaseInfo'
import styles from './index.less'
import Api from '@/api/afterLease/rentalInspectionReport'

import CheckContent from './ContentDesc/CheckContent'
import { DetailLayout } from '@/components/Layout'


import { columns, columns2 } from './ContentDesc/Utils'

import NoPublicContentV3 from './ContentDesc/NoPublicContent/V3'
import NoPublicContentV1V2 from './ContentDesc/NoPublicContent/V1V2'

import PublicContentV3 from './ContentDesc/PublicContent/V3'
import PublicContentV1V2 from './ContentDesc/PublicContent/V1V2'

import LowRiskContentV3 from './ContentDesc/LowRiskContent/V3'
import LowRiskContentV1V2 from './ContentDesc/LowRiskContent/V1V2'

import LowRiskSummaryV1V2 from './ContentDesc/LowRiskSummary/V1V2'

import PublicSummary from './ContentDesc/SummaryShared/PublicSummary'

import NoPublicSummaryV1V2 from './ContentDesc/NoPublicSummary/V1V2'
import EmptySummary from './ContentDesc/SummaryShared/EmptySummary'

import BusContent from './ContentDesc/BusContent/V1V2'
import BusSummary from './ContentDesc/BusSummary/V1'

import StateAssetContent from './ContentDesc/StateAssetContent/V1'
import StateAssetSummary from './ContentDesc/StateAssetSummary/V1'

const { Link } = Anchor
const MODULE_TYPE = 'NEW_AFTER_LEASE_CHECK_REPORT'
const Index = (props) => {
  const { id, active, canEdit, businessVersion, reportTemplateType, editable, canImport } = props
  const isV3 = reportTemplateType === 'V3'
  const contentProps = { reportTemplateType, editable }
  useEffect(() => {
    id && getData()
  }, [id, active])

  const PublicContent = useMemo(() => {
    if (reportTemplateType === 'V3') {
      return PublicContentV3
    }
    return PublicContentV1V2
  }, [reportTemplateType])

  const NoPublicContent = useMemo(() => {
    if (reportTemplateType === 'V3') {
      return NoPublicContentV3
    }
    return NoPublicContentV1V2
  }, [reportTemplateType])

  const LowRiskContent = useMemo(() => {
    if (reportTemplateType === 'V3') {
      return LowRiskContentV3
    }
    return LowRiskContentV1V2
  }, [reportTemplateType])

  const LowRiskSummary = useMemo(() => {
    if (reportTemplateType === 'V3') {
      return EmptySummary
    }
    return LowRiskSummaryV1V2
  }, [reportTemplateType])

  const NoPublicSummary = useMemo(() => {
    if (reportTemplateType === 'V3') {
      return EmptySummary
    }
    return NoPublicSummaryV1V2
  }, [reportTemplateType])

  const ContentComponents = [
    NoPublicContent,
    PublicContent,
    LowRiskContent,
    BusContent,
    StateAssetContent,
  ][active]
  const SummaryComponents = [
    NoPublicSummary,
    PublicSummary,
    LowRiskSummary,
    BusSummary,
    StateAssetSummary,
  ][active]
  const [baseData, setBaseData] = useState({})
  const [contentData, setContentData] = useState([])
  const [summaryData, setSummaryData] = useState([])
  const getData = () => {
    Api.postBaseGet({ id, businessVersion }).then((res) => setBaseData(res))
    Api.postContentGet({ id, businessVersion }).then((res) => setContentData(res.contentList))
    Api.postSummaryGet({ id, businessVersion }).then((res) => setSummaryData(res?.contentList))
  }

  const saveBaseData = async (data) => {
    const param = {
      deadline: baseData?.deadline,
      riskExposure: baseData?.riskExposure,
      checkPlanClientId: id,
      id: baseData?.id,
      ...data,
    }
    await Api.postBaseSave(param)
    const res = await Api.postBaseGet({ id })
    setBaseData(res)
  }
  const columns2FieldList = ['NP_C_3_05_03', 'NP_C_2_03_03', 'LR_C_3_05_03', 'LR_C_2_03_03']
  const columns1FieldList = ['P_C_2_07', 'P_C_3_07', 'B_C_2_01', 'SOA_C_2_01']
  const tableFormat = (fieldValue, fieldName) => {
    // 使用 formTable时需要新增默认数据
    const isColumnsField = columns1FieldList.includes(fieldName)
    const isColumns2Field = columns2FieldList.includes(fieldName)
    if (isColumnsField || isColumns2Field) {
      return JSON.stringify({
        columns: isColumns2Field ? columns2 : columns,
        fieldValue,
      })
    }
    return fieldValue
  }

  const formatContent = (contentList) => {
    const newContentList = []
    Object.entries(contentList).forEach(([key, value]) => {
      if (!value) return
      const isArray = Array.isArray(value)
      if ([...columns2FieldList, ...columns1FieldList].includes(key)) {
        newContentList.push({
          moduleIndex: 0,
          fieldName: key,
          fieldValue: tableFormat(value, key),
        })
        return
      }
      if (isArray) {
        value.forEach((item, index) => {
          Object.entries(item).forEach(([fieldName, fieldValue]) => {
            newContentList.push({
              moduleIndex: index + 1,
              fieldName,
              fieldValue: tableFormat(fieldValue, fieldName),
              attributionList: key,
            })
          })
        })
        return
      }

      newContentList.push({
        moduleIndex: 0,
        fieldName: key,
        fieldValue: value,
      })
    })
    return newContentList
  }
  const saveContent = async (data, saveType = 'SAVE') => {
    const param = {
      contentList: formatContent(data),
      checkPlanClientId: id,
      saveType,
    }
    await Api.postContentSave(param)
    const res = await Api.postContentGet({ id })
    setContentData(res.contentList)
  }
  const saveSummary = async (contentList, saveType = 'SAVE') => {
    const param = {
      contentList: formatContent(contentList),
      checkPlanClientId: id,
      saveType,
    }
    await Api.postSummarySave(param)
    const res = await Api.postSummaryGet({ id })
    setSummaryData(res.contentList)
  }

  const params = {
    mainId: id,
    moduleType: MODULE_TYPE,
    materialsTypes: ['CHECK_REPORT_NON_PUBLIC_ATTACHMENT'],
    businessVersion,
  }

  return (
    <DetailLayout
      anchorList={[
        { label: '基本信息' },
        { label: '检查内容' },
        //  除了 租后检查报告(公共事业类、民生消费类) 模版外，其他还得保留
        { label: '检查总结', isHide: isV3 && ![1, 3, 4].includes(active) },
        {
          label: '检查附件',
        },
      ]}
      title={''}
    >
      <BaseInfo detail={baseData} saveData={saveBaseData} active={active} canEdit={canEdit} />
      <div id="quotationScheme">
        <CheckContent
          title={'检查内容'}
          contentData={contentData}
          saveApi={saveContent}
          canEdit={canEdit}
          active={active}
        >
          <ContentComponents
            reportTemplateType={reportTemplateType}
            chiName={baseData?.clientName}
          />
        </CheckContent>
      </div>
      <div id="cash">
        <CheckContent
          title={'检查总结'}
          contentData={summaryData}
          required
          saveApi={saveSummary}
          canEdit={canEdit}
          active={active}
        >
          <SummaryComponents reportTemplateType={reportTemplateType} />
        </CheckContent>
      </div>
      <div id="report">
        <FileTable
          params={params}
          enumType={'afterLeaseCheckReportMaterialsEnum'}
          uploadApi={({ file, fileType }) => Api.postFileUpload({ file, fileType, id })}
          title={<div className={styles.title}>检查附件</div>}
          canEdit={canEdit || canImport}
          columns={[
            {
              title: '项目评审资料',
              dataIndex: 'name',
              customCol:{
                condition: '融资款使用凭证或相关银行流水、融资明细表/还款计划表',
                cb:(value) => (
                <div style={{position:'relative',overflow:'hidden',textOverflow:'ellipsis'}}>
                  {value}
                  <Tooltip title='需上传资金用途资料或资金用途说明'>
                    <ExclamationCircleOutlined style={{position: 'absolute',right: 0,top:4,color:'red',cursor:'pointer'}}/>
                  </Tooltip>
                </div>
              )}
              // render: (value) => {
              //   if(value === '融资款使用凭证或相关银行流水、融资明细表/还款计划表'){
              //     // return (
              //     //   <div style={{display:'flex',flexDirection:'column',overflow:'hidden'}}>
              //     //     <span style={{textOverflow:'ellipsis'}}>{value}</span>
              //     //     <span style={{color:'red'}}>需上传资金用途资料或资金用途说明</span>
              //     //   </div>
              //     // )
              //     return (
              //       <div style={{position:'relative',overflow:'hidden',textOverflow:'ellipsis'}}>
              //         {value}
              //         <Tooltip title='需上传资金用途资料或资金用途说明'>
              //           <ExclamationCircleOutlined style={{position: 'absolute',right: 0,top:4,color:'red',cursor:'pointer'}}/>
              //         </Tooltip>
              //       </div>
              //     )
              //   }
              //   return value
              // },
            },
            InputColumn({ title: '上传地点', dataIndex: 'location', width: 400 }),
            InputColumn({ title: '上传人', dataIndex: 'createByName' }),
            InputColumn({ title: '上传时间', dataIndex: 'createTime' }),
          ]}
        />
      </div>
    </DetailLayout>
  )
}

export default observer(Index)
