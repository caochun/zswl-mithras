import IconFont from '@/components/Icon'
import { observer } from '@zswl/admin'
import { Button, Modal, Table, TableStore } from '@zswl/components'
import { Carousel, Image, Space } from 'antd'
import { MatchFormat } from '@/components/Format'
import { useMemo, useState } from 'react'
import { LeftOutlined, RightOutlined } from '@ant-design/icons'
import { InvoiceTable } from './Invoice'
import { saveServer } from '@/utils'

export const goOcr = ({ id, businessType = 'invoice', uploadType, rowId }) => {
  const rowIdString = rowId ? `&rowId=${rowId}` : ''
  window.open(
    `/ocr/recognition?id=${id}&businessType=${businessType}&uploadType=${uploadType}${rowIdString}`
  )
}
const ImageRender = ({ dataSource, url, verifyResult, type, invoiceDataSource }) => {
  const columns = [
    { dataIndex: 'name', title: '字段名', width: 100 },
    { dataIndex: 'content', title: '信息内容' },
  ]
  const carCardIconMap = {
    fail: 'icon-heyanshibai',
    yanzhenshibai: 'icon-yanzhenshibai',
    yanzhentongguo: 'icon-yanzhentongguo',
    failure: 'icon-shibieshibai',
    success: 'icon-shibiechenggong',
  }
  const invoiceIconMap = {
    VERIFICATION_NOT_PASSED: 'icon-yanzhenshibai',
    VERIFICATION_PASSED: 'icon-yanzhentongguo',
    IDENTIFY_FAILED: 'icon-shibieshibai',
    IDENTIFY_SUCCESS: 'icon-shibiechenggong',
  }
  const resultIcon =
    type === 'invoice' ? invoiceIconMap[verifyResult] : carCardIconMap[verifyResult]
  const table = useMemo(
    () =>
      new TableStore({
        request: () => dataSource,
        pagination: false,
      }),
    [dataSource]
  )
  return (
    <Space>
      <Image src={url} width={400} />
      <div style={{ position: 'relative', width: 550 }}>
        <Table         columnsFilter={'ocr_list_CompareModal'}
        onFilter={(key,val) => saveServer('ocr_list_CompareModal',val)} columns={columns} store={table} pagination={false} columnWidth={180}></Table>
        {type === 'invoice' && <InvoiceTable dataSource={invoiceDataSource} pagination={false} />}
        <IconFont
          type={resultIcon}
          style={{ position: 'absolute', bottom: 40, right: 50, fontSize: 120 }}
        />
      </div>
    </Space>
  )
}

const CarTableData = [
  { dataIndex: 'vehicleRegistrationNumber', title: '车牌号' },
  { dataIndex: 'vehicleRegistrationOwner', title: '机动车所有人' },
  { dataIndex: 'vehicleVin', title: '车架号' },
  { dataIndex: 'vehicleManufacturer', title: '制造商' },
]
const VoiceTableData = [
  { dataIndex: 'fileName', title: '文件名', width: 150 },
  { dataIndex: 'invoiceNo', title: '发票号码' },
  { dataIndex: 'invoicePayerName', title: '购买方', width: 180 },
  { dataIndex: 'invoiceSellerName', title: '销售方', search: true },
  { dataIndex: 'status', title: '发票状态', matchOption: 'leaseVatInvoiceStatusEnum' },
  // { dataIndex: 'verifyResult', title: '验真结果', matchOption: 'leaseFileOCRStatus' },
]

const Index = ({ store, type = 'invoice', mainId }) => {
  let listData = store?.getInitialValues() ?? []
  listData = listData.map(({ url, id, ...rest }) => {
    const data = type === 'invoice' ? VoiceTableData : CarTableData
    return {
      url,
      id,
      invoiceDataSource: rest.invoiceProductList ?? [rest],
      verifyResult: type === 'invoice' ? rest.verifyResult : rest.status,
      dataSource: data.map((v) => {
        return {
          name: v.title,
          content: v.matchOption ? (
            <MatchFormat value={rest[v.dataIndex]} matchOption={v.matchOption}></MatchFormat>
          ) : (
            rest[v.dataIndex]
          ),
        }
      }),
    }
  })
  const [current, setCurrent] = useState(0)
  const pre = () => {
    setCurrent(current - 1)
  }
  const next = () => {
    setCurrent(current + 1)
  }
  const { url, verifyResult, dataSource, invoiceDataSource, id } = listData[current] ?? {}
  return (
    <Modal title="比对结果" store={store} footer={null} width={1000}>
      <Space style={{ marginBottom: 12 }}>
        {current !== 0 && listData.length >= 1 && (
          <Button onClick={pre} icon={<LeftOutlined />}>
            上一张
          </Button>
        )}
        {current !== listData.length - 1 && listData.length >= 1 && (
          <Button onClick={next} icon={<RightOutlined />}>
            下一张
          </Button>
        )}
      </Space>

      <div key={url} style={{ width: '100%' }}>
        <ImageRender
          dataSource={dataSource}
          url={url}
          verifyResult={verifyResult}
          type={type}
          invoiceDataSource={invoiceDataSource}
        />
        <div style={{ display: 'flex', justifyContent: 'end', marginTop: 30 }}>
          <Button.Upload
            type="primary"
            onClick={() =>
              goOcr({ id: mainId, uploadType: 'reRowUpload', rowId: id, businessType: type })
            }
          >
            重新上传
          </Button.Upload>
          {type === 'invoice' && (
            <Button.Upload
              type="primary"
              style={{ marginLeft: 12 }}
              onClick={() =>
                goOcr({ id: mainId, businessType: type, rowId: id, uploadType: 'reVerify' })
              }
            >
              重新验真
            </Button.Upload>
          )}
        </div>
      </div>
    </Modal>
  )
}
export default observer(Index)
