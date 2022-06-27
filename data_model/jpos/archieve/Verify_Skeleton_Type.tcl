#############################################################
# @Name: Verify skeleton object type
# @Type: VAL_VALComponent, VAL_VALComponentMaterial, Document
# @Version: 1.0
# @Created By: BJIT Ltd.
# @Release: 
#############################################################
tcl;
eval {
	set fp_LogFile [open "varifyType.log" "w"]
	set is_error_found "false"
	
	proc writeLog { fp_LogFile message } {
			set currenttime [clock format [clock seconds] -format "%m/%d/20%y %A %I:%M:%S %p"]
			set outputmessage [ append "Time : " $currenttime " : " $message]
			puts "$outputmessage"
			puts $fp_LogFile $outputmessage
			flush $fp_LogFile
	}
	
	proc check_VAL_VALComponent_TypeAndAttribute { fp_LogFile } {  
	
		set is_error_found "false"
		
		set project "GLOBAL_COMPONENTS_INTERNAL"
		set organization "VALMET_INTERNAL"
		set interface_MBOM_MBOMERP "MBOM_MBOMERP"
		set interface_TRS_ShortEnglish "TRS_ShortEnglish"
		set interface_TRS_TermID "TRS_TermID"
		set interface_DELAsmUnitRefRequired "DELAsmUnitRefRequired"
		set interface_DELAsmLotRefRequired "DELAsmLotRefRequired"
		set interface_MBOM_MBOMReference "MBOM_MBOMReference"	
		
		
		set valComponentID_temp [mql temp query bus "VAL_VALComponent" "skeletonID_do_not_delete" * select id project organization interface dump |]
		
		
		if { $valComponentID_temp == "" } {
			set is_error_found "true"
			writeLog $fp_LogFile "Error ::: No skeleton object found for type VAL_VALComponent with name skeletonID_do_not_delete"
		} else {
			set valComponentID_temp_split [split $valComponentID_temp "|"]
			set ls_valComponent_temp $valComponentID_temp_split
			
			set valComponent_project [lindex $valComponentID_temp_split 4]
			if { $valComponent_project == $project} {
				writeLog $fp_LogFile "Project found for VAL_VALComponent"
			} else {
				set is_error_found "true"
				writeLog $fp_LogFile "Error ::: Correnct Project not found for VAL_VALComponent"
			}
			
			set valComponent_organization [lindex $valComponentID_temp_split 5]	
			if { $valComponent_organization == $organization} {
				writeLog $fp_LogFile "Organization found for VAL_VALComponent"
			} else {
				set is_error_found "true"
				writeLog $fp_LogFile "Error ::: Corrent Organization not found for VAL_VALComponent"
			}
			
			if { [lsearch $ls_valComponent_temp $interface_MBOM_MBOMERP] != -1 } {
				writeLog $fp_LogFile "MBOM_MBOMERP found for VAL_VALComponent"
			} else {
				set is_error_found "true"
				writeLog $fp_LogFile "Error ::: MBOM_MBOMERP not found for VAL_VALComponent"
			}
			
			if { [lsearch $ls_valComponent_temp $interface_TRS_ShortEnglish] != -1 } {
				writeLog $fp_LogFile "TRS_ShortEnglish found for VAL_VALComponent"
			} else {
				set is_error_found "true"
				writeLog $fp_LogFile "Error ::: TRS_ShortEnglish not found for VAL_VALComponent"
			}
			
			if { [lsearch $ls_valComponent_temp $interface_TRS_TermID] != -1 } {
				writeLog $fp_LogFile "TRS_TermID found for VAL_VALComponent"
			} else {
				set is_error_found "true"
				writeLog $fp_LogFile "Error ::: TRS_TermID not found for VAL_VALComponent"
			}
			
			if { [lsearch $ls_valComponent_temp $interface_DELAsmUnitRefRequired] != -1 } {
				writeLog $fp_LogFile "DELAsmUnitRefRequired found for VAL_VALComponent"
			} else {
				set is_error_found "true"
				writeLog $fp_LogFile "Error ::: DELAsmUnitRefRequired not found for VAL_VALComponent"
			}
			
			if { [lsearch $ls_valComponent_temp $interface_DELAsmLotRefRequired] != -1 } {
				writeLog $fp_LogFile "DELAsmLotRefRequired found for VAL_VALComponent"
			} else {
				set is_error_found "true"
				writeLog $fp_LogFile "Error ::: DELAsmLotRefRequired not found for VAL_VALComponent"
			}
			
			if { [lsearch $ls_valComponent_temp $interface_MBOM_MBOMReference] != -1 } {
				writeLog $fp_LogFile "MBOM_MBOMReference found for VAL_VALComponent"
			} else {
				set is_error_found "true"
				writeLog $fp_LogFile "Error ::: MBOM_MBOMReference not found for VAL_VALComponent"
			}	
		}
		
		return $is_error_found
	}
	
	proc check_VAL_VALComponentMaterial_TypeAndAttribute { fp_LogFile } { 
	
		set is_error_found "false"
		
		set project "GLOBAL_COMPONENTS_INTERNAL"
		set organization "VALMET_INTERNAL"
		set interface_MBOM_MBOMERP "MBOM_MBOMERP"
		set interface_TRS_ShortEnglish "TRS_ShortEnglish"
		set interface_TRS_TermID "TRS_TermID"
		set interface_DELAsmUnitRefRequired "DELAsmUnitRefRequired"
		set interface_DELAsmLotRefRequired "DELAsmLotRefRequired"
		set interface_MBOM_MBOMContReference "MBOM_MBOMContReference"		
		
		set valComponentMaterialID_temp [mql temp query bus "VAL_VALComponentMaterial" "skeletonID_do_not_delete" * select id project organization interface dump |]
		
		
		if { $valComponentMaterialID_temp == "" } {
			set is_error_found "true"
			writeLog $fp_LogFile "Error ::: No skeleton object found for type VAL_VALComponentMaterial with name skeletonID_do_not_delete"
		} else {
			set valComponentMaterialID_temp_split [split $valComponentMaterialID_temp "|"]
			set ls_valComponentMaterial_temp $valComponentMaterialID_temp_split
			
			set valComponentMaterial_project [lindex $valComponentMaterialID_temp_split 4]
			if { $valComponentMaterial_project == $project} {
				writeLog $fp_LogFile "Project found for VAL_VALComponentMaterial"
			} else {
				set is_error_found "true"
				writeLog $fp_LogFile "Error ::: Correct Project not found for VAL_VALComponentMaterial"
			}
			
			set valComponentMaterial_organization [lindex $valComponentMaterialID_temp_split 5]	
			if { $valComponentMaterial_organization == $organization} {
				writeLog $fp_LogFile "Organization found for VAL_VALComponentMaterial"
			} else {
				set is_error_found "true"
				writeLog $fp_LogFile "Error ::: Correct Organization not found for VAL_VALComponentMaterial"
			}
			
			if { [lsearch $ls_valComponentMaterial_temp $interface_MBOM_MBOMERP] != -1 } {
				writeLog $fp_LogFile "MBOM_MBOMERP found for VAL_VALComponentMaterial"
			} else {
				set is_error_found "true"
				writeLog $fp_LogFile "Error ::: MBOM_MBOMERP not found for VAL_VALComponentMaterial"
			}
			
			if { [lsearch $ls_valComponentMaterial_temp $interface_TRS_ShortEnglish] != -1 } {
				writeLog $fp_LogFile "TRS_ShortEnglish found for VAL_VALComponentMaterial"
			} else {
				set is_error_found "true"
				writeLog $fp_LogFile "Error ::: TRS_ShortEnglish not found for VAL_VALComponentMaterial"
			}
			
			if { [lsearch $ls_valComponentMaterial_temp $interface_TRS_TermID] != -1 } {
				writeLog $fp_LogFile "TRS_TermID found for VAL_VALComponentMaterial"
			} else {
				set is_error_found "true"
				writeLog $fp_LogFile "Error ::: TRS_TermID not found for VAL_VALComponentMaterial"
			}
			
			if { [lsearch $ls_valComponentMaterial_temp $interface_DELAsmUnitRefRequired] != -1 } {
				writeLog $fp_LogFile "DELAsmUnitRefRequired found for VAL_VALComponentMaterial"
			} else {
				set is_error_found "true"
				writeLog $fp_LogFile "Error ::: DELAsmUnitRefRequired not found for VAL_VALComponentMaterial"
			}
			
			if { [lsearch $ls_valComponentMaterial_temp $interface_DELAsmLotRefRequired] != -1 } {
				writeLog $fp_LogFile "DELAsmLotRefRequired found for VAL_VALComponentMaterial"
			} else {
				set is_error_found "true"
				writeLog $fp_LogFile "Error ::: DELAsmLotRefRequired not found for VAL_VALComponentMaterial"
			}
			
			if { [lsearch $ls_valComponentMaterial_temp $interface_MBOM_MBOMContReference] != -1 } {
				writeLog $fp_LogFile "MBOM_MBOMContReference found for VAL_VALComponentMaterial"
			} else {
				set is_error_found "true"
				writeLog $fp_LogFile "Error ::: MBOM_MBOMContReference not found for VAL_VALComponentMaterial"
			}	
		}
		return $is_error_found
	}
	
	proc check_Document_TypeAndAttribute { fp_LogFile } { 
	
		set is_error_found "false"
		
		set project "GLOBAL_COMPONENTS_INTERNAL"
		set organization "VALMET_INTERNAL"
		set document_policy "Document Release"		
		
		set document_temp [mql temp query bus "Document" "skeletonID_do_not_delete" * where "policy == '$document_policy'" select id project organization policy dump |]
		
		
		if { $document_temp == "" } {
			set is_error_found "true"
			writeLog $fp_LogFile "Error ::: No skeleton object found for type Document with name skeletonID_do_not_delete"
		} else {
			set document_temp_split [split $document_temp "|"]
			
			set document_project [lindex $document_temp_split 4]
			if { $document_project == $project} {
				writeLog $fp_LogFile "Project found for Document"
			} else {
				set is_error_found "true"
				writeLog $fp_LogFile "Error ::: Correct Project not found for Document"
			}
			
			set document_organization [lindex $document_temp_split 5]	
			if { $document_organization == $organization} {
				writeLog $fp_LogFile "Organization found for Document"
			} else {
				set is_error_found "true"
				writeLog $fp_LogFile "Error ::: Correct Organization not found for Document"
			}
			
			set policy [lindex $document_temp_split 6]	
			if { $policy == $document_policy} {
				writeLog $fp_LogFile "Policy correct for Document"
			} else {
				set is_error_found "true"				
				writeLog $fp_LogFile "Error ::: Policy not corrent for Document"
			}				
		}
		return $is_error_found
	}
	
	set iRetCode [ catch {
		
		writeLog $fp_LogFile "Varifying VAL_VALComponent Type!!!\n"
		set is_error_found [check_VAL_VALComponent_TypeAndAttribute $fp_LogFile]
		
		if { $is_error_found == "true"} {
			writeLog $fp_LogFile "Error !!! Error occured in VAL_VALComponent, Check log (varifyType.log)"
		} else {
			writeLog $fp_LogFile "No Error found in VAL_VALComponent."
		}
		
		writeLog $fp_LogFile "\nVarifying VAL_VALComponentMaterial Type!!!\n"
		set is_error_found [check_VAL_VALComponentMaterial_TypeAndAttribute $fp_LogFile]
		
		if { $is_error_found == "true"} {
			writeLog $fp_LogFile "Error !!! Error occured in VAL_VALComponentMaterial, Check log (varifyType.log)"
		} else {
			writeLog $fp_LogFile "No Error found in VAL_VALComponentMaterial."
		}
		
		writeLog $fp_LogFile "\nVarifying Document Type!!!\n"
		set is_error_found [check_Document_TypeAndAttribute $fp_LogFile]
		
		if { $is_error_found == "true"} {
			writeLog $fp_LogFile "Error !!! Error occured in Document, Check log (varifyType.log)"
		} else {
			writeLog $fp_LogFile "No Error found in Document."
		}
		
		set sDateValue [clock format [clock seconds] -format "%m/%d/20%y %A %I:%M:%S %p"]				
	} errMsg ]
	
	
	if { $iRetCode == 0 } {
		writeLog $fp_LogFile "Program Executed Successfully"
	} else {
		puts "\nAborting the Transaction"
		#puts "Error Message : $errMsg"
		writeLog $fp_LogFile "Transaction aborted. Error: $errMsg"
	}
			
}