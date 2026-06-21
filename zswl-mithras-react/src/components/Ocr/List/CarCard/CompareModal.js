import IconFont from '@/components/Icon'
import { observer } from '@zswl/admin'
import { Button, Form, Modal, Select, Table, TableStore } from '@zswl/components'
import { Image, Input, Space, message } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import { LeftOutlined, RightOutlined } from '@ant-design/icons'
import { uniqueId } from 'lodash'
import vehicleCertificateApi from '@/api/ocr/vehicleCertificateApi'
import { DateColumn } from '@/components/Format'
import moment from 'moment'
import { rules } from '@/utils'
import { saveServer } from '@/utils'

export const goOcr = ({ id, businessType = 'invoice', uploadType, rowId, fileId }) => {
  const rowIdString = rowId ? `&rowId=${rowId}` : ''
  const fileIdString = fileId ? `&fileId=${fileId}` : ''
  window.open(
    `/ocr/recognition?id=${id}&businessType=${businessType}&uploadType=${uploadType}${rowIdString}${fileIdString}`
  )
}
const ImageRender = ({ currentData, form, editable, table, homePage, setHomePage }) => {
  const { url, verifyResult, dataSource } = currentData ?? {}
  const columns = [
    { dataIndex: 'name', title: '字段名', width: 40, editable: false },
    {
      dataIndex: 'content',
      title: '信息内容',
      editable: {
        required: true,
        rules: [{ required: true, message: '请输入信息内容' }],
      },
    },
  ]
  const notHomeColumns = [
    {
      dataIndex: 'name',
      title: '姓名/名称',
      editable: {
        required: true,
        rules: [{ required: true, message: '请输入姓名/名称' }],
      },
    },
    DateColumn({
      dataIndex: 'changeDate',
      title: '变更日期',
      editable: {
        required: true,
        rules: [{ required: true, message: '请输入变更日期' }],
      },

      render: (val) => {
        return val && moment(val).format('YYYY-MM-DD')
      },
    }),
    editable && {
      dataIndex: 'id',
      title: '操作',
      width: 100,
      actions: (record) => [
        {
          name: '删除',
          onClick: () => {
            table.deleteRow(record.id)
          },
        },
      ],
    },
  ]
  const carCardIconMap = {
    fail: 'icon-heyanshibai',
    yanzhenshibai: 'icon-yanzhenshibai',
    yanzhentongguo: 'icon-yanzhentongguo',
    failure: 'icon-shibieshibai',
    success: 'icon-shibiechenggong',
  }

  const resultIcon = carCardIconMap[verifyResult]

  const homePageChange = (value) => {
    setHomePage(value)
    const newValue = value ? defaultDataSource : [{ name: '', id: uniqueId() }]
    table.setList(newValue)
  }
  return (
    <Space>
      <Image src={url} width={400} />
      <div style={{ position: 'relative', width: 550 }}>
        <Form.Item
          label="是否首页（车证第1-2 页）"
          name={'isHomePage'}
          style={{ marginBottom: 12 }}
          required
        >
          <Select options={'trueOrFalse'} disabled={!editable} onChange={homePageChange}></Select>
        </Form.Item>
        <Form.Item
          label="机动车登记证书编号"
          name={'registrationPageNo'}
          style={{ marginBottom: 12 }}
          required
        >
          <Input disabled={!editable}></Input>
        </Form.Item>
        <Table
          columnsFilter={'list_CarCard_CompareModal'}
          onFilter={(key, val) => saveServer('list_CarCard_CompareModal', val)}
          columns={homePage ? columns : notHomeColumns}
          scroll={{ x: 'auto' }}
          store={table}
          pagination={false}
          columnWidth={180}
          editable={editable}
          extra={
            !homePage &&
            editable && (
              <Button onClick={() => table.addRow({ name: '', id: uniqueId() })}>新增</Button>
            )
          }
        ></Table>
        {!editable && (
          <IconFont
            type={resultIcon}
            style={{ position: 'absolute', bottom: 40, right: 50, fontSize: 120 }}
          />
        )}
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
const defaultDataSource = [
  { id: 1, name: '车牌号' },
  { id: 2, name: '机动车所有人' },
  { id: 3, name: '车架号' },
  { id: 4, name: '制造商' },
]

const formatList = (list) => {
  return list.map(({ url, id, isHomePage, ...rest }) => {
    return {
      url,
      id,
      invoiceDataSource: rest.invoiceProductList ?? [rest],
      verifyResult: rest.status,
      registrationPageNo: rest.registrationPageNo,
      isHomePage,
      fileId: rest.fileId,
      fileName: rest.fileName,
      dataSource: isHomePage
        ? CarTableData.map((v) => {
          return {
            id: uniqueId(),
            name: v.title,
            content: rest[v.dataIndex],
          }
        })
        : rest.changeRecordList?.map(({ name, changeDate }) => ({
          name,
          changeDate: changeDate && moment(changeDate),
          id: uniqueId(),
        })),
    }
  })
}
const OcrCarCardCompareModal = ({ store, type = 'invoice', mainId }) => {
  let initialValues = store?.compareModal?.getInitialValues() ?? []
  const [listData, setListData] = useState([])
  useEffect(() => {
    const newList = formatList(initialValues)
    setListData(newList)
  }, [JSON.stringify(initialValues)])

  const [current, setCurrent] = useState(0)
  const [form] = Form.useForm()
  const pre = () => {
    setCurrent(current - 1)
    setEditable(false)
  }
  const next = () => {
    setCurrent(current + 1)
    setEditable(false)
  }
  const { url, id, dataSource, fileId, fileName } = listData[current] ?? {}

  const [editable, setEditable] = useState(false)
  const table = useMemo(
    () => new TableStore({ request: () => dataSource, pagination: false }),
    [dataSource]
  )

  const save = async () => {
    const { registrationPageNo, isHomePage } = await form.validateFields()
    const params = {
      ids: [id],
      isChange: listData[current]?.isHomePage !== isHomePage,
      registrationPageNo,
      leaseholdId: mainId,
    }
    const { values, list } = await table.submit()
    if (isHomePage) {
      CarTableData.forEach((v, i) => {
        params[v.dataIndex] = list[i].content
      })
      params.fileId = fileId
      params.fileName = fileName
    } else {
      const changeRecordData = {
        fileId,
        fileName,
        changeRecordList: list.map(({ name, changeDate }) => ({
          name,
          changeDate: changeDate && moment(changeDate).format('YYYY-MM-DD'),
          id: uniqueId(),
        })),
      }

      params.changeRecordData = changeRecordData
    }

    await vehicleCertificateApi.postVehicleUpdate(params)

    message.success('更新成功')
    const newData = [...listData]

    newData[current] = {
      ...newData[current],
      registrationPageNo,
      isHomePage,
      dataSource: list,
    }
    setListData(newData)
    setEditable(false)
    store.table.search()
  }
  const deleteRow = async () => {
    const { isHomePage, id, fileId } = listData[current] ?? {}
    const params = {
      id,
      changeRecordRspList: isHomePage ? [] : [{ fileId }],
    }
    const operateType = isHomePage ? 'DELETE_HOME_PAGE' : 'DELETE_CHANGE_RECORD'
    await store.delete(params, operateType, false)
    const newData = [...listData]
    newData.splice(current, 1)
    if (newData.length === 0) {
      store.compareModal.close()
      store.table.search()
    }
    setListData(newData)
    setCurrent(current >= 1 ? current - 1 : 0)
  }
  const [homePage, setHomePage] = useState(false)

  useEffect(() => {
    cancel()
  }, [JSON.stringify(listData[current])])

  const cancel = () => {
    const { registrationPageNo, isHomePage, dataSource } = listData[current] ?? {}
    form.setFieldsValue({ registrationPageNo, isHomePage })
    table.setList(dataSource)
    setHomePage(isHomePage)
    setEditable(false)
  }
  const onCancel = () => {
    store.compareModal.close()
    setCurrent(0)
  }
  return (
    <Modal
      title="比对结果"
      store={store.compareModal}
      footer={null}
      width={1000}
      destroyOnClose
      onCancel={onCancel}
    >
      <Form form={form}>
        <div key={url} style={{ width: '100%' }}>
          <ImageRender
            currentData={listData[current]}
            editable={editable}
            form={form}
            setHomePage={setHomePage}
            homePage={homePage}
            table={table}
          />
          <Space style={{ display: 'flex', justifyContent: 'end', marginTop: 30 }}>
            <Button.Delete type="primary" onClick={() => deleteRow(id)} confirm>
              删除
            </Button.Delete>
            <Button.Upload
              type="primary"
              onClick={() =>
                goOcr({
                  id: mainId,
                  uploadType: 'reRowUpload',
                  rowId: id,
                  businessType: type,
                  fileId: !listData[current]?.isHomePage && listData[current].fileId,
                })
              }
            >
              重新上传
            </Button.Upload>
            {!editable && (
              <Button type="primary" onClick={() => setEditable(true)}>
                编辑
              </Button>
            )}

            {editable && (
              <Button
                type="primary"
                onClick={async () => {
                  await save()
                }}
              >
                保存
              </Button>
            )}
            {editable && <Button onClick={cancel}>取消</Button>}
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
        </div>
      </Form>
    </Modal>
  )
}
export default observer(OcrCarCardCompareModal)
