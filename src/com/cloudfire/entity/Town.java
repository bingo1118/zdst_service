/**
 * ионГ10:55:16
 */
package com.cloudfire.entity;

/**
 * @author cheng
 *2017-6-8
 *ионГ10:55:16
 */
public class Town {
    private Integer id;

    private String code;

    private String name;

    private String city;

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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city == null ? null : city.trim();
    }
}
