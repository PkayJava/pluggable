package com.angkorteam.pluggable.plugin.query.json;

import java.util.List;
import java.util.Map;

import com.angkorteam.pluggable.framework.doc.ApiObject;
import com.angkorteam.pluggable.framework.json.HttpMessage;

@ApiObject
public class ExportResultsResponse extends
        HttpMessage<List<Map<String, Object>>> {

    /**
     * 
     */
    private static final long serialVersionUID = -8543306980479829898L;

    public ExportResultsResponse(int code, String message,
            List<Map<String, Object>> content) {
        super(code, message, content);
    }
}
