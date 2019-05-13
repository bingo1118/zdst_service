/**
 * обнГ5:01:55
 */
package com.cloudfire.entity;

/**
 * @author cheng
 *2017-6-7
 *обнГ5:01:55
 */
public class City {
    private Integer id;

    private String code;

    private String name;

    private String province;

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

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province == null ? null : province.trim();
    }
}