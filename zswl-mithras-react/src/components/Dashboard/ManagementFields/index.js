import { Popover, Button, Switch } from 'antd'
import { useEffect, useMemo, useState } from 'react'
import { chunk, isBoolean, isEmpty } from 'lodash'
import { getLocalColumnsFilter, setLocalColumnsFilter } from '@/utils'
import styles from './index.less'
import userCustomConfigApi from '@/api/dashboard/userCustomConfigApi'

/**
 * 字段本地存储面板
 * @param {*Object} initFieldsConfig 面板初始化数据
 * @param {*String} columnsFilterKey 存储数据的key
 * @param {*Function} onChange change事件
 * @returns
 */

const DashboardManagementFields = ({ initFieldsConfig, onChange, columnsFilterKey }) => {
  const [fieldsList, setFieldsList] = useState([])
  const [open, setOpen] = useState(false)

  const titleKeys = useMemo(() => {
    return initFieldsConfig?.map((item) => {
      return {
        group: item.group,
        groupCode: item.groupCode,
        show: true,
      }
    })
  }, [initFieldsConfig])

  useEffect(() => {
    const local = getLocalColumnsFilter(columnsFilterKey)
    setFieldsList(isEmpty(local) ? titleKeys : local)
  }, [titleKeys, columnsFilterKey])

  const saveServer = async (value) => {
    await userCustomConfigApi.saveCustomConfig({
      configKey: columnsFilterKey,
      configValue: JSON.stringify(value),
    })
    // setLocalColumnsFilter(columnsFilterKey, fieldsList)
  }
  const onFieldsChange = (checked, item) => {
    const newFieldsList = fieldsList.map((field) => {
      if (field.groupCode === item.groupCode) {
        item.show = checked
      }
      return field
    })
    setFieldsList(newFieldsList)
    saveServer(newFieldsList)
    setLocalColumnsFilter(columnsFilterKey, newFieldsList)

    onChange?.({
      checked,
      fieldInfo: item,
      allFieldsList: newFieldsList,
    })
  }

  const chunkedArray = chunk(fieldsList, 2)

  return (
    <Popover
      content={
        <div className={styles.wrap}>
          {chunkedArray.map((chunkItem, index) => {
            return (
              <div className={styles.rowWrap} key={index}>
                {chunkItem.map((item, index) => {
                  return (
                    <div className={styles.row}>
                      <Switch
                        id={`${item.groupCode}_${index}`}
                        checked={isBoolean(item.show) ? item.show : true}
                        onChange={(checked) => onFieldsChange(checked, item)}
                      ></Switch>
                      <label for={`${item.groupCode}_${index}`} className={styles.label}>
                        {item.group}
                      </label>
                    </div>
                  )
                })}
              </div>
            )
          })}
        </div>
      }
      trigger="click"
      placement="leftTop"
      destroyTooltipOnHide
      open={open}
      onOpenChange={setOpen}
    >
      <Button type="link">管理</Button>
    </Popover>
  )
}

export default DashboardManagementFields
