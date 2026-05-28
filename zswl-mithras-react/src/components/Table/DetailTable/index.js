import { Table, Button, TableStore } from '@zswl/components'
import { observer } from '@zswl/admin'
import DataUpload from '@/components/DataUpload'
import TableExport from '@/components/Actions/TableExport'
import { useEffect, useMemo, useState } from 'react'
import moment from 'moment'
import { message, Space } from 'antd'
import { Modal } from '@zswl/components'
import { create, all } from 'mathjs'
const mathjs = create(all)

/**
 * 扁平化处理列配置，展开有children的列
 * @param {Array} columns - 列配置数组
 * @returns {Array} 扁平化后的列配置
 */
const flatColumns = (columns) => {
  return columns?.flatMap((item) => {
    if (item.children) {
      return flatColumns(item.children)
    }
    return item
  })
}

/**
 * 详情表格组件
 * @param {Object} props - 组件属性
 * @param {Object} props.store - 数据存储对象
 * @param {Array} props.columns - 表格列配置
 * @param {string} props.title - 表格标题
 * @param {Function} props.modifyApi - 修改数据的API
 * @param {boolean} props.needAdd - 是否需要新增功能
 * @param {string} props.unit - 数据单位
 * @param {string} props.listType - 列表类型
 * @param {string} props.currentId - 当前记录ID
 */
const DetailTable = observer(
  ({ columns, title, onSave, needAdd = true, unit, onImport, onDownload, canEdit, request }) => {
    const [cacheForm, setCacheForm] = useState([])
    const [editable, setEditable] = useState(false)

    const pageChange = (page, pageSize) => {
      if (!editable) return
      const { current: currentPage, pageSize: currentPageSize } = store?.getPagination()
      const { list, values } = store?.getEditorData()
      const changeList = list.slice(
        (currentPage - 1) * currentPageSize,
        currentPage * currentPageSize
      )
      const newCache = [...cacheForm]
      changeList.forEach((item) => {
        const index = newCache.findIndex((v) => v.id === item.id)
        if (index !== -1) {
          newCache[index] = { ...newCache[index], ...item }
        } else {
          newCache.push(item)
        }
      })
      setCacheForm(newCache)
      return newCache
    }
    const store = useMemo(
      () =>
        new TableStore({
          request: async () => {
            const data = await request()
            return data
          },
        }),
      [request]
    )
    useEffect(() => {
      const isForm = needAdd === false
      store.setPagination(
        isForm
          ? false
          : {
              onChange: pageChange,
            }
      )
    }, [needAdd, request, editable, cacheForm])
    /**
     * 处理保存操作
     */
    const handleSave = async () => {
      // 扁平化处理，有children的需要展开
      const flattenedColumns = flatColumns(store?.getOptimizedColumns())
      const newCache = pageChange()
      const { values, list } = await store?.submit()

      const newParams = list.map((v) => {
        const findCache = newCache.find((item) => item.id === v.id)
        if (findCache) {
          const result = {}
          Object.keys(findCache).forEach((key) => {
            const { _columnType, initFormat } =
              flattenedColumns.find((item) => item.dataIndex === key) ?? {}

            if (_columnType === 'amount' && findCache[key]) {
              result[key] = mathjs.multiply(mathjs.bignumber(findCache[key]), initFormat).toString()
            } else if (_columnType === 'date' && findCache[key]) {
              result[key] = moment(findCache[key]).format('YYYY-MM-DD')
            } else {
              result[key] = findCache[key]
            }
          })
          return result
        } else {
          return v
        }
      })
      await onSave({ dataList: newParams })
      store.search()
      message.success('修改成功')
      setEditable(false)
    }

    /**
     * 处理取消操作
     */
    const handleCancel = () => {
      setEditable(false)
    }

    /**
     * 处理编辑操作
     */
    const handleEdit = () => {
      setEditable(true)
    }

    const add = async () => {
      store?.addRow({})
    }
    const deleteRow = async ({ id }) => {
      store?.deleteRow(id)
    }
    const importData = async (values) => {
      console.log('values: ', values)
      await onImport(values)
      store.search()
    }
    return (
      <div style={{ height: '100%' }}>
        {unit && (
          <div style={{ marginBottom: 16, textAlign: 'right' }}>
            <span style={{ marginRight: 8 }}>单位：{unit}</span>
          </div>
        )}

        <div className="z-flex-jsb" style={{ marginBottom: 16 }}>
          <div style={{ display: 'flex', gap: 8 }}>
            {canEdit && (
              <DataUpload accept=".xlsx" maxCount={1} api={importData} buttonText="导入" />
            )}
            <Button.Download onClick={onDownload}>模板下载</Button.Download>
            <TableExport table={store} otherExcelProps={{ fileName: `${title}_报表数据` }} />
          </div>

          {canEdit && (
            <Space>
              {needAdd && editable && (
                <Button type="primary" onClick={add}>
                  新增
                </Button>
              )}
              {!editable && (
                <Button type="primary" onClick={handleEdit}>
                  编辑
                </Button>
              )}
              {editable && (
                <Button type="primary" onClick={handleCancel}>
                  取消
                </Button>
              )}
              {editable && (
                <Button type="primary" onClick={handleSave}>
                  保存
                </Button>
              )}
            </Space>
          )}
        </div>

        <Table
          columns={[
            ...columns,
            needAdd &&
              canEdit &&
              editable && {
                title: '操作',
                dataIndex: 'operation',
                width: 100,
                actions: [
                  {
                    name: '删除',
                    onClick: deleteRow,
                  },
                ],
              },
          ].filter(Boolean)}
          store={store}
          serial={{
            render: (v, r, index) => {
              return r.rowNum ?? index + 1
            },
          }}
          rowKey={'id'}
          resizable
          columnWidth={120}
          editable={editable}
          bordered
          scroll={{ x: 'max-content' }}
        />
      </div>
    )
  }
)

export default DetailTable
