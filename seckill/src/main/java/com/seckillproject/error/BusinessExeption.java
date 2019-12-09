package com.seckillproject.error;

//包装器业务异常类实现
public class BusinessExeption  extends  Exception implements  CommonError{

    private CommonError commonError;

    //直接接收EmBusinessError的传参用于构造业务异常
    public BusinessExeption(CommonError commonError){
        super();//Exeption类自身有初始化机制，因此需要
        this.commonError=commonError;
    }
    //接收自定义errMsg的方式构造业务异常
    public BusinessExeption(CommonError commonError,String errMsg){
        super();
        this.commonError=commonError;
        this.commonError.setErrMsg(errMsg);
    }


    @Override
    public int getErrCode() {
        return this.commonError.getErrCode();
    }

    @Override
    public String getErrMsg() {
        return this.commonError.getErrMsg();
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.commonError.setErrMsg(errMsg);
        return this;
    }
}
