import { EditDescription } from '@/components'
import { AmountColumn } from '@/components/Format'
import { observer } from '@zswl/admin'
import { Button, Form, Table } from '@zswl/components'
import { message } from 'antd'
import { forwardRef, useImperativeHandle, useRef, useEffect, useMemo, useState } from 'react'
import DataUpload from '@/components/DataUpload'
import styles from './index.less'
import { PayCircleOutlined } from '@ant-design/icons'
import FormIrr from '@/components/FormIrr'
import irrGenerationApi from '@/api/common/irrGenerationApi'
import mathjs from '@/utils/math'
import mock from './mock'
import _ from 'lodash'
import DownloadTemplate from '@/components/Actions/DownloadTemplate'
import { saveServer } from '@/utils'

const CashFlowStatementTable = ({ tableStore, dataSource, radioValue }) => {
  const columns = [
    { title: '日期', dataIndex: 'cashFlowDate' },
    { title: '期项', dataIndex: 'cashFlowPhase' },
    AmountColumn({
      title: <div style={{ textAlign: 'right' }}>{radioValue ? '应收保理款(元)' : '租金(元)'}</div>,
      dataIndex: 'rent',
    }),
    AmountColumn({
      title: <div style={{ textAlign: 'right' }}>本金(元)</div>,
      dataIndex: 'principal',
    }),
    AmountColumn({
      title: <div style={{ textAlign: 'right' }}>利息(元)</div>,
      dataIndex: 'interest',
    }),
    AmountColumn({
      title: <div style={{ textAlign: 'right' }}>剩余本金(元)</div>,
      dataIndex: 'remainingPrincipal',
    }),
  ]
  return <Table onFilter={(key,val) => saveServer('layout_ToolsModal_Project',val)} columnsFilter={'layout_ToolsModal_Project'} store={tableStore} dataSource={dataSource} columns={columns} />
}

const CashFlowStatement = ({
  compareData,
  setCompareData,
  generate,
  radioValue,
  generationParams,
}) => {
  const templateName = useMemo(() => {
    return radioValue === 1
      ? 'TEMPLATE_OSS_NAME_ESTIMATE_PAYMENT_ITEM'
      : 'TEMPLATE_OSS_NAME_ESTIMATE_RENT_ITEM'
  }, [radioValue])

  const importFiles = async (file) => {
    const { fileList } = DataUpload.classify(file)
    const res = await irrGenerationApi.postGenerationImport({
      file: fileList[0],
    })
    setCompareData(res)
  }
  const exportFile = async () => {
    const fileName = '现金流计划表.xlsx'
    const params = {
      items: compareData,
      generationParams,
      dataSource: radioValue,
    }
    if (!compareData.length) {
      message.error('请先生成现金流')
      return
    }
    const res = await irrGenerationApi.postGenerationExport(params, fileName)
    message.success('导出成功')
  }

  return (
    <>
      <div className={styles.titleWrap}>
        <div className={'z-sub-title'}>
          {'现金流计划表'}
          <Button icon={<PayCircleOutlined />} style={{ marginLeft: 8 }} onClick={generate}>
            生成现金流
          </Button>
        </div>

        <div className={styles.btnWrap}>
          <DownloadTemplate
            params={{
              templateName,
              moduleType: 'CONTRACT',
            }}
          />
          <DataUpload accept=".xlsx" maxCount={1} onChange={importFiles}>
            <Button type="primary" style={{ marginRight: 8 }}>
              数据导入
            </Button>
          </DataUpload>
          <Button onClick={exportFile}>导出</Button>
        </div>
      </div>
      <CashFlowStatementTable dataSource={compareData} radioValue={radioValue} />
    </>
  )
}

const Index = ({ columns, radioValue, form }, ref) => {
  const descRef = useRef()
  const [compareData, setCompareData] = useState([])
  const [generationParams, setGenerationParams] = useState({})
  const generate = async () => {
    const form = descRef.current.form
    const values = await form.validateFields()
    const data = _.cloneDeep(form.getFieldsFormatValue())

    if (!data.interestRate && data.interestRate !== 0) {
      data.interestRate = mathjs.format(mathjs.add(data.lprPercent, data.lprAddPercent))
    }
    const res = await irrGenerationApi.postGenerationExecute?.(data)
    message.success('生成现金流成功')
    setCompareData(res)
    setGenerationParams(data)
  }
  const handleOpen = async () => {
    const { repayRate, leaseMonthCount } = descRef.current.form.getFieldsFormatValue()
    if (!repayRate) {
      message.error('请先填写还款利率')
      return
    }
    if (!compareData.length) {
      message.error('请先生成现金流')
      return
    }
    const params = {
      repayRate,
      monthCount: leaseMonthCount,
      itemList: compareData,
    }
    return await irrGenerationApi.postGenerationIrr(params)
  }
  const restData = () => {
    descRef.current.form.resetFields()
    setCompareData([])
    setGenerationParams({})
    form.setFieldsValue({ irr: '' })
  }
  useEffect(() => {
    restData()
  }, [radioValue])

  useImperativeHandle(ref, () => ({
    descRef: descRef.current.form,
  }))

  return (
    <div>
      <EditDescription
        items={columns}
        title="报价方案"
        className={'z-description'}
        hiddenButton
        initEdit
        dataSource={{}}
        ref={descRef}
      />
      <CashFlowStatement
        generate={generate}
        compareData={compareData}
        radioValue={radioValue}
        setCompareData={setCompareData}
        generationParams={generationParams}
      />

      <div style={{ marginTop: 12 }}>
        <FormIrr.Item
          name="irr"
          label={'IRR'}
          rules={[{ required: true }]}
          // onBlur={store.irrChange}
          handleOpen={handleOpen}
          canEdit={true}
        />
      </div>
    </div>
  )
}

export default observer(forwardRef(Index))
