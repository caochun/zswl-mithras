import { useState, useEffect } from "react";
import { InputNumber } from "antd";
import numeral from 'numeral';

const RATE = 10000;
const formatter = (value, precision) => {
    const valStr = value === null || value === undefined ? '' : String(value);
    const pureNumStr = valStr.replace(/[^\d.]/g, '');
    const dotIndex = pureNumStr.indexOf('.');
    const finalPureStr = dotIndex > -1 
        ? pureNumStr.slice(0, dotIndex + 1) + pureNumStr.slice(dotIndex).replace(/\./g, '') 
        : pureNumStr;
    const rawNum = Number(finalPureStr);
    const validNum = isNaN(rawNum) ? 0 : rawNum;
    const fixedNum = validNum.toFixed(precision);
    return fixedNum.replace(/\B(?=(\d{3})+(?!\d))/g, ',');
};

const parser = (value) => {
    if (!value || value === '') return 0;
    const pureStr = String(value).replace(/,/g, '');
    const num = Number(pureStr);
    return isNaN(num) ? 0 : num;
};
const convertToInnerValue = (outerVal) => {
    if (outerVal === null || outerVal === undefined || isNaN(Number(outerVal))) {
        return 0;
    }
    return numeral(outerVal).divide(RATE).value();
};

const convertToOuterValue = (innerVal) => {
    if (innerVal === null || innerVal === undefined || innerVal === '') {
        return 0;
    }
    const parsedNum = parser(innerVal);
    return numeral(parsedNum).multiply(RATE).value();
};
  const Index = ({ 
  value, 
  onChange, 
  className, 
  step, 
  precision = 2, 
  addonAfter, 
  disabled,
  maxLength
}) => {
    const [val, setVal] = useState(formatter(convertToInnerValue(value), precision));
    useEffect(() => {
        const targetInnerVal = formatter(convertToInnerValue(value), precision);
        if (targetInnerVal !== val) {
            setVal(targetInnerVal);
        }
    }, [value, precision]);
    const handleChange = (innerVal) => {
        setVal(innerVal);
    };

    const handleBlur = (e) => {
        const _innerVal = formatter(e.target.value, precision);
        setVal(_innerVal);
        const outerVal = convertToOuterValue(_innerVal);
        onChange && onChange(outerVal);
    };

    const handleFocus = (e) => {
        const _innerVal = parser(e.target.value);
        setVal(_innerVal);
    };

    return (
        <InputNumber
            step={step || 0.01}
            addonAfter={addonAfter}
            disabled={disabled}
            value={val}
            onChange={handleChange}
            className={className}
            onBlur={handleBlur}
            onFocus={handleFocus}
            precision={precision}
            style={{ width: '100%' }}
            placeholder="请输入"
            controls={false}
            maxLength={maxLength}
        />
    );
};

export default Index;