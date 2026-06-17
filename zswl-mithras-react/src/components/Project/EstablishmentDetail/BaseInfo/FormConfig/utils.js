export const getKeyOptionsLabelMap = (options, key) => {
  const obj = {}
  if (options && options[key]) {
    options[key].forEach((item) => {
      const { label, value } = item
      obj[value] = label
    })
  }
  return obj
}

export const labelRed = (val) => {
  return { color: val ? 'red' : undefined }
}
