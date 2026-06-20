import { EditDescription } from '@/components/Table'
import { getDescColumns, timeFormat } from '@/utils'
import { InputNumberEditable } from '@/components/Format'
import { observer, getQuery, history } from '@zswl/admin'
import { Page } from '@zswl/components'
import { DatePicker } from 'antd'
import ActualTable from '../FundActualTable'
import Api from '@/api/financial/fundApi'
import styles from './index.less'
import { useEffect, useState } from 'react'
import moment from 'moment'

const nameColumns = [
  {
    title: '还款日',
    dataIndex: 'repaymentDate',
    requiredMark: true,
    editable: () => InputNumberEditable({ required: true, addonAfter: '日', max: 31, min: 1 }),
    render: (val) => val,
  },
  {
    title: '实际贷款日期',
    dataIndex: 'actualLoanDate',
    requiredMark: true,
    dateFormat: 'yyyy-MM-DD',
    editable: {
      element: <DatePicker />,
      rules: [{ required: true, message: '请选择' }],
    },
  },
]

const columns = getDescColumns(nameColumns, nameColumns)

function Index({ params: { id } }) {
  const isFormApproval = getQuery('typeId') == 'approval'

  const [detail, setDetail] = useState({})
  const getDetail = async () => {
    const res = await Api.postCarryintersetInfo({
      financingId: id,
    })
    setDetail({
      repaymentDate: res.repayDay,
      actualLoanDate: res.actualLoanDate && moment(res.actualLoanDate),
    })
  }

  useEffect(() => {
    id && getDetail()
  }, [id])

  const saveData = async (values) => {
    const { repaymentDate, actualLoanDate } = values
    await Api.postFlowCarryinterest({
      financingId: id,
      repaymentDate,
      actualLoanDate: timeFormat(actualLoanDate),
    })
    history.push(`/financial/fund`)
  }

  return (
    <Page header={null} current={'融资生效'}>
      <EditDescription
        title="融资生效"
        detail={detail}
        saveData={saveData}
        canEdit={true}
        initEdit={true}
        columns={columns}
      />
      <div className={styles.actualTable}>
        <div className={styles.title}>实际还款计划</div>
        <ActualTable
          isFormApproval={isFormApproval}
          scene={'CARRY_INTEREST'}
          financingId={id}
          showImportBtn={true}
          showActualLoanDate={false}
        ></ActualTable>
      </div>
    </Page>
  )
}

export default observer(Index)
