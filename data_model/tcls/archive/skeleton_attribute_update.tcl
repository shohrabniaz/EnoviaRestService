tcl;
eval {
	set fp_LogFile [open "skeleton_attribute_update_execution.log" "w"]
	set fp_err_LogFile [open "skeleton_attribute_update_error.log" "w"]

	set fp_object_properties_file [open "skeleton_attribute_update_Type_AttributeList.txt" "r"]
	#Edit following files to check if the listed things are available in the system or not
	
	proc writeLog { fp_LogFile message } {
		set currenttime [clock format [clock seconds] -format "%m/%d/20%y %A %I:%M:%S %p"]
		set outputmessage [ append "Time : " $currenttime " : " $message]
		puts "$outputmessage"
		puts $fp_LogFile $outputmessage
		flush $fp_LogFile
	}

	proc read_file_and_proceed_update { fp_object_properties_file fp_LogFile } {
		set file_data [read $fp_object_properties_file]
		set line_data [split $file_data "\n"]
		set type_name "NOT_SET_YET"
		set skeleton_id "NOT_SET_YET"
	 	foreach single_line $line_data {
	 		set splitted_value [split $single_line "|"]
	 		set schema_type [lindex $splitted_value 0]
	 		if { $schema_type == "Type"} {
	 			set type_name [lindex $splitted_value 1]
	 		} elseif {$schema_type == "ID"} {
	 			set skeleton_id [lindex $splitted_value 1]
	 		} elseif {$schema_type == "InterfaceList"} {
	 			set interface_check_list [lindex $splitted_value 1]
	 			check_and_update_interfaces $skeleton_id $interface_check_list $fp_LogFile
	 		} else {
	 			set property_value [lindex $splitted_value 1]
	 			update_property_value $skeleton_id $schema_type $property_value $fp_LogFile
	 		}
	 	}
	}

	proc check_and_update_interfaces { skeleton_id interface_check_list fp_LogFile } {
		set interface_check_list_as_list [ split $interface_check_list "," ]
		set available_interfaces [ mql pri bus $skeleton_id select interface dump | ]
		set available_interface_list [ split $available_interfaces "|" ]
		foreach check_interface $interface_check_list_as_list {
			if { [lsearch $available_interface_list $check_interface] == -1 } {
				writeLog $fp_LogFile "Interface: $check_interface does not exist in skeleton id: $skeleton_id. Going to add."
				set execute_mql [ mql mod bus $skeleton_id add interface $check_interface ]
				writeLog $fp_LogFile "Interface $check_interface added successfully to $skeleton_id."
			}
		}
	}

	proc update_property_value { skeleton_id property value fp_LogFile } {
		writeLog $fp_LogFile "Updating value of $property : to $value"
		set execute_property_update [ mql mod bus $skeleton_id $property $value ]
		writeLog $fp_LogFile "Update done."
	}

	set iRetCode [ catch {
		read_file_and_proceed_update $fp_object_properties_file $fp_LogFile
	} errMsg ]

	if { $iRetCode == 0 } {
		writeLog $fp_LogFile "Program Executed Successfully"	
	} else {
		puts "Aborting execution"
		puts "Error Message : $errMsg"
		writeLog $fp_err_LogFile "Error Message : $errMsg"	
	}	
}