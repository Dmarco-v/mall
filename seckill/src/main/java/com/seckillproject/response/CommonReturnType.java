package com.seckillproject.response;

public class CommonReturnType {
    //表明对应请求的返回结果状态"success" 或"fail"
    private String status;

    //如果status=success，data返回前端需要的数据
    //如果status=fail，data返回统一的错误码
    private Object data;

    //定义通用构造方法
    public static CommonReturnType create(Object result){
        return CommonReturnType.create(result,"success");
    }
    public static CommonReturnType create(Object result,String status){
        CommonReturnType type=new CommonReturnType();
        type.setStatus(status);
        type.setData(result);
        return type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
