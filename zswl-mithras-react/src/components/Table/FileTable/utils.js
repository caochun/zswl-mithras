// 在文件顶部定义常量
const OFFICE_SUFFIX = ['doc', 'docx', 'xls', 'xlsx', 'ppt', 'txt', 'wps']
// 提取工具函数
export const getFileExtension = (filename) => {
  const name = filename?.value || filename
  return name?.split('.').pop()
}
export const canEditFormFileExtension = (filename) => {
  const suffix = getFileExtension(filename)
  return OFFICE_SUFFIX.includes(suffix)
}
export const parts = (record) => {
  if (!record) {
    return ''
  }
  const parts = record?.name?.value ? record?.name?.value.split('.') : record?.name?.split('.')
  const extension = parts.pop() // 弹出最后一个元素
  // 处理可能的空字符串（如文件名以点结尾的情况）

  return parts.length > 0 && extension !== '' && parts.join('.')
}
export const getId = (id) => {
  return id?.value ?? id
}

export const getCanEdit = (canEditItem, canEdit, record) => {
  if (typeof canEditItem === 'function') {
    return canEditItem(record)
  } else if (typeof canEditItem === 'boolean') {
    return canEditItem
  } else {
    return canEdit
  }
}

export const getCanDelete = (canDelete, canEdit, record) => {
  if (typeof canDelete === 'function') {
    return canDelete(record)
  } else if (typeof canDelete === 'boolean') {
    return canDelete
  } else {
    return canEdit
  }
}

export const getRename = (rename, record) => {
  if (typeof rename === 'function') {
    return rename(record)
  } else if (typeof rename === 'boolean') {
    return rename
  }
}

export const getDownload = (canDownload, record) => {
  if (typeof canDownload === 'function') {
    return canDownload(record)
  } else if (typeof canDownload === 'boolean') {
    return canDownload
  }
}

export const getDisableFolder = (disableFolderAction, record) => {
  if (typeof disableFolderAction === 'function') {
    return disableFolderAction(record)
  } else if (typeof disableFolderAction === 'boolean') {
    return disableFolderAction
  }
}
