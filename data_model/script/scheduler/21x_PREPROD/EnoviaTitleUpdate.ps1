# Environment variables
$serviceUrl = 'https://dspp21integration.plm.valmet.com/EnoviaRestService/vsix/translation/title/update';
$logFile = 'H:\Scheduler\EnoviaTitleUpdate\EnoviaTitleUpdate.log'
$lockFile = 'H:\Scheduler\EnoviaTitleUpdate\EnoviaTitleUpdate.lock'

# Log variables
$executionStartPrint = "`n////////////////////////////////   ENOVIA TITLE UPDATE STARTED  ////////////////////////";
$executionEndPrint   = "////////////////////////////////   ENOVIA TITLE UPDATE ENDED    ////////////////////////`n";


$executionStartPrint | Out-File -Append $logFile

#Avoid multiple service call at the same time
if (-not(Test-Path -Path $lockFile -PathType Leaf)) {
	"Execution Start Time: " | Out-File -Append $logFile
	Get-Date | Out-File -Append $logFile
	Write-Host "Creating .lock file"
	"Creating .lock file" | Out-File -Append $logFile
	New-Item -Path $lockFile -ItemType File
	Try {
	   "Service invoking..." | Out-File -Append $logFile
	   Invoke-WebRequest -Uri $serviceUrl | Out-File -Append $logFile
	} catch {
	   Write-Host "Unable to invoke service!"
	   (-join("Unable to invoke service: ", " ", $serviceUrl)) | Out-File -Append $logFile
	}

	Write-Host "Removing .lock file"
	"Removing .lock file" | Out-File -Append $logFile
	"Execution End Time:" | Out-File -Append $logFile
	Get-Date | Out-File -Append $logFile
	Start-Sleep -s 15
	Remove-Item -Path $lockFile
} else {
	"One service request is going on!" | Out-File -Append $logFile
	Get-Date | Out-File -Append $logFile
}

$executionEndPrint | Out-File -Append $logFile