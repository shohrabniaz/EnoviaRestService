//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.11.30 at 05:51:25 PM BDT 
//


package com.bjit.plmkey.ws.controller.expandobject;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="revision" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="userID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="password" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="securityContext" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="outputFileFormat" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="recursionLevel" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="serviceName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="attributeList">
 *           &lt;simpleType>
 *             &lt;list>
 *               &lt;simpleType>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                   &lt;pattern value="(\S+)*|(\s*,\s*(\S+)*)|((\S+)*\s*,\s*)"/>
 *                 &lt;/restriction>
 *               &lt;/simpleType>
 *             &lt;/list>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="typeList">
 *           &lt;simpleType>
 *             &lt;list>
 *               &lt;simpleType>
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                   &lt;pattern value="(\S+)*|(\s*,\s*(\S+)*)|((\S+)*\s*,\s*)"/>
 *                 &lt;/restriction>
 *               &lt;/simpleType>
 *             &lt;/list>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "type",
    "name",
    "revision",
    "userID",
    "password",
    "securityContext",
    "outputFileFormat",
    "recursionLevel",
    "serviceName",
    "attributeList",
    "typeList"
})
@XmlRootElement(name = "expandObjectRequest")
public class ExpandObjectRequest {

    @XmlElement(required = true)
    protected String type;
    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected String revision;
    @XmlElement(required = true)
    protected String userID;
    @XmlElement(required = true)
    protected String password;
    @XmlElement(required = true)
    protected String securityContext;
    @XmlElement(required = true, defaultValue = "XML/JSON")
    protected String outputFileFormat;
    protected int recursionLevel;
    @XmlElement(required = true)
    protected String serviceName;
    @XmlList
    @XmlElement(required = true, defaultValue = "objectId , depth , type , name , revision , description , state , owner , Originator , Marketing_Text , Marketing_Name , MOD_Stacks , Equipment_List_Report_XML , Synopsis , International_Birth_Date , Orphan_Drug , Strength , Notes , Base_Price , Responsible_Design_Engineer , Responsible_Product_Manager , Start_Effectivity , End_Effectivity , Web_Availability , Node_Index , Derivation_Level , Child_Node_Available_Index , Current_Version , Is_Version , Subfeature_Count , Display_Name , Display_Text , Duplicate_Part_XML , Leaf_Level , Logical_Selection_Type , Configuration_Selection_Type , Key-In_Type , Key-In_Value , Title , clau , Is_Version_Object , Move_Files_To_Version , Suspend_Versioning , Designated_User , Access_Type , Checkin_Reason , Language , Version , Primary_Key , Secondary_Keys , File_Version , Version_Date , CAD_Type , Auto_Stop_On_Rejection , Restrict_Members , Sub_Route_Visibility , Current_Route_Node , Route_Instructions , Restart_Upon_Task_Rejection , Co-Owners , Route_Completion_Action , Route_Status , Prefix , Last_Build_Unit_Number , Platform_Management , Series_Count , Model_Number , CatalogNumber , Login_Type , Address , Preferences , Distinguished_Name , Last_Login_Date , Licensed_Hours , JT_Viewer_Type , Icon_Mail , Host_Meetings , Meeting_Key , Meeting_Password , Meeting_Username , Subscription_Level , Absence_Delegate , Absence_End_Date , Absence_Start_Date , File_Store_Symbolic_Name , City , IMDS_Contact_ID , Cell_Phone_Number , Mail_Code , Country , Email_Address , Fax_Number , First_Name , Home_Phone_Number , Last_Name , Middle_Name , Pager_Number , Postal_Code , Region , Web_Site , Work_Phone_Number , Cage_Code , DUNS_Number , Division , Organization_Fax_Number , Organization_ID , Organization_Name , Organization_Phone_Number , Standard_Cost , File_Site , FTP_Directory , FTP_Host , Default_Policy , Meeting_Site_ID , Meeting_Site_Name , Alternate_Name , IMDS_Company_ID , Secondary_Vaults , JAMP_Company_ID , Address1 , Address2 , Plant_ID , Plant_Time_Zone , Location_Code , Region , Non_Production_Days , PnO_Solution , PnO_Family , Security_Level , PnO_Visibility , Child_Marketing_Name , Child_Object_Name , Committed , Component_Location , Derived_Context , Document_Classification , Feature_Allocation_Type , Find_Number , Force_Part_Reuse , Logical_Selection_Criteria , Parent_Marketing_Name , Parent_Object_Name , Quantity , Reference_Designator , Route_Base_Policy , Route_Base_Purpose , Route_Base_State , Rule_Type , Sequence_Order , Usage , Master_Composition , Classification_Path")
    protected List<String> attributeList;
    @XmlList
    @XmlElement(required = true, defaultValue = "Product_Line , Model")
    protected List<String> typeList;

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the revision property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRevision() {
        return revision;
    }

    /**
     * Sets the value of the revision property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRevision(String value) {
        this.revision = value;
    }

    /**
     * Gets the value of the userID property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserID() {
        return userID;
    }

    /**
     * Sets the value of the userID property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserID(String value) {
        this.userID = value;
    }

    /**
     * Gets the value of the password property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the value of the password property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPassword(String value) {
        this.password = value;
    }

    /**
     * Gets the value of the securityContext property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSecurityContext() {
        return securityContext;
    }

    /**
     * Sets the value of the securityContext property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSecurityContext(String value) {
        this.securityContext = value;
    }

    /**
     * Gets the value of the outputFileFormat property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOutputFileFormat() {
        return outputFileFormat;
    }

    /**
     * Sets the value of the outputFileFormat property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOutputFileFormat(String value) {
        this.outputFileFormat = value;
    }

    /**
     * Gets the value of the recursionLevel property.
     * 
     */
    public int getRecursionLevel() {
        return recursionLevel;
    }

    /**
     * Sets the value of the recursionLevel property.
     * 
     */
    public void setRecursionLevel(int value) {
        this.recursionLevel = value;
    }

    /**
     * Gets the value of the serviceName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Sets the value of the serviceName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceName(String value) {
        this.serviceName = value;
    }

    /**
     * Gets the value of the attributeList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the attributeList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttributeList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getAttributeList() {
        if (attributeList == null) {
            attributeList = new ArrayList<String>();
        }
        return this.attributeList;
    }

    /**
     * Gets the value of the typeList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the typeList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTypeList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getTypeList() {
        if (typeList == null) {
            typeList = new ArrayList<String>();
        }
        return this.typeList;
    }

}
