import { AmountColumn, AmountFormat, FiledFormat } from '@/components/Format'
import { getQuery, observer } from '@zswl/admin'
import FormatTable from './components/FormatTable'
import newFtpMonthlyGuidanceExtApi from '@/api/newFtp/newFtpMonthlyGuidanceExtApi'
import newFtpBaseInfoApi from '@/api/newFtp/newFtpBaseInfoApi'
import { useContext, useEffect, useState } from 'react'
import { Form } from '@zswl/components'
import TextAreaEditable from './components/TextAreaEditable'
import { FTPContext } from './index'
import _ from 'lodash'

function Index({ canEdit = false, descChange, setSubmitDisabled }) {
  const isFormApproval = !canEdit
  const FTPValues = useContext(FTPContext)
  const { mainId, businessVersion } = FTPValues
  const [detail, setDetail] = useState([])
  const getList = async () => {
    const api = newFtpMonthlyGuidanceExtApi.postQuarterlyExtDetail
    const res = await api({ id: mainId, version: businessVersion })
    setDetail([res])
  }
  const amountChange = _.debounce(async (val, params) => {
    const { dataSource, dataIndex, notAmount } = params
    const value = notAmount ? val : val * 10000
    await newFtpMonthlyGuidanceExtApi
      .postQuarterlyExtModify({
        id: dataSource.id,
        ...dataSource,
        [dataIndex]: value,
      })
      .finally((v) => setSubmitDisabled(false))
    getList()
  }, 1000)
  useEffect(() => {
    getList()
  }, [])
  const wrapItemProps = {
    inputConfig: {
      step: 0.01,
      onChange: amountChange,
      min: -Infinity,
    },
  }
  const creditTermColumns = [
    {
      title: `季度集团控股公司项目最低收益率定价指引`,
      children: [
        AmountColumn({
          title: '1-3 年',
          dataIndex: 'threeYear',
          width: 120,
          editable: true,
          wrapItemProps,
        }),
        AmountColumn({
          title: '3-5 年',
          dataIndex: 'threeToFiveYear',
          width: 120,
          editable: true,
          wrapItemProps,
        }),
        AmountColumn({
          title: '5年以上',
          dataIndex: 'moreThanFiveYear',
          width: 120,
          editable: true,
          wrapItemProps,
        }),
      ],
    },
  ]

  return (
    <>
      <FormatTable
        dataSource={detail}
        columns={creditTermColumns}
        needClass={false}
        className="titleNoColor"
        rowKey={'id'}
        scroll={false}
        editable={canEdit}
      />
      <Form.Item name="QUARTERLY_PRICING_EXT">
        <TextAreaEditable
          isEdit={canEdit}
          onFocus={() => setSubmitDisabled(true)}
          rows={6}
          onBlur={(e) => descChange(e, 'QUARTERLY_PRICING_EXT')}
        />
      </Form.Item>
    </>
  )
}

export default observer(Index)
