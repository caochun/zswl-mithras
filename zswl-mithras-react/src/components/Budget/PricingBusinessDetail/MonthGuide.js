import { Button, TableStore } from '@zswl/components'
import { observer } from '@zswl/admin'
import { useEffect, useMemo, useState } from 'react'
import { Form, message } from 'antd'
import TextAreaEditable from './components/TextAreaEditable'
import FtpGuideTable from './FtpGuideTable'
import FtpFormatTable from './FtpFormatTable'
import Quarter from './Quarter'
import newFtpBaseInfoApi from '@/api/newFtp/newFtpBaseInfoApi'
import newFtpMonthlyGuidanceExtApi from '@/api/newFtp/newFtpMonthlyGuidanceExtApi'
import newFtpMonthlyGuidanceApi from '@/api/newFtp/newFtpMonthlyGuidanceApi'
import { REGIONAL, ASSET_INDUSTRY, RISK_INDUSTRY, RISK_INDUSTRY_MAP } from './enum'
import _ from 'lodash'
import FormatTable from './components/FormatTable'
import { AmountColumn } from '@/components/Format'

const v1Format = (dataSource, isFormApproval) => {
  const tableList = []
  // 循环取 row 1- 15 的数据
  for (let i = 1; i <= 15; i++) {
    // 跳过工程机械类，注意跳过的时候要去改 span 的大小
    if ([13].includes(i)) {
      continue
    }
    const row = {}
    const data =
      (isFormApproval ? dataSource[`row${i}`]?.diffValueList : dataSource[`row${i}`]) ?? []
    data.forEach((item, index) => {
      if (i > 9 && i <= 15) {
        row[`col${index * 3}`] = isFormApproval ? item : item.value
        row[`col${index * 3}Id`] = item.id
      } else {
        row[`col${index}`] = isFormApproval ? item : item.value
        row[`col${index}Id`] = item.id
      }
    })
    row.riskIndustry = i < 9 ? RISK_INDUSTRY[0] : i < 12 ? RISK_INDUSTRY[1] : RISK_INDUSTRY[i - 11]
    row.assetIndustry = i < 9 ? ASSET_INDUSTRY[Math.ceil((i - 1) / 3)] : '/'
    row.regional = i < 13 ? REGIONAL[(i - 1) % 3] : '/'
    row.id = _.uniqueId()
    tableList.push(row)
  }
  return tableList
}
const v2Format = (dataSource, isFormApproval) => {
  const tableList = []
  const publicList = []

  //上面的表格 循环取 row 1- 15 的数据
  for (let i = 1; i <= 12; i++) {
    // 跳过工程机械类，注意跳过的时候要去改 span 的大小
    if ([10].includes(i)) {
      continue
    }
    const row = {}
    const data =
      (isFormApproval ? dataSource[`row${i}`]?.diffValueList : dataSource[`row${i}`]) ?? []
    data.forEach((item, index) => {
      // if (i > 11 && i <= 12) {
      //   row[`col${index * 3}`] = isFormApproval ? item : item.value
      //   row[`col${index * 3}Id`] = item.id
      // } else {
      row[`col${index}`] = isFormApproval ? item : item.value
      row[`col${index}Id`] = item.id
      // }
    })
    row.riskIndustry = i < 9 ? RISK_INDUSTRY[0] : RISK_INDUSTRY[i - 8]
    row.assetIndustry = i < 9 ? ASSET_INDUSTRY[Math.ceil((i - 1) / 3)] : '/'
    row.regional = i < 10 ? REGIONAL[(i - 1) % 3] : '/'
    row.id = _.uniqueId()
    tableList.push(row)
  }
  // 下面的表格 循环取 publicList 的数据
  const start = 13
  const end = 21
  for (let i = start; i <= end; i++) {
    const row = {}
    const data =
      (isFormApproval ? dataSource[`row${i}`]?.diffValueList : dataSource[`row${i}`]) ?? []
    data.forEach((item, index) => {
      row[`col${index}`] = isFormApproval ? item : item.value
      row[`col${index}Id`] = item.id
    })
    row.riskIndustry = RISK_INDUSTRY_MAP[Math.floor((i - start) / 3)]
    row.regional = REGIONAL[(i - start) % 3]
    row.id = _.uniqueId()
    publicList.push(row)
  }

  return { tableList, publicList }
}
const MonthGuide = ({
  mainId,
  descChange,
  canEdit,
  month,
  businessVersion,
  setSubmitDisabled,
  isV3,
}) => {
  const isFormApproval = !canEdit

  const [publicDetail, setPublicDetail] = useState([])
  const ftpGuideTable = useMemo(
    () =>
      new TableStore({
        pagination: false,
        request: async (searchParams) => {
          const api = isFormApproval
            ? newFtpBaseInfoApi.postCompareMonthly
            : newFtpMonthlyGuidanceApi.postGuidanceDetail
          const res = await api({ mainId, version: businessVersion })

          const detail = isV3 ? v2Format(res, isFormApproval) : v1Format(res, isFormApproval)
          setPublicDetail(detail.publicList)
          return isV3 ? detail.tableList : detail
        },
      }),
    [mainId, isV3]
  )
  const calc = async () => {
    await newFtpBaseInfoApi.postInfoCalculate({ mainId })
    message.success('计算成功')
    getList()
    return ftpGuideTable.search()
  }
  const [detail, setDetail] = useState([])
  const getList = async () => {
    const api = isFormApproval
      ? newFtpBaseInfoApi.postCompareExt
      : newFtpMonthlyGuidanceExtApi.postExtDetail
    const res = await api({ mainId, version: businessVersion })
    setDetail([res])
  }
  const amountChange = _.debounce(async (val, params) => {
    const { dataSource, dataIndex, notAmount } = params
    const value = notAmount ? val : val * 10000
    await newFtpMonthlyGuidanceExtApi
      .postExtModify({
        id: dataSource.id,
        ...dataSource,
        [dataIndex]: value,
      })
      .finally((v) => setSubmitDisabled(false))
    getList()
  }, 1000)
  const wrapItemProps = {
    inputConfig: {
      step: 0.01,
      onChange: amountChange,
      min: -Infinity,
    },
  }
  useEffect(() => {
    getList()
  }, [])

  return (
    <>
      <div className="z-sub-title zl-mrb">月度 FTP 指导报价</div>
      {canEdit && (
        <Button
          className="zl-mrb"
          type="primary"
          onClick={calc}
          confirm={'重新计算将会覆盖已修改的数据，是否确认重新计算？'}
        >
          计算
        </Button>
      )}
      <FtpGuideTable
        table={ftpGuideTable}
        columnProps={{ wrapItemProps }}
        canEdit={canEdit}
        isV3={isV3}
        setSubmitDisabled={setSubmitDisabled}
        publicDetail={publicDetail}
      />

      <FtpFormatTable
        detail={detail}
        columnProps={{ wrapItemProps }}
        canEdit={canEdit}
        month={month}
      />
      <Form.Item name="MONTHLY_GUIDANCE">
        <TextAreaEditable
          isEdit={canEdit}
          rows={4}
          onFocus={() => setSubmitDisabled(true)}
          onBlur={(e) => descChange(e, 'MONTHLY_GUIDANCE')}
        />
      </Form.Item>
      <div className="z-sub-title" style={{ marginBottom: 12 }}>
        补充说明
      </div>
      <Form.Item name="MONTHLY_SUPPLEMENT">
        <TextAreaEditable
          isEdit={canEdit}
          rows={4}
          placeholder="请输入补充说明"
          max={1000}
          onFocus={() => setSubmitDisabled(true)}
          onBlur={(e) => descChange(e, 'MONTHLY_SUPPLEMENT')}
        />
      </Form.Item>
    </>
  )
}

export default observer(MonthGuide)
