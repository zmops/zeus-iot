package com.zmops.iot.model.cache;


import com.google.common.collect.Table;

import java.util.Map;

/**
 * @author yefei
 **/
public class DictionaryCache extends AbstractCache<String, Map<String, String>> {

    public DictionaryCache() {
        super();
    }

    public String getNameByVal(String flag, String value) {
        Map<String, String> dic = get(flag);
        if (dic == null) {
            return null;
        }
        return dic.get(value);
    }

    public void updateDictionaries(Table<String, String, String> dictionaries) {
        update(dictionaries.rowMap());
    }


}
