################################################################################
#
# Description: Script for Delete Structure
#
# Author:  BJIT/Taufiqul Islam Khan
# Date:    2022-02-23
# Version: 1.0
#
#    Modifications:
#
#    Date:            
#    Modifier:        
#    Changed:         
#
################################################################################
tcl;
set fileName [open "structureName.txt" "r"]
set fp_LogFile [ open "structureDelete_execution.log" "w" ]
set fp_dataFile [ open "structureDelete_data.txt" "w" ]

	
	proc writeLog { fp_LogFile message } {
		set currenttime [clock format [clock seconds] -format "%m/%d/%Y %A %I:%M:%S %p"]
		set outputmessage [ append "Time : " $currenttime " : " $message]
		puts "$outputmessage"
		puts $fp_LogFile $outputmessage
		flush $fp_LogFile
		
	}

	proc executeMQLAndDeleteStructure { tnrList fp_LogFile fp_dataFile} {		
		set type [lindex $tnrList  0]
		set name [lindex $tnrList 1]
		set revision [lindex $tnrList 2]
		set mqlResult [mql expand bus $type $name $revision from rel * withroots recurse to all select bus id dump |]
		puts "showing result ......................................................"
		#writeLog $fp_LogFile "Start execute MQL......................................................"
		#puts $mqlResult
		set mqlExecutionTime [clock format [clock seconds] -format "%m/%d/20%y %A %I:%M:%S %p"]
		#writeLog $fp_LogFile "End execute......................................................"
		writeLog $fp_LogFile "MQL running time: $mqlExecutionTime"
		puts "End execute MQL......................................................"
		set newLine "\n"
		set lineData [split $mqlResult $newLine]

		foreach singleLine $lineData {
			set mqlResultSplit [split $singleLine "|"]
			set itemId [lindex $mqlResultSplit 6]
			append itemList "\n" $itemId		
		}
			
		set UniqueList [lsort -unique $itemList]
		puts $fp_dataFile $UniqueList
		#set exe_res "1"
		puts "Start Transaction ......................................................"
		writeLog $fp_LogFile "Start Transaction......................................................"
		set startTransaction [mql start transaction]
		set i 0
		foreach item $UniqueList {
			puts $fp_dataFile $item "\n"
			set exe_res [ catch {	
				set mqlResult [mql delete  bus '$item']
				incr i
			} errorMsg ]
			
		}
		set deleteExecutionTime [clock format [clock seconds] -format "%m/%d/20%y %A %I:%M:%S %p"]
		if { $exe_res != 0 } {
			writeLog $fp_LogFile "Program: Error occured while Executing Command $cmdMql"
			writeLog $fp_LogFile "Program: $errorMsg"
			puts "Abort Transaction ......................................................"
			writeLog $fp_LogFile "Abort Transaction......................................................"
			set abortTransaction [mql abort transaction]
			} elseif { $exe_res == 0 } {
		puts "Commiting Transaction ......................................................"
		writeLog $fp_LogFile "Commit Transaction......................................................"
		set commitTransaction [mql commit transaction]
		}
	writeLog $fp_LogFile "Delete Run time: $deleteExecutionTime"
	
	}
	

	proc readAndExecute { fileName fp_LogFile fp_dataFile } {
		set tnrList {}
		set fileContent [read $fileName]
		set newLine "\n"
		set tnrData [split $fileContent $newLine]
		
		foreach tnrSingleLine $tnrData {
			set tnrSingleLineSplit [split $tnrSingleLine ":"]
			set tnr [lindex $tnrSingleLineSplit 1]
			append tnrList "\n" $tnr
		}
		
		executeMQLAndDeleteStructure $tnrList $fp_LogFile $fp_dataFile
	}



	set iRetCode [ catch {
			readAndExecute $fileName $fp_LogFile $fp_dataFile
			set sDateValue [clock format [clock seconds] -format "%m/%d/20%y %A %I:%M:%S %p"]
			writeLog $fp_LogFile "Total execution time: $sDateValue"
		} errMsg ]
		
		if { $iRetCode == 0 } {
			writeLog $fp_LogFile "Program Execution Completed Successfully"
		} else {
			writeLog $fp_LogFile "Transaction aborted. Error: $errMsg"
		}
