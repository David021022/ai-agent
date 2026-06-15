/*
package com.agent.aiagent.tools;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WebSearchTool {


    private static final String SEARCH_API_URL = "https://www.searchapi.io/api/v1/search";

    private final String apiKey;

    public WebSearchTool(String apiKey) {
        this.apiKey = apiKey;
    }

    @Tool(description = "Search for information from Baidu Search Engine")
    public String searchWeb(
            @ToolParam(description = "Search query keyword") String query) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("q", query);
        paramMap.put("api_key", apiKey);
        paramMap.put("engine", "baidu");
        try {
            String response = HttpUtil.get(SEARCH_API_URL, paramMap);

            JSONObject jsonObject = JSONUtil.parseObj(response);

            JSONArray organicResults = jsonObject.getJSONArray("organic_results");
            List<Object> objects = organicResults.subList(0, 5);

            String result = objects.stream().map(obj -> {
                JSONObject tmpJSONObject = (JSONObject) obj;
                return tmpJSONObject.toString();
            }).collect(Collectors.joining(","));
            return result;
        } catch (Exception e) {
            return "Error searching Baidu: " + e.getMessage();
        }
    }
}*/
package com.agent.aiagent.tools;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class WebSearchTool {

    private static final String SEARCH_API_URL = "https://run.xcrawl.com/v1/search";

    private final String apiKey;

    public WebSearchTool(String apiKey) {
        this.apiKey = apiKey;
    }

    @Tool(description = "Search web pages using XCrawl")
    public String searchWeb(
            @ToolParam(description = "Search query keyword") String query) {
        try {
            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("query", query);
            bodyMap.put("location", "US");
            bodyMap.put("language", "en");
            bodyMap.put("limit", 5);

            String response = HttpRequest.post(SEARCH_API_URL)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .body(JSONUtil.toJsonStr(bodyMap))
                    .timeout(15000)
                    .execute()
                    .body();

            JSONObject root = JSONUtil.parseObj(response);

            JSONArray results = null;
            JSONObject dataObj = root.getJSONObject("data");
            if (dataObj != null) {
                results = dataObj.getJSONArray("data");
            }
            if (results == null) {
                results = root.getJSONArray("data");
            }

            if (results == null || results.isEmpty()) {
                return "No results found.";
            }

            final JSONArray finalResults = results;
            int count = Math.min(finalResults.size(), 5);
            return IntStream.range(0, count)
                    .mapToObj(i -> {
                        JSONObject item = finalResults.getJSONObject(i);
                        String title = item.getStr("title");
                        String url = item.getStr("url");
                        String description = item.getStr("description");
                        return String.format(
                                "title=%s\nurl=%s\ndescription=%s",
                                title, url, description
                        );
                    })
                    .collect(Collectors.joining("\n\n"));
        } catch (Exception e) {
            return "Error searching web: " + e.getMessage();
        }
    }
}
