export const dateRangeTransform = (
  dateRange,
  startField,
  endField,
  format = 'yyyy-MM-DD',
  dataIndex
) => {
  const [startDataTime, endDataTime] = dateRange || []

  return {
    [dataIndex]: undefined,
    [startField]: startDataTime?.format(format),
    [endField]: endDataTime?.format(format),
  }
}

export const dateRangeTransformV2 = (value, dataIndex, format = 'yyyy-MM-DD') => {
  const [startDataTime, endDataTime] = value || []
  return {
    [dataIndex]: undefined,
    [`${dataIndex}From`]: startDataTime?.format(format),
    [`${dataIndex}To`]: endDataTime?.format(format),
  }
}

export const dateTransform = (value, dataIndex, format = 'yyyy-MM-DD') => {
  return {
    [dataIndex]: value ? moment(value)?.format(format) : undefined,
  }
}
