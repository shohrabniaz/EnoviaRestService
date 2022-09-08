if ((Get-ChildItem  G:\Integrations\EnoviaToCPQ\ -Filter *.xml | Select-Object -Property fullname) -and !(Get-ChildItem  G:\Integrations\EnoviaToCPQ\ -Filter *.lock | Select-Object -Property fullname))
{
Get-Date | Out-File -Append G:\Integrations\EnoviaToCPQ\CPQ_Scheduler.log
Write-Host "Creating .lock file"
"Creating .lock file" | Out-File -Append G:\Integrations\EnoviaToCPQ\CPQ_Scheduler.log
New-Item -Path 'G:\Integrations\LNTransfer\scheduler.lock' -ItemType File
Invoke-WebRequest -Uri 'https://dsd2v21integration.plm.valmet.com/EnoviaRestService/bomExportToCPQ' | Out-File -Append G:\Integrations\EnoviaToCPQ\CPQ_Scheduler.log
#Start-Sleep -s 15
#Remove-Item -Path 'G:\Integrations\LNTransfer\scheduler.lock'
Get-Date | Out-File -Append G:\Integrations\EnoviaToCPQ\CPQ_Scheduler.log
}
else
{
if ((Get-ChildItem  G:\Integrations\EnoviaToCPQ\ -Filter *.lock | Select-Object -Property fullname))
{
Get-Date | Out-File -Append G:\Integrations\EnoviaToCPQ\CPQ_Scheduler.log
Write-Host "Lock file is found. Previous file process is still running"
"Lock file is found. Previous file process is still running" | Out-File -Append G:\Integrations\EnoviaToCPQ\CPQ_Scheduler.log
Get-Date | Out-File -Append G:\Integrations\EnoviaToCPQ\CPQ_Scheduler.log
 
}
else
{
Get-Date | Out-File -Append G:\Integrations\EnoviaToCPQ\CPQ_Scheduler.log
Write-Host "No files Found" 
"No files Found" | Out-File -Append G:\Integrations\EnoviaToCPQ\CPQ_Scheduler.log
Get-Date | Out-File -Append G:\Integrations\EnoviaToCPQ\CPQ_Scheduler.log
}
}