package com.xszx.util.db;

/**
 * 事务操作封装类
 */
public class TransactionOperation {
    private String sql;
    private Object[] params;

    public TransactionOperation() {
    }

    public TransactionOperation(String sql, Object... params) {
        this.sql = sql;
        this.params = params;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    @Override
    public String toString() {
        return "TransactionOperation{" +
                "sql='" + sql + '\'' +
                ", paramsCount=" + (params != null ? params.length : 0) +
                '}';
    }
}
