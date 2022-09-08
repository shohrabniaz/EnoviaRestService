#############################################################
# @Name: XML file preparaion on released state
# @Version: 1.0
# @Created By: BJIT Ltd.
# @Release: 
# @Task : 128711
#############################################################
tcl;
eval {
	set fp_LogFile [open "XML_file_preparaion_on_revision_for_CreateAssembly_type.log" "a"]
	proc writeLog { fp_LogFile message } {
		set currenttime [clock format [clock seconds] -format "%m/%d/20%y %A %I:%M:%S %p"]
		set outputmessage [ append "Time : " $currenttime " : " $message]
		puts $fp_LogFile $outputmessage
		flush $fp_LogFile
	}

        set deleteExistingTriggerObj [ catch {

        puts "Going to find existing Revision item Trigger object !!!"
	writeLog $fp_LogFile "Going to find existing Revion item Trigger object !!!"
	
	set triggerType [ split [ mql temp query bus "eService Trigger Program Parameters" "emxCustomRevisionTrigger " * select id dump ] , ]
	set triggerObjId [ lindex $triggerType 3 ]
	
	puts "Trigger object id : $triggerObjId"
	writeLog $fp_LogFile "Trigger object id : $triggerObjId"
	
	puts "Going to delete trigger !!!"
	writeLog $fp_LogFile "Going to delete trigger !!!"
	
	mql delete bus $triggerObjId
	
	puts "Delete trigger done !!!"
	writeLog $fp_LogFile "Delete trigger done !!!"

        } errorMsg ]

        if { $deleteExistingTriggerObj == 0 } {
            puts "Existing trigger deleted successfully"
            writeLog $fp_LogFile "Existing trigger deleted successfully !!!"
	} else {
            puts "No existing trigger found"
            writeLog $fp_LogFile "No existing trigger found !!!"
        }

	puts "Going to create Trigger object !!!"
	writeLog $fp_LogFile "Going to create Trigger object !!!"
	
	mql add bus "eService Trigger Program Parameters" "emxCustomRevisionTrigger"  "CusMastershipRevision"\
	policy "eService Trigger Program Policy"\
	vault "eService Administration"\
	"eService Program Argument 1" "\${OBJECTID}"\
	"eService Program Argument 2" "\${STATENAME}"\
	"eService Program Argument 3" "\${NEXTSTATE}"\
	"eService Program Argument 4" "\${NAME}"\
	"eService Program Argument 5" "\${REVISION}"\
	"eService Program Argument 6" "\${TYPE}"\
	"eService Program Name" "emxCustomRevisionTrigger"\
	"eService Constructor Arguments" "\${OBJECTID} \${STATENAME} \${NEXTSTATE} \${NAME} \${REVISION} \${TYPE}"\
	"eService Method Name" "mastershipChangeFileGeneration"\
	"eService Sequence Number" 1
	
	puts "Trigger object creation done !!!"
	writeLog $fp_LogFile "Trigger object creation done !!!"
	
	puts "Add Triger to policy !!!"
	writeLog $fp_LogFile "Add Triger to policy !!!"

	mql modify type "CreateAssembly"\
	add trigger majorrevision action emxTriggerManagerBase input "emxCustomRevisionTrigger";

	puts "Trigger addition done !!!"
	writeLog $fp_LogFile "Trigger addition done !!!"
	
	set triggerType [ split [ mql temp query bus "eService Trigger Program Parameters" "emxCustomRevisionTrigger" * select id dump ] , ]
	set triggerObjId [ lindex $triggerType 3 ]
	
	puts "Trigger object id : $triggerObjId"
	writeLog $fp_LogFile "Trigger object id : $triggerObjId"
	
	puts "Going to modify trigger object state !!!"
	writeLog $fp_LogFile "Going to modify trigger object state !!!"
	
	mql modify bus $triggerObjId current Active
	
	puts "Modify trigger object state done !!!"
	writeLog $fp_LogFile "Modify trigger object state done !!!"
	
	
}