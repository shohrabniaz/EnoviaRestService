tcl;
eval {
	
	set fp_LogFile [open "Adding_trigger_to_policy.log" "a"]
	proc writeLog { fp_LogFile message } {
		set currenttime [clock format [clock seconds] -format "%m/%d/20%y %A %I:%M:%S %p"]
		set outputmessage [ append "Time : " $currenttime " : " $message]
		puts $fp_LogFile $outputmessage
		flush $fp_LogFile
	}
	
	puts "Going to add trigger to policy  !!!"
	writeLog $fp_LogFile "Going to add trigger to policy !!!"
	
	#Triggers fires in IN_WORK state
	mql modify policy "VPLM_SMB_Definition"\
    state IN_WORK remove trigger promote action\
    add trigger promote action emxTriggerManagerBase  input "StateChangeTrigger CustomEnoviaCPQTransferTrigger MastershipChangeStateChangeTrigger"
	puts "IN_WORK state triggers added to the policy  !!!"
	writeLog $fp_LogFile "IN_WORK state triggers added to the policy  !!!"
	
	
	#Triggers fires in FROZEN state
    mql modify policy "VPLM_SMB_Definition"\
    state FROZEN remove trigger promote action\
    add trigger promote action emxTriggerManagerBase  input "StateChangeTrigger CustomEnoviaCPQTransferTrigger CustomEnoviaCPQTransferTrigger MastershipChangeStateChangeTrigger"
	puts "FROZEN state triggers added to the policy  !!!"
	writeLog $fp_LogFile "FROZEN state triggers added to the policy  !!!"
	
	#Triggers fires in RELEASED state
    mql modify policy "VPLM_SMB_Definition"\
    state RELEASED remove trigger promote action\
    add trigger promote action emxTriggerManagerBase  input "StateChangeTrigger CustomEnoviaCPQTransferTrigger"
	puts "RELEASED state triggers added to the policy  !!!"
	writeLog $fp_LogFile "RELEASED state triggers added to the policy  !!!"
	
	puts "Adding trigger to policy done!!!"
	writeLog $fp_LogFile "Adding trigger to policy done!!!"
	
}