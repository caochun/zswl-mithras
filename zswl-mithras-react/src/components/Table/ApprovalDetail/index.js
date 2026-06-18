import { observer } from '@zswl/admin'
import EditDescription from '../EditDescription'
import { Collapse } from 'antd'
import styles from './index.less'
import { TextAreaColumn } from '@/components/Format'
import { approvalRemarkApi } from '@/utils/api/tableFileApi'
import { useEffect, useState } from 'react'

const { Panel } = Collapse
const Desc = ({ detail, isReconsider, ...rest }) => {
  const items = [
    TextAreaColumn({
      title: isReconsider ? '复议原因' : '变更原因',
      dataIndex: 'reason',
      requiredMark: true,
      editable: true,
      required: true,
    }),
    TextAreaColumn({
      dataIndex: 'originalContent',
      title: isReconsider ? '原内容' : '变更原内容',
      requiredMark: true,
      editable: true,
      required: true,
    }),
    TextAreaColumn({
      dataIndex: 'toBeContent',
      title: isReconsider ? '调整后内容' : '变更后内容',
      requiredMark: true,
      editable: true,
      required: true,
    }),
  ]
  return <EditDescription title={null} detail={detail} initEdit={false} columns={items} {...rest} />
}
function Index({ data = {}, hasTitle = true, showLast = false, canEdit, params }) {
  let { remarkType, id } = data
  const defaultRemarkJsonList =
    showLast && remarkJsonList?.length > 0
      ? [data.remarkJsonList[remarkJsonList.length - 1]]
      : data.remarkJsonList
  const [remarkJsonList, setRemarkJsonList] = useState(defaultRemarkJsonList)

  const isReconsider = remarkType === 'RECONSIDER'
  const saveData = async (values, index) => {
    const { remarkJsonList: newRemarkJsonList, ...rest } = params
    const functionCodeMap = {
      CONTRACT: 'contract',
      GROUP_CREDIT_ESTABLISH: 'creditestablish',
      FUND_FINANCING: 'fundfinancing',
      PROJ_REVIEW: 'projreview',
      GROUP_CREDIT_REVIEW: 'creditreview',
      PROJ_PRICING: 'projpricing',
      APPRAISAL_COMPANY_WHITELIST: 'whitelist',
    }
    await approvalRemarkApi.postRemarkModify(
      { ...rest, id, remarkJson: values },
      `processmodifyremarkmodify${functionCodeMap[params.moduleType]}`
    )
    const newRemarkJsonListCopy = [...remarkJsonList]
    newRemarkJsonListCopy[index] = { ...newRemarkJsonListCopy?.[index], ...values }
    setRemarkJsonList(newRemarkJsonListCopy)
  }
  if (!remarkJsonList?.length) return null
  return (
    <div className={styles.wrap}>
      {hasTitle && <div className={styles.title}>{isReconsider ? '复议说明' : '变更说明'}</div>}
      {remarkJsonList.length > 1 ? (
        <Collapse accordion className={styles.descList}>
          {remarkJsonList.map((v, i) => {
            return (
              <Panel header={v.createTime} key={i}>
                <Desc
                  detail={v}
                  isReconsider={isReconsider}
                  canEdit={v.isUpdate && canEdit}
                  saveData={(values) => saveData(values, i)}
                />
              </Panel>
            )
          })}
        </Collapse>
      ) : (
        <Desc
          isReconsider={isReconsider}
          detail={remarkJsonList?.[0]}
          canEdit={remarkJsonList?.[0].isUpdate && canEdit}
          saveData={(values) => saveData(values, 0)}
        />
      )}
    </div>
  )
}

export default observer(Index)
