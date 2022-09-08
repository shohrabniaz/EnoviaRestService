tcl;
eval {
	file mkdir "JPO_LOGS" "JPO_Backups"
	
	set fp_LogFile [open "JPO_LOGS/jpo_deployment_execution.log" "w"]
	set fp_err_LogFile [open "JPO_LOGS/jpo_deployment_execution_error.log" "w"]
	
	proc backupProgram { name fp_LogFile } {
		set currenttime [clock format [clock seconds] -format "%m_%d_20%y_%A_%I_%M_%S_%p"]
		set lPrograms [split [mql list program] \n]
		if { [lsearch $lPrograms $name] == -1 } {
			writeLog $fp_LogFile "Program : $name not available. Need to add."
		} else {
			writeLog $fp_LogFile "Program : $name exists. Going to take backup."
			
			set backupFileName [ concat $name $currenttime ]
			
			set execute_backup [ mql print program $name select code dump output JPO_Backups/$backupFileName.bak ]
			
			writeLog $fp_LogFile "Backup file has been generated for : $name"

			writeLog $fp_LogFile "Going to delete existing program : $name"

			set execute_delete [ mql delete program $name ]

			writeLog $fp_LogFile "Program : $name deleted from database."
		}
	}

	proc addProgram { name file_name fp_LogFile } {
		set file_str "file"
		
		writeLog $fp_LogFile "Going to add program $name"

		set execute_add [ mql add program $name java $file_str $file_name ]
		
		writeLog $fp_LogFile "Program $name added successfully."

	}

	proc compileProgram { name fp_LogFile } {
		set force_update "force update"

		writeLog $fp_LogFile "Going to compile program: $name"

		set execute_compile [ mql compile program $name $force_update ]
		
		writeLog $fp_LogFile "Compilation done for program : $name"

	}

	proc verifyJPO { name fp_LogFile fp_err_LogFile } {
		set jpoCodeLength [string length [ mql print program $name select code dump ] ]
		
		if { $jpoCodeLength == 0 } {
			writeLog $fp_err_LogFile "Error Message : JPO code deployment error occurred for : $name"
		} else {
			writeLog $fp_LogFile "JPO code deployment successfull for program : $name"
		}
	}

	proc writeLog { fp_LogFile message } {
		set currenttime [clock format [clock seconds] -format "%m/%d/20%y %A %I:%M:%S %p"]
		set outputmessage [ append "Time : " $currenttime " : " $message]
		puts "$outputmessage"
		puts $fp_LogFile $outputmessage
		flush $fp_LogFile
	}

	set iRetCode [ catch {

	
		backupProgram "emxClassificationPath" $fp_LogFile
		backupProgram "UserInformation" $fp_LogFile



		addProgram "emxClassificationPath" "emxClassificationPath_mxJPO.java" $fp_LogFile
		addProgram "UserInformation" "UserInformation_mxJPO.java" $fp_LogFile
	


		compileProgram "emxClassificationPath" $fp_LogFile
		compileProgram "UserInformation" $fp_LogFile

		
		verifyJPO "emxClassificationPath" $fp_LogFile $fp_err_LogFile
		verifyJPO "UserInformation" $fp_LogFile $fp_err_LogFile
		

	} errMsg ]

	if { $iRetCode == 0 } {
		writeLog $fp_LogFile "Program Executed Successfully"
	} else {
		writeLog $fp_err_LogFile "Error Message : $errMsg"	
	}	
}