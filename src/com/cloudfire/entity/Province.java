/**
 * ����2:28:22
 */
package com.cloudfire.entity;

/**
 * @author cheng
 *2017-6-7
 *����2:28:22
 */
public class Province {
    private Integer id;

    private String code;

    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }
}