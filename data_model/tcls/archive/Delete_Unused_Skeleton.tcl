tcl;
eval {
	set fp_LogFile [open "delete_unused_skeleton.log" "w"]
	set fp_err_LogFile [open "delete_unused_skeleton_error.log" "w"]

	set type "ProcessContinuousCreateMaterial"
	set name "skeletonID_do_not_delete"
	# NOTE: rev can be blank or specific. If no revision is specified, then all revisions found, will be deleted.
	set rev ""

	proc writeLog { fp_LogFile message } {
		set currenttime [clock format [clock seconds] -format "%m/%d/20%y %A %I:%M:%S %p"]
		set outputmessage [ append "Time : " $currenttime " : " $message]
		puts "$outputmessage"
		puts $fp_LogFile $outputmessage
		flush $fp_LogFile
	}

	proc delete_unused_skeleton { type name rev fp_LogFile} {
		if { [string length $rev] == 0 } {
			set rev "*"
		}
		set result_list [ mql temp query bus $type $name $rev select id dump | ]
		foreach single_line $result_list {
			set splitted_value [split $single_line "|"]
			set revision [lindex $splitted_value 2]
			set object_id [lindex $splitted_value 3]
			puts $fp_LogFile "Going to delete $type $name $revision."
			puts $fp_LogFile "MQL>delete bus $object_id"
			set mql_delete [ mql delete bus $object_id ]
			puts $fp_LogFile "DELETED!"
		}
	}

	set iRetCode [ catch {
		delete_unused_skeleton $type $name $rev $fp_LogFile
	} errMsg ]

	if { $iRetCode == 0 } {
		writeLog $fp_LogFile "Program Executed Successfully"	
	} else {
		puts "Aborting execution"
		puts "Error Message : $errMsg"
		writeLog $fp_err_LogFile "Error Message : $errMsg"	
	}
}