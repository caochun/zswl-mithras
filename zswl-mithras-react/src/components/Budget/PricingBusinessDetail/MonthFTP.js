import { Button, TableStore } from '@zswl/components'
import { observer } from '@zswl/admin'
import { forwardRef, useEffect, useImperativeHandle, useMemo, useRef, useState } from 'react'
import { Form, Radio, message } from 'antd'
import styles from './index.less'
import CreditTermTable from './CreditTermTable'
import Quarter from './Quarter'
import newFtpBaseInfoApi from '@/api/budget/pricing/ftp/newFtpBaseInfoApi'
import MonthGuide from './MonthGuide'
import { NoEnumFileTable } from '@/components/Table'
import TextAreaEditable from './components/TextAreaEditable'
import { isFinancialOfficer, isPricingDept } from '@/utils'

export const MonthlyDeductionSupport = ({ descChange, canEdit, setSubmitDisabled }) => {
  return (
    <Form.Item name="MONTHLY_DEDUCTION">
      <TextAreaEditable
        isEdit={canEdit}
        rows={12}
        onFocus={() => setSubmitDisabled(true)}
        onBlur={(e) => descChange(e, 'MONTHLY_DEDUCTION')}
      />
    </Form.Item>
  )
}
export const MonthlyDeduction = forwardRef(
  ({ mainId, canEdit, businessVersion, setSubmitDisabled, isV3 }, ref) => {
    const hasValuation = isFinancialOfficer() || isPricingDept()
    const refresh = async () => {
      await newFtpBaseInfoApi.postRefresh({ mainId })
      message.success('刷新成功')
      creditTermTableRef.current?.creditTermStore.search()
    }
    const creditTermTableRef = useRef()
    useImperativeHandle(ref, () => ({
      refresh,
    }))
    return (
      <>
        <div className="z-sub-title zl-mrb z-flex-jsb">
          确认计价标准
          <Button type="primary" onClick={refresh} disabled={!canEdit}>
            刷新
          </Button>
        </div>
        <CreditTermTable
          mainId={mainId}
          canEdit={canEdit}
          businessVersion={businessVersion}
          isV3={isV3}
          setSubmitDisabled={setSubmitDisabled}
          ref={creditTermTableRef}
        />
      </>
    )
  }
)

function Index({ businessVersion, setSubmitDisabled, mainId, descChange, canEdit, detail, isV3 }) {
  const month = detail?.month
  const options = useMemo(
    () =>
      [
        { label: '月度 FTP 指导定价', value: '1' },
        detail?.pricingFrequency === 'QUARTER' && {
          label: '季度项目最低收益率',
          value: '2',
        },
      ].filter(Boolean),
    [detail?.pricingFrequency]
  )
  const [radioValue, setRadioValue] = useState('1')
  return (
    <div className="no-tableClass">
      <div className={styles.title}>FTP 成本定价</div>
      <Radio.Group
        options={options}
        value={radioValue}
        buttonStyle="outline"
        optionType="button"
        onChange={(e) => setRadioValue(e.target.value)}
        className={styles.radioButton}
      />
      {radioValue === '1' ? (
        <MonthGuide
          mainId={mainId}
          descChange={descChange}
          canEdit={canEdit}
          month={month}
          businessVersion={businessVersion}
          setSubmitDisabled={setSubmitDisabled}
          isV3={isV3}
        />
      ) : (
        <>
          <Quarter
            mainId={mainId}
            descChange={descChange}
            isV3={isV3}
            canEdit={canEdit}
            businessVersion={businessVersion}
            setSubmitDisabled={setSubmitDisabled}
          />
        </>
      )}
      <NoEnumFileTable
        title={'附件下载'}
        canEdit={false}
        params={{
          mainId,
          businessVersion,
          moduleType: 'NEW_FTP_GUIDANCE',
          materialsTypes: ['MEETING_FILE'],
        }}
        columns={[
          { title: '资料名称', dataIndex: 'filename' },
          { title: '上传人', dataIndex: 'createByName' },
          { title: '上传时间', dataIndex: 'createTime' },
        ]}
      />
    </div>
  )
}

export default observer(Index)
