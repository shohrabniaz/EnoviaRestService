/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.bjit.common.rest.app.service.model.mvEnoviaItem;

/**
 *
 * @author BJIT
 */
public class MVInfo {

    private String type;
    private String name;
    private String revision;
    private String life_cycle_status;
    private String aton_version;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRevision() {
        return revision;
    }

    public void setRevision(String revision) {
        this.revision = revision;
    }

    public String getLife_cycle_status() {
        return life_cycle_status;
    }

    public void setLife_cycle_status(String life_cycle_status) {
        this.life_cycle_status = life_cycle_status;
    }

    public String getAton_version() {
        return aton_version;
    }

    public void setAton_version(String aton_version) {
        this.aton_version = aton_version;
    }

    @Override
    public String toString() {
        return "MVInfo{" + "type=" + type + ", name=" + name + ", revision=" + revision + ", life_cycle_status=" + life_cycle_status + ", aton_version=" + aton_version + '}';
    }

}
