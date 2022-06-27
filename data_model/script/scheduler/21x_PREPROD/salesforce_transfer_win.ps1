
if ((Get-ChildItem  G:\Integrations\EnoviaToSalesforce\ -Filter *.xml | Select-Object -Property fullname) -and !(Get-ChildItem  G:\Integrations\EnoviaToSalesforce\ -Filter *.lock | Select-Object -Property fullname))
{
Get-Date | Out-File -Append G:\Integrations\EnoviaToSalesforce\Salesforce_Scheduler.txt
Write-Host "Creating .lock file"
"Creating .lock file" | Out-File -Append G:\Integrations\EnoviaToSalesforce\Salesforce_Scheduler.txt
New-Item -Path 'G:\Integrations\EnoviaToSalesforce\transfer.lock' -ItemType File
Invoke-WebRequest -Uri 'https://dspp21integration.plm.valmet.com/EnoviaRestService/salesforce/mv/intregation' | Out-File -Append G:\Integrations\EnoviaToSalesforce\Salesforce_Scheduler.txt
Start-Sleep -s 10
Remove-Item -Path 'G:\Integrations\EnoviaToSalesforce\transfer.lock'
Get-Date | Out-File -Append G:\Integrations\EnoviaToSalesforce\Salesforce_Scheduler.txt
}
else
{
if ((Get-ChildItem  G:\Integrations\EnoviaToSalesforce\ -Filter *.lock | Select-Object -Property fullname))
{
Get-Date | Out-File -Append G:\Integrations\EnoviaToSalesforce\Salesforce_Scheduler.txt
Write-Host "Lock file is found. Previous file process is still running"
"Lock file is found. Previous file process is still running" | Out-File -Append G:\Integrations\EnoviaToSalesforce\Salesforce_Scheduler.txt
Get-Date | Out-File -Append G:\Integrations\EnoviaToSalesforce\Salesforce_Scheduler.txt
 
}
else
{
Get-Date | Out-File -Append G:\Integrations\EnoviaToSalesforce\Salesforce_Scheduler.txt
Write-Host "No files Found" 
"No files Found" | Out-File -Append G:\Integrations\EnoviaToSalesforce\Salesforce_Scheduler.txt
Get-Date | Out-File -Append G:\Integrations\EnoviaToSalesforce\Salesforce_Scheduler.txt
}
}



