package com.agent.aiagent.demo.invoke;


import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;

public class  HttpApiInvoke {
    public static void main(String[] args) {
        String apiKey = System.getenv(TestApikey.API_KEY);

        JSONObject body = new JSONObject();
        body.set("model", "qwen-plus");

        JSONObject input = new JSONObject();
        input.set("messages", new Object[]{
                new JSONObject().set("role", "system").set("content", "You are a helpful assistant."),
                new JSONObject().set("role", "user").set("content", "你是谁？")
        });
        body.set("input", input);

        JSONObject parameters = new JSONObject();
        parameters.set("result_format", "message");
        body.set("parameters", parameters);

        HttpResponse response = HttpRequest.post("https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation")
                .header("Authorization", "Bearer " + TestApikey.API_KEY)
                .header("Content-Type", "application/json")
                .body(body.toString())
                .execute();

        System.out.println(response.body());
    }
}