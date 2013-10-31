/*
 * 文 件 名:  DBException.java
 * 描    述:  <描述>
 * 修 改 人:  y00109551
 * 修改时间:  2009-6-11
 * 跟踪单号:  <跟踪单号>
 * 修改单号:  <修改单号>
 * 修改内容:  <修改内容>
 */
package com.xwtec.util.db;

/**
 * <一句话功能简述> <功能详细描述>
 * 
 * @author y00109551
 * @version [版本号, 2009-6-11]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class DBException extends Exception
{
    /**
     * 注释内容
     */
    private static final long serialVersionUID = -3051748715160422241L;

    /**
     * 
     * [构造简要说明]
     * 
     * @param message message
     */
    public DBException(String message)
    {
        super(message);
    }

    /**
     * 
     * [构造简要说明]
     * 
     * @param message message
     * @param cause cause
     */
    public DBException(String message, Throwable cause)
    {
        super(message,
            cause);
    }

    /**
     * 
     * [构造简要说明]
     * 
     * @param cause cause
     */
    public DBException(Throwable cause)
    {
        super(cause);
    }
}
