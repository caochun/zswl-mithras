import { useState, useCallback, useEffect, useMemo } from 'react';
import { Select, Spin } from 'antd';
import Api from '@/api/groupCredit/common'
import './style.less'

let timeout;
let currentValue;
const fetch = (value, callback) => {
//   if (timeout) {
//     clearTimeout(timeout);
//     timeout = null;
//   }
//   currentValue = value;
//   const fake = () => {
//     const str = qs.stringify({
//       code: 'utf-8',
//       q: value,
//     });
//     jsonp(`https://suggest.taobao.com/sug?${str}`)
//       .then((response) => response.json())
//       .then((d) => {
//         if (currentValue === value) {
//           const { result } = d;
//           const data = result.map((item) => ({
//             value: item[0],
//             text: item[0],
//           }));
//           callback(data);
//         }
//       });
//   };
//   timeout = setTimeout(fake, 300);
};
const SearchInput = ({
    params,
    mode,
    functionCode = 'assetclassifyqueryclientlist',
    canJump = true,
    enterpriseName,
    isHymx = false,
    transformResult = (v) => v,
    setHymxValue,
    clearClientCode,
    ...rest
}) => {
    const { onChange, ...otherRest } = rest ?? {}
    const [list, setList] = useState([])
    const [loading, setLoading] = useState(false)

    useEffect(() => {
        getList();
    }, [JSON.stringify(params), isHymx]);
    const handleSearch = async(newValue) => {
        if (newValue) {
            getList(newValue);
        }
    };
    const handleChange = (val) => {
        onChange?.(val)
    };
    const getList = async (val) => {
        const res = await Api.getClientList(
        {
            clientName: val,
            effected: true, // 只选择已生效客户
            pageSize: 9999,
            containHymx:isHymx,
            ...params,
        },
        {
            functionCode,
        }
        )
        const newRes = transformResult?.(res.list, val)
        if (!newRes || newRes.length === 0) {
            setHymxValue(val)
            clearClientCode(true)
            return [];
        }
        setList(newRes)
        setLoading(false)
    }
    const onKeyDown = (e) => {
        e.stopPropagation()
        if (e.key === 'Enter') {

        }
    }
    const onBlur = () => {
        getList()
        clearClientCode()
    }
    const onSelect = () => {
        list.length < 2 && getList()
    }
    return (
        <Select
            allowClear
            showSearch
            placeholder="请选择"
            defaultActiveFirstOption={false}
            showArrow={false}
            filterOption={false}
            onSearch={handleSearch}
            onSelect={onSelect}
            onChange={handleChange}
            onKeyDown={onKeyDown}
            onBlur={onBlur}
            fieldNames={{ label: 'clientName', value: 'id' }}
            notFoundContent={null}
            maxTagCount={50}
            listHeight={180}
            onClear={clearClientCode}
            dropdownRender={(node) => {
                if (loading) {
                    return (
                        <div className="z-select-loading">
                        <Spin />
                        </div>
                    )
                }
                return node
            }}
            options={list}
            {...rest}
        />
    );
};

export default SearchInput;