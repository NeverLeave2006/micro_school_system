package com.sgj.microschoolsystem.model;

public class ManagerTable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column manager_table.manager_id
     *
     * @mbg.generated Tue May 28 18:44:11 CST 2019
     */
    private String managerId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column manager_table.name
     *
     * @mbg.generated Tue May 28 18:44:11 CST 2019
     */
    private String name;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column manager_table.shcool_name
     *
     * @mbg.generated Tue May 28 18:44:11 CST 2019
     */
    private String shcoolName;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column manager_table.manager_id
     *
     * @return the value of manager_table.manager_id
     *
     * @mbg.generated Tue May 28 18:44:11 CST 2019
     */
    public String getManagerId() {
        return managerId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column manager_table.manager_id
     *
     * @param managerId the value for manager_table.manager_id
     *
     * @mbg.generated Tue May 28 18:44:11 CST 2019
     */
    public void setManagerId(String managerId) {
        this.managerId = managerId == null ? null : managerId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column manager_table.name
     *
     * @return the value of manager_table.name
     *
     * @mbg.generated Tue May 28 18:44:11 CST 2019
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column manager_table.name
     *
     * @param name the value for manager_table.name
     *
     * @mbg.generated Tue May 28 18:44:11 CST 2019
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column manager_table.shcool_name
     *
     * @return the value of manager_table.shcool_name
     *
     * @mbg.generated Tue May 28 18:44:11 CST 2019
     */
    public String getShcoolName() {
        return shcoolName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column manager_table.shcool_name
     *
     * @param shcoolName the value for manager_table.shcool_name
     *
     * @mbg.generated Tue May 28 18:44:11 CST 2019
     */
    public void setShcoolName(String shcoolName) {
        this.shcoolName = shcoolName == null ? null : shcoolName.trim();
    }
}