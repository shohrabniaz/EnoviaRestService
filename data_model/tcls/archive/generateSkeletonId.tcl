tcl;
eval {
	set fp_LogFile [open "generateSkeletonId.log" "w"]
	set fp_SkeletonIdFile [open "SkeletonIdFile.txt" "w"]
	

	proc writeLog { fp_LogFile message } {
			set currenttime [clock format [clock seconds] -format "%m/%d/20%y %A %I:%M:%S %p"]
			set outputmessage [ append "Time : " $currenttime " : " $message]
			puts "$outputmessage"
			puts $fp_LogFile $outputmessage
			flush $fp_LogFile
	}
	proc writeSkeletonId { fp_SkeletonIdFile message } {
			set currenttime [clock format [clock seconds] -format "%m/%d/20%y %A %I:%M:%S %p"]
			set outputmessage [ append "Time : " $currenttime " : " $message]
			puts "$outputmessage"
			puts $fp_SkeletonIdFile $outputmessage
			flush $fp_SkeletonIdFile
	}
	
	proc processMQLModifyCommand { fp_LogFile modify_command } {
		puts $modify_command
		writeLog $fp_LogFile "Executing MQL :"
		writeLog $fp_LogFile $modify_command
		executeCmd $fp_LogFile $modify_command "Modify Skeleton Id"
	}
	
	proc executeCmd {fp_LogFile mqlCommand op } {	
		set mqlRet [ catch {eval $mqlCommand} errorStr ];
		if {$mqlRet != 0} {
		    writeLog $fp_LogFile "$op $mqlCommand run was failed!!\n";
			writeLog $fp_LogFile "$errorStr \n";
			return 0;
		} else {
			writeLog $fp_LogFile "$mqlCommand  $op was  successfull\n";
			return 1;
		}
		flush $fp_LogFile;
	}

	
	proc createSkeletonIdAndUpdateAttr { fp_SkeletonIdFile fp_LogFile } {
	
		set type_attr_list [open "Type_AttributeList.txt" "r"]
		set skeletonId ""
		#set type_attribute_list ""
		while { [eof $type_attr_list] != 1 } {
			set sStarttime [clock seconds]
			set User_Detail [ gets $type_attr_list]
			set lUserDetail [split $User_Detail "|"]
			set attrName [lindex $lUserDetail 0]
			set attrValue [lindex $lUserDetail 1]
			
			if { [string compare $attrName "Type"] == 0 } {
				if { [string compare $attrValue "Document"] == 0} {
					writeLog $fp_LogFile "#################################################>>> $lUserDetail <<<#################################################"
					set policyName "Document Release"
					set mqlResult [mql temp query bus $attrValue * * orderby +id where "(type=='$attrValue') AND (policy=='$policyName')" limit 1 select id dump |]
					puts "Type: $attrValue and Result: $mqlResult"
					if { $mqlResult == "" } {
						puts "No skeleton object found for mentioned type : $attrValue"
						writeLog $fp_LogFile "No skeleton object found for mentioned type : $attrValue"
					} else {
						set mqlResultSplit [split $mqlResult "|"]
						set itemId [lindex $mqlResultSplit 3]
						#skeleton name
						set skeletonName "skeletonID_do_not_delete"
						#skeleton rev
						set skeletonRev [lindex $mqlResultSplit 2]
						puts "Item ID: $itemId, skeletonRev : $skeletonRev"
						#copy object
						set res [ catch {
							mql copy bus $itemId !file to $skeletonName $skeletonRev !history
						} errorMsg ]
						
						if { $res == 0 } {
							puts "Skeleton ID created successfully."
							writeLog $fp_LogFile "Skeleton ID created successfully."
						} else {
							writeLog $fp_LogFile "Skeleton ID already exists : $errorMsg"
							puts "Aborting the Transaction"
							puts "Skeleton ID already exists : $errorMsg"
						}
						set skeletonId [mql print bus $attrValue $skeletonName $skeletonRev select id dump |]
					
						puts "Generated skeletonId: $skeletonId"
						append type_id $attrValue " : " $itemId
						puts $type_id
						#writeSkeletonId $fp_SkeletonIdFile $type_id
						set type_id ""
						set skeletonName ""
						#project and organization do not exists in the list
						set type_attribute_list [ split [ mql print bus $skeletonId dump | ] "|" ]
						#writeSkeletonId $fp_SkeletonIdFile $type_attribute_list
						writeSkeletonId $fp_SkeletonIdFile "$attrValue : $skeletonId"
					}
				} else {
					writeLog $fp_LogFile "#################################################>>> $lUserDetail <<<#################################################"
					set mqlResult [mql temp query bus $attrValue * * orderby +id where type=="$attrValue" limit 1 select id dump |]
					puts "Type: $attrValue and Result: $mqlResult"
					if { $mqlResult == "" } {
						puts "No skeleton object found for mentioned type : $attrValue"
						writeLog $fp_LogFile "No skeleton object found for mentioned type : $attrValue"
					} else {
						set mqlResultSplit [split $mqlResult "|"]
						set itemId [lindex $mqlResultSplit 3]
						#skeleton name
						set skeletonName "skeletonID_do_not_delete"
						#skeleton rev
						set skeletonRev [lindex $mqlResultSplit 2]
						puts "Item ID: $itemId, skeletonRev : $skeletonRev"
						#copy object
						set res [ catch {
							mql copy bus $itemId !file to $skeletonName $skeletonRev !history
						} errorMsg ]
						
						if { $res == 0 } {
							puts "Skeleton ID created successfully."
							writeLog $fp_LogFile "Skeleton ID created successfully."
						} else {
							writeLog $fp_LogFile "Skeleton ID already exists : $errorMsg"
							puts "Aborting the Transaction"
							puts "Skeleton ID already exists : $errorMsg"
						}
						set skeletonId [mql print bus $attrValue $skeletonName $skeletonRev select id dump |]
					
						puts "Generated skeletonId: $skeletonId"
						append type_id $attrValue " : " $itemId
						puts $type_id
						#writeSkeletonId $fp_SkeletonIdFile $type_id
						set type_id ""
						set skeletonName ""
						#project and organization do not exists in the list
						set type_attribute_list [ split [ mql print bus $skeletonId dump | ] "|" ]
						#writeSkeletonId $fp_SkeletonIdFile $type_attribute_list
						writeSkeletonId $fp_SkeletonIdFile "$attrValue : $skeletonId"
					}
				}
			} elseif { [string compare $attrName "InterfaceList"] == 0 } {
				set listOfInterfaces [split $attrValue ","]
				set attrName [lindex $lUserDetail 0]
				set attrValue [lindex $lUserDetail 1]

				foreach interfaceName $listOfInterfaces {
					writeLog $fp_LogFile "#################################################>>> Interface $interfaceName <<<#################################################"
					puts "Adding interface : $interfaceName"
					set modify_command "mql modify bus $skeletonId add interface $interfaceName"
					processMQLModifyCommand $fp_LogFile $modify_command
				}
			} else {
				puts "Skeleton ID: $skeletonId"
				if { $skeletonId != ""} {
					writeLog $fp_LogFile "#################################################>>> Attribute $attrName <<<#################################################"
					writeLog $fp_LogFile "Value : $attrValue"
					set modify_command " mql modify bus $skeletonId $attrName $attrValue"
					processMQLModifyCommand $fp_LogFile $modify_command
				}
			}
		}
	}
	
	
	set iRetCode [ catch { 
	
		createSkeletonIdAndUpdateAttr $fp_SkeletonIdFile $fp_LogFile

		#To set the current date
		set sDateValue [clock format [clock seconds] -format "%m/%d/20%y %A %I:%M:%S %p"]
				

	} errMsg ]

	if { $iRetCode == 0 } {
		puts "Program Executed Successfully"
		writeLog $fp_LogFile "Program Executed Successfully"
	} else {
		puts "Aborting the Transaction"
		puts "Error Message : $errMsg"
		writeLog $fp_LogFile "Transaction aborted. Error: $errMsg"
	}
			
}