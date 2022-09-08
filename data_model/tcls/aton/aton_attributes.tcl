################################################################################
#
# Description: Aton attributes creation TCL to transfer class and library in different environment.
#
# Author:  Farabi_11433
# Date:    2022-07-05
# Version: 1.0
#
#    Modifications:
#    Date:            
#    Modifier:        
#    Changed:         
#
################################################################################
tcl;
eval {
	# LOG file write
	set fp_LogFile [ open "aton_attributes.log" "w" ]
	
	proc writeLog { fp_LogFile message } {
		set currenttime [clock format [clock seconds] -format "%m/%d/%Y %A %I:%M:%S %p"]
		set outputmessage [ append "Time : " $currenttime " : " $message]
		puts "$outputmessage"
		puts $fp_LogFile $outputmessage
		flush $fp_LogFile
	}
	
	proc createAttributes { fp_LogFile } {	
	    
		set fp [open "aton_attributes_input.txt" r]
		set content [read $fp]
		#puts $file_data

		##excel formula##  =CONCAT(TRIM(SUBSTITUTE(B2,CHAR(160)," ")),"|",D2,"|",IF(EXACT(K2,"TRUE"),"true","false"),"|",F2,"|",I2)
		
		#set content "Aton Version|string|false||
		#AUT Lifecycle Status|string|false|Undefined|Active, Demand, Design, Discontinued, Draft, End of Life, Field Tests, Phase out, Pilot, Planning, Proof of Concept, RDReady, Specification, Technical Release Tests, Undefined"

		## Split into attributes on newlines
		set attributes [split $content \n]
		foreach attr $attributes {
			if {[string trim $attr] != ""} {
				## Split into records on |
				set records [split $attr |]
				set query "mql add attribute "
				
				set index 0
				## Iterate over the records
				foreach rec $records {
					#if {[string trim $rec] != ""} {
					   ## Split into fields on colons
					   #set fields [split $rec " "]
					
					   ## Assign fields to variables and print some out...
					   #lassign $fields \
							 name type maxLength default range
					   if {$index == 0} {append query '$rec' " "}
					   if {$index == 1} {append query type " " $rec " "}
					   if {$index == 2} {
							if {[string trim $rec] == "true"} {
								append query multivalue " "
							}
					   }
					   if {$index == 3} {append query default " " '$rec' " "}
					   if {$index == 4} {
							## Split into fields on colons
							set ranges [split $rec ","]
							foreach range $ranges {
								if {[string trim $range] != ""} {
									append query " range = " '[string trim $range]'
								}
							}
					   }
					   incr index
				   # }
				}
				append query " property classificationAttributes value true property parametric value false"
				puts $query
				writeLog $fp_LogFile "Query: $query"
				
				set ans [catch {eval $query} errorStrR]
				if { $ans == 0 } {
					writeLog $fp_LogFile "Attribute created Successfully."
				} else {
					writeLog $fp_LogFile "Error creating attribute : $errorStrR"
				}
			}
		}
		close $fp
	}
	
	set iRetCode [ catch {	
		createAttributes $fp_LogFile 
	} errMsg ]
	
	if { $iRetCode == 0 } {
		writeLog $fp_LogFile "Program Executed Successfully."
	} else {
		writeLog $fp_LogFile "Transaction aborted. Error: $errMsg"
	}
}